package com.happy.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.happy.mall.common.BusinessException;
import com.happy.mall.entity.Product;
import com.happy.mall.mapper.ProductMapper;
import com.happy.mall.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 商品 Service 实现类
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Override
    public Page<Product> getProductPage(Integer pageNum, Integer pageSize, Long categoryId, String keyword) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, 1); // 只查询上架商品

        if (categoryId != null) {
            wrapper.eq(Product::getCategoryId, categoryId);
        }

        if (StringUtils.hasText(keyword)) {
            wrapper.like(Product::getName, keyword);
        }

        wrapper.orderByDesc(Product::getSales);
        return this.page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public Product getProductDetail(Long productId) {
        Product product = this.getById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        return product;
    }

    @Override
    public List<Product> getProductsByCategoryId(Long categoryId, Integer limit) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, 1)
                .eq(Product::getCategoryId, categoryId)
                .orderByDesc(Product::getSales)
                .last("LIMIT " + limit);
        return this.list(wrapper);
    }

    @Override
    public List<Product> searchProducts(String keyword) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, 1)
                .like(Product::getName, keyword)
                .orderByDesc(Product::getSales);
        return this.list(wrapper);
    }

    @Override
    public Page<Product> adminGetProductPage(Integer pageNum, Integer pageSize, String keyword, Integer status) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(keyword)) {
            wrapper.like(Product::getName, keyword);
        }

        if (status != null) {
            wrapper.eq(Product::getStatus, status);
        }

        wrapper.orderByDesc(Product::getCreateTime);
        return this.page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public void adminAddProduct(Product product) {
        if (product.getStock() == null) product.setStock(0);
        if (product.getSales() == null) product.setSales(0);
        if (product.getStatus() == null) product.setStatus(1);
        this.save(product);
    }

    @Override
    public void adminUpdateProduct(Product product) {
        if (product.getId() == null) {
            throw new BusinessException("商品ID不能为空");
        }
        this.updateById(product);
    }

    @Override
    public void adminDeleteProduct(Long productId) {
        this.removeById(productId);
    }

    @Override
    public void adminUpdateProductStatus(Long productId, Integer status) {
        Product product = this.getById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        product.setStatus(status);
        this.updateById(product);
    }
}
