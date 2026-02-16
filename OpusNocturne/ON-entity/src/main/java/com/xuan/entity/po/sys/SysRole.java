package com.xuan.entity.po.sys;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xuan.common.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统角色表实体类
 * 用于存储系统角色信息
 * @author 玄〤
 * @since 2026-02-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
public class SysRole extends BaseEntity {
    /**
     * 角色名称，唯一
     */
    private String roleName;

    /**
     * 角色编码，唯一
     */
    private String roleCode;

    /**
     * 描述
     */
    private String description;

    /**
     * 状态：1-启用；0-禁用
     */
    private Integer status;
}
