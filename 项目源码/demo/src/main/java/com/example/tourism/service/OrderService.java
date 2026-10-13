package com.example.tourism.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.tourism.entity.Order;

public interface OrderService extends IService<Order> {
    void createOrder(Order order);
    void updateOrderStatus(Long id, String status);
    Page<Order> getUserOrders(Long userId, Integer pageNum, Integer pageSize);
    Order getOrderDetail(Long id);
} 