<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '../stores/cart'
import request from '../utils/request'
import { ElMessage } from 'element-plus'

const router = useRouter()
const cartStore = useCartStore()

const addressList = ref([])
const selectedAddress = ref(null)
const cartItems = ref([])
const remark = ref('')
const loading = ref(false)

onMounted(async () => {
  await Promise.all([
    fetchAddressList(),
    fetchCartItems()
  ])
})

const fetchAddressList = async () => {
  try {
    const res = await request.get('/api/address/list')
    addressList.value = res.data
    // 选择默认地址
    selectedAddress.value = addressList.value.find(a => a.isDefault === 1) || addressList.value[0]
  } catch (error) {
    console.error('获取地址失败', error)
  }
}

const fetchCartItems = async () => {
  try {
    const res = await request.get('/api/cart/list')
    cartItems.value = res.data.filter(item => item.checked === 1)
  } catch (error) {
    console.error('获取购物车失败', error)
  }
}

const totalAmount = computed(() => {
  return cartItems.value.reduce((sum, item) => sum + item.subtotal, 0)
})

const selectAddress = (address) => {
  selectedAddress.value = address
}

const submitOrder = async () => {
  if (!selectedAddress.value) {
    ElMessage.warning('请选择收货地址')
    return
  }

  if (cartItems.value.length === 0) {
    ElMessage.warning('没有可结算的商品')
    return
  }

  loading.value = true
  try {
    const res = await request.post('/api/order/create', {
      addressId: selectedAddress.value.id,
      remark: remark.value
    })

    ElMessage.success('订单创建成功')

    // 模拟支付
    await request.post(`/api/order/pay/${res.data.orderId}`)

    ElMessage.success('支付成功')
    router.push(`/order/${res.data.orderId}`)
  } catch (error) {
    console.error('创建订单失败', error)
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="order-confirm">
    <h2>确认订单</h2>

    <!-- 收货地址 -->
    <div class="section">
      <h3>收货地址</h3>
      <div class="address-list">
        <div
          v-for="address in addressList"
          :key="address.id"
          class="address-card"
          :class="{ active: selectedAddress?.id === address.id }"
          @click="selectAddress(address)"
        >
          <div class="address-info">
            <div class="address-name">
              <span>{{ address.name }}</span>
              <span>{{ address.phone }}</span>
            </div>
            <div class="address-detail">
              {{ address.province }}{{ address.city }}{{ address.district }}{{ address.detail }}
            </div>
          </div>
          <el-tag v-if="address.isDefault === 1" type="danger" size="small">默认</el-tag>
        </div>

        <div v-if="addressList.length === 0" class="no-address">
          <p>暂无收货地址</p>
          <el-button type="primary" @click="router.push('/user/address')">添加地址</el-button>
        </div>
      </div>
    </div>

    <!-- 商品信息 -->
    <div class="section">
      <h3>商品信息</h3>
      <div class="product-list">
        <div
          v-for="item in cartItems"
          :key="item.id"
          class="product-item"
        >
          <img :src="item.productImage" :alt="item.productName" />
          <div class="product-info">
            <div class="product-name">{{ item.productName }}</div>
            <div class="product-price">¥{{ item.price }}</div>
          </div>
          <div class="product-quantity">x{{ item.quantity }}</div>
          <div class="product-subtotal">¥{{ item.subtotal.toFixed(2) }}</div>
        </div>
      </div>
    </div>

    <!-- 备注 -->
    <div class="section">
      <h3>订单备注</h3>
      <el-input
        v-model="remark"
        type="textarea"
        :rows="3"
        placeholder="请输入备注信息（选填）"
        maxlength="200"
        show-word-limit
      />
    </div>

    <!-- 结算信息 -->
    <div class="settlement">
      <div class="settlement-info">
        <div class="info-item">
          <span>商品合计：</span>
          <span>¥{{ totalAmount.toFixed(2) }}</span>
        </div>
        <div class="info-item">
          <span>运费：</span>
          <span>¥0.00</span>
        </div>
        <div class="info-item total">
          <span>应付总额：</span>
          <span class="total-price">¥{{ totalAmount.toFixed(2) }}</span>
        </div>
      </div>
      <el-button
        type="primary"
        size="large"
        :loading="loading"
        @click="submitOrder"
      >
        提交订单
      </el-button>
    </div>
  </div>
</template>

<style scoped>
.order-confirm {
  max-width: 1200px;
  margin: 0 auto;
}

h2 {
  margin-bottom: 20px;
}

.section {
  background: white;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.section h3 {
  margin: 0 0 15px 0;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.address-list {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
}

.address-card {
  width: 300px;
  padding: 15px;
  border: 2px solid #eee;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  position: relative;
}

.address-card:hover {
  border-color: #667eea;
}

.address-card.active {
  border-color: #667eea;
  background: #f5f7ff;
}

.address-name {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  font-weight: 500;
}

.address-detail {
  color: #666;
  font-size: 14px;
}

.no-address {
  text-align: center;
  padding: 30px;
  color: #999;
}

.product-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.product-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px;
  background: #f9f9f9;
  border-radius: 8px;
}

.product-item img {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
}

.product-info {
  flex: 1;
}

.product-name {
  font-size: 14px;
  margin-bottom: 5px;
}

.product-price {
  color: #999;
}

.product-quantity {
  color: #666;
}

.product-subtotal {
  color: #f56c6c;
  font-weight: bold;
}

.settlement {
  background: white;
  border-radius: 8px;
  padding: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.settlement-info {
  text-align: right;
}

.info-item {
  margin-bottom: 10px;
  color: #666;
}

.info-item.total {
  font-size: 16px;
  font-weight: 500;
}

.total-price {
  color: #f56c6c;
  font-size: 24px;
  font-weight: bold;
}
</style>
