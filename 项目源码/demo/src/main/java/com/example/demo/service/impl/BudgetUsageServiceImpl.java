package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.entity.Budget;
import com.example.demo.entity.Transaction;
import com.example.demo.mapper.BudgetMapper;
import com.example.demo.mapper.TransactionMapper;
import com.example.demo.service.BudgetUsageService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BudgetUsageServiceImpl implements BudgetUsageService {

    private final BudgetMapper budgetMapper;
    private final TransactionMapper transactionMapper;

    public BudgetUsageServiceImpl(BudgetMapper budgetMapper, TransactionMapper transactionMapper) {
        this.budgetMapper = budgetMapper;
        this.transactionMapper = transactionMapper;
    }

    @Override
    public Map<String, Object> getBudgetUsage(Long budgetId) {
        Budget budget = budgetMapper.selectById(budgetId);
        if (budget == null) {
            throw new RuntimeException("预算不存在");
        }

        // 获取预算期间的支出总额
        BigDecimal totalExpense = transactionMapper.selectList(new LambdaQueryWrapper<Transaction>()
                .eq(Transaction::getType, "EXPENSE")
                .eq(Transaction::getCategory, budget.getCategory())
                .between(Transaction::getTransactionDate, budget.getStartDate(), budget.getEndDate())
                .eq(Transaction::getDeleted, 0))
                .stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> result = new HashMap<>();
        result.put("budget", budget);
        result.put("totalExpense", totalExpense);
        result.put("remaining", budget.getAmount().subtract(totalExpense));
        result.put("usagePercentage", totalExpense.divide(budget.getAmount(), 4, BigDecimal.ROUND_HALF_UP)
                .multiply(new BigDecimal("100")));

        return result;
    }

    @Override
    public List<Map<String, Object>> getAllBudgetUsage() {
        List<Map<String, Object>> result = new ArrayList<>();
        budgetMapper.selectList(null).forEach(budget -> result.add(getBudgetUsage(budget.getId())));
        return result;
    }

    @Override
    public List<Map<String, Object>> getBudgetUsageInRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<Map<String, Object>> result = new ArrayList<>();
        budgetMapper.selectList(new LambdaQueryWrapper<Budget>()
                .between(Budget::getStartDate, startDate, endDate)
                .or()
                .between(Budget::getEndDate, startDate, endDate))
                .forEach(budget -> result.add(getBudgetUsage(budget.getId())));
        return result;
    }

    @Override
    public Map<String, BigDecimal> calculateTotalBudgetAndUsage(LocalDateTime startDate, LocalDateTime endDate) {
        // 获取当前时间范围内的所有预算
        List<Budget> budgets = budgetMapper.selectList(new LambdaQueryWrapper<Budget>()
                .between(Budget::getStartDate, startDate, endDate)
                .or()
                .between(Budget::getEndDate, startDate, endDate));

        System.out.println("Found " + budgets.size() + " budgets in range");
        for (Budget budget : budgets) {
            System.out.println("Budget: " + budget.getCategory() + 
                             ", Amount: " + budget.getAmount() + 
                             ", Start: " + budget.getStartDate() + 
                             ", End: " + budget.getEndDate());
        }

        BigDecimal totalBudget = BigDecimal.ZERO;
        BigDecimal totalUsed = BigDecimal.ZERO;

        for (Budget budget : budgets) {
            // 计算每个预算在当前时间范围内的可用金额
            LocalDateTime effectiveStart = budget.getStartDate().isBefore(startDate) ? startDate : budget.getStartDate();
            LocalDateTime effectiveEnd = budget.getEndDate().isAfter(endDate) ? endDate : budget.getEndDate();
            
            // 计算预算在当前时间范围内的比例
            long totalDays = java.time.Duration.between(budget.getStartDate(), budget.getEndDate()).toDays();
            long effectiveDays = java.time.Duration.between(effectiveStart, effectiveEnd).toDays();
            
            System.out.println("Budget " + budget.getCategory() + 
                             ": Total days = " + totalDays + 
                             ", Effective days = " + effectiveDays);
            
            // 确保天数不为0
            if (totalDays > 0 && effectiveDays > 0) {
                BigDecimal ratio = new BigDecimal(effectiveDays).divide(new BigDecimal(totalDays), 4, BigDecimal.ROUND_HALF_UP);
                
                // 计算当前时间范围内的预算金额
                BigDecimal effectiveBudget = budget.getAmount().multiply(ratio);
                totalBudget = totalBudget.add(effectiveBudget);

                System.out.println("Budget " + budget.getCategory() + 
                                 ": Ratio = " + ratio + 
                                 ", Effective budget = " + effectiveBudget);

                // 计算当前时间范围内的支出
                BigDecimal expense = transactionMapper.selectList(new LambdaQueryWrapper<Transaction>()
                        .eq(Transaction::getType, "EXPENSE")
                        .eq(Transaction::getCategory, budget.getCategory())
                        .between(Transaction::getTransactionDate, effectiveStart, effectiveEnd)
                        .eq(Transaction::getDeleted, 0))
                        .stream()
                        .map(Transaction::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                System.out.println("Budget " + budget.getCategory() + 
                                 ": Expense = " + expense);

                totalUsed = totalUsed.add(expense);
            }
        }

        // 打印调试信息
        System.out.println("Total Budget: " + totalBudget);
        System.out.println("Total Used: " + totalUsed);
        System.out.println("Remaining: " + totalBudget.subtract(totalUsed));

        Map<String, BigDecimal> result = new HashMap<>();
        result.put("totalBudget", totalBudget);
        result.put("totalUsed", totalUsed);
        return result;
    }
} 