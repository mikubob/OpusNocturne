package com.xuan.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.xuan.common.enums.ErrorCode;
import com.xuan.common.exceptions.BusinessException;
import com.xuan.common.utils.JwtUtils;
import com.xuan.common.utils.PasswordUtils;
import com.xuan.common.constant.RedisConstant;
import com.xuan.entity.dto.auth.LoginDTO;
import com.xuan.entity.po.sys.SysUser;
import com.xuan.entity.vo.auth.LoginVO;
import com.xuan.entity.vo.auth.UserInfoVO;
import com.xuan.service.mapper.SysPermissionMapper;
import com.xuan.service.mapper.SysUserMapper;
import com.xuan.service.service.IAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 认证服务实现类
 *
 * @author 玄〤
 * @since 2026-02-20
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final SysUserMapper sysUserMapper;
    private final SysPermissionMapper sysPermissionMapper;
    private final JwtUtils jwtUtils;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        // 1. 根据用户名查找用户
        SysUser user = sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, loginDTO.getUsername()));
        if (user == null) {
            throw new BusinessException(ErrorCode.LOGIN_FAILED);
        }

        // 2. 校验密码
        if (!PasswordUtils.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.LOGIN_FAILED);
        }

        // 3. 检查用户状态
        if (user.getStatus() != 1) {
            throw new BusinessException(ErrorCode.USER_DISABLED);
        }

        // 4. 生成 Token
        String token = jwtUtils.generateToken(user.getUsername(),
                Map.of("userId", user.getId()));

        // 5. 将 Token 存入 Redis（用于登出失效管理）
        String redisKey = RedisConstant.TOKEN_KEY_PREFIX + user.getUsername();
        stringRedisTemplate.opsForValue().set(redisKey, token,
                jwtUtils.getExpiration(), TimeUnit.SECONDS);

        // 6. 更新最后登录时间
        sysUserMapper.update(null, new LambdaUpdateWrapper<SysUser>()
                .eq(SysUser::getId, user.getId())
                .set(SysUser::getLastLoginTime, LocalDateTime.now()));

        // 7. 构建返回结果
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setTokenHead(jwtUtils.getTokenPrefix());
        return loginVO;
    }

    @Override
    public void logout(String username) {
        // 从 Redis 中删除 Token
        String redisKey = RedisConstant.TOKEN_KEY_PREFIX + username;
        stringRedisTemplate.delete(redisKey);
    }

    @Override
    public UserInfoVO getUserInfo(String username) {
        // 1. 查询用户信息
        SysUser user = sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, username));
        if (user == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        // 2. 查询用户权限列表
        List<String> permissions = sysPermissionMapper.selectPermissionCodesByUserId(user.getId());

        // 3. 构建返回结果
        UserInfoVO userInfoVO = new UserInfoVO();
        userInfoVO.setId(user.getId());
        userInfoVO.setUsername(user.getUsername());
        userInfoVO.setNickname(user.getNickname());
        userInfoVO.setAvatar(user.getAvatar());
        userInfoVO.setEmail(user.getEmail());
        userInfoVO.setPermissions(permissions);
        return userInfoVO;
    }

    @Override
    public LoginVO refreshToken(String username) {
        // 1. 生成新 Token
        SysUser user = sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, username));
        if (user == null) {
            throw new BusinessException(ErrorCode.TOKEN_EXPIRED);
        }

        String token = jwtUtils.generateToken(user.getUsername(),
                Map.of("userId", user.getId()));

        // 2. 更新 Redis 中的 Token
        String redisKey = RedisConstant.TOKEN_KEY_PREFIX + user.getUsername();
        stringRedisTemplate.opsForValue().set(redisKey, token,
                jwtUtils.getExpiration(), TimeUnit.SECONDS);

        // 3. 构建返回结果
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setTokenHead(jwtUtils.getTokenPrefix());
        return loginVO;
    }

    @Override
    public void changePassword(Long userId, com.xuan.entity.dto.auth.ChangePasswordDTO dto) {
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "两次输入密码不一致");
        }

        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        // 使用 PasswordUtils 校验
        if (!PasswordUtils.matches(dto.getOldPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "原密码错误");
        }

        // 加密新密码并更新
        sysUserMapper.update(null, new LambdaUpdateWrapper<SysUser>()
                .eq(SysUser::getId, userId)
                .set(SysUser::getPassword, PasswordUtils.encode(dto.getNewPassword())));

        // 登出用户（删除 Token），强制重新登录
        logout(user.getUsername());
    }
}
