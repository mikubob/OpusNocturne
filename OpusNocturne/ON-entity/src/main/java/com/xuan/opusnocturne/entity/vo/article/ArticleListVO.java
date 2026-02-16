package com.xuan.opusnocturne.entity.vo.article;

import com.xuan.opusnocturne.entity.vo.tag.TagVO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章列表响应数据类
 * 对应接口：4.2 前台文章列表响应
 * 用于返回文章列表数据
 * @author 玄〤
 * @since 2026-02-16
 */
@Data
public class ArticleListVO {
    /**
     * 文章ID
     */
    private Long id;
    
    /**
     * 文章标题
     */
    private String title;
    
    /**
     * 摘要
     */
    private String summary;
    
    /**
     * 封面图片
     */
    private String coverImg;
    
    /**
     * 浏览次数
     */
    private Long viewCount;
    
    /**
     * 发布时间
     */
    private LocalDateTime publishTime;
    
    /**
     * 分类名称
     */
    private String categoryName;
    
    /**
     * 标签列表
     */
    private List<TagVO> tags;
}