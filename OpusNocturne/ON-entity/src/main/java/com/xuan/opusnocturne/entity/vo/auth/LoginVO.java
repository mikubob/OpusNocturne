package com.xuan.opusnocturne.entity.vo.auth;

import lombok.Data;

/**
 * 登录响应数据类
 * 对应接口：2.1 用户登录响应
 * 用于返回登录成功后的令牌信息
 * @author 玄〤
 * @since 2026-02-16
 */
@Data
public class LoginVO {
    /**
     * JWT令牌
     */
    private String token;
    
    /**
     * 令牌前缀
     */
    private String tokenHead;
}