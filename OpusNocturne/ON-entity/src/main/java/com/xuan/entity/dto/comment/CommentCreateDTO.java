package com.xuan.entity.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 发表评论请求参数类
 * 对应接口：6.2 发表评论
 * 用于接收前端发表评论的参数
 * @author 玄〤
 * @since 2026-02-16
 */
@Data
public class CommentCreateDTO {
    /**
     * 文章ID，必填，留言板传 0
     */
    @NotNull(message = "文章ID不能为空")
    private Long articleId;
    
    /**
     * 评论内容，必填
     */
    @NotBlank(message = "评论内容不能为空")
    private String content;
    
    /**
     * 父评论ID，回复时必填
     */
    private Long parentId;
    
    /**
     * 根评论ID，回复楼中楼时必填
     */
    private Long rootParentId;
}