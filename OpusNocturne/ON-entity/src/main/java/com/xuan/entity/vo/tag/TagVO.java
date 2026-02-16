package com.xuan.entity.vo.tag;

import lombok.Data;

/**
 * 标签响应数据类
 * 用于在文章列表和详情中展示标签信息
 * @author 玄〤
 * @since 2026-02-16
 */
@Data
public class TagVO {
    /**
     * 标签ID
     */
    private Long id;
    
    /**
     * 标签名称
     */
    private String name;
}