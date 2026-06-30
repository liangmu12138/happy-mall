package com.happy.mall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * 收货地址实体
 */
@Data
@TableName("address")
public class Address extends BaseEntity {

    /**
     * 地址ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 收货人姓名
     */
    private String name;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 省
     */
    private String province;

    /**
     * 市
     */
    private String city;

    /**
     * 区
     */
    private String district;

    /**
     * 详细地址
     */
    private String detail;

    /**
     * 是否默认地址：0-否，1-是
     */
    private Integer isDefault;
}
