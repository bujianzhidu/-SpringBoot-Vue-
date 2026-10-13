package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("budget")
public class Budget {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String category; // 预算分类
    private BigDecimal amount; // 预算金额
    private String period; // 预算周期：MONTHLY, YEARLY
    private LocalDateTime startDate; // 开始日期
    private LocalDateTime endDate; // 结束日期
    private String description; // 预算描述
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
} 