import request from '@/utils/request'

// 用户相关接口
export const userApi = {
  // 登录
  login: (data) => request.post('/api/users/login', data),
  // 注册
  register: (data) => request.post('/api/users/register', data),
  // 登出
  logout: () => request.post('/api/users/logout'),
  // 获取用户信息
  getInfo: () => request.get('/api/users/info'),
  // 更新用户信息
  updateInfo: (data) => request.put('/api/users/info', data),
  // 修改密码
  updatePassword: (data) => request.put('/api/users/password', data)
}

// 交易相关接口
export const transactionApi = {
  // 获取交易列表
  list: (params) => request.get('/api/transactions', params),
  // 创建交易
  create: (data) => request.post('/api/transactions', data),
  // 更新交易
  update: (id, data) => request.put(`/api/transactions/${id}`, data),
  // 删除交易
  delete: (id) => request.delete(`/api/transactions/${id}`),
  // 获取统计数据
  getStatistics: (startDate, endDate) => request.get('/api/transactions/statistics', {
    params: { startDate, endDate }
  }),
  // 获取分类统计
  getCategoryStatistics: (type, startDate, endDate) => request.get('/api/transactions/category-statistics', {
    params: { type, startDate, endDate }
  }),
  // 导出交易数据
  export: (params) => request.get('/api/transactions/export', {
    params,
    responseType: 'blob'
  })
}

// 分类相关接口
export const categoryApi = {
  // 获取所有分类
  list: () => request.get('/api/categories'),
  // 创建分类
  create: (data) => request.post('/api/categories', data),
  // 更新分类
  update: (id, data) => request.put(`/api/categories/${id}`, data),
  // 删除分类
  delete: (id) => request.delete(`/api/categories/${id}`)
}

// 预算相关接口
export const budgetApi = {
  // 获取所有预算
  list: () => request.get('/api/budgets'),
  // 创建预算
  create: (data) => request.post('/api/budgets', data),
  // 更新预算
  update: (id, data) => request.put(`/api/budgets/${id}`, data),
  // 删除预算
  delete: (id) => request.delete(`/api/budgets/${id}`),
  // 获取预算使用情况
  getUsage: (id) => request.get(`/api/budgets/${id}/usage`),
  // 获取所有预算使用情况
  getAllUsage: () => request.get('/api/budgets/usage'),
  // 检查预算是否超支
  isOverBudget: (id) => request.get(`/api/budgets/${id}/over-budget`),
  // 获取预算剩余金额
  getRemaining: (id) => request.get(`/api/budgets/${id}/remaining`)
}

// 提醒相关接口
export const reminderApi = {
  // 获取待处理的提醒列表
  getPendingReminders: () => request.get('/api/reminders'),
  // 完成提醒
  completeReminder: (id) => request.post(`/api/reminders/${id}/complete`),
  // 取消提醒
  cancelReminder: (id) => request.post(`/api/reminders/${id}/cancel`),
  // 创建提醒
  createReminder: (data) => request.post('/api/reminders', data),
  // 更新提醒
  updateReminder: (id, data) => request.put(`/api/reminders/${id}`, data),
  // 删除提醒
  deleteReminder: (id) => request.delete(`/api/reminders/${id}`)
}

// 文件上传相关接口
export const fileApi = {
  // 上传用户头像
  uploadAvatar: (userId, file) => {
    const formData = new FormData()
    formData.append('file', file)
    return request.post(`/api/files/avatar/${userId}`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },
  // 上传交易凭证图片
  uploadTransactionImage: (transactionId, file) => {
    const formData = new FormData()
    formData.append('file', file)
    return request.post(`/api/files/transaction/${transactionId}`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },
  // 删除文件
  deleteFile: (fileUrl) => request.delete('/api/files', { params: { fileUrl } })
}
