package com.xuan.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuan.entity.po.sys.SysRole;
import com.xuan.entity.po.sys.SysRolePermission;
import com.xuan.service.mapper.SysPermissionMapper;
import com.xuan.service.mapper.SysRoleMapper;
import com.xuan.service.mapper.SysRolePermissionMapper;
import com.xuan.service.service.ISysRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色服务实现类
 *
 * @author 玄〤
 * @since 2026-02-20
 */
@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements ISysRoleService {

    private final SysRolePermissionMapper sysRolePermissionMapper;
    private final SysPermissionMapper sysPermissionMapper;

    @Override
    @Transactional
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        // 先删除旧权限关联
        sysRolePermissionMapper.deleteByRoleId(roleId);
        // 再批量插入新的
        if (permissionIds != null && !permissionIds.isEmpty()) {
            LocalDateTime now = LocalDateTime.now();
            for (Long permId : permissionIds) {
                SysRolePermission rp = new SysRolePermission();
                rp.setRoleId(roleId);
                rp.setPermissionId(permId);
                rp.setCreateTime(now);
                sysRolePermissionMapper.insert(rp);
            }
        }
    }

    @Override
    public List<Long> getRolePermissionIds(Long roleId) {
        return sysPermissionMapper.selectPermissionIdsByRoleId(roleId);
    }
}