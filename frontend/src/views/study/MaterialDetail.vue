<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'
import request from '../../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Star, Download, Delete } from '@element-plus/icons-vue'
import { formatFullDateTime } from '../../utils/date'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const material = ref(null)
const loading = ref(false)
const isOwner = ref(false)

onMounted(async () => {
  await fetchMaterial()
})

const fetchMaterial = async () => {
  loading.value = true
  try {
    const res = await request.get(`/api/study/material/${route.params.id}`)
    material.value = res.data
    // 检查是否是自己的资料
    if (userStore.isLoggedIn && material.value.userId === userStore.userInfo?.id) {
      isOwner.value = true
    }
  } catch (error) {
    console.error('获取资料失败', error)
    ElMessage.error('资料不存在')
    router.push('/study/material')
  } finally {
    loading.value = false
  }
}

const handleLike = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    return
  }
  try {
    await request.post(`/api/study/material/${material.value.id}/like`)
    ElMessage.success('点赞成功')
    material.value.likeCount++
  } catch (error) {
    console.error('点赞失败', error)
  }
}

const handleDownload = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    return
  }
  try {
    await request.post(`/api/study/material/${material.value.id}/download`)
    material.value.downloadCount++
    if (material.value.fileUrl) {
      window.open(material.value.fileUrl, '_blank')
    } else {
      ElMessage.info('暂无下载文件')
    }
  } catch (error) {
    console.error('下载失败', error)
  }
}

const handleDelete = async () => {
  await ElMessageBox.confirm('确定要删除这个资料吗？', '提示', { type: 'warning' })
  try {
    await request.delete(`/api/study/material/${material.value.id}`)
    ElMessage.success('删除成功')
    router.push('/study/material')
  } catch (error) {
    console.error('删除失败', error)
  }
}

const getPriceText = (price) => {
  return price > 0 ? `¥${price}` : '免费'
}
</script>

<template>
  <div class="material-detail" v-loading="loading">
    <div class="detail-card" v-if="material">
      <div class="header">
        <div class="title-section">
          <h1>{{ material.title }}</h1>
          <div class="meta">
            <el-tag>{{ material.category }}</el-tag>
            <span v-if="material.subject" class="subject">{{ material.subject }}</span>
            <span class="time">发布时间：{{ formatFullDateTime(material.createTime) }}</span>
          </div>
        </div>
        <div class="price-section">
          <span class="price" :class="{ free: material.price === 0 }">
            {{ getPriceText(material.price) }}
          </span>
        </div>
      </div>

      <div class="content" v-if="material.description">
        <h3>资料描述</h3>
        <p>{{ material.description }}</p>
      </div>

      <div class="image-section" v-if="material.images">
        <h3>预览图</h3>
        <img :src="material.images" class="preview-image" />
      </div>

      <div class="stats">
        <span>❤️ {{ material.likeCount }} 点赞</span>
        <span>📥 {{ material.downloadCount }} 下载</span>
      </div>

      <div class="actions">
        <el-button type="primary" size="large" @click="handleLike">
          <el-icon><Star /></el-icon> 点赞
        </el-button>
        <el-button type="success" size="large" @click="handleDownload">
          <el-icon><Download /></el-icon> 下载
        </el-button>
        <el-button v-if="isOwner" type="danger" size="large" @click="handleDelete">
          <el-icon><Delete /></el-icon> 删除
        </el-button>
      </div>
    </div>

    <div class="back-btn">
      <el-button @click="router.push('/study/material')">返回列表</el-button>
    </div>
  </div>
</template>

<style scoped>
.material-detail {
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
  align-items: flex-start;
  margin-bottom: 30px;
}

.title-section h1 {
  margin: 0 0 15px 0;
  font-size: 24px;
}

.meta {
  display: flex;
  align-items: center;
  gap: 15px;
  color: #999;
  font-size: 14px;
}

.price-section .price {
  font-size: 28px;
  font-weight: bold;
  color: #f56c6c;
}

.price-section .free {
  color: #67c23a;
}

.content {
  margin-bottom: 30px;
}

.content h3 {
  margin-bottom: 15px;
}

.content p {
  color: #666;
  line-height: 1.8;
}

.image-section {
  margin-bottom: 30px;
}

.preview-image {
  max-width: 100%;
  border-radius: 8px;
}

.stats {
  display: flex;
  gap: 30px;
  padding: 20px 0;
  border-top: 1px solid #eee;
  border-bottom: 1px solid #eee;
  margin-bottom: 30px;
  color: #666;
}

.actions {
  display: flex;
  gap: 15px;
}

.back-btn {
  margin-top: 20px;
}
</style>
