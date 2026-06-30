package com.happy.mall.controller;

import com.happy.mall.common.Result;
import com.happy.mall.entity.Address;
import com.happy.mall.entity.User;
import com.happy.mall.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 收货地址控制器
 */
@Tag(name = "收货地址接口")
@RestController
@RequestMapping("/api/address")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @Operation(summary = "获取地址列表")
    @GetMapping("/list")
    public Result<?> getAddressList(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<Address> addresses = addressService.getAddressList(user.getId());
        return Result.success(addresses);
    }

    @Operation(summary = "获取地址详情")
    @GetMapping("/{addressId}")
    public Result<?> getAddressDetail(Authentication authentication, @PathVariable Long addressId) {
        User user = (User) authentication.getPrincipal();
        Address address = addressService.getAddressDetail(user.getId(), addressId);
        return Result.success(address);
    }

    @Operation(summary = "新增地址")
    @PostMapping("/add")
    public Result<?> addAddress(Authentication authentication, @RequestBody Address address) {
        User user = (User) authentication.getPrincipal();
        address.setUserId(user.getId());
        addressService.addAddress(address);
        return Result.success("添加成功");
    }

    @Operation(summary = "更新地址")
    @PutMapping("/update")
    public Result<?> updateAddress(Authentication authentication, @RequestBody Address address) {
        User user = (User) authentication.getPrincipal();
        address.setUserId(user.getId());
        addressService.updateAddress(address);
        return Result.success("更新成功");
    }

    @Operation(summary = "删除地址")
    @DeleteMapping("/{addressId}")
    public Result<?> deleteAddress(Authentication authentication, @PathVariable Long addressId) {
        User user = (User) authentication.getPrincipal();
        addressService.deleteAddress(user.getId(), addressId);
        return Result.success("删除成功");
    }

    @Operation(summary = "设置默认地址")
    @PutMapping("/default/{addressId}")
    public Result<?> setDefaultAddress(Authentication authentication, @PathVariable Long addressId) {
        User user = (User) authentication.getPrincipal();
        addressService.setDefaultAddress(user.getId(), addressId);
        return Result.success("设置成功");
    }
}
