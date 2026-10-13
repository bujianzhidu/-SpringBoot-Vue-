<template>
  <div class="category-container">
    <!-- 操作按钮 -->
    <div class="operation-bar mb-4">
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增分类
      </el-button>
      <el-button type="danger" :disabled="!selectedRows.length" @click="handleBatchDelete">
        <el-icon><Delete /></el-icon>
        批量删除
      </el-button>
    </div>

    <!-- 分类列表 -->
    <el-card>
      <el-table
        v-loading="loading"
        :data="categories"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="name" label="名称" />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.type === 'INCOME' ? 'success' : 'danger'">
              {{ row.type === 'INCOME' ? '收入' : '支出' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="sort" label="排序" width="100" sortable />
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
      :title="dialogType === 'add' ? '新增分类' : '编辑分类'"
      width="500px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="80px"
      >
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入分类名称" />
        </el-form-item>
        
        <el-form-item label="类型" prop="type">
          <el-radio-group v-model="form.type">
            <el-radio label="INCOME">收入</el-radio>
            <el-radio label="EXPENSE">支出</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="请输入描述"
          />
        </el-form-item>
        
        <el-form-item label="排序" prop="sort">
          <el-input-number
            v-model="form.sort"
            :min="0"
            :max="999"
            :step="1"
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
import { categoryApi } from '@/api'

// 表格数据
const loading = ref(false)
const categories = ref([])
const selectedRows = ref([])

// 对话框相关
const dialogVisible = ref(false)
const dialogType = ref('add')
const formRef = ref(null)
const form = reactive({
  name: '',
  type: 'EXPENSE',
  description: '',
  sort: 0
})

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入分类名称', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择类型', trigger: 'change' }
  ]
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

// 新增分类
const handleAdd = () => {
  dialogType.value = 'add'
  Object.keys(form).forEach(key => {
    if (key === 'type') {
      form[key] = 'EXPENSE'
    } else if (key === 'sort') {
      form[key] = 0
    } else {
      form[key] = ''
    }
  })
  dialogVisible.value = true
}

// 编辑分类
const handleEdit = (row) => {
  dialogType.value = 'edit'
  Object.keys(form).forEach(key => {
    form[key] = row[key]
  })
  dialogVisible.value = true
}

// 删除分类
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除这个分类吗？', '提示', {
      type: 'warning'
    })
    
    await categoryApi.delete(row.id)
    ElMessage.success('删除成功')
    loadCategories()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Delete category error:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 批量删除
const handleBatchDelete = async () => {
  if (!selectedRows.value.length) return
  
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedRows.value.length} 个分类吗？`,
      '提示',
      { type: 'warning' }
    )
    
    const ids = selectedRows.value.map(row => row.id)
    await Promise.all(ids.map(id => categoryApi.delete(id)))
    ElMessage.success('删除成功')
    loadCategories()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Batch delete categories error:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    if (dialogType.value === 'add') {
      await categoryApi.create(form)
      ElMessage.success('添加成功')
    } else {
      await categoryApi.update(form.id, form)
      ElMessage.success('更新成功')
    }
    
    dialogVisible.value = false
    loadCategories()
  } catch (error) {
    console.error('Submit form error:', error)
    ElMessage.error('操作失败')
  }
}

onMounted(() => {
  loadCategories()
})
</script>

<style scoped>
.category-container {
  padding: 20px;
}

.mb-4 {
  margin-bottom: 20px;
}

.operation-bar {
  display: flex;
  gap: 10px;
}

@media (max-width: 768px) {
  .category-container {
    padding: 10px;
  }
  
  .operation-bar {
    flex-wrap: wrap;
  }
}
</style> 