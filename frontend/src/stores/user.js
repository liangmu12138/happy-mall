import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import request from '../utils/request'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(null)

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => userInfo.value?.role === 1)

  // 登录
  async function login(username, password) {
    const res = await request.post('/api/user/login', { username, password })
    token.value = res.data.token
    userInfo.value = res.data.user
    localStorage.setItem('token', res.data.token)
    return res
  }

  // 注册（注册后不自动登录，跳转到登录页）
  async function register(userData) {
    const res = await request.post('/api/user/register', userData)
    return res
  }

  // 获取用户信息
  async function getUserInfo() {
    const res = await request.get('/api/user/info')
    userInfo.value = res.data
    return res
  }

  // 退出登录
  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
  }

  // 初始化：如果 token 存在，获取用户信息
  if (token.value) {
    getUserInfo().catch(() => {
      logout()
    })
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    isAdmin,
    login,
    register,
    getUserInfo,
    logout
  }
})
