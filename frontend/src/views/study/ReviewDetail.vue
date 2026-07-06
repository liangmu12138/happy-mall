<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'
import request from '../../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'
import { formatFullDateTime } from '../../utils/date'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const review = ref(null)
const loading = ref(false)
const isOwner = ref(false)

onMounted(async () => {
  await fetchReview()
})

const fetchReview = async () => {
  loading.value = true
  try {
    const res = await request.get(`/api/study/review/${route.params.id}`)
    review.value = res.data
    if (userStore.isLoggedIn && review.value.userId === userStore.userInfo?.id) {
      isOwner.value = true
    }
  } catch (error) {
    console.error('获取评价失败', error)
    ElMessage.error('评价不存在')
    router.push('/study/review')
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
    await request.post(`/api/study/review/${review.value.id}/like`)
    ElMessage.success('点赞成功')
    review.value.likeCount++
  } catch (error) {
    console.error('点赞失败', error)
  }
}

const handleDelete = async () => {
  await ElMessageBox.confirm('确定要删除这个评价吗？', '提示', { type: 'warning' })
  try {
    await request.delete(`/api/study/review/${review.value.id}`)
    ElMessage.success('删除成功')
    router.push('/study/review')
  } catch (error) {
    console.error('删除失败', error)
  }
}
</script>

<template>
  <div class="review-detail" v-loading="loading">
    <div class="detail-card" v-if="review">
      <div class="header">
        <div class="course-info">
          <h1>{{ review.courseName }}</h1>
          <div class="teacher-school">
            <span v-if="review.teacherName">{{ review.teacherName }}老师</span>
            <span v-if="review.school">· {{ review.school }}</span>
          </div>
        </div>
        <div class="rating-badge">
          <span class="score">{{ review.rating }}</span>
          <span class="label">评分</span>
        </div>
      </div>

      <div class="rating-details">
        <div class="detail-item">
          <span class="label">课程难度</span>
          <el-rate v-model="review.difficulty" disabled :max="5" />
        </div>
        <div class="detail-item">
          <span class="label">考试难度</span>
          <el-rate v-model="review.examDifficulty" disabled :max="5" />
        </div>
        <div class="detail-item">
          <span class="label">给分情况</span>
          <el-rate v-model="review.gradeScore" disabled :max="5" />
        </div>
      </div>

      <div class="content-section">
        <h3>评价内容</h3>
        <p>{{ review.content }}</p>
      </div>

      <div class="tips-section" v-if="review.tips">
        <h3>💡 学习建议</h3>
        <p>{{ review.tips }}</p>
      </div>

      <div class="footer">
        <div class="stats">
          <el-button text @click="handleLike">
            ❤️ {{ review.likeCount }} 点赞
          </el-button>
        </div>
        <div class="time">发布时间：{{ formatFullDateTime(review.createTime) }}</div>
      </div>

      <div class="actions" v-if="isOwner">
        <el-button type="danger" @click="handleDelete">
          <el-icon><Delete /></el-icon> 删除
        </el-button>
      </div>
    </div>

    <div class="back-btn">
      <el-button @click="router.push('/study/review')">返回列表</el-button>
    </div>
  </div>
</template>

<style scoped>
.review-detail {
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

.course-info h1 {
  margin: 0 0 10px 0;
  font-size: 24px;
}

.teacher-school {
  color: #666;
  font-size: 16px;
}

.rating-badge {
  text-align: center;
  padding: 15px 25px;
  background: linear-gradient(135deg, #f7ba2a 0%, #f89d1a 100%);
  border-radius: 12px;
  color: white;
}

.rating-badge .score {
  display: block;
  font-size: 36px;
  font-weight: bold;
}

.rating-badge .label {
  font-size: 12px;
}

.rating-details {
  display: flex;
  justify-content: space-around;
  padding: 25px;
  background: #f9f9f9;
  border-radius: 8px;
  margin-bottom: 30px;
}

.detail-item {
  text-align: center;
}

.detail-item .label {
  display: block;
  color: #666;
  margin-bottom: 10px;
  font-size: 14px;
}

.content-section, .tips-section {
  margin-bottom: 30px;
}

.content-section h3, .tips-section h3 {
  margin-bottom: 15px;
}

.content-section p, .tips-section p {
  color: #666;
  line-height: 1.8;
  font-size: 15px;
}

.tips-section {
  padding: 20px;
  background: #f0f9eb;
  border-radius: 8px;
}

.footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.footer .time {
  color: #999;
  font-size: 14px;
}

.actions {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.back-btn {
  margin-top: 20px;
}
</style>
