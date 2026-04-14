<template>
  <div class="newsstand-singleton">
    <div class="article" @click="answer('correct')">
      <img :src="data.correct.imageurl" alt="Article A" />
      <h2>{{ data.correct.title }}</h2>
      <p class="author">{{ data.correct.author }}</p>
      <p>{{ data.correct.text }}</p>
      <button @click.stop="answer('correct')">Denne er ekte</button>
    </div>

    <div class="article" @click="answer('fake')">
      <img :src="data.fake.imageurl" alt="Article B" />
      <h2>{{ data.fake.title }}</h2>
      <p class="author">{{ data.fake.author }}</p>
      <p>{{ data.fake.text }}</p>
      <button @click.stop="answer('fake')">Denne er ekte</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { TaskNewsStandSingleton } from '~/composables/types/TaskTypes'

const props = defineProps<{
  data: TaskNewsStandSingleton
}>()

const emit = defineEmits<{
  (e: 'answered', correct: boolean): void
}>()

function answer(choice: 'correct' | 'fake') {
  emit('answered', choice === 'correct')
}
</script>
