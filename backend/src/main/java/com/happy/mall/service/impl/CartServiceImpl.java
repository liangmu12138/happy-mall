package com.happy.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.happy.mall.common.BusinessException;
import com.happy.mall.entity.Cart;
import com.happy.mall.entity.Product;
import com.happy.mall.mapper.CartMapper;
import com.happy.mall.service.CartService;
import com.happy.mall.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 购物车 Service 实现类
 */
@Service
@RequiredArgsConstructor
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements CartService {

    private final ProductService productService;

    @Override
    public void addToCart(Long userId, Long productId, Integer quantity) {
        // 检查商品是否存在
        Product product = productService.getById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        if (product.getStatus() != 1) {
            throw new BusinessException("商品已下架");
        }
        if (product.getStock() < quantity) {
            throw new BusinessException("库存不足");
        }

        // 检查购物车中是否已有该商品
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId)
                .eq(Cart::getProductId, productId);
        Cart cart = this.getOne(wrapper);

        if (cart != null) {
            // 更新数量
            cart.setQuantity(cart.getQuantity() + quantity);
            this.updateById(cart);
        } else {
            // 新增购物车
            cart = new Cart();
            cart.setUserId(userId);
            cart.setProductId(productId);
            cart.setQuantity(quantity);
            cart.setChecked(1);
            this.save(cart);
        }
    }

    @Override
    public List<Map<String, Object>> getCartList(Long userId) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId)
                .orderByDesc(Cart::getCreateTime);
        List<Cart> cartList = this.list(wrapper);

        List<Map<String, Object>> result = new ArrayList<>();
        for (Cart cart : cartList) {
            Product product = productService.getById(cart.getProductId());
            if (product != null && product.getStatus() == 1) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", cart.getId());
                item.put("productId", cart.getProductId());
                item.put("quantity", cart.getQuantity());
                item.put("checked", cart.getChecked());
                item.put("productName", product.getName());
                item.put("productImage", product.getMainImage());
                item.put("price", product.getPrice());
                item.put("stock", product.getStock());
                item.put("subtotal", product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())));
                result.add(item);
            }
        }
        return result;
    }

    @Override
    public void updateQuantity(Long userId, Long cartId, Integer quantity) {
        Cart cart = this.getById(cartId);
        if (cart == null || !cart.getUserId().equals(userId)) {
            throw new BusinessException("购物车记录不存在");
        }

        Product product = productService.getById(cart.getProductId());
        if (product == null || product.getStock() < quantity) {
            throw new BusinessException("库存不足");
        }

        cart.setQuantity(quantity);
        this.updateById(cart);
    }

    @Override
    public void updateChecked(Long userId, Long cartId, Integer checked) {
        Cart cart = this.getById(cartId);
        if (cart == null || !cart.getUserId().equals(userId)) {
            throw new BusinessException("购物车记录不存在");
        }

        cart.setChecked(checked);
        this.updateById(cart);
    }

    @Override
    public void updateAllChecked(Long userId, Integer checked) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId);

        Cart cart = new Cart();
        cart.setChecked(checked);
        this.update(cart, wrapper);
    }

    @Override
    public void deleteCartItem(Long userId, Long cartId) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getId, cartId)
                .eq(Cart::getUserId, userId);
        this.remove(wrapper);
    }

    @Override
    public void clearCart(Long userId) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId);
        this.remove(wrapper);
    }

    @Override
    public Integer getCartCount(Long userId) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId);
        return (int) this.count(wrapper);
    }
}
