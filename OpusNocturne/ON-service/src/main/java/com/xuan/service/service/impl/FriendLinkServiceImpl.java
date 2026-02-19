package com.xuan.service.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuan.common.enums.ErrorCode;
import com.xuan.common.exceptions.BusinessException;
import com.xuan.entity.dto.friendlink.FriendLinkApplyDTO;
import com.xuan.entity.dto.friendlink.FriendLinkAuditDTO;
import com.xuan.entity.dto.friendlink.FriendLinkPageQueryDTO;
import com.xuan.entity.po.interact.FriendLink;
import com.xuan.service.mapper.FriendLinkMapper;
import com.xuan.service.service.IFriendLinkService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 友情链接服务实现类
 *
 * @author 玄〤
 */
@Service
public class FriendLinkServiceImpl extends ServiceImpl<FriendLinkMapper, FriendLink> implements IFriendLinkService {

    @Override
    public void applyFriendLink(FriendLinkApplyDTO dto) {
        // 校验是否已存在（根据 URL）
        Long count = this.count(new LambdaQueryWrapper<FriendLink>()
                .eq(FriendLink::getUrl, dto.getUrl()));
        if (count > 0) {
            throw new BusinessException(ErrorCode.DATA_ALREADY_EXISTS, "该链接已申请过");
        }

        FriendLink friendLink = BeanUtil.copyProperties(dto, FriendLink.class);
        // 默认为待审核(0)
        friendLink.setStatus(0);
        friendLink.setSort(0);
        this.save(friendLink);
    }

    @Override
    public List<FriendLink> listPublicFriendLinks() {
        // 仅查询状态为 1 (上线) 的友链，按排序降序、创建时间升序排列
        return this.list(new LambdaQueryWrapper<FriendLink>()
                .eq(FriendLink::getStatus, 1)
                .orderByDesc(FriendLink::getSort)
                .orderByAsc(FriendLink::getCreateTime));
    }

    @Override
    public Page<FriendLink> pageFriendLinks(FriendLinkPageQueryDTO query) {
        Page<FriendLink> page = new Page<>(query.getCurrent(), query.getSize());
        LambdaQueryWrapper<FriendLink> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(query.getStatus() != null, FriendLink::getStatus, query.getStatus())
                .like(StrUtil.isNotBlank(query.getName()), FriendLink::getName, query.getName())
                .orderByDesc(FriendLink::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    public void auditFriendLink(Long id, FriendLinkAuditDTO dto) {
        FriendLink friendLink = this.getById(id);
        if (friendLink == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND);
        }
        friendLink.setStatus(dto.getStatus());
        this.updateById(friendLink);
        // TODO: 如果审核未通过，可发送邮件通知站长 (需集成邮件服务)
    }
}
