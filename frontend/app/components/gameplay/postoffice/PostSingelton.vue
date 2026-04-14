<template>
  <div class="post-singleton">
    <img :src="data.avatarImageurl" alt="Sender avatar" />
    <p class="sender">{{ data.sender }}</p>
    <h2>{{ data.subject }}</h2>
    <p>{{ data.content }}</p>
    <div class="actions">
      <button @click="answer(true)">Troverdig</button>
      <button @click="answer(false)">Svindel</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { TaskPostSingleton } from '~/composables/types/TaskTypes'

const props = defineProps<{
  data: TaskPostSingleton
}>()

const emit = defineEmits<{
  (e: 'answered', correct: boolean): void
}>()

function answer(userSaidGood: boolean) {
  const correct = userSaidGood === props.data.isValid;
  emit('answered', correct)
}
</script>

<style scoped></style>