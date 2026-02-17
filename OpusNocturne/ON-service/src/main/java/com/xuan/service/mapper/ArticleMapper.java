package com.xuan.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuan.entity.po.blog.Article;

// TODO 文章Mapper
// 对应数据库表：article, article_tag
public interface ArticleMapper extends BaseMapper<Article> {
    
    // TODO 根据文章ID删除标签关联
    // DELETE FROM article_tag WHERE article_id = ?
    
    // TODO 批量插入文章标签关联
    // INSERT INTO article_tag (article_id, tag_id, create_time) VALUES (?, ?, ?)
    
    // TODO 根据文章ID查询标签ID列表
    // SELECT tag_id FROM article_tag WHERE article_id = ?
    
    // TODO 根据标签ID查询文章数量
    // SELECT COUNT(*) FROM article_tag WHERE tag_id = ?
}

