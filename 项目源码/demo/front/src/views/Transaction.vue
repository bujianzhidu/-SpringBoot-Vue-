<template>
  <div class="transaction-container">
    <!-- 搜索和过滤 -->


    <!-- 操作按钮 -->
    <div class="operation-bar mb-4">
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增交易
      </el-button>
      <el-button type="danger" :disabled="!selectedRows.length" @click="handleBatchDelete">
        <el-icon><Delete /></el-icon>
        批量删除
      </el-button>
      <el-button type="success" @click="handleExport">
        <el-icon><Download /></el-icon>
        导出数据
      </el-button>
    </div>

    <!-- 交易列表 -->
    <el-card>
      <el-table
        v-loading="loading"
        :data="transactions"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="transactionDate" label="日期" width="180" sortable>
          <template #default="{ row }">
            {{ formatDate(row.transactionDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.type === 'INCOME' ? 'success' : 'danger'">
              {{ row.type === 'INCOME' ? '收入' : '支出' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="category" label="分类" width="120" />
        <el-table-column prop="amount" label="金额" width="120" sortable>
          <template #default="{ row }">
            <span :class="row.type === 'INCOME' ? 'income' : 'expense'">
              {{ row.type === 'INCOME' ? '+' : '-' }}{{ formatAmount(row.amount) }}
            </span>
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

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '新增交易' : '编辑交易'"
      width="500px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="80px"
      >
        <el-form-item label="类型" prop="type">
          <el-radio-group v-model="form.type">
            <el-radio label="INCOME">收入</el-radio>
            <el-radio label="EXPENSE">支出</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="选择分类">
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
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
        
        <el-form-item label="日期" prop="date">
          <el-date-picker
            v-model="form.date"
            type="date"
            placeholder="选择日期"
            value-format="YYYY-MM-DDTHH:mm:ss"
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
import {onMounted, reactive, ref} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {Delete, Download, Plus, Refresh, Search} from '@element-plus/icons-vue'
import {categoryApi, transactionApi} from '@/api'

// 搜索表单
const searchForm = reactive({
  type: '',
  categoryId: '',
  dateRange: [],
  minAmount: null,
  maxAmount: null
})

// 分类列表
const categories = ref([])

// 表格数据
const loading = ref(false)
const transactions = ref([])
const selectedRows = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 对话框相关
const dialogVisible = ref(false)
const dialogType = ref('add')
const formRef = ref(null)
const form = reactive({
  type: 'EXPENSE',
  categoryId: '',
  amount: 0,
  date: '',
  description: ''
})

// 表单验证规则
const rules = {
  type: [
    { required: true, message: '请选择类型', trigger: 'change' }
  ],
  categoryId: [
    { required: true, message: '请选择分类', trigger: 'change' }
  ],
  amount: [
    { required: true, message: '请输入金额', trigger: 'blur' }
  ],
  date: [
    { required: true, message: '请选择日期', trigger: 'change' }
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

// 加载分类列表
const loadCategories = async () => {
  try {
    categories.value = await categoryApi.list()
  } catch (error) {
    console.error('Load categories error:', error)
    ElMessage.error('加载分类失败')
  }
}

// 加载交易列表
const loadTransactions = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      type: searchForm.type,
      categoryId: searchForm.categoryId,
      startDate: searchForm.dateRange?.[0],
      endDate: searchForm.dateRange?.[1],
      minAmount: searchForm.minAmount,
      maxAmount: searchForm.maxAmount
    }

    const response = await transactionApi.list(params)
    console.log(response.records)
    transactions.value = response.records || []
    total.value = response.total || 0
  } catch (error) {
    console.error('Load transactions error:', error)
    ElMessage.error('加载交易记录失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  loadTransactions()
}

// 重置搜索
const resetSearch = () => {
  Object.keys(searchForm).forEach(key => {
    if (Array.isArray(searchForm[key])) {
      searchForm[key] = []
    } else {
      searchForm[key] = ''
    }
  })
  handleSearch()
}

// 表格选择变化
const handleSelectionChange = (rows) => {
  selectedRows.value = rows
}

// 新增交易
const handleAdd = () => {
  dialogType.value = 'add'
  Object.keys(form).forEach(key => {
    if (key === 'type') {
      form[key] = 'EXPENSE'
    } else if (key === 'date') {
      form[key] = new Date().toISOString().split('T')[0]
    } else {
      form[key] = ''
    }
  })
  dialogVisible.value = true
}

// 编辑交易
const handleEdit = (row) => {
  dialogType.value = 'edit'
  form.id = row.id
  form.type = row.type
  form.categoryId = row.categoryId
  form.amount = row.amount
  form.date = row.transactionDate
  form.description = row.description
  dialogVisible.value = true
}

// 删除交易
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除这条交易记录吗？', '提示', {
      type: 'warning'
    })
    
    await transactionApi.delete(row.id)
    ElMessage.success('删除成功')
    await loadTransactions()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Delete transaction error:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 批量删除
const handleBatchDelete = async () => {
  if (!selectedRows.value.length) return
  
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedRows.value.length} 条交易记录吗？`,
      '提示',
      { type: 'warning' }
    )
    
    const ids = selectedRows.value.map(row => row.id)
    await Promise.all(ids.map(id => transactionApi.delete(id)))
    ElMessage.success('删除成功')
    loadTransactions()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Batch delete transactions error:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 导出数据
const handleExport = async () => {
  try {
    const response = await transactionApi.exportPdf()
    const blob = new Blob([response], { type: 'application/pdf' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = '交易记录.pdf'
    link.click()
    window.URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (error) {
    console.error('Export transactions error:', error)
    ElMessage.error('导出失败')
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    const submitData = {
      type: form.type,
      categoryId: form.categoryId,
      amount: form.amount,
      transactionDate: form.date,
      description: form.description
    }
    
    if (dialogType.value === 'add') {
      await transactionApi.create(submitData)
      ElMessage.success('添加成功')
    } else {
      await transactionApi.update(form.id, submitData)
      ElMessage.success('更新成功')
    }
    
    dialogVisible.value = false
    await loadTransactions()
  } catch (error) {
    console.error('Submit form error:', error)
    ElMessage.error('操作失败')
  }
}

// 分页相关
const handleSizeChange = (val) => {
  pageSize.value = val
  loadTransactions()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  loadTransactions()
}

onMounted(async () => {
  await Promise.all([
    loadCategories(),
    loadTransactions()
  ])
})
</script>

<style scoped>
.transaction-container {
  padding: 20px;
}

.mb-4 {
  margin-bottom: 20px;
}

.mx-2 {
  margin: 0 8px;
}

.w-full {
  width: 100%;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.amount-input {
  width: 120px;
}

.operation-bar {
  display: flex;
  gap: 10px;
}

.income {
  color: #67C23A;
}

.expense {
  color: #F56C6C;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

@media (max-width: 768px) {
  .transaction-container {
    padding: 10px;
  }
  
  .search-form {
    flex-direction: column;
  }
  
  .amount-input {
    width: 100%;
  }
  
  .operation-bar {
    flex-wrap: wrap;
  }
}
</style> 