<template>
  <div class="chat-app">
    <h2>AI Chatbot 🤖</h2>
    <ChatWindow :messages="messages" :chats-left="chatsLeft" />
    <ChatInput 
      :disabled="chatsLeft <= 0" 
      :loading="loading"
      @send="sendMessage" 
      @reset="resetChat" 
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import ChatWindow from '../components/ChatWindow.vue'
import ChatInput from '../components/ChatInput.vue'
import axios from 'axios'

const messages = ref([])
const chatsLeft = ref(100)
const loading = ref(false)

// Use relative path for development (Vite proxy)
const API = import.meta.env.MODE === 'development' 
  ? '/api' 
  : 'http://localhost:8080/api'

async function sendMessage(userText) {
  if (!userText.trim() || chatsLeft.value <= 0) return
  
  loading.value = true
  messages.value.push({ sender: "user", text: userText })

  try {
    const response = await axios.post(`${API}/chat`, { message: userText })
    messages.value.push({ sender: "bot", text: response.data })
    
    // Update chats left
    await updateChatsLeft()
  } catch (error) {
    console.error('Error sending message:', error)
    messages.value.push({ 
      sender: "bot", 
      text: "Sorry, I encountered an error. Please try again." 
    })
  } finally {
    loading.value = false
  }
}

async function resetChat() {
  try {
    await axios.post(`${API}/reset`)
    messages.value = []
    await updateChatsLeft()
  } catch (error) {
    console.error('Error resetting chat:', error)
  }
}

async function updateChatsLeft() {
  try {
    const response = await axios.get(`${API}/status`)
    chatsLeft.value = response.data.chatsLeft || response.data
  } catch (error) {
    console.error('Error fetching status:', error)
  }
}

onMounted(async () => {
  await updateChatsLeft()
})
</script>

<style scoped>
.chat-app {
  background: white;
  border-radius: 12px;
  padding: 20px;
  max-width: 600px;
  margin: 20px auto;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

h2 {
  text-align: center;
  color: #333;
  margin-bottom: 20px;
}
</style>