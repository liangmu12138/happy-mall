package com.happy.mall.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.happy.mall.common.BusinessException;
import com.happy.mall.entity.*;
import com.happy.mall.mapper.OrderItemMapper;
import com.happy.mall.mapper.OrderMapper;
import com.happy.mall.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 订单 Service 实现类
 */
@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private final OrderItemMapper orderItemMapper;
    private final CartService cartService;
    private final ProductService productService;
    private final AddressService addressService;

    @Override
    @Transactional
    public Map<String, Object> createOrder(Long userId, Long addressId, String remark) {
        // 获取收货地址
        Address address = addressService.getById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException("收货地址不存在");
        }

        // 获取购物车中已选中的商品
        List<Cart> cartList = cartService.list(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, userId)
                .eq(Cart::getChecked, 1));

        if (cartList.isEmpty()) {
            throw new BusinessException("请选择要结算的商品");
        }

        // 计算总金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (Cart cart : cartList) {
            Product product = productService.getById(cart.getProductId());
            if (product == null || product.getStatus() != 1) {
                throw new BusinessException("商品 " + product != null ? product.getName() : "" + " 已下架");
            }
            if (product.getStock() < cart.getQuantity()) {
                throw new BusinessException("商品 " + product.getName() + " 库存不足");
            }

            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity()));
            totalAmount = totalAmount.add(subtotal);

            OrderItem item = new OrderItem();
            item.setProductId(product.getId());
            item.setProductName(product.getName());
            item.setProductImage(product.getMainImage());
            item.setPrice(product.getPrice());
            item.setQuantity(cart.getQuantity());
            item.setSubtotal(subtotal);
            orderItems.add(item);
        }

        // 创建订单
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setAddressId(addressId);
        order.setReceiverName(address.getName());
        order.setReceiverPhone(address.getPhone());
        order.setReceiverAddress(address.getProvince() + address.getCity() + address.getDistrict() + address.getDetail());
        order.setTotalAmount(totalAmount);
        order.setFreight(BigDecimal.ZERO); // 运费暂时为0
        order.setPayAmount(totalAmount);
        order.setStatus(0); // 待付款
        order.setRemark(remark);
        this.save(order);

        // 保存订单详情
        for (OrderItem item : orderItems) {
            item.setOrderId(order.getId());
            orderItemMapper.insert(item);
        }

        // 扣减库存
        for (Cart cart : cartList) {
            Product product = productService.getById(cart.getProductId());
            product.setStock(product.getStock() - cart.getQuantity());
            product.setSales(product.getSales() + cart.getQuantity());
            productService.updateById(product);
        }

        // 清空购物车中已选中的商品
        cartService.remove(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, userId)
                .eq(Cart::getChecked, 1));

        Map<String, Object> result = new HashMap<>();
        result.put("orderId", order.getId());
        result.put("orderNo", order.getOrderNo());
        result.put("payAmount", order.getPayAmount());
        return result;
    }

    @Override
    public Page<Map<String, Object>> getOrderList(Long userId, Integer status, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, userId);

        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }

        wrapper.orderByDesc(Order::getCreateTime);
        Page<Order> orderPage = this.page(new Page<>(pageNum, pageSize), wrapper);

        // 转换为 Map
        Page<Map<String, Object>> result = new Page<>(orderPage.getCurrent(), orderPage.getSize(), orderPage.getTotal());
        List<Map<String, Object>> records = orderPage.getRecords().stream().map(order -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", order.getId());
            map.put("orderNo", order.getOrderNo());
            map.put("totalAmount", order.getTotalAmount());
            map.put("payAmount", order.getPayAmount());
            map.put("status", order.getStatus());
            map.put("statusText", getStatusText(order.getStatus()));
            map.put("createTime", order.getCreateTime());

            // 获取订单商品
            List<OrderItem> items = orderItemMapper.selectList(
                    new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, order.getId()));
            map.put("items", items);

            return map;
        }).collect(Collectors.toList());
        result.setRecords(records);

        return result;
    }

    @Override
    public Map<String, Object> getOrderDetail(Long userId, Long orderId) {
        Order order = this.getById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("订单不存在");
        }

        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId));

        Map<String, Object> result = new HashMap<>();
        result.put("order", order);
        result.put("items", items);
        return result;
    }

    @Override
    @Transactional
    public void cancelOrder(Long userId, Long orderId) {
        Order order = this.getById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("订单不存在");
        }

        if (order.getStatus() != 0 && order.getStatus() != 1) {
            throw new BusinessException("当前订单状态不可取消");
        }

        // 恢复库存
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId));
        for (OrderItem item : items) {
            Product product = productService.getById(item.getProductId());
            if (product != null) {
                product.setStock(product.getStock() + item.getQuantity());
                product.setSales(product.getSales() - item.getQuantity());
                productService.updateById(product);
            }
        }

        order.setStatus(4); // 已取消
        this.updateById(order);
    }

    @Override
    public void confirmReceive(Long userId, Long orderId) {
        Order order = this.getById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("订单不存在");
        }

        if (order.getStatus() != 2) {
            throw new BusinessException("当前订单状态不可确认收货");
        }

        order.setStatus(3); // 已完成
        order.setReceiveTime(LocalDateTime.now());
        this.updateById(order);
    }

    @Override
    public void payOrder(Long userId, Long orderId) {
        Order order = this.getById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("订单不存在");
        }

        if (order.getStatus() != 0) {
            throw new BusinessException("当前订单状态不可支付");
        }

        order.setStatus(1); // 待发货
        order.setPayTime(LocalDateTime.now());
        this.updateById(order);
    }

    @Override
    public Page<Map<String, Object>> adminGetOrderPage(Integer pageNum, Integer pageSize, Integer status, String orderNo) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();

        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }

        if (orderNo != null && !orderNo.isEmpty()) {
            wrapper.like(Order::getOrderNo, orderNo);
        }

        wrapper.orderByDesc(Order::getCreateTime);
        Page<Order> orderPage = this.page(new Page<>(pageNum, pageSize), wrapper);

        Page<Map<String, Object>> result = new Page<>(orderPage.getCurrent(), orderPage.getSize(), orderPage.getTotal());
        List<Map<String, Object>> records = orderPage.getRecords().stream().map(order -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", order.getId());
            map.put("orderNo", order.getOrderNo());
            map.put("userId", order.getUserId());
            map.put("receiverName", order.getReceiverName());
            map.put("receiverPhone", order.getReceiverPhone());
            map.put("totalAmount", order.getTotalAmount());
            map.put("payAmount", order.getPayAmount());
            map.put("status", order.getStatus());
            map.put("statusText", getStatusText(order.getStatus()));
            map.put("createTime", order.getCreateTime());
            return map;
        }).collect(Collectors.toList());
        result.setRecords(records);

        return result;
    }

    @Override
    public void adminShipOrder(Long orderId) {
        Order order = this.getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        if (order.getStatus() != 1) {
            throw new BusinessException("当前订单状态不可发货");
        }

        order.setStatus(2); // 待收货
        order.setShipTime(LocalDateTime.now());
        this.updateById(order);
    }

    @Override
    public Map<String, Object> getOrderStatistics() {
        Map<String, Object> statistics = new HashMap<>();

        // 总订单数
        statistics.put("totalOrders", this.count());

        // 待付款
        statistics.put("pendingPayment", this.count(new LambdaQueryWrapper<Order>().eq(Order::getStatus, 0)));

        // 待发货
        statistics.put("pendingShipment", this.count(new LambdaQueryWrapper<Order>().eq(Order::getStatus, 1)));

        // 待收货
        statistics.put("pendingReceive", this.count(new LambdaQueryWrapper<Order>().eq(Order::getStatus, 2)));

        // 已完成
        statistics.put("completed", this.count(new LambdaQueryWrapper<Order>().eq(Order::getStatus, 3)));

        // 今日订单数
        LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        statistics.put("todayOrders", this.count(new LambdaQueryWrapper<Order>().ge(Order::getCreateTime, todayStart)));

        // 今日销售额
        List<Order> todayOrders = this.list(new LambdaQueryWrapper<Order>()
                .ge(Order::getCreateTime, todayStart)
                .ne(Order::getStatus, 4));
        BigDecimal todaySales = todayOrders.stream()
                .map(Order::getPayAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        statistics.put("todaySales", todaySales);

        return statistics;
    }

    private String generateOrderNo() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String timestamp = LocalDateTime.now().format(formatter);
        String random = String.valueOf((int) (Math.random() * 10000));
        return timestamp + random;
    }

    private String getStatusText(Integer status) {
        switch (status) {
            case 0: return "待付款";
            case 1: return "待发货";
            case 2: return "待收货";
            case 3: return "已完成";
            case 4: return "已取消";
            default: return "未知状态";
        }
    }
}
