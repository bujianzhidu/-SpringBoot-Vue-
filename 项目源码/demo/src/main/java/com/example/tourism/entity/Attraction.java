package com.example.tourism.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_attraction")
public class Attraction {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String description;
    private String location;
    private String city;
    private String province;
    private BigDecimal ticketPrice;
    private String openingHours;
    private String images;
    private Integer rating;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 