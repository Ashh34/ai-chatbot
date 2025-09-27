<template>
  <div :class="['bubble', sender]">
    {{ text }}
  </div>
</template>

<script setup>
defineProps({
  text: String,
  sender: String
})
</script>

<style scoped>
.bubble {
  padding: 10px 15px;
  border-radius: 16px;
  margin: 5px;
  max-width: 70%;
  word-wrap: break-word;
}
.bubble.user {
  background: #4caf50;
  color: white;
  align-self: flex-end;
  margin-left: auto;
}
.bubble.bot {
  background: #e0e0e0;
  align-self: flex-start;
  margin-right: auto;
}
</style>