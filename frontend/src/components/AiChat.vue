<script setup>
import { ref, onMounted, nextTick, watch } from 'vue'
import { useUserStore } from '../stores/user'
import request from '../utils/request'
import { ChatDotRound, Promotion, Close, ChatLineSquare } from '@element-plus/icons-vue'

const userStore = useUserStore()

const isOpen = ref(false)
const inputMessage = ref('')
const messages = ref([])
const isLoading = ref(false)
const chatContainer = ref(null)

// 快捷问题
const quickQuestions = [
  '怎么高效学习？',
  '如何提高记忆力？',
  '期末怎么复习？',
  '时间怎么安排？',
  '怎么找学习搭子？',
  '挂科了怎么办？',
  '大学值得考什么证？',
  '压力大怎么办？',
  '怎么做好笔记？',
  '高数怎么学？',
  '英语怎么提高？',
  '编程怎么入门？'
]

onMounted(async () => {
  await fetchWelcomeMessage()
})

watch(messages, () => {
  nextTick(() => {
    scrollToBottom()
  })
}, { deep: true })

const scrollToBottom = () => {
  if (chatContainer.value) {
    chatContainer.value.scrollTop = chatContainer.value.scrollHeight
  }
}

const fetchWelcomeMessage = async () => {
  try {
    const res = await request.get('/api/ai/welcome')
    // res = { code: 200, data: { content, role, timestamp }, message: "..." }
    if (res.data) {
      messages.value = [res.data]
    }
  } catch (error) {
    console.error('获取欢迎消息失败', error)
    // 添加默认欢迎消息
    messages.value = [{
      content: '你好！👋 我是Happy Campus的AI学习助手！\n\n我可以帮你：\n📚 学习方法指导\n📝 考试技巧分享\n⏰ 时间管理建议\n\n有什么问题尽管问我！',
      role: 'assistant',
      timestamp: new Date().toISOString()
    }]
  }
}

const sendMessage = async (message = inputMessage.value) => {
  if (!message.trim() || isLoading.value) return

  const userMsg = {
    content: message,
    role: 'user',
    timestamp: new Date().toISOString()
  }
  messages.value.push(userMsg)
  inputMessage.value = ''
  isLoading.value = true

  try {
    const res = await request.post('/api/ai/chat', { message })
    // res = { code: 200, data: { content, role, timestamp }, message: "..." }
    if (res.data) {
      messages.value.push(res.data)
    }
  } catch (error) {
    console.error('发送消息失败', error)
    messages.value.push({
      content: '抱歉，网络开小差了，请稍后再试～',
      role: 'assistant',
      timestamp: new Date().toISOString()
    })
  } finally {
    isLoading.value = false
    nextTick(() => scrollToBottom())
  }
}

const handleKeydown = (e) => {
  if (e.key === 'Enter' && !e.shiftKey) {
    e.preventDefault()
    sendMessage()
  }
}

const toggleChat = () => {
  isOpen.value = !isOpen.value
  if (isOpen.value && messages.value.length === 0) {
    fetchWelcomeMessage()
  }
}

const formatTime = (timestamp) => {
  const date = new Date(timestamp)
  const hours = date.getHours().toString().padStart(2, '0')
  const minutes = date.getMinutes().toString().padStart(2, '0')
  return `${hours}:${minutes}`
}

const formatMessage = (content) => {
  if (!content) return ''
  return content.replace(/\n/g, '<br>')
}
</script>

<template>
  <div class="ai-chat-container">
    <!-- 浮动按钮 -->
    <div class="chat-float-btn" @click="toggleChat" :class="{ active: isOpen }">
      <el-icon v-if="!isOpen"><ChatDotRound /></el-icon>
      <el-icon v-else><Close /></el-icon>
    </div>

    <!-- 聊天窗口 -->
    <div class="chat-window" v-show="isOpen">
      <!-- 头部 -->
      <div class="chat-header">
        <div class="header-left">
          <el-icon :size="20"><ChatLineSquare /></el-icon>
          <span>AI学习助手</span>
        </div>
        <div class="header-right">
          <span class="status-dot"></span>
          <span class="status-text">在线</span>
        </div>
      </div>

      <!-- 快捷问题 -->
      <div class="quick-questions" v-if="messages.length <= 1">
        <div class="questions-title">💡 热门问题：</div>
        <div class="questions-list">
          <div
            v-for="(q, index) in quickQuestions"
            :key="index"
            class="question-tag"
            @click="sendMessage(q)"
          >
            {{ q }}
          </div>
        </div>
      </div>

      <!-- 消息区域 -->
      <div class="chat-messages" ref="chatContainer">
        <div
          v-for="(msg, index) in messages"
          :key="index"
          class="message-item"
          :class="msg.role"
        >
          <div class="avatar">
            <span v-if="msg.role === 'user'">
              {{ userStore.userInfo?.nickname?.charAt(0) || 'U' }}
            </span>
            <span v-else>🤖</span>
          </div>
          <div class="message-content">
            <div class="message-text" v-html="formatMessage(msg.content)"></div>
            <div class="message-time">{{ formatTime(msg.timestamp) }}</div>
          </div>
        </div>

        <!-- 加载中 -->
        <div class="message-item assistant" v-if="isLoading">
          <div class="avatar">🤖</div>
          <div class="message-content">
            <div class="typing-indicator">
              <span></span>
              <span></span>
              <span></span>
            </div>
          </div>
        </div>
      </div>

      <!-- 输入区域 -->
      <div class="chat-input">
        <div class="input-wrapper">
          <el-input
            v-model="inputMessage"
            placeholder="输入你的问题..."
            @keydown="handleKeydown"
            :disabled="isLoading"
            size="large"
          />
          <el-button
            type="primary"
            :icon="Promotion"
            @click="sendMessage()"
            :loading="isLoading"
            :disabled="!inputMessage.trim()"
            circle
            size="large"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.ai-chat-container {
  position: fixed;
  bottom: 30px;
  right: 30px;
  z-index: 9999;
}

