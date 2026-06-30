package com.happy.mall.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.happy.mall.entity.Order;

import java.util.Map;

/**
 * 订单 Service
 */
public interface OrderService extends IService<Order> {

    /**
     * 创建订单
     */
    Map<String, Object> createOrder(Long userId, Long addressId, String remark);

    /**
     * 获取订单列表
     */
    Page<Map<String, Object>> getOrderList(Long userId, Integer status, Integer pageNum, Integer pageSize);

    /**
     * 获取订单详情
     */
    Map<String, Object> getOrderDetail(Long userId, Long orderId);

    /**
     * 取消订单
     */
    void cancelOrder(Long userId, Long orderId);

    /**
     * 确认收货
     */
    void confirmReceive(Long userId, Long orderId);

    /**
     * 模拟支付（直接成功）
     */
    void payOrder(Long userId, Long orderId);

    /**
     * 管理端：分页查询订单
     */
    Page<Map<String, Object>> adminGetOrderPage(Integer pageNum, Integer pageSize, Integer status, String orderNo);

    /**
     * 管理端：发货
     */
    void adminShipOrder(Long orderId);

    /**
     * 管理端：获取订单统计
     */
    Map<String, Object> getOrderStatistics();
}
