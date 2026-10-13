package com.example.demo.controller;

import com.example.demo.entity.Budget;
import com.example.demo.service.BudgetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "预算管理", description = "预算相关接口")
@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @Operation(summary = "获取所有预算")
    @GetMapping
    public List<Budget> list() {
        return budgetService.list();
    }

    @Operation(summary = "创建预算")
    @PostMapping
    public boolean create(@RequestBody Budget budget) {
        return budgetService.save(budget);
    }

    @Operation(summary = "更新预算")
    @PutMapping("/{id}")
    public boolean update(@PathVariable Long id, @RequestBody Budget budget) {
        budget.setId(id);
        return budgetService.updateById(budget);
    }

    @Operation(summary = "删除预算")
    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return budgetService.removeById(id);
    }

    @Operation(summary = "获取预算使用情况")
    @GetMapping("/{id}/usage")
    public Map<String, Object> getBudgetUsage(@PathVariable Long id) {
        return budgetService.getBudgetUsage(id);
    }

    @Operation(summary = "获取所有预算使用情况")
    @GetMapping("/usage")
    public List<Map<String, Object>> getAllBudgetUsage() {
        return budgetService.getAllBudgetUsage();
    }

    @Operation(summary = "检查预算是否超支")
    @GetMapping("/{id}/over-budget")
    public boolean isOverBudget(@PathVariable Long id) {
        return budgetService.isOverBudget(id);
    }

    @Operation(summary = "获取预算剩余金额")
    @GetMapping("/{id}/remaining")
    public Map<String, Object> getBudgetRemaining(@PathVariable Long id) {
        return budgetService.getBudgetRemaining(id);
    }
} 