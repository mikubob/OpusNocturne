package com.xuan.entity.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 登录请求参数类
 * 对应接口：2.1 用户登录
 * 用于接收前端登录请求的参数
 * @author 玄〤
 * @since 2026-02-16
 */
@Data
public class LoginDTO {
    /**
     * 用户名，必填
     */
    @NotBlank(message = "用户名不能为空")
    private String username;
    
    /**
     * 密码，必填
     */
    @NotBlank(message = "密码不能为空")
    private String password;
}