<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '../utils/request'
import { formatRelativeTime } from '../utils/date'

const router = useRouter()

const activeTab = ref('material')
const materialList = ref([])
const reviewList = ref([])
const buddyList = ref([])
const loading = ref(false)

const materialCategory = ref('')
const buddyType = ref('')

onMounted(() => {
  fetchMaterials()
  fetchReviews()
  fetchBuddies()
})

const fetchMaterials = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/study/material/list', {
      params: { pageNum: 1, pageSize: 10, category: materialCategory.value || undefined }
    })
    materialList.value = res.data.records
  } catch (error) {
    console.error('获取资料失败', error)
  } finally {
    loading.value = false
  }
}

const fetchReviews = async () => {
  try {
    const res = await request.get('/api/study/review/list', {
      params: { pageNum: 1, pageSize: 5 }
    })
    reviewList.value = res.data.records
  } catch (error) {
    console.error('获取评价失败', error)
  }
}

const fetchBuddies = async () => {
  try {
    const res = await request.get('/api/study/buddy/list', {
      params: { pageNum: 1, pageSize: 5, buddyType: buddyType.value || undefined }
    })
    buddyList.value = res.data.records
  } catch (error) {
    console.error('获取搭子失败', error)
  }
}

const goToMaterialList = () => {
  router.push('/study/material')
}

const goToReviewList = () => {
  router.push('/study/review')
}

const goToBuddyList = () => {
  router.push('/study/buddy')
}

const goToMaterialDetail = (id) => {
  router.push(`/study/material/${id}`)
}

const goToReviewDetail = (id) => {
  router.push(`/study/review/${id}`)
}

const goToBuddyDetail = (id) => {
  router.push(`/study/buddy/${id}`)
}

const getPriceText = (price) => {
  return price > 0 ? `¥${price}` : '免费'
}

const getPriceClass = (price) => {
  return price > 0 ? 'price-paid' : 'price-free'
}
</script>

<template>
  <div class="study-page">
    <!-- 顶部分类 -->
    <div class="category-nav">
      <div class="category-item" @click="activeTab = 'material'">
        <div class="icon">📖</div>
        <div class="title">资料市场</div>
        <div class="desc">免费/低价学习资料</div>
      </div>
      <div class="category-item" @click="activeTab = 'review'">
        <div class="icon">⭐</div>
        <div class="title">学长学姐说</div>
        <div class="desc">课程评价避坑</div>
      </div>
      <div class="category-item" @click="activeTab = 'buddy'">
        <div class="icon">👥</div>
        <div class="title">找学习搭子</div>
        <div class="desc">自习不再孤单</div>
      </div>
    </div>

    <!-- 资料市场 -->
    <section class="section" v-if="activeTab === 'material'">
      <div class="section-header">
        <h2>📖 资料市场</h2>
        <el-button text @click="goToMaterialList">查看更多</el-button>
      </div>

      <div class="filter-bar">
        <el-radio-group v-model="materialCategory" @change="fetchMaterials" size="small">
          <el-radio-button label="">全部</el-radio-button>
          <el-radio-button label="教材">教材</el-radio-button>
          <el-radio-button label="笔记">笔记</el-radio-button>
          <el-radio-button label="考研">考研</el-radio-button>
          <el-radio-button label="考级">考级</el-radio-button>
          <el-radio-button label="课件">课件</el-radio-button>
        </el-radio-group>
      </div>

      <div class="material-grid" v-loading="loading">
        <div
          v-for="item in materialList"
          :key="item.id"
          class="material-card"
          @click="goToMaterialDetail(item.id)"
        >
          <div class="material-image">
            <img :src="item.images || 'https://via.placeholder.com/150'" :alt="item.title" />
            <span class="price-tag" :class="getPriceClass(item.price)">
              {{ getPriceText(item.price) }}
            </span>
          </div>
          <div class="material-info">
            <h3 class="title">{{ item.title }}</h3>
            <div class="meta">
              <span class="category">{{ item.category }}</span>
              <span class="subject" v-if="item.subject">{{ item.subject }}</span>
            </div>
            <div class="stats">
              <span>❤️ {{ item.likeCount }}</span>
              <span>📥 {{ item.downloadCount }}</span>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 学长学姐说 -->
    <section class="section" v-if="activeTab === 'review'">
      <div class="section-header">
        <h2>⭐ 学长学姐说</h2>
        <el-button text @click="goToReviewList">查看更多</el-button>
      </div>

      <div class="review-list">
        <div
          v-for="item in reviewList"
          :key="item.id"
          class="review-card"
          @click="goToReviewDetail(item.id)"
        >
          <div class="review-header">
            <div class="course-info">
              <h3>{{ item.courseName }}</h3>
              <span class="teacher" v-if="item.teacherName">{{ item.teacherName }}老师</span>
            </div>
            <div class="rating">
              <span class="stars">⭐ {{ item.rating }}</span>
            </div>
          </div>
          <div class="review-content">{{ item.content }}</div>
          <div class="review-meta">
            <span class="tips" v-if="item.tips">💡 {{ item.tips }}</span>
          </div>
          <div class="review-footer">
            <span>❤️ {{ item.likeCount }}</span>
            <span class="time">{{ formatRelativeTime(item.createTime) }}</span>
          </div>
        </div>
      </div>
    </section>

    <!-- 找学习搭子 -->
    <section class="section" v-if="activeTab === 'buddy'">
      <div class="section-header">
        <h2>👥 找学习搭子</h2>
        <el-button text @click="goToBuddyList">查看更多</el-button>
      </div>

      <div class="filter-bar">
        <el-radio-group v-model="buddyType" @change="fetchBuddies" size="small">
          <el-radio-button label="">全部</el-radio-button>
          <el-radio-button label="自习">自习</el-radio-button>
          <el-radio-button label="考研">考研</el-radio-button>
          <el-radio-button label="四六级">四六级</el-radio-button>
          <el-radio-button label="编程">编程</el-radio-button>
        </el-radio-group>
      </div>

      <div class="buddy-list">
        <div
          v-for="item in buddyList"
          :key="item.id"
          class="buddy-card"
          @click="goToBuddyDetail(item.id)"
        >
          <div class="buddy-header">
            <div class="user-info">
              <el-avatar :size="40" :src="item.avatar">
                {{ item.nickname?.charAt(0) || 'U' }}
              </el-avatar>
              <div class="info">
                <span class="nickname">{{ item.nickname }}</span>
                <span class="time">{{ formatRelativeTime(item.createTime) }}</span>
              </div>
            </div>
            <el-tag size="small" type="info">{{ item.buddyType }}</el-tag>
          </div>
          <h3 class="title">{{ item.title }}</h3>
          <div class="buddy-meta">
            <span v-if="item.location">📍 {{ item.location }}</span>
            <span v-if="item.timeInfo">⏰ {{ item.timeInfo }}</span>
          </div>
          <div class="buddy-footer">
            <span class="members">👥 {{ item.currentMembers }}/{{ item.maxMembers }}人</span>
            <el-button type="primary" size="small">查看详情</el-button>
          </div>
        </div>
      </div>
    </section>

    <!-- 其他模块敬请期待 -->
    <section class="section coming-soon" v-if="activeTab !== 'material' && activeTab !== 'review' && activeTab !== 'buddy'">
      <div class="coming-soon-content">
        <div class="icon">🔜</div>
        <h2>更多功能敬请期待</h2>
        <p>我们正在努力开发更多实用功能</p>
      </div>
    </section>
  </div>
