package com.xuan.service.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xuan.entity.dto.article.*;
import com.xuan.entity.po.blog.Article;
import com.xuan.entity.vo.article.*;

/**
 * 文章服务接口
 */
public interface IArticleService extends IService<Article> {

    /** 创建/发布文章 */
    ArticleCreatVO creatArticle(ArticleCreateDTO dto);

    /** 后台：分页查询文章列表 */
    Page<ArticleAdminListVO> pageAdminArticles(ArticleAdminPageQueryDTO dto);

    /** 后台：获取文章详情 */
    ArticleAdminDetailVO getAdminArticleDetail(Long id);

    /** 更新文章 */
    void updateArticle(Long id, ArticleUpdateDTO dto);

    /** 删除文章（逻辑删除） */
    void deleteArticle(Long id);

    /** 设置文章置顶状态 */
    void updateArticleTop(Long id, ArticleTopDTO dto);

    /** 更新文章状态 */
    void updateArticleStatus(Long id, ArticleStatusDTO dto);

    /** 前台：分页查询文章列表 */
    Page<ArticleListVO> pageBlogArticles(ArticlePageQueryDTO dto);

    /** 前台：获取文章详情 */
    ArticleDetailVO getBlogArticleDetail(Long id);
}
