package com.xuan.entity.vo.comment;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论树响应数据类
 * 对应接口：6.1 获取文章评论树
 * 用于返回评论的树形结构数据
 * @author 玄〤
 * @since 2026-02-16
 */
@Data
public class CommentTreeVO {
    /**
     * 评论ID
     */
    private Long id;
    
    /**
     * 昵称
     */
    private String nickname;
    
    /**
     * 评论内容
     */
    private String content;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 被回复人昵称
     */
    private String replyNickname;
    
    /**
     * 子评论列表
     */
    private List<CommentTreeVO> children;
}