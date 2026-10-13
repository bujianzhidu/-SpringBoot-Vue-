package com.example.tourism.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.tourism.entity.Order;
import com.example.tourism.entity.TourRoute;
import com.example.tourism.mapper.OrderMapper;
import com.example.tourism.service.OrderService;
import com.example.tourism.service.TourRouteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private final TourRouteService tourRouteService;

    public OrderServiceImpl(TourRouteService tourRouteService) {
        this.tourRouteService = tourRouteService;
    }

    @Override
    @Transactional
    public void createOrder(Order order) {
        TourRoute tourRoute = tourRouteService.getById(order.getTourRouteId());
        if (tourRoute == null || !"ACTIVE".equals(tourRoute.getStatus())) {
            throw new RuntimeException("Tour route not available");
        }

        order.setOrderNo(generateOrderNo());
        order.setStatus("PENDING");
        order.setOrderTime(LocalDateTime.now());
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        order.setTotalAmount(tourRoute.getPrice().multiply(java.math.BigDecimal.valueOf(order.getNumberOfPeople())));

        save(order);
    }

    @Override
    @Transactional
    public void updateOrderStatus(Long id, String status) {
        Order order = getById(id);
        if (order != null) {
            order.setStatus(status);
            if ("PAID".equals(status)) {
                order.setPayTime(LocalDateTime.now());
            }
            order.setUpdateTime(LocalDateTime.now());
            updateById(order);
        }
    }

    @Override
    public Page<Order> getUserOrders(Long userId, Integer pageNum, Integer pageSize) {
        return page(new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getUserId, userId)
                        .orderByDesc(Order::getCreateTime));
    }

    @Override
    public Order getOrderDetail(Long id) {
        return getById(id);
    }

    private String generateOrderNo() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }
} 