<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useCartStore } from '../stores/cart'
import request from '../utils/request'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()

const productList = ref([])
const categories = ref([])
const loading = ref(false)

const filters = ref({
  keyword: '',
  categoryId: null,
  pageNum: 1,
  pageSize: 20
})

const pagination = ref({
  total: 0
})

onMounted(() => {
  filters.value.keyword = route.query.keyword || ''
  filters.value.categoryId = route.query.categoryId ? Number(route.query.categoryId) : null
  fetchCategories()
  fetchProducts()
})

watch(() => route.query, (newQuery) => {
  filters.value.keyword = newQuery.keyword || ''
  filters.value.categoryId = newQuery.categoryId ? Number(newQuery.categoryId) : null
  filters.value.pageNum = 1
  fetchProducts()
})

const fetchCategories = async () => {
  try {
    const res = await request.get('/api/category/list')
    categories.value = res.data
  } catch (error) {
    console.error('获取分类失败', error)
  }
}

const fetchProducts = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: filters.value.pageNum,
      pageSize: filters.value.pageSize,
      keyword: filters.value.keyword || undefined,
      categoryId: filters.value.categoryId || undefined
    }
    const res = await request.get('/api/product/list', { params })
    productList.value = res.data.records
    pagination.value.total = res.data.total
  } catch (error) {
    console.error('获取商品失败', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  filters.value.pageNum = 1
  fetchProducts()
}

const handleCategoryChange = (categoryId) => {
  filters.value.categoryId = categoryId
  filters.value.pageNum = 1
  fetchProducts()
}

const handlePageChange = (page) => {
  filters.value.pageNum = page
  fetchProducts()
}

const goToProduct = (id) => {
  router.push(`/product/${id}`)
}

const addToCart = async (productId) => {
  try {
    await cartStore.addToCart(productId)
  } catch (error) {
    if (error.response?.status === 401) {
      router.push('/login')
    }
  }
}
</script>

<template>
  <div class="search-page">
    <div class="search-sidebar">
      <h3>商品分类</h3>
      <div class="category-list">
        <div
          class="category-item"
          :class="{ active: !filters.categoryId }"
          @click="handleCategoryChange(null)"
        >
          全部商品
        </div>
        <div
          v-for="category in categories"
          :key="category.id"
          class="category-item"
          :class="{ active: filters.categoryId === category.id }"
          @click="handleCategoryChange(category.id)"
        >
          {{ category.name }}
        </div>
      </div>
    </div>

    <div class="search-content">
      <div class="search-header">
        <el-input
          v-model="filters.keyword"
          placeholder="搜索商品"
          clearable
          @keyup.enter="handleSearch"
        >
          <template #append>
            <el-button @click="handleSearch">
              <el-icon><Search /></el-icon>
            </el-button>
          </template>
        </el-input>
      </div>

      <div v-loading="loading" class="product-grid">
        <div v-if="productList.length === 0 && !loading" class="empty-product">
          <el-empty description="暂无商品" />
        </div>

        <div
          v-for="product in productList"
          :key="product.id"
          class="product-card"
          @click="goToProduct(product.id)"
        >
          <div class="product-image">
            <img :src="product.mainImage" :alt="product.name" />
          </div>
          <div class="product-info">
            <h3 class="product-name">{{ product.name }}</h3>
            <p class="product-desc">{{ product.description }}</p>
            <div class="product-price">
              <span class="price">¥{{ product.price }}</span>
              <span v-if="product.originalPrice" class="original-price">¥{{ product.originalPrice }}</span>
            </div>
            <div class="product-sales">已售 {{ product.sales }} 件</div>
            <el-button
              type="primary"
              size="small"
              @click.stop="addToCart(product.id)"
            >
              加入购物车
            </el-button>
          </div>
        </div>
      </div>

      <div class="pagination" v-if="pagination.total > filters.pageSize">
        <el-pagination
          v-model:current-page="filters.pageNum"
          :page-size="filters.pageSize"
          :total="pagination.total"
          layout="prev, pager, next"
          @current-change="handlePageChange"
        />
      </div>
    </div>
  </div>
</template>

<style scoped>
.search-page {
  display: flex;
  gap: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.search-sidebar {
  width: 200px;
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.search-sidebar h3 {
  margin: 0 0 15px 0;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.category-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.category-item {
  padding: 10px;
  cursor: pointer;
  border-radius: 4px;
  transition: all 0.3s;
}

.category-item:hover {
  background: #f5f5f5;
}

.category-item.active {
  background: #667eea;
  color: white;
}

.search-content {
  flex: 1;
}

.search-header {
  margin-bottom: 20px;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.empty-product {
  grid-column: span 3;
}

.product-card {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: all 0.3s;
}

.product-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transform: translateY(-5px);
}

.product-image {
  width: 100%;
  height: 200px;
  overflow: hidden;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-info {
  padding: 15px;
}

.product-name {
  margin: 0 0 8px 0;
  font-size: 14px;
  font-weight: 500;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.product-desc {
  margin: 0 0 8px 0;
  font-size: 12px;
  color: #999;
}

.product-price {
  margin-bottom: 8px;
}

.price {
  color: #f56c6c;
  font-size: 18px;
  font-weight: bold;
}

.original-price {
  color: #999;
  font-size: 12px;
  text-decoration: line-through;
  margin-left: 8px;
}

.product-sales {
  font-size: 12px;
  color: #999;
  margin-bottom: 10px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>
