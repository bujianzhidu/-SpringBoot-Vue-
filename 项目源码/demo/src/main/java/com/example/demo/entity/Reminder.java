package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("reminder")
public class Reminder {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String title;
    
    private String content;
    
    private LocalDateTime remindTime;
    
    private String type;  // BUDGET-预算提醒，GOAL-目标提醒，CUSTOM-自定义提醒
    
    private String status;  // PENDING-待处理，COMPLETED-已完成，CANCELLED-已取消
    
    private Integer repeatType;  // 0-不重复，1-每天，2-每周，3-每月
    
    private LocalDateTime nextRemindTime;  // 下次提醒时间
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
} 