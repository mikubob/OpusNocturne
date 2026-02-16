package com.xuan.entity.vo.auth;

import lombok.Data;

import java.util.List;

/**
 * 用户信息响应数据类
 * 对应接口：2.3 获取当前用户信息
 * 用于返回当前登录用户的详细信息
 * @author 玄〤
 * @since 2026-02-16
 */
@Data
public class UserInfoVO {
    /**
     * 用户ID
     */
    private Long id;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 昵称
     */
    private String nickname;
    
    /**
     * 头像
     */
    private String avatar;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 权限列表
     */
    private List<String> permissions;
}