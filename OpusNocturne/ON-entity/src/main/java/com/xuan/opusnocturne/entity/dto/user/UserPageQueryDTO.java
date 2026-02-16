package com.xuan.opusnocturne.entity.dto.user;

import com.xuan.common.BasePageQueryDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户列表分页查询参数类
 * 对应接口：3.1 分页获取用户列表
 * 用于接收前端分页查询用户列表的参数
 * @author 玄〤
 * @since 2026-02-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserPageQueryDTO extends BasePageQueryDTO {
    /**
     * 用户名搜索
     */
    private String username;
    
    /**
     * 昵称搜索
     */
    private String nickname;
}