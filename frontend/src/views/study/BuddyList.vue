<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'
import request from '../../utils/request'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const buddyList = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const submitLoading = ref(false)

const filters = ref({
  buddyType: '',
  keyword: '',
  pageNum: 1,
  pageSize: 10
})

const pagination = ref({ total: 0 })

const submitForm = ref({
  buddyType: '自习',
  title: '',
  description: '',
  target: '',
  location: '',
  timeInfo: '',
  maxMembers: 2
})

const buddyTypes = ['自习', '考研', '四六级', '编程', '其他']

onMounted(() => {
  fetchBuddies()
})

const fetchBuddies = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/study/buddy/list', { params: filters.value })
    buddyList.value = res.data.records
    pagination.value.total = res.data.total
  } catch (error) {
    console.error('获取搭子失败', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  filters.value.pageNum = 1
  fetchBuddies()
}

const handleTypeChange = (type) => {
  filters.value.buddyType = type
  filters.value.pageNum = 1
  fetchBuddies()
}

const handlePageChange = (page) => {
  filters.value.pageNum = page
  fetchBuddies()
}

const goToDetail = (id) => {
  router.push(`/study/buddy/${id}`)
}

const handleCreate = () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  dialogVisible.value = true
}

const submitBuddy = async () => {
  if (!submitForm.value.title) {
    ElMessage.warning('请输入标题')
    return
  }

  submitLoading.value = true
  try {
    await request.post('/api/study/buddy/create', submitForm.value)
    ElMessage.success('发布成功')
    dialogVisible.value = false
    submitForm.value = {
      buddyType: '自习',
      title: '',
      description: '',
      target: '',
      location: '',
      timeInfo: '',
      maxMembers: 2
    }
    fetchBuddies()
  } catch (error) {
    console.error('发布失败', error)
  } finally {
    submitLoading.value = false
  }
}
</script>

<template>
  <div class="buddy-list-page">
    <div class="page-header">
      <h2>👥 找学习搭子</h2>
      <el-button type="primary" @click="handleCreate">
        <el-icon><Plus /></el-icon> 发布搭子
      </el-button>
    </div>

    <!-- 搜索和筛选 -->
    <div class="filter-section">
      <el-input
        v-model="filters.keyword"
        placeholder="搜索搭子..."
        clearable
        @keyup.enter="handleSearch"
        style="width: 300px"
      >
        <template #append>
          <el-button @click="handleSearch">
            <el-icon><Search /></el-icon>
          </el-button>
        </template>
      </el-input>

      <div class="type-filter">
        <el-button
          :type="!filters.buddyType ? 'primary' : ''"
          @click="handleTypeChange('')"
        >
          全部
        </el-button>
        <el-button
          v-for="type in buddyTypes"
          :key="type"
          :type="filters.buddyType === type ? 'primary' : ''"
          @click="handleTypeChange(type)"
        >
          {{ type }}
        </el-button>
      </div>
    </div>

    <!-- 搭子列表 -->
    <div class="buddy-list" v-loading="loading">
      <div
        v-for="item in buddyList"
        :key="item.id"
        class="buddy-card"
        @click="goToDetail(item.id)"
      >
        <div class="card-header">
          <div class="user-info">
            <el-avatar :size="45" :src="item.avatar">
              {{ item.nickname?.charAt(0) || 'U' }}
            </el-avatar>
            <div class="info">
              <span class="nickname">{{ item.nickname }}</span>
              <span class="time">{{ item.createTime }}</span>
            </div>
          </div>
          <el-tag size="small" :type="getTypeTag(item.buddyType)">
            {{ item.buddyType }}
          </el-tag>
        </div>

        <h3 class="title">{{ item.title }}</h3>
        <p class="desc" v-if="item.description">{{ item.description }}</p>

        <div class="meta">
          <span v-if="item.location">📍 {{ item.location }}</span>
          <span v-if="item.timeInfo">⏰ {{ item.timeInfo }}</span>
          <span v-if="item.target">🎯 {{ item.target }}</span>
        </div>

        <div class="card-footer">
          <span class="members">
            👥 {{ item.currentMembers }}/{{ item.maxMembers }}人
          </span>
          <el-button type="primary" size="small">查看详情</el-button>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <el-empty v-if="!loading && buddyList.length === 0" description="暂无搭子，快来发布一个吧" />

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

    <!-- 发布搭子对话框 -->
    <el-dialog v-model="dialogVisible" title="发布学习搭子" width="600px">
      <el-form :model="submitForm" label-width="80px">
        <el-form-item label="类型" required>
          <el-select v-model="submitForm.buddyType" placeholder="请选择类型">
            <el-option v-for="type in buddyTypes" :key="type" :label="type" :value="type" />
          </el-select>
        </el-form-item>
        <el-form-item label="标题" required>
          <el-input v-model="submitForm.title" placeholder="如：找人一起考研数学复习" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="submitForm.description" type="textarea" :rows="3" placeholder="详细描述你的需求..." />
        </el-form-item>
        <el-form-item label="目标">
          <el-input v-model="submitForm.target" placeholder="如：考研985、过六级500分" />
        </el-form-item>
        <el-form-item label="地点">
          <el-input v-model="submitForm.location" placeholder="如：图书馆三楼" />
        </el-form-item>
        <el-form-item label="时间">
          <el-input v-model="submitForm.timeInfo" placeholder="如：每天下午2-5点" />
        </el-form-item>
        <el-form-item label="人数">
          <el-input-number v-model="submitForm.maxMembers" :min="2" :max="10" />
          <span class="tip">（含自己）</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitBuddy">发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
export default {
  methods: {
    getTypeTag(type) {
      const map = {
        '自习': '',
        '考研': 'warning',
        '四六级': 'success',
        '编程': 'danger',
        '其他': 'info'
      }
      return map[type] || ''
    }
  }
}
</script>

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
  gap: 20px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.type-filter {
  display: flex;
  gap: 10px;
}

.buddy-card {
  background: white;
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 15px;
  cursor: pointer;
  transition: all 0.3s;
}

.buddy-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.card-header {
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
  display: block;
}

.user-info .time {
  font-size: 12px;
  color: #999;
}

.buddy-card .title {
  margin: 0 0 10px 0;
  font-size: 18px;
}

.buddy-card .desc {
  color: #666;
  font-size: 14px;
  margin-bottom: 15px;
  line-height: 1.5;
}

.meta {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  color: #666;
  font-size: 14px;
  margin-bottom: 15px;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 15px;
  border-top: 1px solid #eee;
}

.members {
  color: #999;
}

.tip {
  margin-left: 10px;
  color: #999;
  font-size: 12px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}
</style>
