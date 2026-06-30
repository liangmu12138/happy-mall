package com.happy.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.happy.mall.entity.Order;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单 Mapper
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {
}
