package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.Budget;
import com.example.demo.entity.Category;
import com.example.demo.entity.Transaction;
import com.example.demo.mapper.TransactionMapper;
import com.example.demo.service.CategoryService;
import com.example.demo.service.TransactionService;
import com.example.demo.service.BudgetUsageService;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl extends ServiceImpl<TransactionMapper, Transaction> implements TransactionService {

    private final CategoryService categoryService;
    private final BudgetUsageService budgetUsageService;

    public TransactionServiceImpl(CategoryService categoryService, BudgetUsageService budgetUsageService) {
        this.categoryService = categoryService;
        this.budgetUsageService = budgetUsageService;
    }

    @Override
    public boolean save(Transaction transaction) {
        try {
            // 获取分类信息
            Category category = categoryService.getById(transaction.getCategoryId());
            if (category == null) {
                throw new RuntimeException("分类不存在");
            }
            
            // 设置分类名称
            transaction.setCategory(category.getName());
            
            // 设置交易日期
            if (transaction.getTransactionDate() == null) {
                transaction.setTransactionDate(LocalDateTime.now());
            }
            
            return super.save(transaction);
        } catch (Exception e) {
            log.error("保存交易记录失败", e);
            throw new RuntimeException("保存交易记录失败: " + e.getMessage());
        }
    }

    @Override
    public Map<String, BigDecimal> getStatistics() {
        return getStatistics(null, null);
    }

    @Override
    public Map<String, BigDecimal> getStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, BigDecimal> result = new HashMap<>();
        
        // 获取上一个时间范围
        LocalDateTime previousStartDate;
        LocalDateTime previousEndDate;
        
        if (startDate == null || endDate == null) {
            // 如果没有指定时间范围,使用当前月份
            LocalDateTime now = LocalDateTime.now();
            startDate = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            endDate = now.withDayOfMonth(now.toLocalDate().lengthOfMonth()).withHour(23).withMinute(59).withSecond(59);
            
            // 上个月
            previousStartDate = startDate.minusMonths(1);
            previousEndDate = endDate.minusMonths(1);
        } else {
            // 如果指定了时间范围,计算上个月同期
            previousStartDate = startDate.minusMonths(1);
            previousEndDate = endDate.minusMonths(1);
        }
        
        // 确保日期范围正确
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("开始日期不能晚于结束日期");
        }
        
        System.out.println("Current period: " + startDate + " to " + endDate);
        System.out.println("Previous period: " + previousStartDate + " to " + previousEndDate);
        
        // 统计当前时间范围的收入
        List<Transaction> incomeTransactions = baseMapper.selectList(new LambdaQueryWrapper<Transaction>()
                .eq(Transaction::getType, "INCOME")
                .between(Transaction::getTransactionDate, startDate, endDate)
                .eq(Transaction::getDeleted, 0));
        
        System.out.println("Found " + incomeTransactions.size() + " income transactions");
        for (Transaction t : incomeTransactions) {
            System.out.println("Income transaction: " + t.getTransactionDate() + " - " + t.getAmount());
        }
        
        BigDecimal currentIncome = incomeTransactions.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // 统计当前时间范围的支出
        List<Transaction> expenseTransactions = baseMapper.selectList(new LambdaQueryWrapper<Transaction>()
                .eq(Transaction::getType, "EXPENSE")
                .between(Transaction::getTransactionDate, startDate, endDate)
                .eq(Transaction::getDeleted, 0));
        
        System.out.println("Found " + expenseTransactions.size() + " expense transactions");
        for (Transaction t : expenseTransactions) {
            System.out.println("Expense transaction: " + t.getTransactionDate() + " - " + t.getAmount());
        }
        
        BigDecimal currentExpense = expenseTransactions.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // 统计上一个时间范围的收入
        List<Transaction> previousIncomeTransactions = baseMapper.selectList(new LambdaQueryWrapper<Transaction>()
                .eq(Transaction::getType, "INCOME")
                .between(Transaction::getTransactionDate, previousStartDate, previousEndDate)
                .eq(Transaction::getDeleted, 0));
        
        System.out.println("Found " + previousIncomeTransactions.size() + " previous income transactions");
        for (Transaction t : previousIncomeTransactions) {
            System.out.println("Previous income transaction: " + t.getTransactionDate() + " - " + t.getAmount());
        }
        
        BigDecimal previousIncome = previousIncomeTransactions.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // 统计上一个时间范围的支出
        List<Transaction> previousExpenseTransactions = baseMapper.selectList(new LambdaQueryWrapper<Transaction>()
                .eq(Transaction::getType, "EXPENSE")
                .between(Transaction::getTransactionDate, previousStartDate, previousEndDate)
                .eq(Transaction::getDeleted, 0));
        
        System.out.println("Found " + previousExpenseTransactions.size() + " previous expense transactions");
        for (Transaction t : previousExpenseTransactions) {
            System.out.println("Previous expense transaction: " + t.getTransactionDate() + " - " + t.getAmount());
        }
        
        BigDecimal previousExpense = previousExpenseTransactions.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        System.out.println("Current income: " + currentIncome);
        System.out.println("Current expense: " + currentExpense);
        System.out.println("Previous income: " + previousIncome);
        System.out.println("Previous expense: " + previousExpense);
        
        // 计算环比变化
        BigDecimal incomeChange;
        if (previousIncome.compareTo(BigDecimal.ZERO) == 0) {
            // 如果上期收入为0，且本期有收入，则显示100%增长
            incomeChange = currentIncome.compareTo(BigDecimal.ZERO) > 0 ? 
                    new BigDecimal("100") : 
                    BigDecimal.ZERO;
        } else {
            incomeChange = currentIncome.subtract(previousIncome)
                    .divide(previousIncome, 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(new BigDecimal("100"));
        }
        
        BigDecimal expenseChange;
        if (previousExpense.compareTo(BigDecimal.ZERO) == 0) {
            // 如果上期支出为0，且本期有支出，则显示100%增长
            expenseChange = currentExpense.compareTo(BigDecimal.ZERO) > 0 ? 
                    new BigDecimal("100") : 
                    BigDecimal.ZERO;
        } else {
            expenseChange = currentExpense.subtract(previousExpense)
                    .divide(previousExpense, 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(new BigDecimal("100"));
        }
        
        // 获取预算使用情况
        Map<String, BigDecimal> budgetInfo = budgetUsageService.calculateTotalBudgetAndUsage(startDate, endDate);
        BigDecimal totalBudget = budgetInfo.get("totalBudget");
        BigDecimal totalUsed = budgetInfo.get("totalUsed");
        
        // 计算预算使用率和剩余预算
        BigDecimal budgetUsage;
        BigDecimal remainingBudget;
        
        if (totalBudget.compareTo(BigDecimal.ZERO) == 0) {
            // 如果没有设置预算，使用率为0%，剩余预算为0
            budgetUsage = BigDecimal.ZERO;
            remainingBudget = BigDecimal.ZERO;
        } else {
            // 如果有预算，计算使用率和剩余预算
            budgetUsage = totalUsed.divide(totalBudget, 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(new BigDecimal("100"));
            remainingBudget = totalBudget.subtract(totalUsed);
            
            // 确保预算使用率不超过100%
            if (budgetUsage.compareTo(new BigDecimal("100")) > 0) {
                budgetUsage = new BigDecimal("100");
            }
            
            // 如果剩余预算为负数，将其设置为0
            if (remainingBudget.compareTo(BigDecimal.ZERO) < 0) {
                remainingBudget = BigDecimal.ZERO;
            }
        }
        
        // 设置返回结果
        result.put("income", currentIncome);
        result.put("expense", currentExpense);
        result.put("balance", currentIncome.subtract(currentExpense));
        result.put("incomeChange", incomeChange);
        result.put("expenseChange", expenseChange);
        result.put("balanceChange", incomeChange.subtract(expenseChange));
        result.put("budgetUsage", budgetUsage);
        result.put("remainingBudget", remainingBudget);
        
        System.out.println("Final statistics: " + result);
        
        return result;
    }

    @Override
    public List<Map<String, Object>> getCategoryStatistics(String type) {
        return getCategoryStatistics(type, null, null);
    }

    @Override
    public List<Map<String, Object>> getCategoryStatistics(String type, LocalDateTime startDate, LocalDateTime endDate) {
        if ("trend".equals(type)) {
            return getTrendStatistics(startDate, endDate);
        }
        
        // 如果没有指定时间范围，使用当前月份
        if (startDate == null || endDate == null) {
            LocalDateTime now = LocalDateTime.now();
            startDate = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            endDate = now.withDayOfMonth(now.toLocalDate().lengthOfMonth()).withHour(23).withMinute(59).withSecond(59);
        } else {
            // 确保开始日期是当天的开始时间
            startDate = startDate.withHour(0).withMinute(0).withSecond(0);
            // 确保结束日期是当天的结束时间
            endDate = endDate.withHour(23).withMinute(59).withSecond(59);
        }
        
        System.out.println("Category statistics period: " + startDate + " to " + endDate);
        
        // 按分类统计
        List<Map<String, Object>> result = new ArrayList<>();
        List<Transaction> transactions = baseMapper.selectList(new LambdaQueryWrapper<Transaction>()
                .eq(Transaction::getType, type)
                .between(Transaction::getTransactionDate, startDate, endDate)
                .eq(Transaction::getDeleted, 0));
        
        System.out.println("Found " + transactions.size() + " transactions for type: " + type);
        for (Transaction t : transactions) {
            System.out.println("Transaction: " + t.getTransactionDate() + " - " + t.getCategory() + " - " + t.getAmount());
        }
        
        // 按分类分组并计算总额
        Map<String, BigDecimal> categoryTotals = transactions.stream()
                .collect(Collectors.groupingBy(
                    Transaction::getCategory,
                    Collectors.mapping(
                        Transaction::getAmount,
                        Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                    )
                ));
        
        // 计算总金额
        BigDecimal totalAmount = categoryTotals.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // 转换为前端需要的格式
        categoryTotals.forEach((category, amount) -> {
            Map<String, Object> item = new HashMap<>();
            item.put("category", category);
            item.put("amount", amount);
            item.put("percentage", totalAmount.compareTo(BigDecimal.ZERO) == 0 ? 
                    BigDecimal.ZERO : 
                    amount.divide(totalAmount, 4, BigDecimal.ROUND_HALF_UP)
                            .multiply(new BigDecimal("100")));
            result.add(item);
        });
        
        // 按金额降序排序
        result.sort((a, b) -> ((BigDecimal) b.get("amount")).compareTo((BigDecimal) a.get("amount")));
        
        System.out.println("Category statistics result: " + result);
        
        return result;
    }

    private List<Map<String, Object>> getTrendStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        // 如果没有指定时间范围，使用最近6个月
        if (startDate == null || endDate == null) {
            endDate = LocalDateTime.now();
            startDate = endDate.minusMonths(5);
        }
        
        // 获取所有交易记录
        List<Transaction> transactions = baseMapper.selectList(new LambdaQueryWrapper<Transaction>()
                .between(Transaction::getTransactionDate, startDate, endDate));
        
        // 按月份分组
        Map<String, List<Transaction>> monthlyTransactions = transactions.stream()
                .collect(Collectors.groupingBy(t -> t.getTransactionDate().format(DateTimeFormatter.ofPattern("yyyy-MM"))));
        
        // 生成所有月份
        List<String> months = new ArrayList<>();
        LocalDateTime current = startDate;
        while (!current.isAfter(endDate)) {
            months.add(current.format(DateTimeFormatter.ofPattern("yyyy-MM")));
            current = current.plusMonths(1);
        }
        
        // 计算每个月的收入和支出
        List<Map<String, Object>> result = new ArrayList<>();
        for (String month : months) {
            Map<String, Object> monthData = new HashMap<>();
            monthData.put("month", month);
            
            List<Transaction> monthTransactions = monthlyTransactions.getOrDefault(month, Collections.emptyList());
            
            BigDecimal income = monthTransactions.stream()
                    .filter(t -> "INCOME".equals(t.getType()))
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            BigDecimal expense = monthTransactions.stream()
                    .filter(t -> "EXPENSE".equals(t.getType()))
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            monthData.put("income", income);
            monthData.put("expense", expense);
            result.add(monthData);
        }
        
        return result;
    }

    @Override
    public byte[] exportToPdf() {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // 添加标题
            Paragraph title = new Paragraph("个人财务报告")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(20);
            document.add(title);

            // 添加统计信息
            Map<String, BigDecimal> stats = getStatistics(null, null);
            document.add(new Paragraph("总收入: " + stats.get("totalIncome")));
            document.add(new Paragraph("总支出: " + stats.get("totalExpense")));
            document.add(new Paragraph("结余: " + stats.get("balance")));

            // 添加交易记录表格
            Table table = new Table(5);
            table.addCell("类型");
            table.addCell("分类");
            table.addCell("金额");
            table.addCell("描述");
            table.addCell("日期");

            List<Transaction> transactions = list();
            for (Transaction t : transactions) {
                table.addCell(t.getType());
                table.addCell(t.getCategory());
                table.addCell(t.getAmount().toString());
                table.addCell(t.getDescription());
                table.addCell(t.getTransactionDate().toString());
            }
            document.add(table);

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("生成PDF失败", e);
        }
    }

    @Override
    public byte[] exportToExcel() {
        try (Workbook workbook = new HSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("交易记录");

            // 创建标题样式
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            // 创建标题字体
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);

            // 创建内容样式
            CellStyle contentStyle = workbook.createCellStyle();
            contentStyle.setBorderBottom(BorderStyle.THIN);
            contentStyle.setBorderTop(BorderStyle.THIN);
            contentStyle.setBorderLeft(BorderStyle.THIN);
            contentStyle.setBorderRight(BorderStyle.THIN);
            contentStyle.setAlignment(HorizontalAlignment.CENTER);
            contentStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            // 创建标题行
            Row headerRow = sheet.createRow(0);
            String[] headers = {"交易类型", "分类", "金额", "描述", "交易日期", "创建时间"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // 获取所有交易记录
            List<Transaction> transactions = baseMapper.selectList(new LambdaQueryWrapper<Transaction>()
                    .eq(Transaction::getDeleted, 0)
                    .orderByDesc(Transaction::getTransactionDate));

            System.out.println("Found " + transactions.size() + " transactions to export");

            // 填充数据
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            int rowNum = 1;
            for (Transaction transaction : transactions) {
                Row row = sheet.createRow(rowNum++);
                
                // 交易类型
                Cell typeCell = row.createCell(0);
                typeCell.setCellValue(transaction.getType().equals("INCOME") ? "收入" : "支出");
                typeCell.setCellStyle(contentStyle);
                
                // 分类
                Cell categoryCell = row.createCell(1);
                categoryCell.setCellValue(transaction.getCategory());
                categoryCell.setCellStyle(contentStyle);
                
                // 金额
                Cell amountCell = row.createCell(2);
                amountCell.setCellValue(transaction.getAmount().doubleValue());
                amountCell.setCellStyle(contentStyle);
                
                // 描述
                Cell descriptionCell = row.createCell(3);
                descriptionCell.setCellValue(transaction.getDescription() != null ? transaction.getDescription() : "");
                descriptionCell.setCellStyle(contentStyle);
                
                // 交易日期
                Cell dateCell = row.createCell(4);
                dateCell.setCellValue(transaction.getTransactionDate().format(formatter));
                dateCell.setCellStyle(contentStyle);
                
                // 创建时间
                Cell createTimeCell = row.createCell(5);
                createTimeCell.setCellValue(transaction.getCreateTime().format(formatter));
                createTimeCell.setCellStyle(contentStyle);
            }

            // 自动调整列宽
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // 写入到字节数组
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            byte[] data = outputStream.toByteArray();
            System.out.println("Generated Excel file size: " + data.length + " bytes");
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("导出Excel失败: " + e.getMessage(), e);
        }
    }
} 