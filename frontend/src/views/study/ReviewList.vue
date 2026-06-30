<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'
import request from '../../utils/request'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const reviewList = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const submitLoading = ref(false)

const filters = ref({
  courseName: '',
  school: '',
  pageNum: 1,
  pageSize: 10
})

const pagination = ref({ total: 0 })

const submitForm = ref({
  courseName: '',
  teacherName: '',
  school: '',
  rating: 5,
  difficulty: 3,
  examDifficulty: 3,
  gradeScore: 3,
  content: '',
  tips: ''
})

onMounted(() => {
  fetchReviews()
})

const fetchReviews = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/study/review/list', { params: filters.value })
    reviewList.value = res.data.records
    pagination.value.total = res.data.total
  } catch (error) {
    console.error('获取评价失败', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  filters.value.pageNum = 1
  fetchReviews()
}

const handlePageChange = (page) => {
  filters.value.pageNum = page
  fetchReviews()
}

const goToDetail = (id) => {
  router.push(`/study/review/${id}`)
}

const handleSubmit = () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  dialogVisible.value = true
}

const submitReview = async () => {
  if (!submitForm.value.courseName || !submitForm.value.content) {
    ElMessage.warning('请填写课程名称和评价内容')
    return
  }

  submitLoading.value = true
  try {
    await request.post('/api/study/review/add', submitForm.value)
    ElMessage.success('发表成功')
    dialogVisible.value = false
    submitForm.value = {
      courseName: '',
      teacherName: '',
      school: '',
      rating: 5,
      difficulty: 3,
      examDifficulty: 3,
      gradeScore: 3,
      content: '',
      tips: ''
    }
    fetchReviews()
  } catch (error) {
    console.error('发表失败', error)
  } finally {
    submitLoading.value = false
  }
}

const handleLike = async (id, event) => {
  event.stopPropagation()
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    return
  }
  try {
    await request.post(`/api/study/review/${id}/like`)
    ElMessage.success('点赞成功')
    fetchReviews()
  } catch (error) {
    console.error('点赞失败', error)
  }
}
</script>

<template>
  <div class="review-list-page">
    <div class="page-header">
      <h2>⭐ 学长学姐说</h2>
      <el-button type="primary" @click="handleSubmit">
        <el-icon><Edit /></el-icon> 发表评价
      </el-button>
    </div>

    <!-- 搜索 -->
    <div class="filter-section">
      <el-input
        v-model="filters.courseName"
        placeholder="搜索课程名称..."
        clearable
        @keyup.enter="handleSearch"
        style="width: 250px"
      />
      <el-input
        v-model="filters.school"
        placeholder="搜索学校..."
        clearable
        @keyup.enter="handleSearch"
        style="width: 200px"
      />
      <el-button type="primary" @click="handleSearch">搜索</el-button>
    </div>

    <!-- 评价列表 -->
    <div class="review-list" v-loading="loading">
      <div
        v-for="item in reviewList"
        :key="item.id"
        class="review-card"
        @click="goToDetail(item.id)"
      >
        <div class="review-header">
          <div class="course-info">
            <h3>{{ item.courseName }}</h3>
            <div class="teacher-school">
              <span v-if="item.teacherName">{{ item.teacherName }}老师</span>
              <span v-if="item.school">· {{ item.school }}</span>
            </div>
          </div>
          <div class="rating-score">
            <span class="score">{{ item.rating }}</span>
            <span class="label">评分</span>
          </div>
        </div>

        <div class="rating-details">
          <div class="detail-item">
            <span class="label">难度</span>
            <el-rate v-model="item.difficulty" disabled :max="5" />
          </div>
          <div class="detail-item">
            <span class="label">考试难度</span>
            <el-rate v-model="item.examDifficulty" disabled :max="5" />
          </div>
          <div class="detail-item">
            <span class="label">给分</span>
            <el-rate v-model="item.gradeScore" disabled :max="5" />
          </div>
        </div>

        <div class="review-content">{{ item.content }}</div>

        <div class="review-tips" v-if="item.tips">
          <span class="tips-label">💡 学习建议：</span>
          {{ item.tips }}
        </div>

        <div class="review-footer">
          <el-button text @click="(e) => handleLike(item.id, e)">
            ❤️ {{ item.likeCount }}
          </el-button>
          <span class="time">{{ item.createTime }}</span>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <el-empty v-if="!loading && reviewList.length === 0" description="暂无评价" />

    <!-- 分页 -->
    <div class="pagination" v-if="pagination.total > filters.pageSize">
      <el-pagination
        v-model:current-page="filters.pageNum"
        :page-size="filters.pageSize"
        :total="pagination.total"
        layout="prev, pager, next"
        @current-change="handlePageChange"
      />
    </div>

    <!-- 发表评价对话框 -->
    <el-dialog v-model="dialogVisible" title="发表课程评价" width="600px">
      <el-form :model="submitForm" label-width="100px">
        <el-form-item label="课程名称" required>
          <el-input v-model="submitForm.courseName" placeholder="如：高等数学A" />
        </el-form-item>
        <el-form-item label="教师姓名">
          <el-input v-model="submitForm.teacherName" placeholder="如：张三" />
        </el-form-item>
        <el-form-item label="学校">
          <el-input v-model="submitForm.school" placeholder="如：清华大学" />
        </el-form-item>
        <el-form-item label="整体评分" required>
          <el-rate v-model="submitForm.rating" show-score />
        </el-form-item>
        <el-form-item label="课程难度">
          <el-rate v-model="submitForm.difficulty" />
        </el-form-item>
        <el-form-item label="考试难度">
          <el-rate v-model="submitForm.examDifficulty" />
        </el-form-item>
        <el-form-item label="给分情况">
          <el-rate v-model="submitForm.gradeScore" />
        </el-form-item>
        <el-form-item label="评价内容" required>
          <el-input v-model="submitForm.content" type="textarea" :rows="4" placeholder="请分享你对这门课的真实感受..." />
        </el-form-item>
        <el-form-item label="学习建议">
          <el-input v-model="submitForm.tips" type="textarea" :rows="2" placeholder="给学弟学妹的建议（选填）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitReview">发表</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
}

.filter-section {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
}

.review-card {
  background: white;
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
  font-size: 18px;
}

.teacher-school {
  color: #666;
  font-size: 14px;
}

.rating-score {
  text-align: center;
}

.rating-score .score {
  display: block;
  font-size: 28px;
  font-weight: bold;
  color: #f7ba2a;
}

.rating-score .label {
  font-size: 12px;
  color: #999;
}

.rating-details {
  display: flex;
  gap: 30px;
  margin-bottom: 15px;
  padding: 15px;
  background: #f9f9f9;
  border-radius: 8px;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.detail-item .label {
  color: #666;
  font-size: 13px;
}

.review-content {
  color: #333;
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: 15px;
}

.review-tips {
  background: #f0f9eb;
  padding: 12px;
  border-radius: 6px;
  font-size: 13px;
  color: #67c23a;
  margin-bottom: 15px;
}

.tips-label {
  font-weight: 500;
}

.review-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 15px;
  border-top: 1px solid #eee;
}

.review-footer .time {
  color: #999;
  font-size: 12px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}
</style>
