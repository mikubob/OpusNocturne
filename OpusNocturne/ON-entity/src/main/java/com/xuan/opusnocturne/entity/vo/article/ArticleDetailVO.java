package com.xuan.opusnocturne.entity.vo.article;

import com.xuan.opusnocturne.entity.vo.tag.TagVO;
import lombok.Data;

import java.util.List;

/**
 * 文章详情响应数据类
 * 对应接口：4.3 前台文章详情响应
 * 用于返回文章详细信息
 * @author 玄〤
 * @since 2026-02-16
 */
@Data
public class ArticleDetailVO {
    /**
     * 文章ID
     */
    private Long id;
    
    /**
     * 文章标题
     */
    private String title;
    
    /**
     * 文章内容
     */
    private String content;
    
    /**
     * 分类ID
     */
    private Long categoryId;
    
    /**
     * 分类名称
     */
    private String categoryName;
    
    /**
     * 作者昵称
     */
    private String authorNickname;
    
    /**
     * 标签列表
     */
    private List<TagVO> tags;
    
    /**
     * 上一篇文章
     */
    private ArticleVO prevArticle;
    
    /**
     * 下一篇文章
     */
    private ArticleVO nextArticle;
}

/**
 * 文章前后篇响应数据类
 * 用于在文章详情中展示上一篇和下一篇文章信息
 */
@Data
class ArticleVO {
    /**
     * 文章ID
     */
    private Long id;
    
    /**
     * 文章标题
     */
    private String title;
}