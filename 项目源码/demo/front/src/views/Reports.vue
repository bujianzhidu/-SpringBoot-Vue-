<template>
  <div class="reports-container">
    <!-- 时间范围选择 -->
    <el-card class="mb-4">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="时间范围">
          <el-select 
            v-model="searchForm.timeRange" 
            @change="handleTimeRangeChange" 
            :disabled="loading"
            placeholder="请选择时间范围"
            style="width: 120px"
          >
            <el-option label="本月" value="month" />
            <el-option label="上月" value="lastMonth" />
            <el-option label="本季度" value="quarter" />
            <el-option label="本年" value="year" />
            <el-option label="自定义" value="custom" />
          </el-select>
        </el-form-item>
        
        <el-form-item v-if="searchForm.timeRange === 'custom'">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            :shortcuts="dateShortcuts"
            :disabled-date="disabledDate"
            value-format="YYYY-MM-DD"
            @change="handleDateRangeChange"
            :disabled="loading"
          />
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 加载状态 -->
    <el-skeleton :loading="loading" animated :count="4" :throttle="500">
      <template #template>
        <div style="padding: 20px">
          <el-skeleton-item variant="text" style="width: 30%" />
          <div style="display: flex; align-items: center; justify-content: space-between">
            <el-skeleton-item variant="text" style="margin-right: 16px" />
            <el-skeleton-item variant="text" style="width: 30%" />
          </div>
        </div>
      </template>
      
      <template #default>
        <!-- 统计卡片 -->
        <el-row :gutter="20" class="mb-4">
          <el-col :xs="24" :sm="12" :md="6">
            <el-card shadow="hover" class="stat-card">
              <template #header>
                <div class="card-header">
                  <span>总收入</span>
                </div>
              </template>
              <div class="stat-value income">
                {{ formatAmount(statistics.totalIncome) }}
              </div>
              <div class="stat-compare">
                较上期
                <span :class="statistics.incomeChange >= 0 ? 'up' : 'down'">
                  {{ statistics.incomeChange >= 0 ? '+' : '' }}{{ statistics.incomeChange }}%
                </span>
              </div>
            </el-card>
          </el-col>
          
          <el-col :xs="24" :sm="12" :md="6">
            <el-card shadow="hover" class="stat-card">
              <template #header>
                <div class="card-header">
                  <span>总支出</span>
                </div>
              </template>
              <div class="stat-value expense">
                {{ formatAmount(statistics.totalExpense) }}
              </div>
              <div class="stat-compare">
                较上期
                <span :class="statistics.expenseChange >= 0 ? 'up' : 'down'">
                  {{ statistics.expenseChange >= 0 ? '+' : '' }}{{ statistics.expenseChange }}%
                </span>
              </div>
            </el-card>
          </el-col>
          
          <el-col :xs="24" :sm="12" :md="6">
            <el-card shadow="hover" class="stat-card">
              <template #header>
                <div class="card-header">
                  <span>净收入</span>
                </div>
              </template>
              <div class="stat-value" :class="statistics.netIncome >= 0 ? 'income' : 'expense'">
                {{ formatAmount(statistics.netIncome) }}
              </div>
              <div class="stat-compare">
                较上期
                <span :class="statistics.netIncomeChange >= 0 ? 'up' : 'down'">
                  {{ statistics.netIncomeChange >= 0 ? '+' : '' }}{{ statistics.netIncomeChange }}%
                </span>
              </div>
            </el-card>
          </el-col>
          
          <el-col :xs="24" :sm="12" :md="6">
            <el-card shadow="hover" class="stat-card">
              <template #header>
                <div class="card-header">
                  <span>预算使用率</span>
                </div>
              </template>
              <div class="stat-value">
                <el-progress
                  :percentage="statistics.budgetUsage"
                  :status="getBudgetStatus(statistics.budgetUsage)"
                />
              </div>
              <div class="stat-compare">
                剩余预算
                <span class="normal">
                  {{ formatAmount(statistics.remainingBudget) }}
                </span>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <!-- 图表区域 -->
        <el-row :gutter="20">
          <el-col :xs="24" :lg="12">
            <el-card class="chart-card">
              <template #header>
                <div class="card-header">
                  <span>收支趋势</span>
                </div>
              </template>
              <div ref="trendChartRef" class="chart"></div>
            </el-card>
          </el-col>
          
          <el-col :xs="24" :lg="12">
            <el-card class="chart-card">
              <template #header>
                <div class="card-header">
                  <span>支出分类占比</span>
                </div>
              </template>
              <div ref="categoryChartRef" class="chart"></div>
            </el-card>
          </el-col>
        </el-row>

        <!-- 详细数据表格 -->
        <el-card class="mt-4">
          <template #header>
            <div class="card-header">
              <span>详细数据</span>
              <el-button type="success" @click="exportData">
                <el-icon><Download /></el-icon>
                导出Excel
              </el-button>
            </div>
          </template>
          
          <el-table :data="detailedData" style="width: 100%">
            <el-table-column prop="date" label="日期" width="120" />
            <el-table-column prop="category" label="分类" width="120" />
            <el-table-column prop="type" label="类型" width="100">
              <template #default="{ row }">
                <el-tag :type="row.type === 'INCOME' ? 'success' : 'danger'">
                  {{ row.type === 'INCOME' ? '收入' : '支出' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="amount" label="金额" width="120">
              <template #default="{ row }">
                <span :class="row.type === 'INCOME' ? 'income' : 'expense'">
                  {{ row.type === 'INCOME' ? '+' : '-' }}{{ formatAmount(row.amount) }}
                </span>
              </template>
            </el-table-column>
            <el-table-column prop="description" label="描述" />
          </el-table>
        </el-card>
      </template>
    </el-skeleton>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Download } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { transactionApi } from '@/api'
import dayjs from 'dayjs'
import axios from 'axios'

// 加载状态
const loading = ref(false)

// 搜索表单
const searchForm = reactive({
  timeRange: 'month',
  dateRange: []
})

// 统计数据
const statistics = ref({
  totalIncome: 0,
  totalExpense: 0,
  netIncome: 0,
  incomeChange: 0,
  expenseChange: 0,
  netIncomeChange: 0,
  budgetUsage: 0,
  remainingBudget: 0
})

// 详细数据
const detailedData = ref([])

// 图表相关
const trendChartRef = ref(null)
const categoryChartRef = ref(null)
let trendChart = null
let categoryChart = null

// 格式化金额
const formatAmount = (amount) => {
  return new Intl.NumberFormat('zh-CN', {
    style: 'currency',
    currency: 'CNY'
  }).format(amount)
}

// 格式化日期
const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  })
}

