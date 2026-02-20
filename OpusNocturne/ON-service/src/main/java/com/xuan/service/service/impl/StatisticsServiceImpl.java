package com.xuan.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuan.entity.po.blog.Article;
import com.xuan.entity.po.interact.Comment;
import com.xuan.service.mapper.ArticleMapper;
import com.xuan.service.mapper.CategoryMapper;
import com.xuan.service.mapper.CommentMapper;
import com.xuan.service.mapper.SysUserMapper;
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
 * - 文章/分类/标签/评论/用户数量：数据库查询
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
        private final SysUserMapper sysUserMapper;
        private final IVisitLogService visitLogService;

        /**
         * 获取站点概览统计
         * 接口文档 §11.1：返回 articleCount, categoryCount, tagCount, commentCount, userCount,
         * totalViewCount
         */
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
                result.put("userCount", sysUserMapper.selectCount(null));

                // 总浏览量（所有已发布文章的 viewCount 之和）
                Long totalViewCount = articleMapper.selectList(
                                new LambdaQueryWrapper<Article>().eq(Article::getStatus, 1)
                                                .select(Article::getViewCount))
                                .stream()
                                .mapToLong(a -> a.getViewCount() != null ? a.getViewCount() : 0)
                                .sum();
                result.put("totalViewCount", totalViewCount);

                return result;
        }

        /**
         * 获取文章发布趋势
         * 接口文档 §11.2：返回 labels + data
         */
        @Override
        public Map<String, Object> getArticleTrend() {
                Map<String, Object> result = new LinkedHashMap<>();
                // 过去7天每天发布的文章数
                List<String> labels = new ArrayList<>();
                List<Long> data = new ArrayList<>();
                for (int i = 6; i >= 0; i--) {
                        LocalDate date = LocalDate.now().minusDays(i);
                        LocalDateTime start = date.atStartOfDay();
                        LocalDateTime end = date.atTime(LocalTime.MAX);
                        labels.add(date.toString());
                        long count = articleMapper.selectCount(
                                        new LambdaQueryWrapper<Article>()
                                                        .between(Article::getCreateTime, start, end)
                                                        .eq(Article::getStatus, 1));
                        data.add(count);
                }
                result.put("labels", labels);
                result.put("data", data);
                return result;
        }

        /**
         * 获取访问统计
         * 接口文档 §11.3：返回 totalVisits, totalPageViews, trend, topPages
         */
        @Override
        public Map<String, Object> getVisitStats() {
                Map<String, Object> result = new LinkedHashMap<>();

                // 总访问量（来自 Redis）
                result.put("totalVisits", visitLogService.getTodayUV());
                result.put("totalPageViews", visitLogService.getTotalPV());

                // 过去7天访问趋势（来自数据库 visit_log 聚合查询）
                result.put("trend", visitLogService.getVisitTrend(7));

                // TODO: topPages 需要从 visit_log 表聚合查询热门页面
                result.put("topPages", Collections.emptyList());

                return result;
        }
}
