package com.example.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.Budget;
import com.example.demo.mapper.BudgetMapper;
import com.example.demo.service.BudgetService;
import com.example.demo.service.BudgetUsageService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BudgetServiceImpl extends ServiceImpl<BudgetMapper, Budget> implements BudgetService {

    private final BudgetUsageService budgetUsageService;

    public BudgetServiceImpl(BudgetUsageService budgetUsageService) {
        this.budgetUsageService = budgetUsageService;
    }

    @Override
    public Map<String, Object> getBudgetUsage(Long budgetId) {
        return budgetUsageService.getBudgetUsage(budgetId);
    }

    @Override
    public List<Map<String, Object>> getAllBudgetUsage() {
        return budgetUsageService.getAllBudgetUsage();
    }

    @Override
    public boolean isOverBudget(Long budgetId) {
        Map<String, Object> usage = getBudgetUsage(budgetId);
        BigDecimal remaining = (BigDecimal) usage.get("remaining");
        return remaining.compareTo(BigDecimal.ZERO) < 0;
    }

    @Override
    public Map<String, Object> getBudgetRemaining(Long budgetId) {
        Map<String, Object> usage = getBudgetUsage(budgetId);
        Map<String, Object> result = new HashMap<>();
        result.put("remaining", usage.get("remaining"));
        result.put("isOverBudget", isOverBudget(budgetId));
        return result;
    }
} 