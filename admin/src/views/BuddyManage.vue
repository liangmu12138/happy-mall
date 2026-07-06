<script setup>
import { ref, onMounted } from 'vue'
import request from '../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'
import { formatDateTime } from '../utils/date'

const buddyList = ref([])
const loading = ref(false)

const filters = ref({
  keyword: '',
  buddyType: '',
  pageNum: 1,
  pageSize: 10
})

const pagination = ref({ total: 0 })

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

const handleDelete = async (row) => {
  await ElMessageBox.confirm(`确定要删除搭子"${row.title}"吗？`, '提示', { type: 'warning' })
  try {
    await request.delete(`/api/study/buddy/${row.id}`)
    ElMessage.success('删除成功')
    fetchBuddies()
  } catch (error) {
    console.error('删除失败', error)
  }
}

const handlePageChange = (page) => {
  filters.value.pageNum = page
  fetchBuddies()
}

const getTypeTag = (type) => {
  const map = { '自习': '', '考研': 'warning', '四六级': 'success', '编程': 'danger', '其他': 'info' }
  return map[type] || ''
}
</script>

<template>
  <div class="manage-page">
    <div class="page-header">
      <h2>👥 搭子管理</h2>
    </div>

    <!-- 搜索 -->
    <div class="filter-section">
      <el-input
        v-model="filters.keyword"
        placeholder="搜索搭子标题..."
        clearable
        @keyup.enter="handleSearch"
        style="width: 250px"
      />
      <el-select v-model="filters.buddyType" placeholder="全部类型" clearable style="width: 150px">
        <el-option v-for="type in buddyTypes" :key="type" :label="type" :value="type" />
      </el-select>
      <el-button type="primary" @click="handleSearch">搜索</el-button>
    </div>

    <!-- 表格 -->
    <el-table :data="buddyList" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
      <el-table-column label="类型" width="100">
        <template #default="{ row }">
          <el-tag size="small" :type="getTypeTag(row.buddyType)">{{ row.buddyType }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="location" label="地点" width="120" />
      <el-table-column prop="timeInfo" label="时间" width="150" />
      <el-table-column label="人数" width="100">
        <template #default="{ row }">
          {{ row.currentMembers }}/{{ row.maxMembers }}
        </template>
      </el-table-column>
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
.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>
