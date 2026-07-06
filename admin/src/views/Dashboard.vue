<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '../utils/request'

const router = useRouter()

const stats = ref({
  totalUsers: 0,
  totalMaterials: 0,
  totalReviews: 0,
  totalBuddies: 0
})

const loading = ref(false)

onMounted(async () => {
  await fetchStats()
})

const fetchStats = async () => {
  loading.value = true
  try {
    // 获取各项统计数据
    const [userRes, materialRes, reviewRes, buddyRes] = await Promise.all([
      request.get('/api/admin/user/list', { params: { pageNum: 1, pageSize: 1 } }),
      request.get('/api/study/material/list', { params: { pageNum: 1, pageSize: 1 } }),
      request.get('/api/study/review/list', { params: { pageNum: 1, pageSize: 1 } }),
      request.get('/api/study/buddy/list', { params: { pageNum: 1, pageSize: 1 } })
    ])
    stats.value = {
      totalUsers: userRes.data.total || 0,
      totalMaterials: materialRes.data.total || 0,
      totalReviews: reviewRes.data.total || 0,
      totalBuddies: buddyRes.data.total || 0
    }
  } catch (error) {
    console.error('获取统计失败', error)
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="dashboard" v-loading="loading">
    <h2>🎓 控制台</h2>

    <!-- 统计卡片 -->
    <div class="stat-cards">
      <el-card class="stat-card" @click="router.push('/user')">
        <div class="stat-content">
          <div class="stat-info">
            <div class="stat-title">总用户数</div>
            <div class="stat-value">{{ stats.totalUsers }}</div>
          </div>
          <el-icon class="stat-icon" :size="48" color="#409EFF">
            <User />
          </el-icon>
        </div>
      </el-card>

      <el-card class="stat-card" @click="router.push('/material')">
        <div class="stat-content">
          <div class="stat-info">
            <div class="stat-title">学习资料</div>
            <div class="stat-value">{{ stats.totalMaterials }}</div>
          </div>
          <el-icon class="stat-icon" :size="48" color="#67C23A">
            <Document />
          </el-icon>
        </div>
      </el-card>

      <el-card class="stat-card" @click="router.push('/review')">
        <div class="stat-content">
          <div class="stat-info">
            <div class="stat-title">课程评价</div>
            <div class="stat-value">{{ stats.totalReviews }}</div>
          </div>
          <el-icon class="stat-icon" :size="48" color="#E6A23C">
            <Star />
          </el-icon>
        </div>
      </el-card>

      <el-card class="stat-card" @click="router.push('/buddy')">
        <div class="stat-content">
          <div class="stat-info">
            <div class="stat-title">学习搭子</div>
            <div class="stat-value">{{ stats.totalBuddies }}</div>
          </div>
          <el-icon class="stat-icon" :size="48" color="#F56C6C">
            <UserFilled />
          </el-icon>
        </div>
      </el-card>
    </div>

    <!-- 快捷操作 -->
    <el-card class="section-card">
      <template #header>
        <span>快捷操作</span>
      </template>
      <el-row :gutter="20">
        <el-col :span="6">
          <el-button type="primary" @click="router.push('/material')">📖 资料管理</el-button>
        </el-col>
        <el-col :span="6">
          <el-button type="warning" @click="router.push('/review')">⭐ 评价管理</el-button>
        </el-col>
        <el-col :span="6">
          <el-button type="success" @click="router.push('/buddy')">👥 搭子管理</el-button>
        </el-col>
        <el-col :span="6">
          <el-button type="info" @click="router.push('/user')">👤 用户管理</el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 系统信息 -->
    <el-card class="section-card">
      <template #header>
        <span>系统信息</span>
      </template>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="系统名称">Happy Campus 学习平台</el-descriptions-item>
        <el-descriptions-item label="当前版本">v1.0.0</el-descriptions-item>
        <el-descriptions-item label="技术栈">Spring Boot + Vue 3</el-descriptions-item>
        <el-descriptions-item label="管理员">admin</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<style scoped>
.dashboard h2 {
  margin-bottom: 20px;
}

.stat-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card {
  cursor: pointer;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.stat-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stat-title {
  color: #999;
  margin-bottom: 10px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #333;
}

.section-card {
  margin-bottom: 20px;
}
</style>
