package com.example.demo.service;

import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

public interface ImportService {
    /**
     * 导入交易记录
     * @param file Excel文件
     * @return 导入结果
     */
    Map<String, Object> importTransactions(MultipartFile file);
    
    /**
     * 导入预算数据
     * @param file Excel文件
     * @return 导入结果
     */
    Map<String, Object> importBudgets(MultipartFile file);
    
    /**
     * 导入财务目标
     * @param file Excel文件
     * @return 导入结果
     */
    Map<String, Object> importFinancialGoals(MultipartFile file);
    
    /**
     * 导入交易分类
     * @param file Excel文件
     * @return 导入结果
     */
    Map<String, Object> importCategories(MultipartFile file);
} 