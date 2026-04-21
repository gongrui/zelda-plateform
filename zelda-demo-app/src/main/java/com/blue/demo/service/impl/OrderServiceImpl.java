package com.blue.demo.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blue.demo.entity.Order;
import com.blue.demo.mapper.OrderMapper;
import com.blue.demo.service.OrderService;
import com.mybatis.flex.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 订单服务实现类
 */
@Service
public class OrderServiceImpl extends BaseServiceImpl<OrderMapper, Order> implements OrderService {

    @Override
    public Page<Order> pageOrders(int pageNum, int pageSize) {
        Page<Order> page = new Page<>(pageNum, pageSize);
        return baseMapper.selectPage(page, null);
    }

    @Override
    public Order getByOrderNo(String orderNo) {
        return baseMapper.selectOneByColumn(Order::getOrderNo, orderNo);
    }
}
