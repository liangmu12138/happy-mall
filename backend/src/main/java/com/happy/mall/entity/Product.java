package com.happy.mall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 商品实体
 */
@Data
@TableName("product")
public class Product extends BaseEntity {

    /**
     * 商品ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品简介
     */
    private String description;

    /**
     * 商品详情（富文本）
     */
    private String detail;

    /**
     * 商品价格
     */
    private BigDecimal price;

    /**
     * 原价
     */
    private BigDecimal originalPrice;

    /**
     * 库存
     */
    private Integer stock;

    /**
     * 销量
     */
    private Integer sales;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 商品主图
     */
    private String mainImage;

    /**
     * 状态：0-下架，1-上架
     */
    private Integer status;

    /**
     * 排序
     */
    private Integer sort;
}
