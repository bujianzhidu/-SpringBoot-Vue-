package com.example.tourism.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.tourism.entity.Order;
import com.example.tourism.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Order Management", description = "Order related operations")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Create a new order")
    public ResponseEntity<Void> createOrder(@RequestBody Order order) {
        orderService.createOrder(order);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Update order status")
    public ResponseEntity<Void> updateOrderStatus(@PathVariable Long id, @RequestParam String status) {
        orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get user's orders")
    public ResponseEntity<Page<Order>> getUserOrders(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return ResponseEntity.ok(orderService.getUserOrders(userId, pageNum, pageSize));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Get order details")
    public ResponseEntity<Order> getOrderDetail(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderDetail(id));
    }
} 