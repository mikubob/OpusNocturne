package com.xuan.entity.dto.article;

import com.xuan.common.domain.BasePageQueryDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文章列表分页查询参数类
 * 对应接口：4.2 前台文章列表
 * 用于接收前端分页查询文章列表的参数
 * @author 玄〤
 * @since 2026-02-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ArticlePageQueryDTO extends BasePageQueryDTO {
    /**
     * 按分类筛选
     */
    private Long categoryId;
    
    /**
     * 按标签筛选
     */
    private Long tagId;
}