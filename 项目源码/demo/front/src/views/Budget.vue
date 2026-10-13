<template>
  <div class="budget-container">
    <!-- 操作按钮 -->
    <div class="operation-bar mb-4">
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增预算
      </el-button>
      <el-button type="danger" :disabled="!selectedRows.length" @click="handleBatchDelete">
        <el-icon><Delete /></el-icon>
        批量删除
      </el-button>
    </div>

    <!-- 预算列表 -->
    <el-card>
      <el-table
        v-loading="loading"
        :data="budgets"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="category" label="分类" />
        <el-table-column prop="amount" label="预算金额" width="150">
          <template #default="{ row }">
            {{ formatAmount(row.amount) }}
          </template>
        </el-table-column>
        <el-table-column prop="period" label="周期" width="100">
          <template #default="{ row }">
            {{ row.period === 'MONTHLY' ? '月度' : '年度' }}
          </template>
        </el-table-column>
        <el-table-column prop="startDate" label="开始日期" width="120">
          <template #default="{ row }">
            {{ formatDate(row.startDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="endDate" label="结束日期" width="120">
          <template #default="{ row }">
            {{ formatDate(row.endDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button type="danger" link @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '新增预算' : '编辑预算'"
      width="500px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="80px"
      >
        <el-form-item label="分类" prop="category">
          <el-select v-model="form.category" placeholder="选择分类">
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.name"
              :value="category.name"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="周期" prop="period">
          <el-select v-model="form.period" placeholder="选择预算周期">
            <el-option label="月度" value="MONTHLY" />
            <el-option label="年度" value="YEARLY" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="金额" prop="amount">
          <el-input-number
            v-model="form.amount"
            :min="0"
            :precision="2"
            :step="100"
            class="w-full"
          />
        </el-form-item>
        
        <el-form-item label="日期范围" prop="dateRange">
          <el-date-picker
            v-model="form.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            class="w-full"
          />
        </el-form-item>
        
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="请输入描述"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Delete } from '@element-plus/icons-vue'
import { budgetApi, categoryApi } from '@/api'

// 表格数据
const loading = ref(false)
const budgets = ref([])
const selectedRows = ref([])
const categories = ref([])

// 对话框相关
const dialogVisible = ref(false)
const dialogType = ref('add')
const formRef = ref(null)
const form = reactive({
  category: '',
  amount: 0,
  dateRange: [],
  description: '',
  period: 'MONTHLY'
})

// 表单验证规则
const rules = {
  category: [
    { required: true, message: '请选择分类', trigger: 'change' }
  ],
  amount: [
    { required: true, message: '请输入金额', trigger: 'blur' }
  ],
  dateRange: [
    { required: true, message: '请选择日期范围', trigger: 'change' }
  ],
  period: [
    { required: true, message: '请选择预算周期', trigger: 'change' }
  ]
}

// 格式化金额
const formatAmount = (amount) => {
  return new Intl.NumberFormat('zh-CN', {
    style: 'currency',
    currency: 'CNY'
  }).format(amount)
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN', {
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

// 加载预算列表
const loadBudgets = async () => {
  loading.value = true
  try {
    const response = await budgetApi.list()
    budgets.value = response // 直接使用 response，因为后端返回的就是数组
    console.log('Loaded budgets:', budgets.value) // 添加日志
  } catch (error) {
    console.error('Load budgets error:', error)
    ElMessage.error('加载预算列表失败')
  } finally {
    loading.value = false
  }
}

// 加载分类列表
const loadCategories = async () => {
  try {
    const response = await categoryApi.list()
    categories.value = response
  } catch (error) {
    console.error('Load categories error:', error)
    ElMessage.error('加载分类失败')
  }
}

// 表格选择变化
const handleSelectionChange = (rows) => {
  selectedRows.value = rows
}

// 新增预算
const handleAdd = () => {
  dialogType.value = 'add'
  form.category = ''
  form.amount = 0
  form.dateRange = []
  form.description = ''
  form.period = 'MONTHLY'
  dialogVisible.value = true
}

// 编辑预算
const handleEdit = (row) => {
  dialogType.value = 'edit'
  form.id = row.id
  form.category = row.category
  form.amount = row.amount
  form.dateRange = [row.startDate, row.endDate]
  form.description = row.description
  form.period = row.period
  dialogVisible.value = true
}

// 删除预算
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除这个预算吗？', '提示', {
      type: 'warning'
    })
    
    await budgetApi.delete(row.id)
    ElMessage.success('删除成功')
    loadBudgets()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Delete budget error:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 批量删除
const handleBatchDelete = async () => {
  if (!selectedRows.value.length) return
  
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedRows.value.length} 个预算吗？`,
      '提示',
      { type: 'warning' }
    )
    
    const ids = selectedRows.value.map(row => row.id)
    await Promise.all(ids.map(id => budgetApi.delete(id)))
    ElMessage.success('删除成功')
    loadBudgets()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Batch delete budgets error:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    const [startDate, endDate] = form.dateRange
    const submitData = {
      category: form.category,
      amount: form.amount,
      startDate: `${startDate}T00:00:00`,
      endDate: `${endDate}T23:59:59`,
      description: form.description,
      period: form.period
    }
    
    if (dialogType.value === 'add') {
      await budgetApi.create(submitData)
      ElMessage.success('添加成功')
    } else {
      await budgetApi.update(form.id, submitData)
      ElMessage.success('更新成功')
    }
    
    dialogVisible.value = false
    loadBudgets()
  } catch (error) {
    console.error('Submit form error:', error)
    ElMessage.error('操作失败')
  }
}

onMounted(async () => {
  await Promise.all([
    loadBudgets(),
    loadCategories()
  ])
})
</script>

<style scoped>
.budget-container {
  padding: 20px;
}

.mb-4 {
  margin-bottom: 20px;
}

.w-full {
  width: 100%;
}

.operation-bar {
  display: flex;
  gap: 10px;
}

@media (max-width: 768px) {
  .budget-container {
    padding: 10px;
  }
  
  .operation-bar {
    flex-wrap: wrap;
  }
}
</style> 