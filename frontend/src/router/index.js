import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../stores/user'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue')
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('../views/Home.vue'),
    meta: { requiresAuth: true }
  },
  // 学习模块
  {
    path: '/study',
    name: 'Study',
    component: () => import('../views/Study.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/study/material',
    name: 'MaterialList',
    component: () => import('../views/study/MaterialList.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/study/material/:id',
    name: 'MaterialDetail',
    component: () => import('../views/study/MaterialDetail.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/study/review',
    name: 'ReviewList',
    component: () => import('../views/study/ReviewList.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/study/review/:id',
    name: 'ReviewDetail',
    component: () => import('../views/study/ReviewDetail.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/study/buddy',
    name: 'BuddyList',
    component: () => import('../views/study/BuddyList.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/study/buddy/:id',
    name: 'BuddyDetail',
    component: () => import('../views/study/BuddyDetail.vue'),
    meta: { requiresAuth: true }
  },
  // 其他模块（敬请期待）
  {
    path: '/sport',
    name: 'Sport',
    component: () => import('../views/ComingSoon.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/entertainment',
    name: 'Entertainment',
    component: () => import('../views/ComingSoon.vue'),
    meta: { requiresAuth: true }
  },
  // 用户相关
  {
    path: '/user/profile',
    name: 'UserProfile',
    component: () => import('../views/UserProfile.vue'),
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
