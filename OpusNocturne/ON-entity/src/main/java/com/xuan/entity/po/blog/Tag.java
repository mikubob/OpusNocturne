package com.xuan.entity.po.blog;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xuan.common.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文章标签表实体类
 * 对应数据库表：tag
 * 用于存储文章标签信息
 * @author 玄〤
 * @since 2026-02-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tag")
public class Tag extends BaseEntity {
    /**
     * 标签名称，唯一
     */
    private String name;

    /**
     * 标签颜色
     */
    private String color;
}
