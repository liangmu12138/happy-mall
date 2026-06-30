<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '../utils/request'

const router = useRouter()

const materialList = ref([])
const reviewList = ref([])
const buddyList = ref([])

onMounted(async () => {
  await Promise.all([
    fetchMaterials(),
    fetchReviews(),
    fetchBuddies()
  ])
})

const fetchMaterials = async () => {
  try {
    const res = await request.get('/api/study/material/list', {
      params: { pageNum: 1, pageSize: 4 }
    })
    materialList.value = res.data.records
  } catch (error) {
    console.error('获取资料失败', error)
  }
}

const fetchReviews = async () => {
  try {
    const res = await request.get('/api/study/review/list', {
      params: { pageNum: 1, pageSize: 3 }
    })
    reviewList.value = res.data.records
  } catch (error) {
    console.error('获取评价失败', error)
  }
}

const fetchBuddies = async () => {
  try {
    const res = await request.get('/api/study/buddy/list', {
      params: { pageNum: 1, pageSize: 3 }
    })
    buddyList.value = res.data.records
  } catch (error) {
    console.error('获取搭子失败', error)
  }
}

const goToStudy = () => router.push('/study')
const goToMaterial = () => router.push('/study/material')
const goToReview = () => router.push('/study/review')
const goToBuddy = () => router.push('/study/buddy')

const goToMaterialDetail = (id) => router.push(`/study/material/${id}`)
const goToReviewDetail = (id) => router.push(`/study/review/${id}`)
const goToBuddyDetail = (id) => router.push(`/study/buddy/${id}`)

const getPriceText = (price) => price > 0 ? `¥${price}` : '免费'
</script>

<template>
  <div class="home">
    <!-- 顶部欢迎 -->
    <div class="welcome-section">
      <div class="welcome-content">
        <h1>🎓 欢迎来到 Happy Campus</h1>
        <p>大学生学习资源共享平台</p>
        <div class="action-buttons">
          <el-button type="primary" size="large" @click="goToStudy">
            开始探索
          </el-button>
        </div>
      </div>
    </div>

    <!-- 功能入口 -->
    <div class="feature-nav">
      <div class="feature-item" @click="goToMaterial">
        <div class="icon">📖</div>
        <div class="title">资料市场</div>
        <div class="desc">免费/低价学习资料</div>
      </div>
      <div class="feature-item" @click="goToReview">
        <div class="icon">⭐</div>
        <div class="title">学长学姐说</div>
        <div class="desc">课程评价避坑</div>
      </div>
      <div class="feature-item" @click="goToBuddy">
        <div class="icon">👥</div>
        <div class="title">找学习搭子</div>
        <div class="desc">自习不再孤单</div>
      </div>
      <div class="feature-item coming-soon">
        <div class="icon">⚽</div>
        <div class="title">体育模块</div>
        <div class="desc">敬请期待</div>
      </div>
      <div class="feature-item coming-soon">
        <div class="icon">🎮</div>
        <div class="title">娱乐模块</div>
        <div class="desc">敬请期待</div>
      </div>
    </div>

    <!-- 最新资料 -->
    <section class="section" v-if="materialList.length > 0">
      <div class="section-header">
        <h2>📖 最新资料</h2>
        <el-button text @click="goToMaterial">查看更多</el-button>
      </div>
      <div class="material-grid">
        <div
          v-for="item in materialList"
          :key="item.id"
          class="material-card"
          @click="goToMaterialDetail(item.id)"
        >
          <div class="material-image">
            <img :src="item.images || 'https://via.placeholder.com/150'" :alt="item.title" />
            <span class="price-tag" :class="{ free: item.price === 0 }">
              {{ getPriceText(item.price) }}
            </span>
          </div>
          <div class="material-info">
            <h3>{{ item.title }}</h3>
            <div class="meta">
              <span>{{ item.category }}</span>
              <span>❤️ {{ item.likeCount }}</span>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 学长学姐说 -->
    <section class="section" v-if="reviewList.length > 0">
      <div class="section-header">
        <h2>⭐ 学长学姐说</h2>
        <el-button text @click="goToReview">查看更多</el-button>
      </div>
      <div class="review-list">
        <div
          v-for="item in reviewList"
          :key="item.id"
          class="review-card"
          @click="goToReviewDetail(item.id)"
        >
          <div class="review-header">
            <h3>{{ item.courseName }}</h3>
            <span class="rating">⭐ {{ item.rating }}</span>
          </div>
          <p class="content">{{ item.content }}</p>
          <div class="footer">
            <span v-if="item.teacherName">{{ item.teacherName }}老师</span>
            <span>❤️ {{ item.likeCount }}</span>
          </div>
        </div>
      </div>
    </section>

    <!-- 找学习搭子 -->
    <section class="section" v-if="buddyList.length > 0">
      <div class="section-header">
        <h2>👥 找学习搭子</h2>
        <el-button text @click="goToBuddy">查看更多</el-button>
      </div>
      <div class="buddy-list">
        <div
          v-for="item in buddyList"
          :key="item.id"
          class="buddy-card"
          @click="goToBuddyDetail(item.id)"
        >
          <div class="buddy-header">
            <el-tag size="small">{{ item.buddyType }}</el-tag>
            <span class="members">👥 {{ item.currentMembers }}/{{ item.maxMembers }}</span>
          </div>
          <h3>{{ item.title }}</h3>
          <div class="buddy-meta">
            <span v-if="item.location">📍 {{ item.location }}</span>
            <span v-if="item.timeInfo">⏰ {{ item.timeInfo }}</span>
          </div>
        </div>
      </div>
    </section>

    <!-- 空状态提示 -->
    <section class="section empty-section" v-if="materialList.length === 0 && reviewList.length === 0">
      <div class="empty-content">
        <div class="icon">📚</div>
        <h3>暂无内容</h3>
        <p>成为第一个分享学习资料的人吧！</p>
        <el-button type="primary" @click="goToStudy">去学习模块</el-button>
      </div>
    </section>
  </div>
