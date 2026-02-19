package com.xuan.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuan.common.constant.RedisConstant;
import com.xuan.entity.po.sys.VisitLog;
import com.xuan.service.mapper.VisitLogMapper;
import com.xuan.service.service.IVisitLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 访问记录服务实现类
 * 使用 Redis HyperLogLog 进行 UV 统计，计数器进行 PV 统计
 *
 * @author 玄〤
 * @since 2026-02-20
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VisitLogServiceImpl extends ServiceImpl<VisitLogMapper, VisitLog> implements IVisitLogService {

    private final StringRedisTemplate redisTemplate;

    @Async
    @Override
    public void recordVisit(String ipAddress, String userAgent, String pageUrl, String referer) {
        try {
            // 1. 写入数据库（持久化日志记录）
            VisitLog visitLog = new VisitLog();
            visitLog.setIpAddress(ipAddress);
            visitLog.setUserAgent(userAgent);
            visitLog.setPageUrl(pageUrl);
            visitLog.setReferer(referer);
            visitLog.setVisitTime(LocalDateTime.now());
            this.save(visitLog);

            // 2. Redis 计数 — 今日 PV
            String today = LocalDate.now().toString();
            String pvKey = RedisConstant.STATS_PV_KEY_PREFIX + today;
            redisTemplate.opsForValue().increment(pvKey);
            redisTemplate.expire(pvKey, RedisConstant.STATS_TTL_DAYS, TimeUnit.DAYS);

            // 3. Redis HyperLogLog — 今日 UV（基于 IP 去重）
            String uvKey = RedisConstant.STATS_UV_KEY_PREFIX + today;
            redisTemplate.opsForHyperLogLog().add(uvKey, ipAddress);
            redisTemplate.expire(uvKey, RedisConstant.STATS_TTL_DAYS, TimeUnit.DAYS);

            // 4. 全站总 PV
            redisTemplate.opsForValue().increment(RedisConstant.STATS_TOTAL_PV_KEY);
        } catch (Exception e) {
            log.error("记录访问日志失败: {}", e.getMessage(), e);
        }
    }

    @Override
    public Long getTodayPV() {
        String pvKey = RedisConstant.STATS_PV_KEY_PREFIX + LocalDate.now().toString();
        String val = redisTemplate.opsForValue().get(pvKey);
        return val != null ? Long.parseLong(val) : 0L;
    }

    @Override
    public Long getTodayUV() {
        String uvKey = RedisConstant.STATS_UV_KEY_PREFIX + LocalDate.now().toString();
        Long size = redisTemplate.opsForHyperLogLog().size(uvKey);
        return size != null ? size : 0L;
    }

    @Override
    public Long getTotalPV() {
        String val = redisTemplate.opsForValue().get(RedisConstant.STATS_TOTAL_PV_KEY);
        return val != null ? Long.parseLong(val) : 0L;
    }

    @Override
    public List<Map<String, Object>> getVisitTrend(int days) {
        LocalDateTime end = LocalDate.now().atTime(LocalTime.MAX);
        LocalDateTime start = LocalDate.now().minusDays(days - 1).atStartOfDay();
        return baseMapper.getVisitTrend(start, end);
    }
}