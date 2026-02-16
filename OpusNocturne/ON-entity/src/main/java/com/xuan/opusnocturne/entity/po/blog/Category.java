package com.xuan.opusnocturne.entity.po.blog;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xuan.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文章分类表实体类
 * 对应数据库表：category
 * 用于存储文章分类信息
 * @author 玄〤
 * @since 2026-02-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("category")
public class Category extends BaseEntity {
    /**
     * 分类名称，唯一
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 排序（升序）
     */
    private Integer sort;

    /**
     * 状态：1-启用；0-禁用
     */
    private Integer status;
}
