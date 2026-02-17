package com.xuan.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuan.entity.po.sys.VisitLog;
import org.apache.ibatis.annotations.Mapper;

// TODO 访问记录Mapper
// 对应数据库表：visit_log
// 用于站点访问记录的CRUD操作和统计查询
@Mapper
public interface VisitLogMapper extends BaseMapper<VisitLog> {
    
    // TODO 统计指定时间范围内的访问次数
    // SELECT COUNT(*) FROM visit_log WHERE visit_time >= ? AND visit_time <= ?
    
    // TODO 统计指定时间范围内的页面浏览量
    // SELECT COUNT(*) FROM visit_log WHERE visit_time >= ? AND visit_time <= ?
    
    // TODO 获取访问趋势数据
    // SELECT DATE(visit_time) as date, COUNT(*) as visits FROM visit_log WHERE visit_time >= ? AND visit_time <= ? GROUP BY DATE(visit_time)
    
    // TODO 获取热门页面排行
    // SELECT page_url, COUNT(*) as views FROM visit_log WHERE visit_time >= ? AND visit_time <= ? GROUP BY page_url ORDER BY views DESC LIMIT ?
}
