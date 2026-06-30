package com.happy.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.happy.mall.entity.Address;

import java.util.List;

/**
 * 收货地址 Service
 */
public interface AddressService extends IService<Address> {

    /**
     * 获取用户地址列表
     */
    List<Address> getAddressList(Long userId);

    /**
     * 获取地址详情
     */
    Address getAddressDetail(Long userId, Long addressId);

    /**
     * 新增地址
     */
    void addAddress(Address address);

    /**
     * 更新地址
     */
    void updateAddress(Address address);

    /**
     * 删除地址
     */
    void deleteAddress(Long userId, Long addressId);

    /**
     * 设置默认地址
     */
    void setDefaultAddress(Long userId, Long addressId);
}
