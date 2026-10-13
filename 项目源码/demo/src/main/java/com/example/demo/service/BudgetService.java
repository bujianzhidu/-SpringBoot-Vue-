package com.example.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.entity.Budget;
import java.util.List;
import java.util.Map;

public interface BudgetService extends IService<Budget> {
    // 获取预算使用情况
    Map<String, Object> getBudgetUsage(Long budgetId);
    
    // 获取所有预算使用情况
    List<Map<String, Object>> getAllBudgetUsage();
    
    // 检查预算是否超支
    boolean isOverBudget(Long budgetId);
    
    // 获取预算剩余金额
    Map<String, Object> getBudgetRemaining(Long budgetId);
} 