<template>
  <div class="chat-window">
    <div v-for="(msg, index) in messages" :key="index" class="message-container" :class="msg.sender">
      <ChatBubble :text="msg.text" :sender="msg.sender" />
    </div>
    <!-- Removed chats-left display -->
  </div>
</template>

<script setup>
import ChatBubble from './ChatBubble.vue'

defineProps({
  messages: Array
})
</script>

<style scoped>
.chat-window {
  height: 400px;
  overflow-y: auto;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 10px;
  margin-bottom: 10px;
  background: #f9f9f9;
}

.message-container {
  display: flex;
  margin: 10px 0;
}

.message-container.user {
  justify-content: flex-end;
}

.message-container.bot {
  justify-content: flex-start;
}
</style>