// 获取预算状态
const getBudgetStatus = (usage) => {
  if (usage >= 100) return 'exception'
  if (usage >= 80) return 'warning'
  return 'success'
}

// 初始化趋势图表
const initTrendChart = () => {
  if (!trendChartRef.value) return
  
  trendChart = echarts.init(trendChartRef.value)
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['收入', '支出']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: []
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '收入',
        type: 'line',
        data: [],
        itemStyle: {
          color: '#67C23A'
        }
      },
      {
        name: '支出',
        type: 'line',
        data: [],
        itemStyle: {
          color: '#F56C6C'
        }
      }
    ]
  }
  trendChart.setOption(option)
}

// 初始化分类图表
const initCategoryChart = () => {
  if (!categoryChartRef.value) return
  
  categoryChart = echarts.init(categoryChartRef.value)
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        type: 'pie',
        radius: '50%',
        data: [],
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  }
  categoryChart.setOption(option)
}

// 时间范围变化
const handleTimeRangeChange = () => {
  if (searchForm.timeRange !== 'custom') {
    // 根据选择的时间范围设置日期
    const now = new Date()
    let startDate = new Date()
    let endDate = new Date()
    
    switch (searchForm.timeRange) {
      case 'month':
        startDate.setDate(1)
        startDate.setHours(0, 0, 0, 0)
        endDate.setMonth(endDate.getMonth() + 1)
        endDate.setDate(0)
        endDate.setHours(23, 59, 59, 999)
        break
      case 'lastMonth':
        // 设置开始日期为上个月的第一天
        startDate.setMonth(startDate.getMonth() - 1)
        startDate.setDate(1)
        startDate.setHours(0, 0, 0, 0)
        // 设置结束日期为上个月的最后一天
        endDate = new Date(startDate)
        endDate.setMonth(endDate.getMonth() + 1)
        endDate.setDate(0)
        endDate.setHours(23, 59, 59, 999)
        break
      case 'quarter':
        const quarter = Math.floor(now.getMonth() / 3)
        startDate.setMonth(quarter * 3)
        startDate.setDate(1)
        startDate.setHours(0, 0, 0, 0)
        endDate.setMonth((quarter + 1) * 3)
        endDate.setDate(0)
        endDate.setHours(23, 59, 59, 999)
        break
      case 'year':
        startDate.setMonth(0)
        startDate.setDate(1)
        startDate.setHours(0, 0, 0, 0)
        endDate.setMonth(11)
        endDate.setDate(31)
        endDate.setHours(23, 59, 59, 999)
        break
    }
    
    console.log('Selected time range:', searchForm.timeRange)
    console.log('Start date:', startDate.toISOString())
    console.log('End date:', endDate.toISOString())
    
    searchForm.dateRange = [startDate, endDate]
  } else {
    // 如果选择自定义，清空日期范围
    searchForm.dateRange = []
  }
  loadAllData()
}

