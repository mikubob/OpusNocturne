package com.xuan.opusnocturne.entity.vo.category;

import lombok.Data;

/**
 * 分类响应数据类
 * 对应接口：5.1 获取全部分类
 * 用于返回分类及其文章数量信息
 * @author 玄〤
 * @since 2026-02-16
 */
@Data
public class CategoryVO {
    /**
     * 分类ID
     */
    private Long id;
    
    /**
     * 分类名称
     */
    private String name;
    
    /**
     * 文章数量
     */
    private Integer articleCount;
}