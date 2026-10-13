package com.example.demo.service;

import java.time.LocalDate;

public interface ExportService {
    // 导出交易记录
    byte[] exportTransactions(LocalDate startDate, LocalDate endDate);
    
    // 导出预算执行情况
    byte[] exportBudgets(LocalDate startDate, LocalDate endDate);
    
    // 导出财务目标完成情况
    byte[] exportFinancialGoals();
    
    // 导出统计报表
    byte[] exportStatistics(LocalDate startDate, LocalDate endDate);
} 