// 日期范围变化
const handleDateRangeChange = () => {
  if (searchForm.dateRange?.length === 2) {
    // 确保日期范围有效
    const [start, end] = searchForm.dateRange
    if (start && end) {
      loadAllData()
    }
  }
}

// 加载统计数据
const loadStatistics = async () => {
  try {
    let startDate = null
    let endDate = null
    
    if (searchForm.dateRange?.length === 2) {
      const [start, end] = searchForm.dateRange
      if (start && end) {
        // 设置开始日期为当天的开始时间
        startDate = new Date(start)
        startDate.setHours(0, 0, 0, 0)
        // 设置结束日期为当天的结束时间
        endDate = new Date(end)
        endDate.setHours(23, 59, 59, 999)
      }
    }
    
    console.log('Loading statistics with date range:', {
      startDate: startDate?.toISOString(),
      endDate: endDate?.toISOString()
    })
    
    // 确保日期格式正确
    const formattedStartDate = startDate ? startDate.toISOString().replace('Z', '') : null
    const formattedEndDate = endDate ? endDate.toISOString().replace('Z', '') : null
    
    const res = await transactionApi.getStatistics(formattedStartDate, formattedEndDate)
    console.log('Statistics response:', res)
    
    // 确保所有数值都是数字类型
    const income = Number(res.income) || 0
    const expense = Number(res.expense) || 0
    const incomeChange = Number(res.incomeChange) || 0
    const expenseChange = Number(res.expenseChange) || 0
    const balanceChange = Number(res.balanceChange) || 0
    const budgetUsage = Number(res.budgetUsage) || 0
    const remainingBudget = Number(res.remainingBudget) || 0
    
    statistics.value = {
      totalIncome: income,
      totalExpense: expense,
      netIncome: income - expense,
      incomeChange,
      expenseChange,
      netIncomeChange: balanceChange,
      budgetUsage,
      remainingBudget
    }
  } catch (error) {
    console.error('Load statistics error:', error)
    ElMessage.error('加载统计数据失败')
  }
}

// 加载趋势数据
const loadTrendData = async () => {
  try {
    let startDate = null
    let endDate = null
    
    if (searchForm.dateRange?.length === 2) {
      const [start, end] = searchForm.dateRange
      if (start && end) {
        // 设置开始日期为当天的开始时间
        startDate = new Date(start)
        startDate.setHours(0, 0, 0, 0)
        // 设置结束日期为当天的结束时间
        endDate = new Date(end)
        endDate.setHours(23, 59, 59, 999)
      }
    }
    
    console.log('Loading trend data with date range:', {
      startDate: startDate?.toISOString(),
      endDate: endDate?.toISOString()
    })
    
    const res = await transactionApi.getCategoryStatistics('trend', startDate?.toISOString(), endDate?.toISOString())
    const dates = res.map(item => item.month)
    const income = res.map(item => Number(item.income))
    const expense = res.map(item => Number(item.expense))
    
    if (trendChart) {
      trendChart.setOption({
        xAxis: {
          data: dates
        },
        series: [
          {
            name: '收入',
            data: income
          },
          {
            name: '支出',
            data: expense
          }
        ]
      })
    }
  } catch (error) {
    console.error('Load trend data error:', error)
    ElMessage.error('加载趋势数据失败')
  }
}

