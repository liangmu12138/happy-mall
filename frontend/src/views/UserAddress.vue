<script setup>
import { ref, onMounted } from 'vue'
import request from '../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const addressList = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const formLoading = ref(false)

const form = ref({
  id: null,
  name: '',
  phone: '',
  province: '',
  city: '',
  district: '',
  detail: '',
  isDefault: 0
})

const rules = {
  name: [{ required: true, message: '请输入收货人姓名', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  province: [{ required: true, message: '请输入省份', trigger: 'blur' }],
  city: [{ required: true, message: '请输入城市', trigger: 'blur' }],
  district: [{ required: true, message: '请输入区县', trigger: 'blur' }],
  detail: [{ required: true, message: '请输入详细地址', trigger: 'blur' }]
}

onMounted(() => {
  fetchAddressList()
})

const fetchAddressList = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/address/list')
    addressList.value = res.data
  } catch (error) {
    console.error('获取地址失败', error)
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  form.value = {
    id: null,
    name: '',
    phone: '',
    province: '',
    city: '',
    district: '',
    detail: '',
    isDefault: 0
  }
  dialogVisible.value = true
}

const handleEdit = (address) => {
  form.value = { ...address }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  formLoading.value = true
  try {
    if (form.value.id) {
      await request.put('/api/address/update', form.value)
      ElMessage.success('更新成功')
    } else {
      await request.post('/api/address/add', form.value)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    fetchAddressList()
  } catch (error) {
    console.error('保存失败', error)
  } finally {
    formLoading.value = false
  }
}

const handleDelete = async (addressId) => {
  await ElMessageBox.confirm('确定要删除该地址吗？', '提示', {
    type: 'warning'
  })
  try {
    await request.delete(`/api/address/${addressId}`)
    ElMessage.success('删除成功')
    fetchAddressList()
  } catch (error) {
    console.error('删除失败', error)
  }
}

const handleSetDefault = async (addressId) => {
  try {
    await request.put(`/api/address/default/${addressId}`)
    ElMessage.success('设置成功')
    fetchAddressList()
  } catch (error) {
    console.error('设置失败', error)
  }
}
</script>

<template>
  <div class="user-address">
    <div class="address-header">
      <h2>收货地址</h2>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon> 新增地址
      </el-button>
    </div>

    <div v-loading="loading" class="address-list">
      <div v-if="addressList.length === 0" class="empty-address">
        <el-empty description="暂无收货地址">
          <el-button type="primary" @click="handleAdd">添加地址</el-button>
        </el-empty>
      </div>

      <div
        v-for="address in addressList"
        :key="address.id"
        class="address-card"
      >
        <div class="address-info">
          <div class="name-phone">
            <span class="name">{{ address.name }}</span>
            <span class="phone">{{ address.phone }}</span>
            <el-tag v-if="address.isDefault === 1" type="danger" size="small">默认</el-tag>
          </div>
          <div class="address-detail">
            {{ address.province }}{{ address.city }}{{ address.district }}{{ address.detail }}
          </div>
        </div>
        <div class="address-actions">
          <el-button text @click="handleEdit(address)">编辑</el-button>
          <el-button text type="danger" @click="handleDelete(address.id)">删除</el-button>
          <el-button
            v-if="address.isDefault !== 1"
            text
            @click="handleSetDefault(address.id)"
          >
            设为默认
          </el-button>
        </div>
      </div>
    </div>

    <!-- 编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="form.id ? '编辑地址' : '新增地址'"
      width="500px"
    >
      <el-form :model="form" :rules="rules" label-width="80px">
        <el-form-item label="收货人" prop="name">
          <el-input v-model="form.name" placeholder="请输入收货人姓名" />
        </el-form-item>

        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>

        <el-form-item label="省份" prop="province">
          <el-input v-model="form.province" placeholder="请输入省份" />
        </el-form-item>

        <el-form-item label="城市" prop="city">
          <el-input v-model="form.city" placeholder="请输入城市" />
        </el-form-item>

        <el-form-item label="区县" prop="district">
          <el-input v-model="form.district" placeholder="请输入区县" />
        </el-form-item>

        <el-form-item label="详细地址" prop="detail">
          <el-input v-model="form.detail" type="textarea" :rows="2" placeholder="请输入详细地址" />
        </el-form-item>

        <el-form-item label="默认地址">
          <el-switch v-model="form.isDefault" :active-value="1" :inactive-value="0" />
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
.user-address {
  max-width: 800px;
  margin: 0 auto;
}

.address-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.address-header h2 {
  margin: 0;
}

.address-list {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.empty-address {
  padding: 40px;
  text-align: center;
}

.address-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border: 1px solid #eee;
  border-radius: 8px;
  margin-bottom: 15px;
}

.address-card:last-child {
  margin-bottom: 0;
}

.name-phone {
  margin-bottom: 10px;
}

.name-phone .name {
  font-weight: 500;
  margin-right: 20px;
}

.name-phone .phone {
  color: #666;
}

.address-detail {
  color: #666;
  font-size: 14px;
}

.address-actions {
  display: flex;
  gap: 10px;
}
</style>
