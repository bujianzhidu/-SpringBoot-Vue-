<script setup>
import { ref, onMounted } from 'vue'
import { transactionApi } from '@/api'
import { ElMessage } from 'element-plus'
import * as XLSX from 'xlsx'
import dayjs from 'dayjs'
import { jsPDF } from 'jspdf'
import autoTable from 'jspdf-autotable'

// ... existing code ...

// 导出数据
const exportData = () => {
  try {
    // 创建一个隐藏的a标签用于下载
    const link = document.createElement('a')
    link.style.display = 'none'
    link.href = '/api/transactions/export'
    link.download = `交易记录_${dayjs().format('YYYY-MM-DD_HH-mm-ss')}.xlsx`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    
    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败')
  }
}

// 导出PDF
const exportPDF = () => {
  try {
    // 创建PDF文档
    const doc = new jsPDF()
    
    // 添加标题
    doc.setFontSize(16)
    doc.text('交易记录', 14, 15)
    
    // 添加导出时间
    doc.setFontSize(10)
    doc.text(`导出时间：${dayjs().format('YYYY-MM-DD HH:mm:ss')}`, 14, 22)
    
    // 准备表格数据
    const data = tableData.value.map(item => [
      item.type === 'INCOME' ? '收入' : '支出',
      item.category,
      item.amount,
      item.description || '',
      dayjs(item.transactionDate).format('YYYY-MM-DD HH:mm:ss')
    ])
    
    // 添加表格
    autoTable(doc, {
      head: [['交易类型', '分类', '金额', '描述', '交易日期']],
      body: data,
      startY: 30,
      theme: 'grid',
      headStyles: {
        fillColor: [41, 128, 185],
        textColor: 255,
        fontSize: 12,
        fontStyle: 'bold',
        halign: 'center'
      },
      bodyStyles: {
        fontSize: 10,
        halign: 'center'
      },
      columnStyles: {
        0: { cellWidth: 25 },
        1: { cellWidth: 30 },
        2: { cellWidth: 25 },
        3: { cellWidth: 50 },
        4: { cellWidth: 40 }
      },
      styles: {
        cellPadding: 3,
        lineColor: [41, 128, 185],
        lineWidth: 0.1
      }
    })
    
    // 生成文件名
    const fileName = `交易记录_${dayjs().format('YYYY-MM-DD_HH-mm-ss')}.pdf`
    
    // 保存文件
    doc.save(fileName)
    
    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败')
  }
}

// ... existing code ...
</script>

<template>
  <div class="p-4">
    <div class="flex justify-between items-center mb-4">
      <div class="flex gap-2">
        <el-button type="primary" @click="handleAdd">新增交易</el-button>
        <el-button type="success" @click="exportData">导出Excel</el-button>
        <el-button type="success" @click="exportPDF">导出PDF</el-button>
      </div>
      <div class="flex gap-2">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="YYYY-MM-DD"
          @change="handleDateRangeChange"
        />
        <el-select v-model="type" placeholder="交易类型" @change="handleTypeChange">
          <el-option label="全部" value="" />
          <el-option label="收入" value="INCOME" />
          <el-option label="支出" value="EXPENSE" />
        </el-select>
      </div>
    </div>
    <!-- ... rest of the template ... -->
  </div>
</template>
