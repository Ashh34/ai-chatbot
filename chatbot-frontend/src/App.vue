<template>
  <div id="app">
    <div class="chat-app">
      <h2>AI Chatbot 🤖 
        <span class="status-badge" :class="serviceStatus">{{ statusText }}</span>
      </h2>
      <ChatWindow :messages="messages" />
      <ChatInput 
        :loading="loading"
        @send="sendMessage" 
        @reset="resetChat" 
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import ChatWindow from './components/ChatWindow.vue'
import ChatInput from './components/ChatInput.vue'
import axios from 'axios'

const messages = ref([])
const loading = ref(false)
const serviceEnabled = ref(true)

const API = 'http://localhost:8081/api'

const serviceStatus = computed(() => serviceEnabled.value ? 'enabled' : 'disabled')
const statusText = computed(() => serviceEnabled.value ? 'Gemini Active' : 'Basic Mode')

async function sendMessage(userText) {
  if (!userText.trim()) return
  
  loading.value = true
  messages.value.push({ sender: "user", text: userText })

  try {
    const response = await axios.post(`${API}/chat`, { message: userText })
    messages.value.push({ sender: "bot", text: response.data })
    
    // Scroll to bottom
    setTimeout(() => {
      const chatWindow = document.querySelector('.chat-window')
      if (chatWindow) chatWindow.scrollTop = chatWindow.scrollHeight
    }, 100)
    
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
  } catch (error) {
    console.error('Error resetting chat:', error)
  }
}

onMounted(async () => {
  // No need to check status for limits anymore
})
</script>

<style>
/* Same styles as before */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  min-height: 100vh;
  padding: 20px;
}

#app {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
}

.chat-app {
  background: white;
  border-radius: 12px;
  padding: 20px;
  max-width: 600px;
  width: 100%;
  margin: 20px auto;
  box-shadow: 0 2px 20px rgba(0, 0, 0, 0.1);
}

h2 {
  text-align: center;
  color: #333;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

.status-badge {
  font-size: 0.6em;
  padding: 4px 12px;
  border-radius: 20px;
  font-weight: normal;
}

.status-badge.enabled {
  background-color: #4caf50;
  color: white;
}

.status-badge.disabled {
  background-color: #ff9800;
  color: white;
}
</style>