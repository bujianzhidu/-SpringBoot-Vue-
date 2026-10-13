import { auth } from './auth.js';

// 基础 URL
const BASE_URL = '/api';

// 请求拦截器
async function request(url, options = {}) {
    // 添加 token 到请求头
    const token = auth.getToken();
    if (token) {
        options.headers = {
            ...options.headers,
            'Authorization': `Bearer ${token}`
        };
    }

    try {
        const response = await fetch(`${BASE_URL}${url}`, {
            ...options,
            headers: {
                'Content-Type': 'application/json',
                ...options.headers
            }
        });

        // 如果返回 401，说明 token 失效，需要重新登录
        if (response.status === 401) {
            auth.removeToken();
            window.location.href = '/login.html';
            return;
        }

        const data = await response.json();
        return data;
    } catch (error) {
        console.error('Request failed:', error);
        throw error;
    }
}

// 导出请求方法
export const http = {
    get(url) {
        return request(url, { method: 'GET' });
    },

    post(url, data) {
        return request(url, {
            method: 'POST',
            body: JSON.stringify(data)
        });
    },

    put(url, data) {
        return request(url, {
            method: 'PUT',
            body: JSON.stringify(data)
        });
    },

    delete(url) {
        return request(url, { method: 'DELETE' });
    }
}; 