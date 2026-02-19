package com.xuan.service.controller.blog;

import com.xuan.common.domain.Result;
import com.xuan.entity.dto.comment.CommentCreateDTO;
import com.xuan.entity.vo.comment.CommentTreeVO;
import com.xuan.service.service.ICommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 前台评论控制器
 */
@Tag(name = "前台评论")
@RestController("blogCommentController")
@RequestMapping("/api/blog/comment")
@RequiredArgsConstructor
public class CommentController {

    private final ICommentService commentService;

    @Operation(summary = "获取文章评论树")
    @GetMapping("/tree/{articleId}")
    public Result<List<CommentTreeVO>> getCommentTree(@PathVariable Long articleId) {
        return Result.success(commentService.getCommentTree(articleId));
    }

    @Operation(summary = "获取文章评论统计")
    @GetMapping("/stats/{articleId}")
    public Result<java.util.Map<String, Long>> getCommentStats(@PathVariable Long articleId) {
        return Result.success(commentService.getArticleCommentStats(articleId));
    }

    @Operation(summary = "发表评论")
    @PostMapping
    public Result<Void> createComment(@Validated @RequestBody CommentCreateDTO dto, HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        commentService.createComment(dto, ip, userAgent);
        return Result.success();
    }
}