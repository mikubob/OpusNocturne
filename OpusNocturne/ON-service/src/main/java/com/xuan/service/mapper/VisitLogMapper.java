package com.xuan.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuan.entity.po.sys.VisitLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 访问记录 Mapper
 *
 * @author 玄〤
 * @since 2026-02-20
 */
@Mapper
public interface VisitLogMapper extends BaseMapper<VisitLog> {

    /**
     * 统计指定时间范围内的独立访客数（UV）
     */
    @Select("SELECT COUNT(DISTINCT ip_address) FROM visit_log WHERE visit_time >= #{start} AND visit_time <= #{end}")
    Long countUniqueVisitors(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /**
     * 统计指定时间范围内的页面浏览量（PV）
     */
    @Select("SELECT COUNT(*) FROM visit_log WHERE visit_time >= #{start} AND visit_time <= #{end}")
    Long countPageViews(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /**
     * 获取每日访问趋势数据
     */
    @Select("SELECT DATE(visit_time) as visit_date, COUNT(*) as pv, COUNT(DISTINCT ip_address) as uv " +
            "FROM visit_log WHERE visit_time >= #{start} AND visit_time <= #{end} " +
            "GROUP BY DATE(visit_time) ORDER BY visit_date")
    List<Map<String, Object>> getVisitTrend(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
