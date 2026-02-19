package com.xuan.service.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuan.common.domain.Result;
import com.xuan.entity.dto.article.*;
import com.xuan.entity.vo.article.*;
import com.xuan.service.service.IArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 后台文章管理控制器
 */
@Tag(name = "后台文章管理")
@RestController
@RequestMapping("/api/admin/article")
@RequiredArgsConstructor
public class ArticleController {

    private final IArticleService articleService;

    @Operation(summary = "创建文章")
    @PostMapping
    public Result<ArticleCreatVO> creatArticle(@Validated @RequestBody ArticleCreateDTO dto) {
        return Result.success(articleService.creatArticle(dto));
    }

    @Operation(summary = "后台文章列表")
    @GetMapping("/page")
    public Result<Page<ArticleAdminListVO>> pageArticles(@Validated ArticleAdminPageQueryDTO dto) {
        return Result.success(articleService.pageAdminArticles(dto));
    }

    @Operation(summary = "文章详情（后台）")
    @GetMapping("/{id}")
    public Result<ArticleAdminDetailVO> getArticleDetail(@PathVariable Long id) {
        return Result.success(articleService.getAdminArticleDetail(id));
    }

    @Operation(summary = "更新文章")
    @PutMapping("/{id}")
    public Result<Void> updateArticle(@PathVariable Long id, @Validated @RequestBody ArticleUpdateDTO dto) {
        articleService.updateArticle(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除文章")
    @DeleteMapping("/{id}")
    public Result<Void> deleteArticle(@PathVariable Long id) {
        articleService.deleteArticle(id);
        return Result.success();
    }

    @Operation(summary = "文章置顶/取消置顶")
    @PutMapping("/{id}/top")
    public Result<Void> updateArticleTop(@PathVariable Long id, @Validated @RequestBody ArticleTopDTO dto) {
        articleService.updateArticleTop(id, dto);
        return Result.success();
    }

    @Operation(summary = "更新文章状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateArticleStatus(@PathVariable Long id, @Validated @RequestBody ArticleStatusDTO dto) {
        articleService.updateArticleStatus(id, dto);
        return Result.success();
    }
}
