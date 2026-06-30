<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()

const orderList = ref([])
const loading = ref(false)
const activeTab = ref('')
const pagination = ref({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const tabs = [
  { label: '全部', value: '' },
  { label: '待付款', value: '0' },
  { label: '待发货', value: '1' },
  { label: '待收货', value: '2' },
  { label: '已完成', value: '3' }
]

onMounted(() => {
  fetchOrders()
})

const fetchOrders = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/order/list', {
      params: {
        status: activeTab.value || undefined,
        pageNum: pagination.value.pageNum,
        pageSize: pagination.value.pageSize
      }
    })
    orderList.value = res.data.records
    pagination.value.total = res.data.total
  } catch (error) {
    console.error('获取订单失败', error)
  } finally {
    loading.value = false
  }
}

const handleTabChange = () => {
  pagination.value.pageNum = 1
  fetchOrders()
}

const handlePageChange = (page) => {
  pagination.value.pageNum = page
  fetchOrders()
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

const goToDetail = (orderId) => {
  router.push(`/order/${orderId}`)
}

const payOrder = async (orderId) => {
  try {
    await request.post(`/api/order/pay/${orderId}`)
    ElMessage.success('支付成功')
    fetchOrders()
  } catch (error) {
    console.error('支付失败', error)
  }
}

const cancelOrder = async (orderId) => {
  await ElMessageBox.confirm('确定要取消该订单吗？', '提示', {
    type: 'warning'
  })
  try {
    await request.post(`/api/order/cancel/${orderId}`)
    ElMessage.success('订单已取消')
    fetchOrders()
  } catch (error) {
    console.error('取消订单失败', error)
  }
}

const confirmReceive = async (orderId) => {
  await ElMessageBox.confirm('确定已收到商品吗？', '确认收货', {
    type: 'info'
  })
  try {
    await request.post(`/api/order/confirm/${orderId}`)
    ElMessage.success('已确认收货')
    fetchOrders()
  } catch (error) {
    console.error('确认收货失败', error)
  }
}
</script>

<template>
  <div class="order-list">
    <h2>我的订单</h2>

    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <el-tab-pane
        v-for="tab in tabs"
        :key="tab.value"
        :label="tab.label"
        :name="tab.value"
      />
    </el-tabs>

    <div v-loading="loading">
      <div v-if="orderList.length === 0" class="empty-order">
        <el-empty description="暂无订单">
          <el-button type="primary" @click="router.push('/')">去逛逛</el-button>
        </el-empty>
      </div>

      <div
        v-for="order in orderList"
        :key="order.id"
        class="order-card"
      >
        <div class="order-header">
          <span class="order-no">订单号：{{ order.orderNo }}</span>
          <el-tag :type="getStatusType(order.status)" size="small">
            {{ getStatusText(order.status) }}
          </el-tag>
        </div>

        <div class="order-items">
          <div
            v-for="item in order.items"
            :key="item.id"
            class="order-item"
            @click="goToDetail(order.id)"
          >
            <img :src="item.productImage" :alt="item.productName" />
            <div class="item-info">
              <div class="item-name">{{ item.productName }}</div>
              <div class="item-price">¥{{ item.price }} x {{ item.quantity }}</div>
            </div>
            <div class="item-subtotal">¥{{ item.subtotal.toFixed(2) }}</div>
          </div>
        </div>

        <div class="order-footer">
          <div class="order-total">
            共 {{ order.items.length }} 件商品，合计：
            <span class="total-price">¥{{ order.payAmount.toFixed(2) }}</span>
          </div>
          <div class="order-actions">
            <el-button v-if="order.status === 0" type="primary" size="small" @click="payOrder(order.id)">
              立即支付
            </el-button>
            <el-button v-if="order.status === 0" size="small" @click="cancelOrder(order.id)">
              取消订单
            </el-button>
            <el-button v-if="order.status === 2" type="primary" size="small" @click="confirmReceive(order.id)">
              确认收货
            </el-button>
            <el-button size="small" @click="goToDetail(order.id)">
              查看详情
            </el-button>
          </div>
        </div>
      </div>

      <div class="pagination" v-if="pagination.total > pagination.pageSize">
        <el-pagination
          v-model:current-page="pagination.pageNum"
          :page-size="pagination.pageSize"
          :total="pagination.total"
          layout="prev, pager, next"
          @current-change="handlePageChange"
        />
      </div>
    </div>
  </div>
</template>

<style scoped>
.order-list {
  max-width: 1200px;
  margin: 0 auto;
}

h2 {
  margin-bottom: 20px;
}

.empty-order {
  background: white;
  border-radius: 8px;
  padding: 60px;
  text-align: center;
}

.order-card {
  background: white;
  border-radius: 8px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  background: #f5f5f5;
  border-bottom: 1px solid #eee;
}

.order-no {
  color: #666;
  font-size: 14px;
}

.order-items {
  padding: 20px;
}

.order-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 10px 0;
  cursor: pointer;
}

.order-item:not(:last-child) {
  border-bottom: 1px solid #eee;
}

.order-item img {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 4px;
}

.item-info {
  flex: 1;
}

.item-name {
  font-size: 14px;
  margin-bottom: 5px;
}

.item-price {
  color: #999;
  font-size: 12px;
}

.item-subtotal {
  color: #333;
  font-weight: 500;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  border-top: 1px solid #eee;
}

.order-total {
  color: #666;
}

.total-price {
  color: #f56c6c;
  font-size: 18px;
  font-weight: bold;
}

.order-actions {
  display: flex;
  gap: 10px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>
