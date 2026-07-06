<script setup>
import { ref, onMounted } from 'vue'
import request from '../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'
import { formatDateTime } from '../utils/date'

const materialList = ref([])
const loading = ref(false)

const filters = ref({
  keyword: '',
  category: '',
  pageNum: 1,
  pageSize: 10
})

const pagination = ref({ total: 0 })

const categories = ['教材', '笔记', '考研', '考级', '课件', '其他']

onMounted(() => {
  fetchMaterials()
})

const fetchMaterials = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/study/material/list', { params: filters.value })
    materialList.value = res.data.records
    pagination.value.total = res.data.total
  } catch (error) {
    console.error('获取资料失败', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  filters.value.pageNum = 1
  fetchMaterials()
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm(`确定要删除资料"${row.title}"吗？`, '提示', { type: 'warning' })
  try {
    await request.delete(`/api/study/material/${row.id}`)
    ElMessage.success('删除成功')
    fetchMaterials()
  } catch (error) {
    console.error('删除失败', error)
  }
}

const handlePageChange = (page) => {
  filters.value.pageNum = page
  fetchMaterials()
}

const getPriceText = (price) => price > 0 ? `¥${price}` : '免费'
</script>

<template>
  <div class="manage-page">
    <div class="page-header">
      <h2>📖 资料管理</h2>
    </div>

    <!-- 搜索 -->
    <div class="filter-section">
      <el-input
        v-model="filters.keyword"
        placeholder="搜索资料标题..."
        clearable
        @keyup.enter="handleSearch"
        style="width: 250px"
      />
      <el-select v-model="filters.category" placeholder="全部分类" clearable style="width: 150px">
        <el-option v-for="cat in categories" :key="cat" :label="cat" :value="cat" />
      </el-select>
      <el-button type="primary" @click="handleSearch">搜索</el-button>
    </div>

    <!-- 表格 -->
    <el-table :data="materialList" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
      <el-table-column prop="category" label="分类" width="100">
        <template #default="{ row }">
          <el-tag size="small">{{ row.category }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="subject" label="学科" width="120" />
      <el-table-column label="价格" width="100">
        <template #default="{ row }">
          <span :class="row.price > 0 ? 'price-paid' : 'price-free'">
            {{ getPriceText(row.price) }}
          </span>
        </template>
      </el-table-column>
      <el-table-column prop="likeCount" label="点赞" width="80" />
      <el-table-column prop="downloadCount" label="下载" width="80" />
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
.price-paid { color: #f56c6c; }
.price-free { color: #67c23a; }
.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>