</template>

<style scoped>
.home {
  max-width: 1200px;
  margin: 0 auto;
}

/* 欢迎区域 */
.welcome-section {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  padding: 60px 40px;
  margin-bottom: 30px;
  color: white;
  text-align: center;
}

.welcome-content h1 {
  margin-bottom: 15px;
  font-size: 36px;
}

.welcome-content p {
  font-size: 18px;
  opacity: 0.9;
  margin-bottom: 30px;
}

/* 功能入口 */
.feature-nav {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 20px;
  margin-bottom: 30px;
}

.feature-item {
  background: white;
  border-radius: 12px;
  padding: 25px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.feature-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.feature-item.coming-soon {
  opacity: 0.6;
  cursor: default;
}

.feature-item .icon {
  font-size: 40px;
  margin-bottom: 12px;
}

.feature-item .title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 5px;
}

.feature-item .desc {
  font-size: 12px;
  color: #999;
}

/* 通用部分 */
.section {
  background: white;
  border-radius: 12px;
  padding: 25px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h2 {
  margin: 0;
  font-size: 20px;
}

/* 资料网格 */
.material-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.material-card {
  border: 1px solid #eee;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
}

.material-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.material-image {
  position: relative;
  height: 140px;
  overflow: hidden;
}

.material-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.price-tag {
  position: absolute;
  top: 8px;
  right: 8px;
  padding: 3px 8px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: bold;
  background: #f56c6c;
  color: white;
}

.price-tag.free {
  background: #67c23a;
}

.material-info {
  padding: 12px;
}

.material-info h3 {
  margin: 0 0 8px 0;
  font-size: 13px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.material-info .meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #999;
}

/* 评价列表 */
.review-list {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.review-card {
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 15px;
  cursor: pointer;
  transition: all 0.3s;
}

.review-card:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.review-header h3 {
  margin: 0;
  font-size: 15px;
}

.rating {
  color: #f7ba2a;
  font-weight: bold;
}

.review-card .content {
  color: #666;
  font-size: 13px;
  line-height: 1.5;
  margin-bottom: 10px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.review-card .footer {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #999;
}

/* 搭子列表 */
.buddy-list {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.buddy-card {
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 15px;
  cursor: pointer;
  transition: all 0.3s;
}

.buddy-card:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.buddy-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.buddy-card h3 {
  margin: 0 0 10px 0;
  font-size: 15px;
}

.buddy-meta {
  display: flex;
  gap: 15px;
  font-size: 12px;
  color: #666;
}

/* 空状态 */
.empty-section {
  text-align: center;
  padding: 60px;
}

.empty-content .icon {
  font-size: 64px;
  margin-bottom: 20px;
}

.empty-content h3 {
  margin-bottom: 10px;
}

.empty-content p {
  color: #999;
  margin-bottom: 20px;
}
</style>
