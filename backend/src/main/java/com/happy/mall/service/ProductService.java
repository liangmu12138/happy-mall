package com.happy.mall.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.happy.mall.entity.Product;

import java.util.List;

/**
 * 商品 Service
 */
public interface ProductService extends IService<Product> {

    /**
     * 分页查询商品列表
     */
    Page<Product> getProductPage(Integer pageNum, Integer pageSize, Long categoryId, String keyword);

    /**
     * 获取商品详情（包含图片）
     */
    Product getProductDetail(Long productId);

    /**
     * 获取分类下的商品
     */
    List<Product> getProductsByCategoryId(Long categoryId, Integer limit);

    /**
     * 搜索商品
     */
    List<Product> searchProducts(String keyword);

    /**
     * 管理端：分页查询商品
     */
    Page<Product> adminGetProductPage(Integer pageNum, Integer pageSize, String keyword, Integer status);

    /**
     * 管理端：新增商品
     */
    void adminAddProduct(Product product);

    /**
     * 管理端：更新商品
     */
    void adminUpdateProduct(Product product);

    /**
     * 管理端：删除商品
     */
    void adminDeleteProduct(Long productId);

    /**
     * 管理端：上架/下架商品
     */
    void adminUpdateProductStatus(Long productId, Integer status);
}
