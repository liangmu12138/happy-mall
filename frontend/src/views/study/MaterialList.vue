<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'
import request from '../../utils/request'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const materialList = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const uploadLoading = ref(false)

const filters = ref({
  category: '',
  keyword: '',
  pageNum: 1,
  pageSize: 20
})

const pagination = ref({ total: 0 })

const uploadForm = ref({
  title: '',
  description: '',
  category: '',
  subject: '',
  price: 0,
  images: '',
  fileUrl: ''
})

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

const handleCategoryChange = (category) => {
  filters.value.category = category
  filters.value.pageNum = 1
  fetchMaterials()
}

const handlePageChange = (page) => {
  filters.value.pageNum = page
  fetchMaterials()
}

const goToDetail = (id) => {
  router.push(`/study/material/${id}`)
}

const handleUpload = () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  dialogVisible.value = true
}

const submitUpload = async () => {
  if (!uploadForm.value.title || !uploadForm.value.category) {
    ElMessage.warning('请填写标题和分类')
    return
  }

  uploadLoading.value = true
  try {
    await request.post('/api/study/material/upload', uploadForm.value)
    ElMessage.success('上传成功')
    dialogVisible.value = false
    uploadForm.value = {
      title: '',
      description: '',
      category: '',
      subject: '',
      price: 0,
      images: '',
      fileUrl: ''
    }
    fetchMaterials()
  } catch (error) {
    console.error('上传失败', error)
  } finally {
    uploadLoading.value = false
  }
}

const getPriceText = (price) => {
  return price > 0 ? `¥${price}` : '免费'
}

const getPriceClass = (price) => {
  return price > 0 ? 'price-paid' : 'price-free'
}
</script>

<template>
  <div class="material-list-page">
    <div class="page-header">
      <h2>📖 资料市场</h2>
      <el-button type="primary" @click="handleUpload">
        <el-icon><Upload /></el-icon> 上传资料
      </el-button>
    </div>

    <!-- 搜索和筛选 -->
    <div class="filter-section">
      <el-input
        v-model="filters.keyword"
        placeholder="搜索资料..."
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

      <div class="category-filter">
        <el-button
          :type="!filters.category ? 'primary' : ''"
          @click="handleCategoryChange('')"
        >
          全部
        </el-button>
        <el-button
          v-for="cat in categories"
          :key="cat"
          :type="filters.category === cat ? 'primary' : ''"
          @click="handleCategoryChange(cat)"
        >
          {{ cat }}
        </el-button>
      </div>
    </div>

    <!-- 资料列表 -->
    <div class="material-grid" v-loading="loading">
      <div
        v-for="item in materialList"
        :key="item.id"
        class="material-card"
        @click="goToDetail(item.id)"
      >
        <div class="material-image">
          <img :src="item.images || 'https://via.placeholder.com/200'" :alt="item.title" />
          <span class="price-tag" :class="getPriceClass(item.price)">
            {{ getPriceText(item.price) }}
          </span>
        </div>
        <div class="material-info">
          <h3 class="title">{{ item.title }}</h3>
          <p class="desc">{{ item.description }}</p>
          <div class="meta">
            <span class="category">{{ item.category }}</span>
            <span class="subject" v-if="item.subject">{{ item.subject }}</span>
          </div>
          <div class="footer">
            <span class="stats">
              <span>❤️ {{ item.likeCount }}</span>
              <span>📥 {{ item.downloadCount }}</span>
            </span>
            <span class="time">{{ item.createTime }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <el-empty v-if="!loading && materialList.length === 0" description="暂无资料" />

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

    <!-- 上传对话框 -->
    <el-dialog v-model="dialogVisible" title="上传资料" width="600px">
      <el-form :model="uploadForm" label-width="80px">
        <el-form-item label="标题" required>
          <el-input v-model="uploadForm.title" placeholder="请输入资料标题" />
        </el-form-item>
        <el-form-item label="分类" required>
          <el-select v-model="uploadForm.category" placeholder="请选择分类">
            <el-option v-for="cat in categories" :key="cat" :label="cat" :value="cat" />
          </el-select>
        </el-form-item>
        <el-form-item label="学科">
          <el-input v-model="uploadForm.subject" placeholder="如：高等数学、数据结构" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="uploadForm.description" type="textarea" :rows="3" placeholder="请描述资料内容" />
        </el-form-item>
        <el-form-item label="价格">
          <el-input-number v-model="uploadForm.price" :min="0" :max="999" :step="0.1" />
          <span class="price-tip">设置为0表示免费</span>
        </el-form-item>
        <el-form-item label="封面图URL">
          <el-input v-model="uploadForm.images" placeholder="请输入图片URL（选填）" />
        </el-form-item>
        <el-form-item label="文件URL">
          <el-input v-model="uploadForm.fileUrl" placeholder="请输入文件下载URL" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="uploadLoading" @click="submitUpload">发布</el-button>
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
  gap: 20px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.category-filter {
  display: flex;
  gap: 10px;
}

.material-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.material-card {
  background: white;
  border: 1px solid #eee;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
}

.material-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transform: translateY(-5px);
}

.material-image {
  position: relative;
  height: 180px;
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
  padding: 4px 10px;
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

.material-info .desc {
  color: #999;
  font-size: 12px;
  margin-bottom: 10px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.meta {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
}

.meta .category {
  background: #f5f5f5;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.meta .subject {
  color: #666;
  font-size: 12px;
}

.footer {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #999;
}

.stats {
  display: flex;
  gap: 15px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}

.price-tip {
  margin-left: 10px;
  color: #999;
  font-size: 12px;
}
</style>
