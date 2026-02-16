package com.xuan.entity.po.interact;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xuan.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 评论表实体类
 * 对应数据库表：comment
 * 用于存储文章评论信息
 * @author 玄〤
 * @since 2026-02-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("comment")
public class Comment extends BaseEntity {
    /**
     * 文章id(null为留言)
     */
    private Long articleId;

    /**
     * 评论人id(游客为null)
     */
    private Long userId;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 内容
     */
    private String content;

    /**
     * 根评论id
     */
    private Long rootParentId;

    /**
     * 父评论id
     */
    private Long parentId;

    /**
     * 被回复人id
     */
    private Long replyUserId;

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * 设备信息
     */
    private String userAgent;

    /**
     * 状态：0-待审核；1-审核通过；2-审核未通过
     */
    private Integer status;
}