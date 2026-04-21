package com.blue.demo.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blue.demo.entity.Order;
import com.mybatis.flex.service.IBaseService;

/**
 * 订单服务接口
 */
public interface OrderService extends IBaseService<Order> {

    /**
     * 分页查询订单列表
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    Page<Order> pageOrders(int pageNum, int pageSize);

    /**
     * 根据订单编号查询订单
     *
     * @param orderNo 订单编号
     * @return 订单信息
     */
    Order getByOrderNo(String orderNo);
}
