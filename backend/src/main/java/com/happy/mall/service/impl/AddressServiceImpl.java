package com.happy.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.happy.mall.common.BusinessException;
import com.happy.mall.entity.Address;
import com.happy.mall.mapper.AddressMapper;
import com.happy.mall.service.AddressService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 收货地址 Service 实现类
 */
@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements AddressService {

    @Override
    public List<Address> getAddressList(Long userId) {
        return this.list(new LambdaQueryWrapper<Address>()
                .eq(Address::getUserId, userId)
                .orderByDesc(Address::getIsDefault)
                .orderByDesc(Address::getCreateTime));
    }

    @Override
    public Address getAddressDetail(Long userId, Long addressId) {
        Address address = this.getById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException("地址不存在");
        }
        return address;
    }

    @Override
    public void addAddress(Address address) {
        // 如果是第一个地址，自动设为默认
        Long count = this.count(new LambdaQueryWrapper<Address>()
                .eq(Address::getUserId, address.getUserId()));
        if (count == 0) {
            address.setIsDefault(1);
        }

        // 如果设为默认，取消其他默认
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            clearDefaultAddress(address.getUserId());
        }

        this.save(address);
    }

    @Override
    public void updateAddress(Address address) {
        if (address.getId() == null) {
            throw new BusinessException("地址ID不能为空");
        }

        // 如果设为默认，取消其他默认
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            clearDefaultAddress(address.getUserId());
        }

        this.updateById(address);
    }

    @Override
    public void deleteAddress(Long userId, Long addressId) {
        Address address = this.getById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException("地址不存在");
        }

        boolean wasDefault = address.getIsDefault() != null && address.getIsDefault() == 1;
        this.removeById(addressId);

        // 如果删除的是默认地址，将第一个地址设为默认
        if (wasDefault) {
            Address firstAddress = this.getOne(new LambdaQueryWrapper<Address>()
                    .eq(Address::getUserId, userId)
                    .orderByAsc(Address::getCreateTime)
                    .last("LIMIT 1"));
            if (firstAddress != null) {
                firstAddress.setIsDefault(1);
                this.updateById(firstAddress);
            }
        }
    }

    @Override
    public void setDefaultAddress(Long userId, Long addressId) {
        Address address = this.getById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException("地址不存在");
        }

        clearDefaultAddress(userId);
        address.setIsDefault(1);
        this.updateById(address);
    }

    private void clearDefaultAddress(Long userId) {
        Address update = new Address();
        update.setIsDefault(0);
        this.update(update, new LambdaQueryWrapper<Address>()
                .eq(Address::getUserId, userId)
                .eq(Address::getIsDefault, 1));
    }
}
