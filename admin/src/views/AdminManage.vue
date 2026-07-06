<script setup>
import { ref, onMounted } from 'vue'
import request from '../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'
import { formatDateTime } from '../utils/date'

const adminList = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const formLoading = ref(false)

const form = ref({
  username: '',
  password: '',
  nickname: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少6位', trigger: 'blur' }
  ],
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }]
}

onMounted(() => {
  fetchAdmins()
})

const fetchAdmins = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/admin/admin/list')
    adminList.value = res.data
  } catch (error) {
    console.error('获取管理员失败', error)
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  form.value = {
    username: '',
    password: '',
    nickname: ''
  }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!form.value.username || !form.value.password || !form.value.nickname) {
    ElMessage.warning('请填写完整信息')
    return
  }

  formLoading.value = true
  try {
    await request.post('/api/admin/admin/create', form.value)
    ElMessage.success('管理员创建成功')
    dialogVisible.value = false
    fetchAdmins()
  } catch (error) {
    console.error('创建失败', error)
  } finally {
    formLoading.value = false
  }
}

const handleDelete = async (admin) => {
  await ElMessageBox.confirm(`确定要删除管理员 "${admin.nickname}" 吗？`, '提示', {
    type: 'warning'
  })
  try {
    await request.put('/api/admin/admin/status', {
      id: admin.id,
      status: 0
    })
    ElMessage.success('已禁用')
    fetchAdmins()
  } catch (error) {
    console.error('操作失败', error)
  }
}

const handleEnable = async (admin) => {
  try {
    await request.put('/api/admin/admin/status', {
      id: admin.id,
      status: 1
    })
    ElMessage.success('已启用')
    fetchAdmins()
  } catch (error) {
    console.error('操作失败', error)
  }
}
</script>

<template>
  <div class="admin-manage">
    <div class="page-header">
      <h2>管理员管理</h2>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon> 添加管理员
      </el-button>
    </div>

    <el-card>
      <el-table :data="adminList" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="150" />
        <el-table-column prop="nickname" label="昵称" width="150" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 0"
              text
              type="success"
              @click="handleEnable(row)"
            >
              启用
            </el-button>
            <el-button
              v-else
              text
              type="danger"
              @click="handleDelete(row)"
            >
              禁用
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加管理员对话框 -->
    <el-dialog v-model="dialogVisible" title="添加管理员" width="400px">
      <el-form :model="form" :rules="rules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="form.nickname" placeholder="请输入昵称" />
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
</style>
