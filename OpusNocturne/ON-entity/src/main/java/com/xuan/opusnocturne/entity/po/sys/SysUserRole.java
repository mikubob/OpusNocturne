package com.xuan.opusnocturne.entity.po.sys;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户-角色关联表实体类
 * 对应数据库表：sys_user_role
 * 用于存储用户与角色的关联关系
 * @author 玄〤
 * @since 2026-02-16
 */
@Data
@TableName("sys_user_role")
public class SysUserRole {
    /**
     * 主键id
     */
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 角色id
     */
    private Long roleId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}