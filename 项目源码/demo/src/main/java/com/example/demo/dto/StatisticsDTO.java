package com.example.demo.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class StatisticsDTO {
    // 总收入和支出
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal balance;
    
    // 按月份统计
    private List<MonthlyStatistics> monthlyStatistics;
    
    // 按分类统计
    private List<CategoryStatistics> categoryStatistics;
    
    // 预算执行情况
    private List<BudgetStatistics> budgetStatistics;
    
    @Data
    public static class MonthlyStatistics {
        private String month;
        private BigDecimal income;
        private BigDecimal expense;
        private BigDecimal balance;
    }
    
    @Data
    public static class CategoryStatistics {
        private String categoryName;
        private BigDecimal amount;
        private String type;  // INCOME-收入，EXPENSE-支出
        private BigDecimal percentage;  // 占比
    }
    
    @Data
    public static class BudgetStatistics {
        private String category;
        private BigDecimal budget;
        private BigDecimal actual;
        private BigDecimal remaining;
        private BigDecimal percentage;  // 使用百分比
    }
} 