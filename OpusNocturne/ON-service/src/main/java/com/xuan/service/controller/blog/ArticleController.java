package com.xuan.service.controller.blog;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuan.common.domain.Result;
import com.xuan.entity.dto.article.ArticlePageQueryDTO;
import com.xuan.entity.vo.article.ArticleDetailVO;
import com.xuan.entity.vo.article.ArticleListVO;
import com.xuan.service.service.IArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 前台文章控制器
 */
@Tag(name = "前台文章")
@RestController("blogArticleController")
@RequestMapping("/api/blog/article")
@RequiredArgsConstructor
public class ArticleController {

    private final IArticleService articleService;

    @Operation(summary = "前台文章列表")
    @GetMapping("/page")
    public Result<Page<ArticleListVO>> pageBlogArticles(@Validated ArticlePageQueryDTO dto) {
        return Result.success(articleService.pageBlogArticles(dto));
    }

    @Operation(summary = "前台文章详情")
    @GetMapping("/{id}")
    public Result<ArticleDetailVO> getArticleDetail(@PathVariable Long id) {
        return Result.success(articleService.getBlogArticleDetail(id));
    }
}