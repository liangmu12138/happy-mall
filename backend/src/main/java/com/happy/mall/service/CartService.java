package com.happy.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.happy.mall.entity.Cart;

import java.util.List;
import java.util.Map;

/**
 * 购物车 Service
 */
public interface CartService extends IService<Cart> {

    /**
     * 添加商品到购物车
     */
    void addToCart(Long userId, Long productId, Integer quantity);

    /**
     * 获取购物车列表
     */
    List<Map<String, Object>> getCartList(Long userId);

    /**
     * 更新购物车商品数量
     */
    void updateQuantity(Long userId, Long cartId, Integer quantity);

    /**
     * 更新选中状态
     */
    void updateChecked(Long userId, Long cartId, Integer checked);

    /**
     * 全选/取消全选
     */
    void updateAllChecked(Long userId, Integer checked);

    /**
     * 删除购物车商品
     */
    void deleteCartItem(Long userId, Long cartId);

    /**
     * 清空购物车
     */
    void clearCart(Long userId);

    /**
     * 获取购物车数量
     */
    Integer getCartCount(Long userId);
}
