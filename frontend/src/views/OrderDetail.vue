<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import request from '../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()

const order = ref(null)
const orderItems = ref([])
const loading = ref(false)

onMounted(async () => {
  await fetchOrderDetail()
})

const fetchOrderDetail = async () => {
  loading.value = true
  try {
    const res = await request.get(`/api/order/${route.params.id}`)
    order.value = res.data.order
    orderItems.value = res.data.items
  } catch (error) {
    console.error('获取订单详情失败', error)
    ElMessage.error('订单不存在')
    router.push('/order/list')
  } finally {
    loading.value = false
  }
}

const getStatusText = (status) => {
  const map = {
    0: '待付款',
    1: '待发货',
    2: '待收货',
    3: '已完成',
    4: '已取消'
  }
  return map[status] || '未知状态'
}

const getStatusType = (status) => {
  const map = {
    0: 'warning',
    1: 'info',
    2: 'primary',
    3: 'success',
    4: 'danger'
  }
  return map[status] || 'info'
}

const payOrder = async () => {
  try {
    await request.post(`/api/order/pay/${order.value.id}`)
    ElMessage.success('支付成功')
    fetchOrderDetail()
  } catch (error) {
    console.error('支付失败', error)
  }
}

const cancelOrder = async () => {
  await ElMessageBox.confirm('确定要取消该订单吗？', '提示', {
    type: 'warning'
  })
  try {
    await request.post(`/api/order/cancel/${order.value.id}`)
    ElMessage.success('订单已取消')
    fetchOrderDetail()
  } catch (error) {
    console.error('取消订单失败', error)
  }
}

const confirmReceive = async () => {
  await ElMessageBox.confirm('确定已收到商品吗？', '确认收货', {
    type: 'info'
  })
  try {
    await request.post(`/api/order/confirm/${order.value.id}`)
    ElMessage.success('已确认收货')
    fetchOrderDetail()
  } catch (error) {
    console.error('确认收货失败', error)
  }
}
</script>

<template>
  <div class="order-detail" v-loading="loading">
    <h2>订单详情</h2>

    <div v-if="order" class="detail-content">
      <!-- 订单状态 -->
      <div class="status-section">
        <el-tag :type="getStatusType(order.status)" size="large">
          {{ getStatusText(order.status) }}
        </el-tag>
        <span class="order-no">订单号：{{ order.orderNo }}</span>
      </div>

      <!-- 收货信息 -->
      <div class="section">
        <h3>收货信息</h3>
        <div class="address-info">
          <div class="name-phone">
            <span class="name">{{ order.receiverName }}</span>
            <span class="phone">{{ order.receiverPhone }}</span>
          </div>
          <div class="address">{{ order.receiverAddress }}</div>
        </div>
      </div>

      <!-- 商品信息 -->
      <div class="section">
        <h3>商品信息</h3>
        <div class="product-list">
          <div
            v-for="item in orderItems"
            :key="item.id"
            class="product-item"
          >
            <img :src="item.productImage" :alt="item.productName" />
            <div class="product-info">
              <div class="product-name">{{ item.productName }}</div>
              <div class="product-price">¥{{ item.price }} x {{ item.quantity }}</div>
            </div>
            <div class="product-subtotal">¥{{ item.subtotal.toFixed(2) }}</div>
          </div>
        </div>
      </div>

      <!-- 订单信息 -->
      <div class="section">
        <h3>订单信息</h3>
        <div class="order-info">
          <div class="info-row">
            <span class="label">创建时间：</span>
            <span>{{ order.createTime }}</span>
          </div>
          <div v-if="order.payTime" class="info-row">
            <span class="label">支付时间：</span>
            <span>{{ order.payTime }}</span>
          </div>
          <div v-if="order.shipTime" class="info-row">
            <span class="label">发货时间：</span>
            <span>{{ order.shipTime }}</span>
          </div>
          <div v-if="order.receiveTime" class="info-row">
            <span class="label">收货时间：</span>
            <span>{{ order.receiveTime }}</span>
          </div>
          <div v-if="order.remark" class="info-row">
            <span class="label">备注：</span>
            <span>{{ order.remark }}</span>
          </div>
        </div>
      </div>

      <!-- 金额信息 -->
      <div class="section amount-section">
        <div class="amount-row">
          <span>商品合计：</span>
          <span>¥{{ order.totalAmount.toFixed(2) }}</span>
        </div>
        <div class="amount-row">
          <span>运费：</span>
          <span>¥{{ order.freight.toFixed(2) }}</span>
        </div>
        <div class="amount-row total">
          <span>实付金额：</span>
          <span class="total-price">¥{{ order.payAmount.toFixed(2) }}</span>
        </div>
      </div>

      <!-- 操作按钮 -->
      <div class="action-buttons">
        <el-button v-if="order.status === 0" type="primary" size="large" @click="payOrder">
          立即支付
        </el-button>
        <el-button v-if="order.status === 0" size="large" @click="cancelOrder">
          取消订单
        </el-button>
        <el-button v-if="order.status === 2" type="primary" size="large" @click="confirmReceive">
          确认收货
        </el-button>
        <el-button size="large" @click="router.push('/order/list')">
          返回订单列表
        </el-button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.order-detail {
  max-width: 800px;
  margin: 0 auto;
}

h2 {
  margin-bottom: 20px;
}

.status-section {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 20px;
}

.order-no {
  color: #999;
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

.address-info {
  background: #f5f7ff;
  padding: 15px;
  border-radius: 4px;
}

.name-phone {
  margin-bottom: 10px;
}

.name-phone .name {
  font-weight: 500;
  margin-right: 20px;
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
  border-radius: 4px;
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
  font-size: 12px;
}

.product-subtotal {
  color: #333;
  font-weight: 500;
}

.order-info {
  color: #666;
}

.info-row {
  margin-bottom: 10px;
}

.info-row .label {
  color: #999;
  margin-right: 10px;
}

.amount-section {
  text-align: right;
}

.amount-row {
  margin-bottom: 10px;
}

.amount-row.total {
  font-size: 16px;
  font-weight: 500;
  padding-top: 10px;
  border-top: 1px solid #eee;
}

.total-price {
  color: #f56c6c;
  font-size: 24px;
  font-weight: bold;
}

.action-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 15px;
  margin-top: 20px;
}
</style>
