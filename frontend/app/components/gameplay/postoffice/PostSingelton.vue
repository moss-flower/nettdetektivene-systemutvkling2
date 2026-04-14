<template>
  <div class="post-singleton">
    <img :src="data.imageurl" alt="Email" />
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