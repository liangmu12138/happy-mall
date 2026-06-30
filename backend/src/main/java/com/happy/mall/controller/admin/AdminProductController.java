package com.happy.mall.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.happy.mall.common.Result;
import com.happy.mall.entity.Product;
import com.happy.mall.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 管理端 - 商品控制器
 */
@Tag(name = "管理端-商品接口")
@RestController
@RequestMapping("/api/admin/product")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductService productService;

    @Operation(summary = "分页查询商品")
    @GetMapping("/list")
    public Result<?> getProductPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        Page<Product> page = productService.adminGetProductPage(pageNum, pageSize, keyword, status);
        return Result.success(page);
    }

    @Operation(summary = "获取商品详情")
    @GetMapping("/{id}")
    public Result<?> getProductDetail(@PathVariable Long id) {
        Product product = productService.getProductDetail(id);
        return Result.success(product);
    }

    @Operation(summary = "新增商品")
    @PostMapping("/add")
    public Result<?> addProduct(@RequestBody Product product) {
        productService.adminAddProduct(product);
        return Result.success("添加成功");
    }

    @Operation(summary = "更新商品")
    @PutMapping("/update")
    public Result<?> updateProduct(@RequestBody Product product) {
        productService.adminUpdateProduct(product);
        return Result.success("更新成功");
    }

    @Operation(summary = "删除商品")
    @DeleteMapping("/{id}")
    public Result<?> deleteProduct(@PathVariable Long id) {
        productService.adminDeleteProduct(id);
        return Result.success("删除成功");
    }

    @Operation(summary = "上架/下架商品")
    @PutMapping("/status")
    public Result<?> updateProductStatus(@RequestBody java.util.Map<String, Object> params) {
        Long id = Long.parseLong(params.get("id").toString());
        Integer status = Integer.parseInt(params.get("status").toString());
        productService.adminUpdateProductStatus(id, status);
        return Result.success("操作成功");
    }
}
