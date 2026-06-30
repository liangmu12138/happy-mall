package com.happy.mall.controller;

import com.happy.mall.common.Result;
import com.happy.mall.entity.User;
import com.happy.mall.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 购物车控制器
 */
@Tag(name = "购物车接口")
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @Operation(summary = "添加商品到购物车")
    @PostMapping("/add")
    public Result<?> addToCart(Authentication authentication, @RequestBody Map<String, Object> params) {
        User user = (User) authentication.getPrincipal();
        Long productId = Long.parseLong(params.get("productId").toString());
        Integer quantity = params.get("quantity") != null ? Integer.parseInt(params.get("quantity").toString()) : 1;
        cartService.addToCart(user.getId(), productId, quantity);
        return Result.success("添加成功");
    }

    @Operation(summary = "获取购物车列表")
    @GetMapping("/list")
    public Result<?> getCartList(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<Map<String, Object>> cartList = cartService.getCartList(user.getId());
        return Result.success(cartList);
    }

    @Operation(summary = "更新购物车商品数量")
    @PutMapping("/quantity")
    public Result<?> updateQuantity(Authentication authentication, @RequestBody Map<String, Object> params) {
        User user = (User) authentication.getPrincipal();
        Long cartId = Long.parseLong(params.get("cartId").toString());
        Integer quantity = Integer.parseInt(params.get("quantity").toString());
        cartService.updateQuantity(user.getId(), cartId, quantity);
        return Result.success("更新成功");
    }

    @Operation(summary = "更新选中状态")
    @PutMapping("/checked")
    public Result<?> updateChecked(Authentication authentication, @RequestBody Map<String, Object> params) {
        User user = (User) authentication.getPrincipal();
        Long cartId = Long.parseLong(params.get("cartId").toString());
        Integer checked = Integer.parseInt(params.get("checked").toString());
        cartService.updateChecked(user.getId(), cartId, checked);
        return Result.success("更新成功");
    }

    @Operation(summary = "全选/取消全选")
    @PutMapping("/all-checked")
    public Result<?> updateAllChecked(Authentication authentication, @RequestBody Map<String, Object> params) {
        User user = (User) authentication.getPrincipal();
        Integer checked = Integer.parseInt(params.get("checked").toString());
        cartService.updateAllChecked(user.getId(), checked);
        return Result.success("更新成功");
    }

    @Operation(summary = "删除购物车商品")
    @DeleteMapping("/{cartId}")
    public Result<?> deleteCartItem(Authentication authentication, @PathVariable Long cartId) {
        User user = (User) authentication.getPrincipal();
        cartService.deleteCartItem(user.getId(), cartId);
        return Result.success("删除成功");
    }

    @Operation(summary = "清空购物车")
    @DeleteMapping("/clear")
    public Result<?> clearCart(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        cartService.clearCart(user.getId());
        return Result.success("清空成功");
    }

    @Operation(summary = "获取购物车数量")
    @GetMapping("/count")
    public Result<?> getCartCount(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Integer count = cartService.getCartCount(user.getId());
        return Result.success(count);
    }
}
