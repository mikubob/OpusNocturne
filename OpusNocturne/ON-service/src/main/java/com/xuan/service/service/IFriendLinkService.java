package com.xuan.service.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xuan.entity.dto.friendlink.FriendLinkApplyDTO;
import com.xuan.entity.dto.friendlink.FriendLinkAuditDTO;
import com.xuan.entity.dto.friendlink.FriendLinkPageQueryDTO;
import com.xuan.entity.po.interact.FriendLink;

import java.util.List;

/**
 * 友情链接服务接口
 *
 * @author 玄〤
 */
public interface IFriendLinkService extends IService<FriendLink> {

    /**
     * 申请友链
     *
     * @param dto 申请参数
     */
    void applyFriendLink(FriendLinkApplyDTO dto);

    /**
     * 获取公开友链列表
     *
     * @return 友链列表
     */
    List<FriendLink> listPublicFriendLinks();

    /**
     * 分页查询友链（后台）
     *
     * @param query 查询参数
     * @return 分页结果
     */
    Page<FriendLink> pageFriendLinks(FriendLinkPageQueryDTO query);

    /**
     * 审核友链
     *
     * @param id  友链ID
     * @param dto 审核参数
     */
    void auditFriendLink(Long id, FriendLinkAuditDTO dto);
}
