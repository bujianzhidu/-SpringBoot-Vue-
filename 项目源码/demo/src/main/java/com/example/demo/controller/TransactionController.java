package com.example.demo.controller;

import com.example.demo.entity.Transaction;
import com.example.demo.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import jakarta.servlet.http.HttpServletResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.util.StringUtils;

@Tag(name = "交易管理", description = "交易记录相关接口")
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @Operation(summary = "添加交易记录")
    @PostMapping
    public boolean add(@RequestBody Transaction transaction) {
        return transactionService.save(transaction);
    }

    @Operation(summary = "更新交易记录")
    @PutMapping("/{id}")
    public boolean update(@PathVariable Long id, @RequestBody Transaction transaction) {
        transaction.setId(id);
        return transactionService.updateById(transaction);
    }

    @Operation(summary = "删除交易记录")
    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return transactionService.removeById(id);
    }

    @Operation(summary = "获取交易记录")
    @GetMapping("/{id}")
    public Transaction get(@PathVariable Long id) {
        return transactionService.getById(id);
    }

    @Operation(summary = "获取所有交易记录")
    @GetMapping
    public IPage<Transaction> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            @RequestParam(required = false) String keyword) {
        
        // 创建分页对象
        Page<Transaction> pageParam = new Page<>(page, size);
        
        // 创建查询条件
        LambdaQueryWrapper<Transaction> queryWrapper = new LambdaQueryWrapper<>();
        
        // 添加筛选条件
        if (StringUtils.hasText(type)) {
            queryWrapper.eq(Transaction::getType, type);
        }
        if (categoryId != null) {
            queryWrapper.eq(Transaction::getCategoryId, categoryId);
        }
        if (StringUtils.hasText(startDate)) {
            LocalDateTime start = LocalDateTime.parse(startDate + "T00:00:00");
            queryWrapper.ge(Transaction::getTransactionDate, start);
        }
        if (StringUtils.hasText(endDate)) {
            LocalDateTime end = LocalDateTime.parse(endDate + "T23:59:59");
            queryWrapper.le(Transaction::getTransactionDate, end);
        }
        if (minAmount != null) {
            queryWrapper.ge(Transaction::getAmount, minAmount);
        }
        if (maxAmount != null) {
            queryWrapper.le(Transaction::getAmount, maxAmount);
        }
        if (StringUtils.hasText(keyword)) {
            queryWrapper.like(Transaction::getDescription, keyword)
                    .or()
                    .like(Transaction::getCategory, keyword);
        }
        
        // 按交易日期降序排序
        queryWrapper.orderByDesc(Transaction::getTransactionDate);
        
        // 执行查询
        return transactionService.page(pageParam, queryWrapper);
    }

    @Operation(summary = "获取收支统计")
    @GetMapping("/statistics")
    public Map<String, BigDecimal> getStatistics(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        LocalDateTime start = null;
        LocalDateTime end = null;
        
        if (startDate != null) {
            start = LocalDateTime.parse(startDate, DateTimeFormatter.ISO_DATE_TIME);
        }
        if (endDate != null) {
            end = LocalDateTime.parse(endDate, DateTimeFormatter.ISO_DATE_TIME);
        }
        
        return transactionService.getStatistics(start, end);
    }

    @Operation(summary = "获取分类统计")
    @GetMapping("/category-statistics")
    public List<Map<String, Object>> getCategoryStatistics(@RequestParam String type) {
        return transactionService.getCategoryStatistics(type);
    }

    @Operation(summary = "导出PDF报表")
    @GetMapping("/export-pdf")
    public ResponseEntity<byte[]> exportPdf() {
        byte[] pdfBytes = transactionService.exportToPdf();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=finance-report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @Operation(summary = "导出交易记录")
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportToExcel() {
        try {
            byte[] excelData = transactionService.exportToExcel();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.ms-excel"));
            headers.setContentDispositionFormData("attachment", "transactions.xls");
            headers.setContentLength(excelData.length);
            
            return new ResponseEntity<>(excelData, headers, HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException("导出Excel失败", e);
        }
    }
} 