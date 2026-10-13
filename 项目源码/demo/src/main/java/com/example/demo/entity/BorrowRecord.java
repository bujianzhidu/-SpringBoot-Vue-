package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("borrow_record")
public class BorrowRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long bookId;
    private Long readerId;
    private LocalDateTime borrowTime;
    private LocalDateTime dueTime;
    private LocalDateTime returnTime;
    private String status;  // BORROWED, RETURNED, OVERDUE
    private String remarks;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 