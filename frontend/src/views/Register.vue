<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const form = ref({
  username: '',
  password: '',
  confirmPassword: '',
  nickname: '',
  studentCardId: '',
  school: '',
  major: '',
  grade: '',
  className: ''
})
const loading = ref(false)
const currentStep = ref(0)

const handleRegister = async () => {
  if (currentStep.value === 0) {
    // 验证基本信息
    if (!form.value.username || !form.value.password) {
      ElMessage.warning('请输入用户名和密码')
      return
    }
    if (!form.value.studentCardId) {
      ElMessage.warning('请输入学生卡ID')
      return
    }
    if (form.value.password !== form.value.confirmPassword) {
      ElMessage.warning('两次输入的密码不一致')
      return
    }
    if (form.value.password.length < 6) {
      ElMessage.warning('密码长度不能少于6位')
      return
    }
    currentStep.value = 1
    return
  }

  loading.value = true
  try {
    await userStore.register(form.value)
    ElMessage.success('注册成功')
    router.push('/')
  } catch (error) {
    console.error('注册失败', error)
  } finally {
    loading.value = false
  }
}

const goBack = () => {
  currentStep.value = 0
}
</script>

<template>
  <div class="register-container">
    <div class="register-card">
      <h2 class="register-title">🎓 学生注册</h2>

      <!-- 步骤指示器 -->
      <el-steps :active="currentStep" finish-status="success" align-center class="steps">
        <el-step title="账号信息" />
        <el-step title="学校信息" />
      </el-steps>

      <!-- 第一步：账号信息 -->
      <el-form v-if="currentStep === 0" :model="form" @keyup.enter="handleRegister">
        <el-form-item label="学生卡ID" required>
          <el-input
            v-model="form.studentCardId"
            placeholder="请输入学生卡ID"
            size="large"
          />
          <div class="form-tip">用于验证学生身份，请填写真实的学生卡号</div>
        </el-form-item>

        <el-form-item label="用户名" required>
          <el-input
            v-model="form.username"
            placeholder="请输入用户名"
            size="large"
          />
        </el-form-item>

        <el-form-item label="昵称">
          <el-input
            v-model="form.nickname"
            placeholder="请输入昵称（选填）"
            size="large"
          />
        </el-form-item>

        <el-form-item label="密码" required>
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码（至少6位）"
            size="large"
            show-password
          />
        </el-form-item>

        <el-form-item label="确认密码" required>
          <el-input
            v-model="form.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            size="large"
            show-password
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            @click="handleRegister"
            style="width: 100%"
          >
            下一步
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 第二步：学校信息 -->
      <el-form v-else :model="form" @keyup.enter="handleRegister">
        <el-form-item label="学校名称">
          <el-input
            v-model="form.school"
            placeholder="请输入学校名称"
            size="large"
          />
        </el-form-item>

        <el-form-item label="专业">
          <el-input
            v-model="form.major"
            placeholder="请输入专业"
            size="large"
          />
        </el-form-item>

        <el-form-item label="年级">
          <el-select v-model="form.grade" placeholder="请选择年级" size="large" style="width: 100%">
            <el-option label="大一" value="大一" />
            <el-option label="大二" value="大二" />
            <el-option label="大三" value="大三" />
            <el-option label="大四" value="大四" />
            <el-option label="研一" value="研一" />
            <el-option label="研二" value="研二" />
            <el-option label="研三" value="研三" />
          </el-select>
        </el-form-item>

        <el-form-item label="班级">
          <el-input
            v-model="form.className"
            placeholder="请输入班级（如：计算机2001班）"
            size="large"
          />
        </el-form-item>

        <el-form-item>
          <el-button size="large" @click="goBack" style="width: 48%">
            上一步
          </el-button>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            @click="handleRegister"
            style="width: 48%"
          >
            完成注册
          </el-button>
        </el-form-item>
      </el-form>

      <div class="register-footer">
        已有账号？
        <router-link to="/login">立即登录</router-link>
      </div>
    </div>
  </div>
</template>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 200px);
}

.register-card {
  width: 450px;
  padding: 40px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.register-title {
  text-align: center;
  margin-bottom: 20px;
  color: #333;
}

.steps {
  margin-bottom: 30px;
}

.form-tip {
  font-size: 12px;
  color: #999;
  margin-top: 5px;
}

.register-footer {
  text-align: center;
  margin-top: 20px;
}

.register-footer a {
  color: #667eea;
  text-decoration: none;
}
</style>