</template>

<style scoped>
.study-page {
  max-width: 1200px;
  margin: 0 auto;
}

.category-nav {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 30px;
}

.category-item {
  background: white;
  border-radius: 12px;
  padding: 30px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.category-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.category-item .icon {
  font-size: 48px;
  margin-bottom: 15px;
}

.category-item .title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 8px;
}

.category-item .desc {
  color: #999;
  font-size: 14px;
}

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

.filter-bar {
  margin-bottom: 20px;
}

/* 资料卡片 */
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
  height: 150px;
  overflow: hidden;
}

.material-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.price-tag {
  position: absolute;
  top: 10px;
  right: 10px;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: bold;
}

.price-free {
  background: #67c23a;
  color: white;
}

.price-paid {
  background: #f56c6c;
  color: white;
}

.material-info {
  padding: 15px;
}

.material-info .title {
  margin: 0 0 10px 0;
  font-size: 14px;
  font-weight: 500;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.material-info .meta {
  font-size: 12px;
  color: #999;
  margin-bottom: 10px;
}

.material-info .stats {
  display: flex;
  gap: 15px;
  font-size: 12px;
  color: #666;
}

/* 评价卡片 */
.review-card {
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 15px;
  cursor: pointer;
  transition: all 0.3s;
}

.review-card:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
}

.review-header h3 {
  margin: 0 0 5px 0;
  font-size: 16px;
}

.review-header .teacher {
  color: #666;
  font-size: 14px;
}

.stars {
  color: #f7ba2a;
  font-weight: bold;
}

.review-content {
  color: #666;
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: 10px;
}

.review-meta .tips {
  color: #67c23a;
  font-size: 13px;
}

.review-footer {
  display: flex;
  justify-content: space-between;
  color: #999;
  font-size: 12px;
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid #eee;
}

/* 搭子卡片 */
.buddy-card {
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 15px;
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
  margin-bottom: 15px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-info .nickname {
  font-weight: 500;
}

.user-info .time {
  font-size: 12px;
  color: #999;
}

.buddy-card .title {
  margin: 0 0 15px 0;
  font-size: 16px;
}

.buddy-meta {
  display: flex;
  gap: 20px;
  color: #666;
  font-size: 14px;
  margin-bottom: 15px;
}

.buddy-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 15px;
  border-top: 1px solid #eee;
}

.members {
  color: #999;
}

/* 敬请期待 */
.coming-soon {
  text-align: center;
  padding: 60px;
}

.coming-soon-content .icon {
  font-size: 64px;
  margin-bottom: 20px;
}

.coming-soon-content h2 {
  margin-bottom: 10px;
}

.coming-soon-content p {
  color: #999;
}
</style>
