<script setup>
import { ref, onMounted } from 'vue'
import request from '../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const orderList = ref([])
const loading = ref(false)

const filters = ref({
  orderNo: '',
  status: null,
  pageNum: 1,
  pageSize: 10
})

const pagination = ref({ total: 0 })

const statusOptions = [
  { label: '待付款', value: 0 },
  { label: '待发货', value: 1 },
  { label: '待收货', value: 2 },
  { label: '已完成', value: 3 },
  { label: '已取消', value: 4 }
]

onMounted(() => {
  fetchOrders()
})

const fetchOrders = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/admin/order/list', { params: filters.value })
    orderList.value = res.data.records
    pagination.value.total = res.data.total
  } catch (error) {
    console.error('获取订单失败', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  filters.value.pageNum = 1
  fetchOrders()
}

const handleShip = async (orderId) => {
  await ElMessageBox.confirm('确认发货吗？', '提示', { type: 'info' })
  try {
    await request.post(`/api/admin/order/ship/${orderId}`)
    ElMessage.success('发货成功')
    fetchOrders()
  } catch (error) {
    console.error('发货失败', error)
  }
}

const getStatusText = (status) => {
  const map = { 0: '待付款', 1: '待发货', 2: '待收货', 3: '已完成', 4: '已取消' }
  return map[status] || '未知'
}

const getStatusType = (status) => {
  const map = { 0: 'warning', 1: 'info', 2: 'primary', 3: 'success', 4: 'danger' }
  return map[status] || 'info'
}

const handlePageChange = (page) => {
  filters.value.pageNum = page
  fetchOrders()
}
</script>

<template>
  <div class="order-page">
    <div class="page-header">
      <h2>订单管理</h2>
    </div>

    <!-- 搜索栏 -->
    <el-card class="filter-card">
      <el-form :inline="true">
        <el-form-item label="订单号">
          <el-input v-model="filters.orderNo" placeholder="订单号" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="filters.status" placeholder="全部" clearable>
            <el-option v-for="opt in statusOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 订单列表 -->
    <el-card>
      <el-table :data="orderList" v-loading="loading" stripe>
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="receiverName" label="收货人" width="100" />
        <el-table-column prop="receiverPhone" label="联系电话" width="120" />
        <el-table-column label="订单金额" width="120">
          <template #default="{ row }">
            <span style="color: #f56c6c; font-weight: bold;">¥{{ row.payAmount?.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 1"
              type="primary"
              size="small"
              @click="handleShip(row.id)"
            >
              发货
            </el-button>
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
  </div>
</template>

<style scoped>
.page-header h2 {
  margin-bottom: 20px;
}

.filter-card {
  margin-bottom: 20px;
}

.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>
