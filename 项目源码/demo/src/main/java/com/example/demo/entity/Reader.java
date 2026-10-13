package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("reader")
public class Reader {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String readerNo;
    private String name;
    private String type;  // 读者类型：学生、教师等
    private String phone;
    private String email;
    private Integer maxBorrowDays;  // 最大借阅天数
    private Integer maxBorrowBooks;  // 最大借阅数量
    private Boolean status;  // 账号状态：true-正常，false-禁用
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 