import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../stores/user'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/Home.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { requiresGuest: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue'),
    meta: { requiresGuest: true }
  },
  // 学习模块
  {
    path: '/study',
    name: 'Study',
    component: () => import('../views/Study.vue')
  },
  {
    path: '/study/material',
    name: 'MaterialList',
    component: () => import('../views/study/MaterialList.vue')
  },
  {
    path: '/study/material/:id',
    name: 'MaterialDetail',
    component: () => import('../views/study/MaterialDetail.vue')
  },
  {
    path: '/study/review',
    name: 'ReviewList',
    component: () => import('../views/study/ReviewList.vue')
  },
  {
    path: '/study/review/:id',
    name: 'ReviewDetail',
    component: () => import('../views/study/ReviewDetail.vue')
  },
  {
    path: '/study/buddy',
    name: 'BuddyList',
    component: () => import('../views/study/BuddyList.vue')
  },
  {
    path: '/study/buddy/:id',
    name: 'BuddyDetail',
    component: () => import('../views/study/BuddyDetail.vue')
  },
  // 其他模块（敬请期待）
  {
    path: '/sport',
    name: 'Sport',
    component: () => import('../views/ComingSoon.vue')
  },
  {
    path: '/entertainment',
    name: 'Entertainment',
    component: () => import('../views/ComingSoon.vue')
  },
  // 用户相关
  {
    path: '/user/profile',
    name: 'UserProfile',
    component: () => import('../views/UserProfile.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/user/address',
    name: 'UserAddress',
    component: () => import('../views/UserAddress.vue'),
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const isLoggedIn = userStore.isLoggedIn

  if (to.meta.requiresAuth && !isLoggedIn) {
    next({ name: 'Login', query: { redirect: to.fullPath } })
  } else if (to.meta.requiresGuest && isLoggedIn) {
    next({ name: 'Home' })
  } else {
    next()
  }
})

export default router
