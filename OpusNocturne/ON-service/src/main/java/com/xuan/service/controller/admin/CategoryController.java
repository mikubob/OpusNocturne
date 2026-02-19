package com.xuan.service.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuan.common.domain.Result;
import com.xuan.entity.dto.category.CategoryCreateDTO;
import com.xuan.entity.dto.category.CategoryPageQueryDTO;
import com.xuan.entity.dto.category.CategoryUpdateDTO;
import com.xuan.entity.vo.category.CategoryAdminVO;
import com.xuan.service.service.ICategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 后台分类管理控制器
 */
@Tag(name = "后台分类管理")
@RestController
@RequestMapping("/api/admin/category")
@RequiredArgsConstructor
public class CategoryController {

    private final ICategoryService categoryService;

    @Operation(summary = "分页获取分类列表")
    @GetMapping("/list")
    public Result<Page<CategoryAdminVO>> pageCategories(@Validated CategoryPageQueryDTO queryDTO) {
        return Result.success(categoryService.pageCategories(queryDTO));
    }

    @Operation(summary = "创建分类")
    @PostMapping
    public Result<Void> createCategory(@Validated @RequestBody CategoryCreateDTO createDTO) {
        categoryService.createCategory(createDTO);
        return Result.success();
    }

    @Operation(summary = "更新分类")
    @PutMapping("/{id}")
    public Result<Void> updateCategory(@PathVariable Long id, @Validated @RequestBody CategoryUpdateDTO updateDTO) {
        categoryService.updateCategory(id, updateDTO);
        return Result.success();
    }

    @Operation(summary = "删除分类")
    @DeleteMapping("/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return Result.success();
    }
}