package com.example.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.FinancialGoal;
import com.example.demo.mapper.FinancialGoalMapper;
import com.example.demo.service.FinancialGoalService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FinancialGoalServiceImpl extends ServiceImpl<FinancialGoalMapper, FinancialGoal> implements FinancialGoalService {

    @Override
    public boolean updateProgress(Long goalId, BigDecimal amount) {
        FinancialGoal goal = getById(goalId);
        if (goal == null) {
            throw new RuntimeException("目标不存在");
        }

        goal.setCurrentAmount(goal.getCurrentAmount().add(amount));
        
        // 检查目标是否完成
        if (goal.getCurrentAmount().compareTo(goal.getTargetAmount()) >= 0) {
            goal.setStatus("COMPLETED");
        }
        
        return updateById(goal);
    }

    @Override
    public Map<String, Object> getGoalProgress(Long goalId) {
        FinancialGoal goal = getById(goalId);
        if (goal == null) {
            throw new RuntimeException("目标不存在");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("goal", goal);
        result.put("progressPercentage", goal.getCurrentAmount()
                .divide(goal.getTargetAmount(), 4, BigDecimal.ROUND_HALF_UP)
                .multiply(new BigDecimal("100")));
        result.put("remainingAmount", goal.getTargetAmount().subtract(goal.getCurrentAmount()));
        
        return result;
    }

    @Override
    public List<Map<String, Object>> getAllGoalsProgress() {
        List<Map<String, Object>> result = new ArrayList<>();
        list().forEach(goal -> result.add(getGoalProgress(goal.getId())));
        return result;
    }

    @Override
    public boolean checkGoalCompletion(Long goalId) {
        FinancialGoal goal = getById(goalId);
        if (goal == null) {
            throw new RuntimeException("目标不存在");
        }
        return goal.getCurrentAmount().compareTo(goal.getTargetAmount()) >= 0;
    }

    @Override
    public Map<String, Object> getGoalRemainingTime(Long goalId) {
        FinancialGoal goal = getById(goalId);
        if (goal == null) {
            throw new RuntimeException("目标不存在");
        }

        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(now, goal.getTargetDate());
        
        Map<String, Object> result = new HashMap<>();
        result.put("daysRemaining", duration.toDays());
        result.put("hoursRemaining", duration.toHours() % 24);
        result.put("isOverdue", now.isAfter(goal.getTargetDate()));
        
        return result;
    }
} 