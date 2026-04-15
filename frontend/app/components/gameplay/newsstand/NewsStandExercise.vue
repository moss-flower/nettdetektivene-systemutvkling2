<script setup lang="ts">
import type { NewsStandPair, NewsStandTask } from '~/composables/types/TaskTypes'

const props = defineProps<{
  data: NewsStandPair
}>()

const emit = defineEmits<{
  (e: 'answered', correct: boolean): void
}>()

const currentExercise = ref<number>(0)

function answer(choice: 'correct' | 'fake') {
  emit('answered', choice === 'correct')
}
</script>

<template>
  <div class="newsstand-singleton">
    <div class="article" @click="answer('correct')">
      <img
        :src="props.data.correct.imageUrl"
        alt="Article A"
      />
      <h2>{{ props.data.correct.title }}</h2>
      <p class="author">{{ props.data.correct.author }}</p>
      <p>{{ props.data.correct.text }}</p>
      <button @click.stop="answer('correct')">Denne er ekte</button>
    </div>

    <div class="article" @click="answer('fake')">
      <img
        :src="props.data.fake.imageUrl"
        alt="Article B"
      />
      <h2>{{ props.data.fake.title }}</h2>
      <p class="author">{{ props.data.fake.author }}</p>
      <p>{{ props.data.fake.text }}</p>
      <button @click.stop="answer('fake')">Denne er ekte</button>
    </div>
  </div>
</template>