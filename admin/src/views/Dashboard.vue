<script setup>
import { ref, onMounted } from 'vue'
import request from '../utils/request'

const statistics = ref({
  totalOrders: 0,
  pendingPayment: 0,
  pendingShipment: 0,
  pendingReceive: 0,
  completed: 0,
  todayOrders: 0,
  todaySales: 0
})

const userStats = ref({
  totalUsers: 0,
  todayNewUsers: 0
})

onMounted(async () => {
  await fetchStatistics()
})

const fetchStatistics = async () => {
  try {
    const [orderRes, userRes] = await Promise.all([
      request.get('/api/admin/order/statistics'),
      request.get('/api/admin/user/statistics')
    ])
    statistics.value = orderRes.data
    userStats.value = userRes.data
  } catch (error) {
    console.error('获取统计失败', error)
  }
}
</script>

<template>
  <div class="dashboard">
    <h2>控制台</h2>

    <!-- 统计卡片 -->
    <div class="stat-cards">
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-info">
            <div class="stat-title">今日订单</div>
            <div class="stat-value">{{ statistics.todayOrders }}</div>
          </div>
          <el-icon class="stat-icon" :size="48" color="#409EFF">
            <Document />
          </el-icon>
        </div>
      </el-card>

      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-info">
            <div class="stat-title">今日销售额</div>
            <div class="stat-value">¥{{ statistics.todaySales?.toFixed(2) || '0.00' }}</div>
          </div>
          <el-icon class="stat-icon" :size="48" color="#67C23A">
            <Money />
          </el-icon>
        </div>
      </el-card>

      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-info">
            <div class="stat-title">待发货</div>
            <div class="stat-value">{{ statistics.pendingShipment }}</div>
          </div>
          <el-icon class="stat-icon" :size="48" color="#E6A23C">
            <Van />
          </el-icon>
        </div>
      </el-card>

      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-info">
            <div class="stat-title">总用户数</div>
            <div class="stat-value">{{ userStats.totalUsers }}</div>
          </div>
          <el-icon class="stat-icon" :size="48" color="#F56C6C">
            <User />
          </el-icon>
        </div>
      </el-card>
    </div>

    <!-- 订单状态统计 -->
    <el-card class="section-card">
      <template #header>
        <span>订单状态统计</span>
      </template>
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="status-item">
            <div class="status-value warning">{{ statistics.pendingPayment }}</div>
            <div class="status-label">待付款</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="status-item">
            <div class="status-value primary">{{ statistics.pendingShipment }}</div>
            <div class="status-label">待发货</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="status-item">
            <div class="status-value info">{{ statistics.pendingReceive }}</div>
            <div class="status-label">待收货</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="status-item">
            <div class="status-value success">{{ statistics.completed }}</div>
            <div class="status-label">已完成</div>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- 快捷操作 -->
    <el-card class="section-card">
      <template #header>
        <span>快捷操作</span>
      </template>
      <el-row :gutter="20">
        <el-col :span="6">
          <el-button type="primary" @click="$router.push('/product')">商品管理</el-button>
        </el-col>
        <el-col :span="6">
          <el-button type="warning" @click="$router.push('/order')">订单管理</el-button>
        </el-col>
        <el-col :span="6">
          <el-button type="success" @click="$router.push('/category')">分类管理</el-button>
        </el-col>
        <el-col :span="6">
          <el-button type="info" @click="$router.push('/user')">用户管理</el-button>
        </el-col>
      </el-row>
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

.status-item {
  text-align: center;
  padding: 20px;
}

.status-value {
  font-size: 36px;
  font-weight: bold;
  margin-bottom: 10px;
}

.status-value.warning { color: #E6A23C; }
.status-value.primary { color: #409EFF; }
.status-value.info { color: #909399; }
.status-value.success { color: #67C23A; }

.status-label {
  color: #999;
}
</style>
