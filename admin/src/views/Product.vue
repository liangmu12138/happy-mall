<script setup>
import { ref, onMounted } from 'vue'
import request from '../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const productList = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const formLoading = ref(false)

const filters = ref({
  keyword: '',
  status: null,
  pageNum: 1,
  pageSize: 10
})

const pagination = ref({ total: 0 })

const form = ref({
  id: null,
  name: '',
  description: '',
  price: null,
  originalPrice: null,
  stock: null,
  categoryId: null,
  mainImage: '',
  status: 1
})

const categories = ref([])

onMounted(async () => {
  await Promise.all([fetchProducts(), fetchCategories()])
})

const fetchProducts = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/admin/product/list', { params: filters.value })
    productList.value = res.data.records
    pagination.value.total = res.data.total
  } catch (error) {
    console.error('获取商品失败', error)
  } finally {
    loading.value = false
  }
}

const fetchCategories = async () => {
  try {
    const res = await request.get('/api/admin/category/list')
    categories.value = res.data
  } catch (error) {
    console.error('获取分类失败', error)
  }
}

const handleSearch = () => {
  filters.value.pageNum = 1
  fetchProducts()
}

const handleAdd = () => {
  form.value = {
    id: null,
    name: '',
    description: '',
    price: null,
    originalPrice: null,
    stock: null,
    categoryId: null,
    mainImage: '',
    status: 1
  }
  dialogVisible.value = true
}

const handleEdit = (product) => {
  form.value = { ...product }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!form.value.name || !form.value.price) {
    ElMessage.warning('请填写必要信息')
    return
  }

  formLoading.value = true
  try {
    if (form.value.id) {
      await request.put('/api/admin/product/update', form.value)
      ElMessage.success('更新成功')
    } else {
      await request.post('/api/admin/product/add', form.value)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    fetchProducts()
  } catch (error) {
    console.error('保存失败', error)
  } finally {
    formLoading.value = false
  }
}

const handleDelete = async (id) => {
  await ElMessageBox.confirm('确定要删除该商品吗？', '提示', { type: 'warning' })
  try {
    await request.delete(`/api/admin/product/${id}`)
    ElMessage.success('删除成功')
    fetchProducts()
  } catch (error) {
    console.error('删除失败', error)
  }
}

const handleStatusChange = async (product) => {
  try {
    await request.put('/api/admin/product/status', {
      id: product.id,
      status: product.status
    })
    ElMessage.success('操作成功')
  } catch (error) {
    console.error('操作失败', error)
    fetchProducts()
  }
}

const handlePageChange = (page) => {
  filters.value.pageNum = page
  fetchProducts()
}
</script>

<template>
  <div class="product-page">
    <div class="page-header">
      <h2>商品管理</h2>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon> 新增商品
      </el-button>
    </div>

    <!-- 搜索栏 -->
    <el-card class="filter-card">
      <el-form :inline="true">
        <el-form-item label="关键词">
          <el-input v-model="filters.keyword" placeholder="商品名称" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="filters.status" placeholder="全部" clearable>
            <el-option label="上架" :value="1" />
            <el-option label="下架" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 商品列表 -->
    <el-card>
      <el-table :data="productList" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="商品图片" width="100">
          <template #default="{ row }">
            <img :src="row.mainImage" class="product-image" />
          </template>
        </el-table-column>
        <el-table-column prop="name" label="商品名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="price" label="价格" width="100">
          <template #default="{ row }">¥{{ row.price }}</template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="80" />
        <el-table-column prop="sales" label="销量" width="80" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button text type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button text type="danger" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination" v-if="pagination.total > filters.pageSize">
        <el-pagination
          v-model:current-page="filters.pageNum"
          :page-size="filters.pageSize"
          :total="pagination.total"
          layout="total, prev, pager, next"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <!-- 编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑商品' : '新增商品'" width="600px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="商品名称" required>
          <el-input v-model="form.name" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="商品简介">
          <el-input v-model="form.description" type="textarea" :rows="2" placeholder="请输入商品简介" />
        </el-form-item>
        <el-form-item label="价格" required>
          <el-input-number v-model="form.price" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="原价">
          <el-input-number v-model="form.originalPrice" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="库存">
          <el-input-number v-model="form.stock" :min="0" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="form.categoryId" placeholder="请选择分类">
            <el-option
              v-for="cat in categories"
              :key="cat.id"
              :label="cat.name"
              :value="cat.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="主图URL">
          <el-input v-model="form.mainImage" placeholder="请输入图片URL" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="formLoading" @click="handleSubmit">确定</el-button>
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

.filter-card {
  margin-bottom: 20px;
}

.product-image {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 4px;
}

.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>
