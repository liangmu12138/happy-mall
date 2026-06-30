package com.happy.mall.controller;

import com.happy.mall.common.Result;
import com.happy.mall.entity.Category;
import com.happy.mall.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类控制器
 */
@Tag(name = "分类接口")
@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "获取分类列表")
    @GetMapping("/list")
    public Result<?> getCategoryList() {
        List<Category> categories = categoryService.getAllCategories();
        return Result.success(categories);
    }

    @Operation(summary = "获取分类树形结构")
    @GetMapping("/tree")
    public Result<?> getCategoryTree() {
        List<Category> categories = categoryService.getCategoryTree();
        return Result.success(categories);
    }
}
