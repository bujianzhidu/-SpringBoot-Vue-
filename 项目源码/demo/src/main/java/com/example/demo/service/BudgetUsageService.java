package com.example.demo.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface BudgetUsageService {
    // 获取单个预算的使用情况
    Map<String, Object> getBudgetUsage(Long budgetId);
    
    // 获取所有预算的使用情况
    List<Map<String, Object>> getAllBudgetUsage();
    
    // 获取指定时间范围内的预算使用情况
    List<Map<String, Object>> getBudgetUsageInRange(LocalDateTime startDate, LocalDateTime endDate);
    
    // 计算总预算和已使用金额
    Map<String, BigDecimal> calculateTotalBudgetAndUsage(LocalDateTime startDate, LocalDateTime endDate);
} 