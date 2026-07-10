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
  courseName: '',
  teacherName: '',
  school: '',
  rating: 5,
  difficulty: 3,
  examDifficulty: 3,
  gradeScore: 3,
  content: '',
  tips: ''
})

// 监听弹窗打开，重置表单
watch(() => props.visible, (val) => {
  if (val) {
    activeStep.value = 0
    generatedContent.value = ''
    form.value = {
      courseName: '',
      teacherName: '',
      school: '',
      rating: 5,
      difficulty: 3,
      examDifficulty: 3,
      gradeScore: 3,
      content: '',
      tips: ''
    }
  }
})

const handleGenerate = async () => {
  if (!form.value.courseName) {
    ElMessage.warning('请填写课程名称')
    return
  }

  loading.value = true
  try {
    const res = await request.post('/api/ai/generate/review', form.value)
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
  router.push('/study/review')
}
</script>

<template>
  <el-dialog
    :model-value="visible"
    @update:model-value="(val) => emit('update:visible', val)"
    title="⭐ AI生成课程评价"
    width="650px"
    :close-on-click-modal="false"
    @close="handleClose"
    top="8vh"
    destroy-on-close
  >
    <!-- 步骤条 -->
    <el-steps :active="activeStep" finish-status="success" align-center style="margin-bottom: 24px;">
      <el-step title="填写评价" />
      <el-step title="生成内容" />
    </el-steps>

    <!-- 第一步：填写表单 -->
    <div v-if="activeStep === 0">
      <el-form :model="form" label-width="80px">
        <el-form-item label="课程名称" required>
          <el-input v-model="form.courseName" placeholder="如：高等数学A" />
        </el-form-item>

        <el-form-item label="教师姓名">
          <el-input v-model="form.teacherName" placeholder="如：张老师" />
        </el-form-item>

        <el-form-item label="学校">
          <el-input v-model="form.school" placeholder="如：清华大学" />
        </el-form-item>

        <el-form-item label="整体评分">
          <el-rate v-model="form.rating" show-score />
        </el-form-item>

        <el-form-item label="课程难度">
          <el-rate v-model="form.difficulty" />
        </el-form-item>

        <el-form-item label="给分情况">
          <el-rate v-model="form.gradeScore" />
        </el-form-item>

        <el-form-item label="评价内容">
          <el-input v-model="form.content" type="textarea" :rows="3" placeholder="你对这门课的真实感受（选填，AI会帮你润色）" />
        </el-form-item>

        <el-form-item label="学习建议">
          <el-input v-model="form.tips" type="textarea" :rows="2" placeholder="给学弟学妹的建议（选填）" />
        </el-form-item>
      </el-form>
    </div>

    <!-- 第二步：生成结果 -->
    <div v-if="activeStep === 1">
      <el-alert
        title="✅ AI已为你生成课程评价"
        type="success"
        :closable="false"
        show-icon
        style="margin-bottom: 16px;"
      />
      <div style="background: #f5f7fa; border: 1px solid #e4e7ed; border-radius: 8px; padding: 20px; min-height: 200px; max-height: 350px; overflow-y: auto;">
        <pre style="margin: 0; white-space: pre-wrap; word-wrap: break-word; font-family: inherit; font-size: 14px; line-height: 1.8; color: #303133;">{{ generatedContent }}</pre>
      </div>
      <div style="margin-top: 12px; padding: 12px; background: #fdf6ec; border-radius: 6px; font-size: 13px; color: #e6a23c;">
        💡 你可以复制内容后去发表评价，也可以根据需要修改
      </div>
    </div>

    <template #footer>
      <div style="display: flex; justify-content: flex-end; gap: 10px;">
        <template v-if="activeStep === 0">
          <el-button @click="emit('update:visible', false)">取消</el-button>
          <el-button type="primary" @click="handleGenerate" :loading="loading">
            ✨ 生成评价
          </el-button>
        </template>
        <template v-else>
          <el-button @click="activeStep = 0">返回修改</el-button>
          <el-button @click="copyContent">📋 复制内容</el-button>
          <el-button type="primary" @click="goToPost">
            🚀 去发表评价
          </el-button>
        </template>
      </div>
    </template>
  </el-dialog>
</template>
