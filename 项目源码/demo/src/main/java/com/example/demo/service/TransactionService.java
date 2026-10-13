package com.example.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.entity.Transaction;
import java.util.List;
import java.util.Map;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface TransactionService extends IService<Transaction> {
    // 获取收支统计（无时间范围）
    Map<String, BigDecimal> getStatistics();
    
    // 获取收支统计（有时间范围）
    Map<String, BigDecimal> getStatistics(LocalDateTime startDate, LocalDateTime endDate);
    
    // 按类别统计（无时间范围）
    List<Map<String, Object>> getCategoryStatistics(String type);
    
    // 按类别统计（有时间范围）
    List<Map<String, Object>> getCategoryStatistics(String type, LocalDateTime startDate, LocalDateTime endDate);
    
    // 导出PDF报表
    byte[] exportToPdf();
    
    // 导出Excel报表
    byte[] exportToExcel();
} 