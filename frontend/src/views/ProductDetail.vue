<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useCartStore } from '../stores/cart'
import { useUserStore } from '../stores/user'
import request from '../utils/request'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()
const userStore = useUserStore()

const product = ref(null)
const quantity = ref(1)
const loading = ref(false)

onMounted(async () => {
  await fetchProduct()
})

const fetchProduct = async () => {
  try {
    const res = await request.get(`/api/product/${route.params.id}`)
    product.value = res.data
  } catch (error) {
    console.error('获取商品详情失败', error)
    ElMessage.error('商品不存在')
    router.push('/')
  }
}

const handleQuantityChange = (val) => {
  if (val < 1) quantity.value = 1
  if (val > product.value.stock) quantity.value = product.value.stock
}

const addToCart = async () => {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }

  loading.value = true
  try {
    await cartStore.addToCart(product.value.id, quantity.value)
  } catch (error) {
    console.error('加入购物车失败', error)
  } finally {
    loading.value = false
  }
}

const buyNow = async () => {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }

  await addToCart()
  router.push('/cart')
}
</script>

<template>
  <div class="product-detail" v-if="product">
    <div class="product-main">
      <!-- 商品图片 -->
      <div class="product-gallery">
        <img :src="product.mainImage" :alt="product.name" class="main-image" />
      </div>

      <!-- 商品信息 -->
      <div class="product-info">
        <h1 class="product-name">{{ product.name }}</h1>

        <div class="product-desc">{{ product.description }}</div>

        <div class="price-section">
          <div class="current-price">
            <span class="label">价格</span>
            <span class="price">¥{{ product.price }}</span>
          </div>
          <div v-if="product.originalPrice" class="original-price">
            <span class="label">原价</span>
            <span class="price">¥{{ product.originalPrice }}</span>
          </div>
        </div>

        <div class="sales-info">
          <span>已售 {{ product.sales }} 件</span>
          <span>库存 {{ product.stock }} 件</span>
        </div>

        <div class="quantity-section">
          <span class="label">数量</span>
          <el-input-number
            v-model="quantity"
            :min="1"
            :max="product.stock"
            @change="handleQuantityChange"
          />
          <span class="stock-tip">有货</span>
        </div>

        <div class="action-buttons">
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            :disabled="product.stock === 0"
            @click="addToCart"
          >
            加入购物车
          </el-button>
          <el-button
            type="danger"
            size="large"
            :loading="loading"
            :disabled="product.stock === 0"
            @click="buyNow"
          >
            立即购买
          </el-button>
        </div>
      </div>
    </div>

    <!-- 商品详情 -->
    <div class="product-detail-content">
      <h2>商品详情</h2>
      <div class="detail-content" v-html="product.detail || '暂无详情'"></div>
    </div>
  </div>
</template>

<style scoped>
.product-detail {
  max-width: 1200px;
  margin: 0 auto;
}

.product-main {
  display: flex;
  gap: 40px;
  background: white;
  border-radius: 8px;
  padding: 30px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.product-gallery {
  flex: 1;
  max-width: 500px;
}

.main-image {
  width: 100%;
  border-radius: 8px;
}

.product-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.product-name {
  margin: 0;
  font-size: 24px;
  font-weight: 500;
  color: #333;
}

.product-desc {
  color: #666;
  font-size: 14px;
}

.price-section {
  background: #fff5f5;
  padding: 20px;
  border-radius: 8px;
}

.price-section .label {
  color: #999;
  margin-right: 20px;
}

.current-price .price {
  color: #f56c6c;
  font-size: 28px;
  font-weight: bold;
}

.original-price .price {
  color: #999;
  text-decoration: line-through;
  font-size: 14px;
}

.sales-info {
  display: flex;
  gap: 30px;
  color: #999;
  font-size: 14px;
}

.quantity-section {
  display: flex;
  align-items: center;
  gap: 15px;
}

.quantity-section .label {
  color: #999;
}

.stock-tip {
  color: #67c23a;
  font-size: 12px;
}

.action-buttons {
  display: flex;
  gap: 20px;
  margin-top: 20px;
}

.product-detail-content {
  background: white;
  border-radius: 8px;
  padding: 30px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.product-detail-content h2 {
  margin: 0 0 20px 0;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.detail-content {
  min-height: 200px;
}
</style>
