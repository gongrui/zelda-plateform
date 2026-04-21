package com.blue.demo.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blue.demo.entity.Order;
import com.blue.demo.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 订单控制器
 * 演示 Sa-Token 的三个核心注解：
 * - @SaCheckLogin: 登录校验
 * - @SaCheckRole: 角色校验
 * - @SaCheckPermission: 权限校验
 */
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 查询订单列表（需要登录）
     */
    @GetMapping
    @SaCheckLogin
    public Page<Order> list(@RequestParam(defaultValue = "1") int pageNum,
                            @RequestParam(defaultValue = "10") int pageSize) {
        return orderService.pageOrders(pageNum, pageSize);
    }

    /**
     * 查询订单详情（需要登录）
     */
    @GetMapping("/{id}")
    @SaCheckLogin
    public Order detail(@PathVariable Long id) {
        return orderService.getById(id);
    }

    /**
     * 创建订单（需要 admin 角色）
     */
    @PostMapping
    @SaCheckLogin
    @SaCheckRole("admin")
    public Order create(@RequestBody Order order) {
        orderService.save(order);
        return order;
    }

    /**
     * 更新订单（需要 user:update 权限）
     */
    @PutMapping("/{id}")
    @SaCheckLogin
    @SaCheckPermission("user:update")
    public Order update(@PathVariable Long id, @RequestBody Order order) {
        order.setId(id);
        orderService.updateById(order);
        return order;
    }

    /**
     * 删除订单（需要 user:add 权限）
     */
    @DeleteMapping("/{id}")
    @SaCheckLogin
    @SaCheckPermission("user:add")
    public void delete(@PathVariable Long id) {
        orderService.removeById(id);
    }
}
