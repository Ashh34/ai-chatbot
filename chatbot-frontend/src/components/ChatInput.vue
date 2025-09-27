<template>
  <div class="chat-input">
    <input 
      v-model="text" 
      :disabled="loading" 
      @keyup.enter="send" 
      placeholder="Type your message..." 
      class="input-field"
    />
    <button @click="send" :disabled="loading || !text.trim()" class="send-btn">
      {{ loading ? 'Sending...' : 'Send' }}
    </button>
    <button @click="reset" :disabled="loading" class="reset-btn">
      Reset
    </button>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const props = defineProps({ 
  loading: Boolean
})

const emit = defineEmits(['send', 'reset'])
const text = ref('')

function send() {
  if (!text.value.trim()) return
  emit('send', text.value.trim())
  text.value = ''
}

function reset() {
  emit('reset')
}
</script>

<style scoped>
/* Same styles as before */
.chat-input {
  display: flex;
  gap: 10px;
  align-items: center;
}

.input-field {
  flex: 1;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.input-field:disabled {
  background-color: #f5f5f5;
  cursor: not-allowed;
}

.send-btn, .reset-btn {
  padding: 10px 15px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s;
}

.send-btn {
  background-color: #4caf50;
  color: white;
}

.send-btn:hover:not(:disabled) {
  background-color: #45a049;
}

.send-btn:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

.reset-btn {
  background-color: #ff9800;
  color: white;
}

.reset-btn:hover:not(:disabled) {
  background-color: #e68900;
}

.reset-btn:disabled {
  background-color: #ffcc80;
  cursor: not-allowed;
}
</style>