<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'

const router = useRouter()
const webSocket = ref(null)
const messages = ref([])
const inputMessage = ref('')
const currentUser = JSON.parse(localStorage.getItem('loginUser'))

// 格式化时间
const formatTime = (timestamp) => {
  const date = new Date(timestamp)
  return `${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}:${date.getSeconds().toString().padStart(2, '0')}`
}

// 连接 WebSocket
const connectWebSocket = () => {
  // 替换为你的 Java 后端 WebSocket 地址
  const wsUrl = `ws://localhost:8080/chat/${currentUser.username}?token=${currentUser.token}`
  webSocket.value = new WebSocket(wsUrl)
  
  webSocket.value.onopen = () => {
    ElMessage.success('已连接到聊天室')
  }
  
  webSocket.value.onmessage = (event) => {
    const message = JSON.parse(event.data)
    messages.value.push(message)
  }
  
  webSocket.value.onclose = () => {
    ElMessage.info('与聊天室断开连接')
  }
  
  webSocket.value.onerror = (error) => {
    ElMessage.error('WebSocket 连接错误')
    console.error('WebSocket error:', error)
  }
}

// 发送消息
const sendMessage = () => {
  if (!inputMessage.value.trim()) {
    return
  }
  
  if (webSocket.value && webSocket.value.readyState === WebSocket.OPEN) {
    const message = {
      username: currentUser.username,
      content: inputMessage.value,
      image : currentUser.image,
      timestamp: new Date().getTime()
    }
    
    webSocket.value.send(JSON.stringify(message))
    inputMessage.value = ''
  } else {
    ElMessage.error('未连接到聊天室')
  }
}

onMounted(() => {
  connectWebSocket()
})

onUnmounted(() => {
  if (webSocket.value) {
    webSocket.value.close()
  }
})
</script>


<template>
  <div class="chat-container">
    <div class="chat-header">
      <h2>聊天室</h2>
    </div>
    <div class="chat-messages">
      <div v-for="message in messages" :key="message.timestamp" 
           :class="['message-item', { 'current-user-message': message.username === currentUser.username }]">
        <template v-if="message.username !== currentUser.username">
          <!-- 其他用户的消息 -->
          <img :src="message.image" alt="头像" class="message-avatar" v-if="message.image">
          <div class="message-content">
            <div class="message-user">{{ message.username }}</div>
            <div class="message-text">{{ message.content }}</div>
            <div class="message-time">{{ formatTime(message.timestamp) }}</div>
          </div>
        </template>
        <template v-else>
          <!-- 当前用户的消息 -->
          <div class="message-content">
            <div class="message-user">{{ message.username }}</div>
            <div class="message-text">{{ message.content }}</div>
            <div class="message-time">{{ formatTime(message.timestamp) }}</div>
          </div>
          <img :src="message.image" alt="头像" class="message-avatar" v-if="message.image">
        </template>
      </div>
    </div>
    <div class="chat-input-area">
      <el-input
        v-model="inputMessage"
        placeholder="输入消息..."
        @keyup.enter="sendMessage"
        class="message-input"
      />
      <el-button type="primary" @click="sendMessage" class="send-button">发送</el-button>
    </div>
  </div>
</template>



<style scoped>
.chat-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 20px;
}

.chat-header {
  border-bottom: 1px solid #eee;
  padding-bottom: 15px;
  margin-bottom: 15px;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 10px;
  background-color: #f5f5f5;
  border-radius: 4px;
  margin-bottom: 15px;
  max-height: calc(100vh - 250px);
}

.message-item {
  display: flex;
  align-items: flex-start;
  padding: 8px 0;
  border-bottom: 1px solid #eee;
}

.message-item.current-user-message {
  flex-direction: row-reverse;
}

.message-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  margin: 0 10px;
}

.message-content {
  display: flex;
  flex-direction: column;
}

.message-user {
  font-weight: bold;
  color: #409eff;
  margin-bottom: 4px;
}

.message-text {
  padding: 8px 12px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
  margin-bottom: 4px;
  max-width: 300px;
}

.message-item.current-user-message .message-text {
  background-color: #409eff;
  color: white;
}

.message-time {
  color: #999;
  font-size: 12px;
}

.chat-input-area {
  display: flex;
  gap: 10px;
}

.message-input {
  flex: 1;
}

.send-button {
  width: 80px;
}
</style>