.chat-float-btn {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.4);
  transition: all 0.3s;
  font-size: 24px;
}

.chat-float-btn:hover {
  transform: scale(1.1);
  box-shadow: 0 6px 25px rgba(102, 126, 234, 0.5);
}

.chat-float-btn.active {
  background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
}

.chat-window {
  position: absolute;
  bottom: 80px;
  right: 0;
  width: 380px;
  height: 550px;
  background: white;
  border-radius: 20px;
  box-shadow: 0 10px 50px rgba(0, 0, 0, 0.25);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.chat-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 16px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 16px;
  font-weight: 600;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 6px;
}

.status-dot {
  width: 8px;
  height: 8px;
  background: #67c23a;
  border-radius: 50%;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.status-text {
  font-size: 12px;
  opacity: 0.9;
}

/* 快捷问题 */
.quick-questions {
  padding: 12px;
  background: #f8f9fb;
  border-bottom: 1px solid #eee;
}

.questions-title {
  font-size: 12px;
  color: #999;
  margin-bottom: 8px;
}

.questions-list {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.question-tag {
  padding: 6px 12px;
  background: white;
  border: 1px solid #e4e7ed;
  border-radius: 16px;
  font-size: 12px;
  color: #667eea;
  cursor: pointer;
  transition: all 0.3s;
}

.question-tag:hover {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-color: transparent;
}

/* 消息区域 */
.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  background: #f8f9fb;
}

.message-item {
  display: flex;
  gap: 10px;
  margin-bottom: 16px;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(8px); }
  to { opacity: 1; transform: translateY(0); }
}

.message-item.user {
  flex-direction: row-reverse;
}

.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  flex-shrink: 0;
}

.message-item.user .avatar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.message-item.assistant .avatar {
  background: #f0f0f0;
}

.message-content {
  max-width: 80%;
}

.message-text {
  padding: 12px 14px;
  border-radius: 14px;
  font-size: 14px;
  line-height: 1.6;
  word-break: break-word;
}

.message-item.user .message-text {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-bottom-right-radius: 4px;
}

.message-item.assistant .message-text {
  background: white;
  color: #333;
  border-bottom-left-radius: 4px;
  box-shadow: 0 1px 6px rgba(0, 0, 0, 0.05);
}

.message-time {
  font-size: 11px;
  color: #999;
  margin-top: 4px;
  padding: 0 4px;
}

.message-item.user .message-time {
  text-align: right;
}

/* 打字动画 */
.typing-indicator {
  display: flex;
  gap: 4px;
  padding: 14px 18px;
  background: white;
  border-radius: 14px;
  box-shadow: 0 1px 6px rgba(0, 0, 0, 0.05);
}

.typing-indicator span {
  width: 8px;
  height: 8px;
  background: #ccc;
  border-radius: 50%;
  animation: bounce 1.4s infinite ease-in-out;
}

.typing-indicator span:nth-child(1) { animation-delay: -0.32s; }
.typing-indicator span:nth-child(2) { animation-delay: -0.16s; }

@keyframes bounce {
  0%, 80%, 100% { transform: scale(0); }
  40% { transform: scale(1); }
}

/* 输入区域 */
.chat-input {
  padding: 12px 16px;
  background: white;
  border-top: 1px solid #eee;
}

.input-wrapper {
  display: flex;
  gap: 8px;
  align-items: center;
}

.input-wrapper :deep(.el-input) {
  flex: 1;
}

.input-wrapper :deep(.el-button) {
  flex-shrink: 0;
}
</style>
