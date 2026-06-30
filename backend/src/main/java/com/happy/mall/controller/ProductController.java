package com.happy.mall.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.happy.mall.common.Result;
import com.happy.mall.entity.Product;
import com.happy.mall.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品控制器
 */
@Tag(name = "商品接口")
@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "获取商品列表")
    @GetMapping("/list")
    public Result<?> getProductList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword) {
        Page<Product> page = productService.getProductPage(pageNum, pageSize, categoryId, keyword);
        return Result.success(page);
    }

    @Operation(summary = "获取商品详情")
    @GetMapping("/{id}")
    public Result<?> getProductDetail(@PathVariable Long id) {
        Product product = productService.getProductDetail(id);
        return Result.success(product);
    }

    @Operation(summary = "获取分类下的商品")
    @GetMapping("/category/{categoryId}")
    public Result<?> getProductsByCategoryId(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "10") Integer limit) {
        List<Product> products = productService.getProductsByCategoryId(categoryId, limit);
        return Result.success(products);
    }

    @Operation(summary = "搜索商品")
    @GetMapping("/search")
    public Result<?> searchProducts(@RequestParam String keyword) {
        List<Product> products = productService.searchProducts(keyword);
        return Result.success(products);
    }
}
