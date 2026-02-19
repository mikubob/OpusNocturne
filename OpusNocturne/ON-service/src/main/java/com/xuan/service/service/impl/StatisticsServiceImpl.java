package com.xuan.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuan.entity.po.blog.Article;
import com.xuan.entity.po.interact.Comment;
import com.xuan.service.mapper.ArticleMapper;
import com.xuan.service.mapper.CategoryMapper;
import com.xuan.service.mapper.CommentMapper;
import com.xuan.service.mapper.TagMapper;
import com.xuan.service.service.IStatisticsService;
import com.xuan.service.service.IVisitLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * 站点统计服务实现类
 * 数据来源：
 * - 文章/分类/标签/评论数量：数据库查询
 * - 浏览量/UV/PV：Redis 实时统计（通过 IVisitLogService）
 * - 访问趋势：数据库 visit_log 表聚合
 *
 * @author 玄〤
 * @since 2026-02-20
 */
@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements IStatisticsService {

        private final ArticleMapper articleMapper;
        private final CategoryMapper categoryMapper;
        private final TagMapper tagMapper;
        private final CommentMapper commentMapper;
        private final IVisitLogService visitLogService;

        @Override
        public Map<String, Object> getOverview() {
                Map<String, Object> result = new LinkedHashMap<>();

                // 基础计数（数据库查询）
                result.put("articleCount", articleMapper.selectCount(
                                new LambdaQueryWrapper<Article>().eq(Article::getStatus, 1)));
                result.put("categoryCount", categoryMapper.selectCount(null));
                result.put("tagCount", tagMapper.selectCount(null));
                result.put("commentCount", commentMapper.selectCount(
                                new LambdaQueryWrapper<Comment>().eq(Comment::getStatus, 1)));

                // 浏览量统计（Redis 实时数据）
                result.put("totalPV", visitLogService.getTotalPV());
                result.put("todayPV", visitLogService.getTodayPV());
                result.put("todayUV", visitLogService.getTodayUV());

                return result;
        }

        @Override
        public Map<String, Object> getArticleTrend() {
                Map<String, Object> result = new LinkedHashMap<>();
                // 过去7天每天发布的文章数
                List<String> dates = new ArrayList<>();
                List<Long> counts = new ArrayList<>();
                for (int i = 6; i >= 0; i--) {
                        LocalDate date = LocalDate.now().minusDays(i);
                        LocalDateTime start = date.atStartOfDay();
                        LocalDateTime end = date.atTime(LocalTime.MAX);
                        dates.add(date.toString());
                        long count = articleMapper.selectCount(
                                        new LambdaQueryWrapper<Article>()
                                                        .between(Article::getCreateTime, start, end)
                                                        .eq(Article::getStatus, 1));
                        counts.add(count);
                }
                result.put("dates", dates);
                result.put("counts", counts);
                return result;
        }

        @Override
        public Map<String, Object> getVisitStats() {
                Map<String, Object> result = new LinkedHashMap<>();

                // 实时统计（来自 Redis）
                result.put("todayPV", visitLogService.getTodayPV());
                result.put("todayUV", visitLogService.getTodayUV());
                result.put("totalPV", visitLogService.getTotalPV());

                // 过去7天访问趋势（来自数据库 visit_log 聚合查询）
                result.put("trend", visitLogService.getVisitTrend(7));

                return result;
        }
}
