package com.xuan.entity.dto.article;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 创建文章请求参数类
 * 对应接口：4.1 发布/创建文章
 * 用于接收前端创建文章的参数
 * @author 玄〤
 * @since 2026-02-16
 */
@Data
public class ArticleCreateDTO {
    /**
     * 文章标题，必填
     */
    @NotBlank(message = "文章标题不能为空")
    private String title;
    
    /**
     * 文章内容，必填
     */
    @NotBlank(message = "文章内容不能为空")
    private String content;
    
    /**
     * 摘要
     */
    private String summary;
    
    /**
     * 分类ID，必填
     */
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;
    
    /**
     * 标签ID列表
     */
    private List<Long> tagIds;
    
    /**
     * 封面图片URL
     */
    private String coverImg;
    
    /**
     * 是否置顶：1-是，0-否
     */
    private Integer isTop;
    
    /**
     * 状态：0-草稿，1-发布，必填
     */
    @NotNull(message = "状态不能为空")
    private Integer status;
}