package com.happy.mall.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.happy.mall.common.Result;
import com.happy.mall.entity.User;
import com.happy.mall.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 订单控制器
 */
@Tag(name = "订单接口")
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "创建订单")
    @PostMapping("/create")
    public Result<?> createOrder(Authentication authentication, @RequestBody Map<String, Object> params) {
        User user = (User) authentication.getPrincipal();
        Long addressId = Long.parseLong(params.get("addressId").toString());
        String remark = params.get("remark") != null ? params.get("remark").toString() : null;
        Map<String, Object> result = orderService.createOrder(user.getId(), addressId, remark);
        return Result.success("订单创建成功", result);
    }

    @Operation(summary = "获取订单列表")
    @GetMapping("/list")
    public Result<?> getOrderList(
            Authentication authentication,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        User user = (User) authentication.getPrincipal();
        Page<Map<String, Object>> result = orderService.getOrderList(user.getId(), status, pageNum, pageSize);
        return Result.success(result);
    }

    @Operation(summary = "获取订单详情")
    @GetMapping("/{orderId}")
    public Result<?> getOrderDetail(Authentication authentication, @PathVariable Long orderId) {
        User user = (User) authentication.getPrincipal();
        Map<String, Object> result = orderService.getOrderDetail(user.getId(), orderId);
        return Result.success(result);
    }

    @Operation(summary = "取消订单")
    @PostMapping("/cancel/{orderId}")
    public Result<?> cancelOrder(Authentication authentication, @PathVariable Long orderId) {
        User user = (User) authentication.getPrincipal();
        orderService.cancelOrder(user.getId(), orderId);
        return Result.success("订单已取消");
    }

    @Operation(summary = "确认收货")
    @PostMapping("/confirm/{orderId}")
    public Result<?> confirmReceive(Authentication authentication, @PathVariable Long orderId) {
        User user = (User) authentication.getPrincipal();
        orderService.confirmReceive(user.getId(), orderId);
        return Result.success("已确认收货");
    }

    @Operation(summary = "模拟支付")
    @PostMapping("/pay/{orderId}")
    public Result<?> payOrder(Authentication authentication, @PathVariable Long orderId) {
        User user = (User) authentication.getPrincipal();
        orderService.payOrder(user.getId(), orderId);
        return Result.success("支付成功");
    }
}
