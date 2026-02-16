package com.xuan.opusnocturne.entity.po.blog;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文章-标签关联表实体类
 * 对应数据库表：article_tag
 * 用于存储文章与标签的关联关系
 * @author 玄〤
 * @since 2026-02-16
 */
@Data
@TableName("article_tag")
public class ArticleTag {
    /**
     * 主键id
     */
    private Long id;

    /**
     * 文章id
     */
    private Long articleId;

    /**
     * 标签id
     */
    private Long tagId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
