<template>
  <el-container class="layout-container">
    <el-aside :width="isCollapse ? '64px' : '200px'" class="aside">
      <div class="logo">
        <h1 v-show="!isCollapse">个人财务管理系统</h1>
        <h1 v-show="isCollapse">财</h1>
      </div>
      <el-menu
        :default-active="activeMenu"
        class="el-menu-vertical"
        :router="true"
        :collapse="isCollapse"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <el-menu-item index="/">
          <el-icon><HomeFilled /></el-icon>
          <template #title>仪表盘</template>
        </el-menu-item>
        
        <el-menu-item index="/transactions">
          <el-icon><Money /></el-icon>
          <template #title>交易管理</template>
        </el-menu-item>
        
        <el-menu-item index="/categories">
          <el-icon><Files /></el-icon>
          <template #title>分类管理</template>
        </el-menu-item>
        
        <el-menu-item index="/budgets">
          <el-icon><PieChart /></el-icon>
          <template #title>预算管理</template>
        </el-menu-item>
        
        <el-menu-item index="/reports">
          <el-icon><TrendCharts /></el-icon>
          <template #title>财务报表</template>
        </el-menu-item>
      </el-menu>
    </el-aside>
    
    <el-container>
      <el-header class="header">
        <div class="header-left">
          <el-button
            type="text"
            @click="toggleCollapse"
          >
            <el-icon :size="20">
              <component :is="isCollapse ? 'Expand' : 'Fold'" />
            </el-icon>
          </el-button>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentRoute }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        
        <div class="header-right">
          <el-dropdown trigger="click">
            <span class="user-dropdown">
              {{ userStore.username }}
              <el-icon><CaretBottom /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="handleProfile">个人信息</el-dropdown-item>
                <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      
      <el-main>
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessageBox } from 'element-plus'
import {
  HomeFilled,
  Money,
  Files,
  PieChart,
  TrendCharts,
  Expand,
  Fold,
  CaretBottom
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const isCollapse = ref(false)

const activeMenu = computed(() => route.path)
const currentRoute = computed(() => route.meta.title || '')

const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

const handleProfile = () => {
  router.push('/profile')
}

const handleLogout = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要退出登录吗？',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    await userStore.logout()
    router.push('/login')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Logout error:', error)
    }
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.aside {
  background-color: #304156;
  color: #fff;
  transition: width 0.3s;
  overflow: hidden;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #2b2f3a;
  overflow: hidden;
}

.logo h1 {
  color: #fff;
  font-size: 18px;
  margin: 0;
  white-space: nowrap;
}

.header {
  background-color: #fff;
  border-bottom: 1px solid #dcdfe6;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  height: 60px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-dropdown {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: #606266;
}

.el-menu-vertical:not(.el-menu--collapse) {
  width: 200px;
}

.el-menu {
  border-right: none;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

@media (max-width: 768px) {
  .aside {
    position: fixed;
    z-index: 1000;
    height: 100vh;
  }
  
  .el-main {
    margin-left: 64px;
  }
  
  .header {
    padding: 0 10px;
  }
  
  .header-left {
    gap: 10px;
  }
}
</style> 