package com.example.tourism.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_order")
public class Order {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String orderNo;
    private Long userId;
    private Long tourRouteId;
    private Integer numberOfPeople;
    private BigDecimal totalAmount;
    private String contactName;
    private String contactPhone;
    private String specialRequirements;
    private String status; // PENDING, PAID, CANCELLED, COMPLETED
    private LocalDateTime orderTime;
    private LocalDateTime payTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 