<template>
  <div class="dashboard-container">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="mb-4">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="stat-card">
          <template #header>
            <div class="card-header">
              <span>本月收入</span>
              <el-tag type="success">月度</el-tag>
            </div>
          </template>
          <div class="stat-value income">
            {{ formatAmount(monthlyStats.income) }}
          </div>
          <div class="stat-compare">
            较上月
            <span :class="monthlyStats.incomeChange >= 0 ? 'up' : 'down'">
              {{ monthlyStats.incomeChange >= 0 ? '+' : '' }}{{ monthlyStats.incomeChange }}%
            </span>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="stat-card">
          <template #header>
            <div class="card-header">
              <span>本月支出</span>
              <el-tag type="danger">月度</el-tag>
            </div>
          </template>
          <div class="stat-value expense">
            {{ formatAmount(monthlyStats.expense) }}
          </div>
          <div class="stat-compare">
            较上月
            <span :class="monthlyStats.expenseChange >= 0 ? 'up' : 'down'">
              {{ monthlyStats.expenseChange >= 0 ? '+' : '' }}{{ monthlyStats.expenseChange }}%
            </span>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="stat-card">
          <template #header>
            <div class="card-header">
              <span>本月结余</span>
              <el-tag type="info">月度</el-tag>
            </div>
          </template>
          <div class="stat-value" :class="monthlyStats.balance >= 0 ? 'income' : 'expense'">
            {{ formatAmount(monthlyStats.balance) }}
          </div>
          <div class="stat-compare">
            较上月
            <span :class="monthlyStats.balanceChange >= 0 ? 'up' : 'down'">
              {{ monthlyStats.balanceChange >= 0 ? '+' : '' }}{{ monthlyStats.balanceChange }}%
            </span>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="stat-card">
          <template #header>
            <div class="card-header">
              <span>预算使用率</span>
              <el-tag type="warning">月度</el-tag>
            </div>
          </template>
          <div class="stat-value">
            <el-progress
                :percentage="monthlyStats.budgetUsage"
                :status="getBudgetStatus(monthlyStats.budgetUsage)"
            />
          </div>
          <div class="stat-compare">
            剩余预算
            <span class="normal">
              {{ formatAmount(monthlyStats.remainingBudget) }}
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
              <el-radio-group v-model="trendTimeRange" size="small">
                <el-radio-button label="week">周</el-radio-button>
                <el-radio-button label="month">月</el-radio-button>
                <el-radio-button label="year">年</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div ref="trendChartRef" class="chart"></div>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span class="text-sm"></span>
              <el-select v-model="categoryTimeRange" size="small">
                <el-option label="本月" value="month"/>
                <el-option label="本季度" value="quarter"/>
                <el-option label="本年" value="year"/>
              </el-select>
            </div>
          </template>
          <div ref="categoryChartRef" class="chart"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最近交易 -->
    <el-card class="mt-4">
      <template #header>
        <div class="card-header">
          <span>最近交易</span>
          <el-button type="primary" link @click="$router.push('/transactions')">
            查看全部
          </el-button>
        </div>
      </template>

      <el-table :data="recentTransactions" style="width: 100%">
        <el-table-column prop="updateTime" label="日期" width="180"/>
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.type === 'INCOME' ? 'success' : 'danger'">
              {{ row.type === 'INCOME' ? '收入' : '支出' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="category.name" label="分类" width="120"/>
        <el-table-column prop="amount" label="金额" width="120">
          <template #default="{ row }">
            <span :class="row.type === 'INCOME' ? 'income' : 'expense'">
              {{ row.type === 'INCOME' ? '+' : '-' }}{{ formatAmount(row.amount) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述"/>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import {ref, onMounted, watch, onUnmounted} from 'vue'
import {ElMessage} from 'element-plus'
import * as echarts from 'echarts'
import {transactionApi} from '@/api'

// 统计数据
const monthlyStats = ref({
  income: 0,
  expense: 0,
  balance: 0,
  incomeChange: 0,
  expenseChange: 0,
  balanceChange: 0,
  budgetUsage: 0,
  remainingBudget: 0
})

// 图表相关
const trendChartRef = ref(null)
const categoryChartRef = ref(null)
const trendTimeRange = ref('month')
const categoryTimeRange = ref('month')
let trendChart = null
let categoryChart = null

// 最近交易
const recentTransactions = ref([])

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
      trigger: 'axis',
      formatter: function (params) {
        let result = params[0].axisValue + '<br/>'
        params.forEach(param => {
          result += param.seriesName + ': ' + formatAmount(param.value) + '<br/>'
        })
        return result
      }
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
      type: 'value',
      axisLabel: {
        formatter: (value) => formatAmount(value)
      }
    },
    series: [
      {
        name: '收入',
        type: 'line',
        data: [],
        itemStyle: {
          color: '#67C23A'
        },
        smooth: true
      },
      {
        name: '支出',
        type: 'line',
        data: [],
        itemStyle: {
          color: '#F56C6C'
        },
        smooth: true
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
      formatter: function (params) {
        return params.name + ': ' + formatAmount(params.value) + ' (' + params.percent + '%)'
      }
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      type: 'scroll'
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
        },
        label: {
          formatter: '{b}: {d}%'
        }
      }
    ]
  }
  categoryChart.setOption(option)
}

