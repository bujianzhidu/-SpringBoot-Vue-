package com.example.demo.controller;

import com.example.demo.entity.Category;
import com.example.demo.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "分类管理", description = "交易分类相关接口")
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "获取所有分类")
    @GetMapping
    public List<Category> list() {
        return categoryService.list();
    }

    @Operation(summary = "创建分类")
    @PostMapping
    public boolean create(@RequestBody Category category) {
        return categoryService.save(category);
    }

    @Operation(summary = "更新分类")
    @PutMapping("/{id}")
    public boolean update(@PathVariable Long id, @RequestBody Category category) {
        category.setId(id);
        return categoryService.updateById(category);
    }

    @Operation(summary = "删除分类")
    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return categoryService.removeById(id);
    }

    @Operation(summary = "获取收入分类")
    @GetMapping("/income")
    public List<Category> getIncomeCategories() {
        return categoryService.list(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Category>()
                .eq(Category::getType, "INCOME")
                .orderByAsc(Category::getSort));
    }

    @Operation(summary = "获取支出分类")
    @GetMapping("/expense")
    public List<Category> getExpenseCategories() {
        return categoryService.list(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Category>()
                .eq(Category::getType, "EXPENSE")
                .orderByAsc(Category::getSort));
    }
} 