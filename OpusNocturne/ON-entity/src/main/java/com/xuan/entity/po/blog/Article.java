package com.xuan.entity.po.blog;


import com.baomidou.mybatisplus.annotation.TableName;
import com.xuan.common.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 文章主表实体类
 * 对应数据库表：article
 * 用于存储文章的基本信息和内容
 * @author 玄〤
 * @since 2026-02-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("article")
public class Article extends BaseEntity {
    /**
     * 作者id
     */
    private Long authorId;

    /**
     * 分类id
     */
    private Long categoryId;

    /**
     * 文章标题
     */
    private String title;

    /**
     * URL别名(SEO)，唯一
     */
    private String slug;

    /**
     * 文章摘要
     */
    private String summary;

    /**
     * 文章内容(markdown)
     */
    private String content;

    /**
     * 封面图片
     */
    private String coverImg;

    /**
     * SEO关键词
     */
    private String keywords;

    /**
     * 浏览次数(持久化用)
     */
    private Long viewCount;

    /**
     * 是否置顶：1-是；0-否
     */
    private Integer isTop;

    /**
     * 状态：0-草稿，1-发布，2-下架
     */
    private Integer status;

    /**
     * 发布时间
     */
    private LocalDateTime publishTime;
}
