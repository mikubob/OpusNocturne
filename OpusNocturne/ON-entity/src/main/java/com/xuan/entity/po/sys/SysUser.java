package com.xuan.entity.po.sys;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xuan.common.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户角色表实体
 * 用于储存系统用户的基本信息
 * @author 玄〤
 * @since 2026-02-16
 */
@EqualsAndHashCode(callSuper = true)// 继承父类属性
@Data
@TableName("sys_user")
public class SysUser extends BaseEntity {
    /**
     * 用户名，唯一
     */
    private String username;

    /**
     * 加密后的密码（BCrypt）
     */
    private String password;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 邮箱，唯一
     */
    private String email;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 状态：1-启用；0-禁用
     */
    private Integer status;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;
}
