// Token 管理
const TOKEN_KEY = 'auth_token';

export const auth = {
    // 保存 token
    setToken(token) {
        localStorage.setItem(TOKEN_KEY, token);
    },

    // 获取 token
    getToken() {
        return localStorage.getItem(TOKEN_KEY);
    },

    // 移除 token
    removeToken() {
        localStorage.removeItem(TOKEN_KEY);
    },

    // 检查是否已登录
    isAuthenticated() {
        return !!this.getToken();
    }
}; 