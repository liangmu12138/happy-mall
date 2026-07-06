<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'
import request from '../../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'
import { formatFullDateTime } from '../../utils/date'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const buddy = ref(null)
const members = ref([])
const loading = ref(false)
const isOwner = ref(false)
const isMember = ref(false)

onMounted(async () => {
  await fetchBuddy()
})

const fetchBuddy = async () => {
  loading.value = true
  try {
    const res = await request.get(`/api/study/buddy/${route.params.id}`)
    buddy.value = res.data.buddy
    members.value = res.data.members || []

    if (userStore.isLoggedIn) {
      isOwner.value = buddy.value.userId === userStore.userInfo?.id
      isMember.value = members.value.some(m => m.id === userStore.userInfo?.id)
    }
  } catch (error) {
    console.error('获取搭子详情失败', error)
    ElMessage.error('搭子不存在')
    router.push('/study/buddy')
  } finally {
    loading.value = false
  }
}

const handleJoin = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    return
  }
  try {
    await request.post(`/api/study/buddy/${buddy.value.id}/join`)
    ElMessage.success('加入成功')
    fetchBuddy()
  } catch (error) {
    console.error('加入失败', error)
  }
}

const handleLeave = async () => {
  await ElMessageBox.confirm('确定要退出吗？', '提示', { type: 'warning' })
  try {
    await request.post(`/api/study/buddy/${buddy.value.id}/leave`)
    ElMessage.success('已退出')
    fetchBuddy()
  } catch (error) {
    console.error('退出失败', error)
  }
}

const handleDelete = async () => {
  await ElMessageBox.confirm('确定要删除这个搭子吗？', '提示', { type: 'warning' })
  try {
    await request.delete(`/api/study/buddy/${buddy.value.id}`)
    ElMessage.success('删除成功')
    router.push('/study/buddy')
  } catch (error) {
    console.error('删除失败', error)
  }
}

const isFull = computed(() => {
  return buddy.value && buddy.value.currentMembers >= buddy.value.maxMembers
})
</script>

<template>
  <div class="buddy-detail" v-loading="loading">
    <div class="detail-card" v-if="buddy">
      <div class="header">
        <div class="type-badge">
          <el-tag size="large">{{ buddy.buddyType }}</el-tag>
        </div>
        <div class="status" :class="{ full: isFull }">
          {{ isFull ? '已满员' : '招募中' }}
        </div>
      </div>

      <h1>{{ buddy.title }}</h1>

      <div class="info-section">
        <div class="info-item" v-if="buddy.location">
          <span class="label">📍 地点</span>
          <span class="value">{{ buddy.location }}</span>
        </div>
        <div class="info-item" v-if="buddy.timeInfo">
          <span class="label">⏰ 时间</span>
          <span class="value">{{ buddy.timeInfo }}</span>
        </div>
        <div class="info-item" v-if="buddy.target">
          <span class="label">🎯 目标</span>
          <span class="value">{{ buddy.target }}</span>
        </div>
      </div>

      <div class="description" v-if="buddy.description">
        <h3>详细描述</h3>
        <p>{{ buddy.description }}</p>
      </div>

      <div class="members-section">
        <h3>成员 ({{ buddy.currentMembers }}/{{ buddy.maxMembers }})</h3>
        <div class="members-list">
          <div
            v-for="member in members"
            :key="member.id"
            class="member-item"
          >
            <el-avatar :size="50" :src="member.avatar">
              {{ member.nickname?.charAt(0) || 'U' }}
            </el-avatar>
            <span class="name">{{ member.nickname }}</span>
            <el-tag v-if="member.id === buddy.userId" size="small" type="warning">发起人</el-tag>
          </div>
        </div>
      </div>

      <div class="publisher">
        <h3>发起人</h3>
        <div class="publisher-info">
          <el-avatar :size="60" :src="buddy.publisher?.avatar">
            {{ buddy.publisher?.nickname?.charAt(0) || 'U' }}
          </el-avatar>
          <div class="info">
            <span class="name">{{ buddy.publisher?.nickname }}</span>
            <span class="time">发布于 {{ formatFullDateTime(buddy.createTime) }}</span>
          </div>
        </div>
      </div>

      <div class="actions">
        <template v-if="isOwner">
          <el-button type="danger" size="large" @click="handleDelete">
            删除搭子
          </el-button>
        </template>
        <template v-else-if="isMember">
          <el-button type="warning" size="large" @click="handleLeave">
            退出搭子
          </el-button>
        </template>
        <template v-else>
          <el-button
            type="primary"
            size="large"
            :disabled="isFull"
            @click="handleJoin"
          >
            {{ isFull ? '已满员' : '加入搭子' }}
          </el-button>
        </template>
      </div>
    </div>

    <div class="back-btn">
      <el-button @click="router.push('/study/buddy')">返回列表</el-button>
    </div>
  </div>
</template>

<style scoped>
.buddy-detail {
  max-width: 800px;
  margin: 0 auto;
}

.detail-card {
  background: white;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.status {
  padding: 6px 15px;
  border-radius: 20px;
  background: #67c23a;
  color: white;
  font-size: 14px;
}

.status.full {
  background: #909399;
}

h1 {
  margin-bottom: 30px;
  font-size: 24px;
}

.info-section {
  display: flex;
  flex-wrap: wrap;
  gap: 30px;
  padding: 20px;
  background: #f9f9f9;
  border-radius: 8px;
  margin-bottom: 30px;
}

.info-item .label {
  display: block;
  color: #999;
  font-size: 13px;
  margin-bottom: 5px;
}

.info-item .value {
  font-size: 16px;
  color: #333;
}

.description {
  margin-bottom: 30px;
}

.description h3 {
  margin-bottom: 15px;
}

.description p {
  color: #666;
  line-height: 1.8;
}

.members-section {
  margin-bottom: 30px;
}

.members-section h3 {
  margin-bottom: 15px;
}

.members-list {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.member-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.member-item .name {
  font-size: 13px;
  color: #666;
}

.publisher {
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;
  margin-bottom: 30px;
}

.publisher h3 {
  margin-bottom: 15px;
}

.publisher-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.publisher-info .name {
  display: block;
  font-weight: 500;
}

.publisher-info .time {
  font-size: 13px;
  color: #999;
}

.actions {
  display: flex;
  justify-content: center;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.back-btn {
  margin-top: 20px;
}
</style>
