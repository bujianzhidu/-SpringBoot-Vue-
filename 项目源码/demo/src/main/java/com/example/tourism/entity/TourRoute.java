package com.example.tourism.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_tour_route")
public class TourRoute {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String description;
    private Integer days;
    private String destinations;
    private BigDecimal price;
    private String transportation;
    private String accommodation;
    private String itinerary;
    private String images;
    private Integer maxParticipants;
    private String status; // ACTIVE, INACTIVE
    private LocalDateTime departureTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 