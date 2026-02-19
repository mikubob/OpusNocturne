package com.xuan.service.service;

import com.xuan.entity.dto.auth.LoginDTO;
import com.xuan.entity.vo.auth.LoginVO;
import com.xuan.entity.vo.auth.UserInfoVO;

/**
 * 认证服务接口
 *
 * @author 玄〤
 * @since 2026-02-20
 */
public interface IAuthService {

    /**
     * 用户登录
     */
    LoginVO login(LoginDTO loginDTO);

    /**
     * 退出登录
     */
    void logout(String username);

    /**
     * 获取当前用户信息
     */
    UserInfoVO getUserInfo(String username);

    /**
     * 刷新 Token
     */
    LoginVO refreshToken(String username);

    /**
     * 修改密码
     */
    void changePassword(Long userId, com.xuan.entity.dto.auth.ChangePasswordDTO dto);
}
