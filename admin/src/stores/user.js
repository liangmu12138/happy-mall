import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import request from '../utils/request'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('admin_token') || '')
  const userInfo = ref(null)

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => userInfo.value?.role === 1)

  async function login(username, password) {
    const res = await request.post('/api/user/login', { username, password })
    if (res.data.user.role !== 1) {
      throw new Error('您没有管理员权限')
    }
    token.value = res.data.token
    userInfo.value = res.data.user
    localStorage.setItem('admin_token', res.data.token)
    return res
  }

  async function getUserInfo() {
    const res = await request.get('/api/user/info')
    userInfo.value = res.data
    if (res.data.role !== 1) {
      throw new Error('您没有管理员权限')
    }
    return res
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('admin_token')
  }

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
    getUserInfo,
    logout
  }
})
