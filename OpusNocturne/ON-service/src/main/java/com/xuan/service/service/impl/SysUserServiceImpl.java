package com.xuan.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuan.common.enums.ErrorCode;
import com.xuan.common.exceptions.BusinessException;
import com.xuan.common.utils.PasswordUtils;
import com.xuan.entity.dto.user.UserCreateDTO;
import com.xuan.entity.dto.user.UserPageQueryDTO;
import com.xuan.entity.dto.user.UserResetPasswordDTO;
import com.xuan.entity.dto.user.UserUpdateDTO;
import com.xuan.entity.po.sys.SysUser;
import com.xuan.entity.po.sys.SysUserRole;
import com.xuan.entity.vo.auth.UserInfoVO;
import com.xuan.service.mapper.SysPermissionMapper;
import com.xuan.service.mapper.SysUserMapper;
import com.xuan.service.mapper.SysUserRoleMapper;
import com.xuan.service.service.ISysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户管理服务实现类
 *
 * @author 玄〤
 * @since 2026-02-20
 */
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysPermissionMapper sysPermissionMapper;

    @Override
    public Page<UserInfoVO> pageUsers(UserPageQueryDTO dto) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (dto.getUsername() != null && !dto.getUsername().isEmpty()) {
            wrapper.like(SysUser::getUsername, dto.getUsername());
        }
        if (dto.getNickname() != null && !dto.getNickname().isEmpty()) {
            wrapper.like(SysUser::getNickname, dto.getNickname());
        }
        wrapper.orderByDesc(SysUser::getCreateTime);

        // 确保分页参数不为null，提供默认值
        Integer currentPage = dto.getCurrent() != null ? dto.getCurrent() : 1;
        Integer pageSize = dto.getSize() != null ? dto.getSize() : 10;
        Page<SysUser> page = this.page(new Page<>(currentPage, pageSize), wrapper);

        Page<UserInfoVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        voPage.setRecords(page.getRecords().stream().map(user -> {
            UserInfoVO vo = new UserInfoVO();
            vo.setId(user.getId());
            vo.setUsername(user.getUsername());
            vo.setNickname(user.getNickname());
            vo.setAvatar(user.getAvatar());
            vo.setEmail(user.getEmail());
            vo.setStatus(user.getStatus());
            vo.setCreateTime(user.getCreateTime());
            return vo;
        }).collect(Collectors.toList()));
        return voPage;
    }

    @Override
    @Transactional
    public void createUser(UserCreateDTO dto) {
        // 检查用户名唯一
        long count = this.count(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, dto.getUsername()));
        if (count > 0) {
            throw new BusinessException(ErrorCode.USER_EXISTS);
        }

        SysUser user = new SysUser();
        user.setUsername(dto.getUsername());
        user.setPassword(PasswordUtils.encode(dto.getPassword()));
        user.setNickname(dto.getNickname());
        user.setEmail(dto.getEmail());
        user.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        this.save(user);

        // 保存用户角色关联
        saveUserRoles(user.getId(), dto.getRoleIds());
    }

    @Override
    @Transactional
    public void updateUser(Long id, UserUpdateDTO dto) {
        SysUser user = this.getById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        if (dto.getNickname() != null)
            user.setNickname(dto.getNickname());
        if (dto.getEmail() != null)
            user.setEmail(dto.getEmail());
        if (dto.getStatus() != null)
            user.setStatus(dto.getStatus());
        this.updateById(user);

        // 更新角色关联
        if (dto.getRoleIds() != null) {
            sysUserRoleMapper.deleteByUserId(id);
            saveUserRoles(id, dto.getRoleIds());
        }
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        SysUser user = this.getById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        this.removeById(id);
        sysUserRoleMapper.deleteByUserId(id);
    }

    @Override
    public UserInfoVO getUserDetail(Long id) {
        SysUser user = this.getById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        UserInfoVO vo = new UserInfoVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setEmail(user.getEmail());
        vo.setStatus(user.getStatus());
        vo.setCreateTime(user.getCreateTime());

        // 查询角色ID列表
        List<Long> roleIds = sysUserRoleMapper.selectRoleIdsByUserId(id);
        vo.setRoleIds(roleIds);

        // 查询权限
        List<String> permissions = sysPermissionMapper.selectPermissionCodesByUserId(id);
        vo.setPermissions(permissions);
        return vo;
    }

    @Override
    @Transactional
    public void resetPassword(Long id, UserResetPasswordDTO dto) {
        SysUser user = this.getById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        user.setPassword(PasswordUtils.encode(dto.getPassword()));
        this.updateById(user);
    }

    private void saveUserRoles(Long userId, List<Long> roleIds) {
        if (roleIds != null && !roleIds.isEmpty()) {
            LocalDateTime now = LocalDateTime.now();
            for (Long roleId : roleIds) {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(userId);
                ur.setRoleId(roleId);
                ur.setCreateTime(now);
                sysUserRoleMapper.insert(ur);
            }
        }
    }
}