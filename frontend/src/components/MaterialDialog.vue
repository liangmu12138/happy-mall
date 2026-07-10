<script setup>
import { ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import request from '../utils/request'
import { ElMessage } from 'element-plus'

const router = useRouter()

const props = defineProps({
  visible: Boolean
})

const emit = defineEmits(['update:visible', 'close'])

const loading = ref(false)
const generatedContent = ref('')
const activeStep = ref(0)

const form = ref({
  title: '',
  category: '',
  subject: '',
  content: '',
  price: 0
})

const categories = ['教材', '笔记', '考研', '考级', '课件', '其他']

// 监听弹窗打开，重置表单
watch(() => props.visible, (val) => {
  if (val) {
    activeStep.value = 0
    generatedContent.value = ''
    form.value = {
      title: '',
      category: '',
      subject: '',
      content: '',
      price: 0
    }
  }
})

const handleGenerate = async () => {
  if (!form.value.title || !form.value.category) {
    ElMessage.warning('请填写资料标题和分类')
    return
  }

  loading.value = true
  try {
    const res = await request.post('/api/ai/generate/material', form.value)
    generatedContent.value = res.data.description
    activeStep.value = 1
  } catch (error) {
    console.error('生成失败', error)
    ElMessage.error('生成失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

const copyContent = () => {
  navigator.clipboard.writeText(generatedContent.value)
  ElMessage.success('已复制到剪贴板')
}

const handleClose = () => {
  emit('close', generatedContent.value)
  emit('update:visible', false)
}

const goToPost = () => {
  copyContent()
  emit('update:visible', false)
  router.push('/study/material')
}
</script>

<template>
  <el-dialog
    :model-value="visible"
    @update:model-value="(val) => emit('update:visible', val)"
    title="📝 AI生成资料描述"
    width="650px"
    :close-on-click-modal="false"
    @close="handleClose"
    top="8vh"
    destroy-on-close
  >
    <!-- 步骤条 -->
    <el-steps :active="activeStep" finish-status="success" align-center style="margin-bottom: 24px;">
      <el-step title="填写信息" />
      <el-step title="生成描述" />
    </el-steps>

    <!-- 第一步：填写表单 -->
    <div v-if="activeStep === 0">
      <el-form :model="form" label-width="80px">
        <el-form-item label="资料标题" required>
          <el-input v-model="form.title" placeholder="如：高等数学上册笔记" />
        </el-form-item>

        <el-form-item label="分类" required>
          <el-select v-model="form.category" placeholder="请选择分类" style="width: 100%" :teleported="false">
            <el-option
              v-for="cat in categories"
              :key="cat"
              :label="cat"
              :value="cat"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="学科">
          <el-input v-model="form.subject" placeholder="如：高等数学" />
        </el-form-item>

        <el-form-item label="资料内容">
          <el-input v-model="form.content" type="textarea" :rows="3" placeholder="简述资料包含的内容，AI会帮你生成更完整的描述" />
        </el-form-item>

        <el-form-item label="价格">
          <el-input-number v-model="form.price" :min="0" :max="999" />
          <span style="margin-left: 12px; color: #999; font-size: 13px;">设置为0表示免费分享</span>
        </el-form-item>
      </el-form>
    </div>

    <!-- 第二步：生成结果 -->
    <div v-if="activeStep === 1">
      <el-alert
        title="✅ AI已为你生成资料描述"
        type="success"
        :closable="false"
        show-icon
        style="margin-bottom: 16px;"
      />
      <div style="background: #f5f7fa; border: 1px solid #e4e7ed; border-radius: 8px; padding: 20px; min-height: 200px; max-height: 350px; overflow-y: auto;">
        <pre style="margin: 0; white-space: pre-wrap; word-wrap: break-word; font-family: inherit; font-size: 14px; line-height: 1.8; color: #303133;">{{ generatedContent }}</pre>
      </div>
      <div style="margin-top: 12px; padding: 12px; background: #fdf6ec; border-radius: 6px; font-size: 13px; color: #e6a23c;">
        💡 你可以复制内容后去发布资料，也可以根据需要修改
      </div>
    </div>

    <template #footer>
      <div style="display: flex; justify-content: flex-end; gap: 10px;">
        <template v-if="activeStep === 0">
          <el-button @click="emit('update:visible', false)">取消</el-button>
          <el-button type="primary" @click="handleGenerate" :loading="loading">
            ✨ 生成描述
          </el-button>
        </template>
        <template v-else>
          <el-button @click="activeStep = 0">返回修改</el-button>
          <el-button @click="copyContent">📋 复制内容</el-button>
          <el-button type="primary" @click="goToPost">
            🚀 去发布资料
          </el-button>
        </template>
      </div>
    </template>
  </el-dialog>
</template>

<style>
/* 确保下拉框显示在弹窗之上 */
.material-select-popper {
  z-index: 3000 !important;
}
</style>
