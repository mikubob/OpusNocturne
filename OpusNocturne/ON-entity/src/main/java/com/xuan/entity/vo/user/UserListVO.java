package com.xuan.entity.vo.user;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户列表响应数据类
 * 对应接口：3.1 分页获取用户列表响应
 * 用于返回用户列表数据
 * @author 玄〤
 * @since 2026-02-16
 */
@Data
public class UserListVO {
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
     * 头像URL
     */
    private String avatar;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 状态
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}