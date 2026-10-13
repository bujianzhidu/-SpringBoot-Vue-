package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("financial_goal")
public class FinancialGoal {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String name; // 目标名称
    private BigDecimal targetAmount; // 目标金额
    private BigDecimal currentAmount; // 当前金额
    private LocalDateTime startDate; // 开始日期
    private LocalDateTime targetDate; // 目标日期
    private String status; // 状态：IN_PROGRESS-进行中，COMPLETED-已完成，FAILED-失败
    private String description; // 目标描述
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
} 