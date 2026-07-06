import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { requiresGuest: true }
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('../layout/index.vue'),
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/Dashboard.vue'),
        meta: { title: '控制台' }
      },
      {
        path: 'material',
        name: 'MaterialManage',
        component: () => import('../views/MaterialManage.vue'),
        meta: { title: '资料管理' }
      },
      {
        path: 'review',
        name: 'ReviewManage',
        component: () => import('../views/ReviewManage.vue'),
        meta: { title: '评价管理' }
      },
      {
        path: 'buddy',
        name: 'BuddyManage',
        component: () => import('../views/BuddyManage.vue'),
        meta: { title: '搭子管理' }
      },
      {
        path: 'user',
        name: 'User',
        component: () => import('../views/User.vue'),
        meta: { title: '用户管理' }
      },
      {
        path: 'admin',
        name: 'AdminManage',
        component: () => import('../views/AdminManage.vue'),
        meta: { title: '管理员管理' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫 - 延迟加载 store 避免循环依赖
router.beforeEach((to, from, next) => {
  // 动态导入避免初始化时循环依赖
  import('../stores/user.js').then(({ useUserStore }) => {
    const userStore = useUserStore()
    const isLoggedIn = userStore.isLoggedIn
    const isAdmin = userStore.isAdmin

    if (to.meta.requiresAuth && (!isLoggedIn || !isAdmin)) {
      next({ name: 'Login' })
    } else if (to.meta.requiresGuest && isLoggedIn) {
      next({ name: 'Dashboard' })
    } else {
      next()
    }
  }).catch(() => {
    next()
  })
})

export default router
