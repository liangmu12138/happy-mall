<script setup>
import { ref, onMounted } from 'vue'
import request from '../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const categoryList = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const formLoading = ref(false)

const form = ref({
  id: null,
  name: '',
  parentId: 0,
  sort: 0,
  status: 1
})

onMounted(() => {
  fetchCategories()
})

const fetchCategories = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/admin/category/list')
    categoryList.value = res.data
  } catch (error) {
    console.error('获取分类失败', error)
  } finally {
    loading.value = false
  }
}

const handleAdd = (parentId = 0) => {
  form.value = {
    id: null,
    name: '',
    parentId: parentId,
    sort: 0,
    status: 1
  }
  dialogVisible.value = true
}

const handleEdit = (category) => {
  form.value = { ...category }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!form.value.name) {
    ElMessage.warning('请输入分类名称')
    return
  }

  formLoading.value = true
  try {
    if (form.value.id) {
      await request.put('/api/admin/category/update', form.value)
      ElMessage.success('更新成功')
    } else {
      await request.post('/api/admin/category/add', form.value)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    fetchCategories()
  } catch (error) {
    console.error('保存失败', error)
  } finally {
    formLoading.value = false
  }
}

const handleDelete = async (id) => {
  await ElMessageBox.confirm('确定要删除该分类吗？', '提示', { type: 'warning' })
  try {
    await request.delete(`/api/admin/category/${id}`)
    ElMessage.success('删除成功')
    fetchCategories()
  } catch (error) {
    console.error('删除失败', error)
  }
}

// 构建树形结构
const buildTree = (list, parentId = 0) => {
  return list
    .filter(item => item.parentId === parentId)
    .map(item => ({
      ...item,
      children: buildTree(list, item.id)
    }))
}

const treeData = computed(() => buildTree(categoryList.value))
</script>

<template>
  <div class="category-page">
    <div class="page-header">
      <h2>分类管理</h2>
      <el-button type="primary" @click="handleAdd()">
        <el-icon><Plus /></el-icon> 新增分类
      </el-button>
    </div>

    <el-card>
      <el-table :data="treeData" v-loading="loading" row-key="id" default-expand-all>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="分类名称" min-width="200" />
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button text type="primary" @click="handleAdd(row.id)">添加子分类</el-button>
            <el-button text type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button text type="danger" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑分类' : '新增分类'" width="400px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="分类名称" required>
          <el-input v-model="form.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="父分类">
          <el-select v-model="form.parentId" placeholder="无" clearable>
            <el-option label="顶级分类" :value="0" />
            <el-option
              v-for="cat in categoryList.filter(c => c.parentId === 0)"
              :key="cat.id"
              :label="cat.name"
              :value="cat.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" />
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
</style>
