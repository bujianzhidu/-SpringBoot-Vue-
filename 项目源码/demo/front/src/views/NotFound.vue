<template>
  <div class="not-found-container">
    <div class="content">
      <h1>404</h1>
      <h2>页面未找到</h2>
      <p>抱歉，您访问的页面不存在或已被移除</p>
      <el-button type="primary" @click="$router.push('/')">
        返回首页
      </el-button>
    </div>
  </div>
</template>

<style scoped>
.not-found-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f7fa;
}

.content {
  text-align: center;
}

h1 {
  font-size: 120px;
  margin: 0;
  color: var(--el-color-primary);
  line-height: 1.2;
}

h2 {
  font-size: 30px;
  margin: 20px 0;
  color: #303133;
}

p {
  font-size: 16px;
  color: #606266;
  margin-bottom: 30px;
}

@media (max-width: 768px) {
  h1 {
    font-size: 80px;
  }
  
  h2 {
    font-size: 24px;
  }
  
  p {
    font-size: 14px;
  }
}
</style> 