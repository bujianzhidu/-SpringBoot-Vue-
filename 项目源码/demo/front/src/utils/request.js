import axios from 'axios'
import {ElMessage} from 'element-plus'
import router from '@/router'

// 创建 axios 实例
const request = axios.create({
    baseURL: 'http://localhost:8081',
    timeout: 15000
})

// 请求拦截器
request.interceptors.request.use(
    config => {
        // 从 localStorage 获取 token
        const token = localStorage.getItem('token')
        console.log('Current token:', token)
        
        if (token) {
            // 确保 token 格式正确
            config.headers['Authorization'] = token.startsWith('Bearer ') ? token : `Bearer ${token}`
            console.log('Request headers:', config.headers)
        }
        
        return config
    },
    error => {
        console.error('Request error:', error)
        return Promise.reject(error)
    }
)

// 响应拦截器
request.interceptors.response.use(
    response => {
        // 如果是文件下载，直接返回
        if (response.config.responseType === 'blob') {
            return response
        }

        // 直接返回响应数据
        return response.data
    },
    error => {
        console.error('Response error:', error)
        const {response} = error

        if (response) {
            console.log('Error response:', {
                status: response.status,
                data: response.data,
                headers: response.headers
            })
            
            switch (response.status) {
                case 401:
                    // 未授权，清除 token 并跳转到登录页
                    localStorage.removeItem('token')
                    localStorage.removeItem('userInfo')
                    router.push('/login')
                    ElMessage.error('登录已过期，请重新登录')
                    break
                case 403:
                    ElMessage.error('没有权限访问该资源')
                    break
                case 404:
                    ElMessage.error('请求的资源不存在')
                    break
                case 500:
                    ElMessage.error('服务器错误，请稍后重试')
                    break
                default:
                    ElMessage.error(response.data?.message || '请求失败')
            }
        } else {
            ElMessage.error('网络错误，请检查网络连接')
        }

        return Promise.reject(error)
    }
)

export default request 