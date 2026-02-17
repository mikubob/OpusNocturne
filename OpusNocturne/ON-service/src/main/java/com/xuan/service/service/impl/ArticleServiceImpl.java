package com.xuan.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuan.entity.po.blog.Article;
import com.xuan.service.mapper.ArticleMapper;

//TODO 文章服务实现类
// 对应数据库表：article, article_tag
// 对应接口文档：4. 文章管理 (Blog Article)
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> {
    
    // TODO 创建文章
    // 1. 保存文章基本信息到 article 表
    // 2. 根据 tagIds 参数处理文章-标签关联关系，保存到 article_tag 表
    // 对应接口：POST /api/admin/article
    
    // TODO 更新文章
    // 1. 更新文章基本信息
    // 2. 先删除原有的文章-标签关联记录
    // 3. 根据新的 tagIds 参数重新生成关联记录
    // 对应接口：PUT /api/admin/article/{id}
    
    // TODO 删除文章
    // 1. 逻辑删除 article 表中的记录
    // 2. 删除对应的 article_tag 关联记录
    // 对应接口：DELETE /api/admin/article/{id}
    
    // TODO 获取文章详情
    // 1. 获取文章基本信息
    // 2. 查询关联的标签信息
    // 对应接口：GET /api/admin/article/{id}
}

