<script setup>
import { ref, onMounted } from 'vue'
import request from '../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'
import { formatDateTime } from '../utils/date'

const reviewList = ref([])
const loading = ref(false)

const filters = ref({
  courseName: '',
  pageNum: 1,
  pageSize: 10
})

const pagination = ref({ total: 0 })

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

const handleDelete = async (row) => {
  await ElMessageBox.confirm(`确定要删除"${row.courseName}"的评价吗？`, '提示', { type: 'warning' })
  try {
    await request.delete(`/api/study/review/${row.id}`)
    ElMessage.success('删除成功')
    fetchReviews()
  } catch (error) {
    console.error('删除失败', error)
  }
}

const handlePageChange = (page) => {
  filters.value.pageNum = page
  fetchReviews()
}
</script>

<template>
  <div class="manage-page">
    <div class="page-header">
      <h2>⭐ 评价管理</h2>
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
      <el-button type="primary" @click="handleSearch">搜索</el-button>
    </div>

    <!-- 表格 -->
    <el-table :data="reviewList" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="courseName" label="课程名称" min-width="150" />
      <el-table-column prop="teacherName" label="教师" width="100" />
      <el-table-column prop="school" label="学校" width="120" />
      <el-table-column label="评分" width="80">
        <template #default="{ row }">
          <span class="rating">⭐ {{ row.rating }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="content" label="评价内容" min-width="200" show-overflow-tooltip />
      <el-table-column prop="likeCount" label="点赞" width="80" />
      <el-table-column label="发布时间" width="180">
        <template #default="{ row }">
          {{ formatDateTime(row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{ row }">
          <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination" v-if="pagination.total > filters.pageSize">
      <el-pagination
        v-model:current-page="filters.pageNum"
        :page-size="filters.pageSize"
        :total="pagination.total"
        layout="total, prev, pager, next"
        @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<style scoped>
.page-header {
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
.rating {
  color: #f7ba2a;
  font-weight: bold;
}
.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>
