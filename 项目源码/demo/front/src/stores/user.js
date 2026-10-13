import { defineStore } from 'pinia'
import { userApi } from '@/api'
import router from '@/router'

export const useUserStore = defineStore('user', {
  state: () => {
    let userInfo = {}
    try {
      const storedUserInfo = localStorage.getItem('userInfo')
      if (storedUserInfo) {
        userInfo = JSON.parse(storedUserInfo)
      }
    } catch (error) {
      console.error('Error parsing userInfo from localStorage:', error)
      localStorage.removeItem('userInfo')
    }

    return {
      token: localStorage.getItem('token') || '',
      userInfo
    }
  },

  getters: {
    isLoggedIn: (state) => !!state.token,
    username: (state) => state.userInfo?.username || '',
    avatar: (state) => state.userInfo?.avatar || ''
  },

  actions: {
    async login(credentials) {
      try {
        const res = await userApi.login(credentials)
        console.log('Login response:', res)
        
        if (res.data.token) {
          const token = res.data.token;
          this.token = token
          this.userInfo = res.data.user || {}
          
          localStorage.setItem('token', token)
          localStorage.setItem('userInfo', JSON.stringify(res.user || {}))
          
          const redirect = router.currentRoute.value.query.redirect || '/'
          await router.push(redirect)
        } else {
          throw new Error('登录响应中没有 token')
        }
      } catch (error) {
        console.error('Login error:', error)
        throw error
      }
    },

    async register(userData) {
      try {
        const res = await userApi.register(userData)
        if (res.code !== 200) {
          throw new Error(res.message || '注册失败')
        }
      } catch (error) {
        throw error
      }
    },

    async logout() {
      try {
        await userApi.logout()
      } catch (error) {
        console.error('Logout error:', error)
      } finally {
        this.token = ''
        this.userInfo = {}
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        router.push('/login')
      }
    },

    async updateUserInfo(userInfo) {
      try {
        const res = await userApi.updateInfo(userInfo)
        if (res.code === 200 && res.data) {
          this.userInfo = res.data
          localStorage.setItem('userInfo', JSON.stringify(res.data))
        } else {
          throw new Error(res.message || '更新失败')
        }
      } catch (error) {
        throw error
      }
    },

    async updatePassword(passwordData) {
      try {
        const res = await userApi.updatePassword(passwordData)
        if (res.code !== 200) {
          throw new Error(res.message || '修改密码失败')
        }
      } catch (error) {
        throw error
      }
    }
  }
}) 