package com.happy.mall.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.happy.mall.common.Result;
import com.happy.mall.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 管理端 - 订单控制器
 */
@Tag(name = "管理端-订单接口")
@RestController
@RequestMapping("/api/admin/order")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;

    @Operation(summary = "分页查询订单")
    @GetMapping("/list")
    public Result<?> getOrderPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String orderNo) {
        Page<Map<String, Object>> page = orderService.adminGetOrderPage(pageNum, pageSize, status, orderNo);
        return Result.success(page);
    }

    @Operation(summary = "获取订单详情")
    @GetMapping("/{orderId}")
    public Result<?> getOrderDetail(@PathVariable Long orderId) {
        Map<String, Object> result = orderService.getOrderDetail(null, orderId);
        return Result.success(result);
    }

    @Operation(summary = "发货")
    @PostMapping("/ship/{orderId}")
    public Result<?> shipOrder(@PathVariable Long orderId) {
        orderService.adminShipOrder(orderId);
        return Result.success("发货成功");
    }

    @Operation(summary = "获取订单统计")
    @GetMapping("/statistics")
    public Result<?> getOrderStatistics() {
        Map<String, Object> statistics = orderService.getOrderStatistics();
        return Result.success(statistics);
    }
}
