package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("book")
public class Book {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private String category;
    private Integer totalCopies;
    private Integer availableCopies;
    private String location;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 