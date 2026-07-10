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
  buddyType: '自习',
  title: '',
  description: '',
  target: '',
  location: '',
  timeInfo: '',
  maxMembers: 2
})

const buddyTypes = ['自习', '考研', '四六级', '编程', '其他']

// 监听弹窗打开，重置表单
watch(() => props.visible, (val) => {
  if (val) {
    activeStep.value = 0
    generatedContent.value = ''
    form.value = {
      buddyType: '自习',
      title: '',
      description: '',
      target: '',
      location: '',
      timeInfo: '',
      maxMembers: 2
    }
  }
})

const handleGenerate = async () => {
  if (!form.value.title) {
    ElMessage.warning('请填写标题')
    return
  }

  loading.value = true
  try {
    const res = await request.post('/api/ai/generate/buddy', form.value)
    generatedContent.value = res.data.content
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
  router.push('/study/buddy')
}
</script>

<template>
  <el-dialog
    :model-value="visible"
    @update:model-value="(val) => emit('update:visible', val)"
    title="👥 AI生成搭子帖子"
    width="650px"
    :close-on-click-modal="false"
    @close="handleClose"
    top="8vh"
    destroy-on-close
  >
    <!-- 步骤条 -->
    <el-steps :active="activeStep" finish-status="success" align-center style="margin-bottom: 24px;">
      <el-step title="填写信息" />
      <el-step title="生成帖子" />
    </el-steps>

    <!-- 第一步：填写表单 -->
    <div v-if="activeStep === 0">
      <el-form :model="form" label-width="80px">
        <el-form-item label="搭子类型" required>
          <el-select v-model="form.buddyType" placeholder="请选择类型" style="width: 100%" :teleported="false">
            <el-option
              v-for="type in buddyTypes"
              :key="type"
              :label="type"
              :value="type"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="标题" required>
          <el-input v-model="form.title" placeholder="如：期末复习自习搭子" />
        </el-form-item>

        <el-form-item label="学习地点">
          <el-input v-model="form.location" placeholder="如：图书馆三楼" />
        </el-form-item>

        <el-form-item label="学习时间">
          <el-input v-model="form.timeInfo" placeholder="如：每天晚上7-10点" />
        </el-form-item>

        <el-form-item label="学习目标">
          <el-input v-model="form.target" placeholder="如：期末考试全科通过" />
        </el-form-item>

        <el-form-item label="补充说明">
          <el-input v-model="form.description" type="textarea" :rows="2" placeholder="其他想说的（选填）" />
        </el-form-item>

        <el-form-item label="招募人数">
          <el-input-number v-model="form.maxMembers" :min="2" :max="10" />
        </el-form-item>
      </el-form>
    </div>

    <!-- 第二步：生成结果 -->
    <div v-if="activeStep === 1">
      <el-alert
        title="✅ AI已为你生成搭子帖子"
        type="success"
        :closable="false"
        show-icon
        style="margin-bottom: 16px;"
      />
      <div style="background: #f5f7fa; border: 1px solid #e4e7ed; border-radius: 8px; padding: 20px; min-height: 200px; max-height: 350px; overflow-y: auto;">
        <pre style="margin: 0; white-space: pre-wrap; word-wrap: break-word; font-family: inherit; font-size: 14px; line-height: 1.8; color: #303133;">{{ generatedContent }}</pre>
      </div>
      <div style="margin-top: 12px; padding: 12px; background: #fdf6ec; border-radius: 6px; font-size: 13px; color: #e6a23c;">
        💡 你可以复制内容后去发布搭子，也可以根据需要修改
      </div>
    </div>

    <template #footer>
      <div style="display: flex; justify-content: flex-end; gap: 10px;">
        <template v-if="activeStep === 0">
          <el-button @click="emit('update:visible', false)">取消</el-button>
          <el-button type="primary" @click="handleGenerate" :loading="loading">
            ✨ 生成帖子
          </el-button>
        </template>
        <template v-else>
          <el-button @click="activeStep = 0">返回修改</el-button>
          <el-button @click="copyContent">📋 复制内容</el-button>
          <el-button type="primary" @click="goToPost">
            🚀 去发布搭子
          </el-button>
        </template>
      </div>
    </template>
  </el-dialog>
</template>

<style>
/* 确保下拉框显示在弹窗之上 */
.buddy-select-popper {
  z-index: 3000 !important;
}
</style>
