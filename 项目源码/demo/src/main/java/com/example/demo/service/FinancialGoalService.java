package com.example.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.entity.FinancialGoal;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface FinancialGoalService extends IService<FinancialGoal> {
    // 更新目标进度
    boolean updateProgress(Long goalId, BigDecimal amount);
    
    // 获取目标完成情况
    Map<String, Object> getGoalProgress(Long goalId);
    
    // 获取所有目标完成情况
    List<Map<String, Object>> getAllGoalsProgress();
    
    // 检查目标是否完成
    boolean checkGoalCompletion(Long goalId);
    
    // 获取目标剩余时间
    Map<String, Object> getGoalRemainingTime(Long goalId);
} 