// 加载统计数据
const loadStatistics = async () => {
  try {
    const res = await transactionApi.getStatistics()
    console.log('Statistics response:', res)

    // 确保所有数值都是数字类型
    const income = Number(res.income) || 0
    const expense = Number(res.expense) || 0
    const incomeChange = Number(res.incomeChange) || 0
    const expenseChange = Number(res.expenseChange) || 0
    const balanceChange = Number(res.balanceChange) || 0
    const budgetUsage = Number(res.budgetUsage) || 0
    const remainingBudget = Number(res.remainingBudget) || 0

    monthlyStats.value = {
      income,
      expense,
      balance: income - expense,
      incomeChange,
      expenseChange,
      balanceChange,
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
    const res = await transactionApi.getCategoryStatistics('trend')
    console.log('Trend data response:', res)

    if (Array.isArray(res)) {
      const months = res.map(item => item.month)
      const income = res.map(item => Number(item.income) || 0)
      const expense = res.map(item => Number(item.expense) || 0)

      if (trendChart) {
        trendChart.setOption({
          xAxis: {
            data: months
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
    }
  } catch (error) {
    console.error('Load trend data error:', error)
    ElMessage.error('加载趋势数据失败')
  }
}

// 加载分类数据
const loadCategoryData = async () => {
  try {
    const res = await transactionApi.getCategoryStatistics('expense')
    console.log('Category data response:', res)

    if (Array.isArray(res)) {
      if (categoryChart) {
        categoryChart.setOption({
          series: [
            {
              data: res.map(item => ({
                name: item.category,
                value: Number(item.amount) || 0
              }))
            }
          ]
        })
      }
    }
  } catch (error) {
    console.error('Load category data error:', error)
    ElMessage.error('加载分类数据失败')
  }
}

// 加载最近交易
const loadRecentTransactions = async () => {
  const res = await transactionApi.list()
  console.log('Recent transactions:', res)
  recentTransactions.value = res.records;
}

// 监听时间范围变化
watch(trendTimeRange, () => {
  loadTrendData()
})

watch(categoryTimeRange, () => {
  loadCategoryData()
})

// 监听窗口大小变化
const handleResize = () => {
  trendChart?.resize()
  categoryChart?.resize()
}

onMounted(async () => {
  initTrendChart()
  initCategoryChart()
  await Promise.all([
    loadStatistics(),
    loadTrendData(),
    loadCategoryData(),
    loadRecentTransactions()
  ])
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  trendChart?.dispose()
  categoryChart?.dispose()
})
</script>

<style scoped>
.dashboard-container {
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

@media (max-width: 768px) {
  .dashboard-container {
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