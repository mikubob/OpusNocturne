package com.xuan.entity.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * 创建用户请求参数类
 * 对应接口：3.2 创建用户
 * 用于接收前端创建用户的参数
 * @author 玄〤
 * @since 2026-02-16
 */
@Data
public class UserCreateDTO {
    /**
     * 用户名，必填
     */
    @NotBlank(message = "用户名不能为空")
    private String username;
    
    /**
     * 初始密码，必填
     */
    @NotBlank(message = "密码不能为空")
    private String password;
    
    /**
     * 昵称
     */
    private String nickname;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 关联角色ID列表
     */
    private List<Long> roleIds;
    
    /**
     * 状态：1-启用，0-禁用 (默认1)
     */
    private Integer status;
}