// 加载分类数据
const loadCategoryData = async () => {
  try {
    let startDate = null
    let endDate = null
    
    if (searchForm.dateRange?.length === 2) {
      const [start, end] = searchForm.dateRange
      if (start && end) {
        // 设置开始日期为当天的开始时间
        startDate = new Date(start)
        startDate.setHours(0, 0, 0, 0)
        // 设置结束日期为当天的结束时间
        endDate = new Date(end)
        endDate.setHours(23, 59, 59, 999)
      }
    }
    
    console.log('Loading category data with date range:', {
      startDate: startDate?.toISOString(),
      endDate: endDate?.toISOString()
    })
    
    const res = await transactionApi.getCategoryStatistics('expense', startDate?.toISOString(), endDate?.toISOString())
    if (categoryChart) {
      categoryChart.setOption({
        series: [
          {
            data: res.map(item => ({
              name: item.category,
              value: Number(item.amount)
            }))
          }
        ]
      })
    }
  } catch (error) {
    console.error('Load category data error:', error)
    ElMessage.error('加载分类数据失败')
  }
}

// 加载详细数据
const loadDetailedData = async () => {
  try {
    let startDate = null
    let endDate = null
    
    if (searchForm.dateRange?.length === 2) {
      const [start, end] = searchForm.dateRange
      if (start && end) {
        // 设置开始日期为当天的开始时间
        startDate = new Date(start)
        startDate.setHours(0, 0, 0, 0)
        // 设置结束日期为当天的结束时间
        endDate = new Date(end)
        endDate.setHours(23, 59, 59, 999)
      }
    }
    
    console.log('Loading detailed data with date range:', {
      startDate: startDate?.toISOString(),
      endDate: endDate?.toISOString()
    })

    const res = await transactionApi.list({
      startDate: startDate?.toISOString(),
      endDate: endDate?.toISOString()
    })
    console.log('Detailed data response:', res)

    const records = res?.records || []

    detailedData.value = records.map(item => ({
      date: formatDate(item.transactionDate),
      category: item.category?.name || '',
      type: item.type,
      amount: item.amount,
      description: item.description
    }))
  } catch (error) {
    console.error('Load detailed data error:', error)
    ElMessage.error('加载详细数据失败')
  }
}

// 导出数据
const exportData = async () => {
  try {
    const response = await axios({
      url: '/api/transactions/export',
      method: 'GET',
      responseType: 'blob'
    })
    
    const blob = new Blob([response.data], { 
      type: 'application/vnd.ms-excel' 
    })
    const link = document.createElement('a')
    link.href = window.URL.createObjectURL(blob)
    link.download = `财务报表_${dayjs().format('YYYY-MM-DD_HH-mm-ss')}.xls`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(link.href)
    
    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败')
  }
}

// 加载所有数据
const loadAllData = async () => {
  loading.value = true
  try {
    await Promise.all([
      loadStatistics(),
      loadTrendData(),
      loadCategoryData(),
      loadDetailedData()
    ])
  } catch (error) {
    console.error('Load all data error:', error)
    // 错误已在请求拦截器中处理
  } finally {
    loading.value = false
  }
}

// 监听窗口大小变化
const handleResize = () => {
  trendChart?.resize()
  categoryChart?.resize()
}

// 日期快捷选项
const dateShortcuts = [
  {
    text: '最近一周',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
      return [start, end]
    }
  },
  {
    text: '最近一个月',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 30)
      return [start, end]
    }
  },
  {
    text: '最近三个月',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 90)
      return [start, end]
    }
  }
]

// 禁用未来日期
const disabledDate = (time) => {
  return time.getTime() > Date.now()
}

onMounted(async () => {
  initTrendChart()
  initCategoryChart()
  await loadAllData()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  trendChart?.dispose()
  categoryChart?.dispose()
})
</script>

<style scoped>
.reports-container {
  padding: 20px;
}

.mb-4 {
  margin-bottom: 20px;
}

.mt-4 {
  margin-top: 20px;
}

.stat-card {
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  margin: 10px 0;
}

.stat-value.income {
  color: #67C23A;
}

.stat-value.expense {
  color: #F56C6C;
}

.stat-compare {
  font-size: 14px;
  color: #909399;
}

.stat-compare .up {
  color: #67C23A;
}

.stat-compare .down {
  color: #F56C6C;
}

.stat-compare .normal {
  color: #909399;
}

.chart-card {
  margin-bottom: 20px;
}

.chart {
  height: 300px;
}

.income {
  color: #67C23A;
}

.expense {
  color: #F56C6C;
}

@media (max-width: 768px) {
  .reports-container {
    padding: 10px;
  }
  
  .chart {
    height: 250px;
  }
  
  .stat-value {
    font-size: 20px;
  }
}
</style> 