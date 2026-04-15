<script setup lang="ts">
import type { NewsStandTask } from '~/composables/types/TaskTypes'
import { sampleAndShuffle } from '~/composables/utils/gameUtils'

const mockData: NewsStandTask = {
  id: "1",
  kind: "newsStand",
  description: "Hvilke nyheter er ekte og hvilke er falske?",
  articles: [
    {
      correct: { author: "NRK", title: "Ekte nyhet 1", imageUrl: "https://placehold.co/600x400?text=Real+1", text: "Dette er en ekte nyhetsartikkel." },
      fake: { author: "Ukjent", title: "Falsk nyhet 1", imageUrl: "https://placehold.co/600x400?text=Fake+1", text: "Dette er en falsk nyhetsartikkel." },
    },
    {
      correct: { author: "VG", title: "Ekte nyhet 2", imageUrl: "https://placehold.co/600x400?text=Real+2", text: "Dette er også en ekte nyhetsartikkel." },
      fake: { author: "Tull", title: "Falsk nyhet 2", imageUrl: "https://placehold.co/600x400?text=Fake+2", text: "Dette er også en falsk nyhetsartikkel." },
    },
  ]
}

const tasks = sampleAndShuffle(mockData.articles, mockData.articles.length)
const currentIndex = ref(0)
const showResultPopup = ref(false)
const showCompletedPopup = ref(false)
const lastAnswerCorrect = ref(false)
const correctCount = ref(0)

const onAnswered = (correct: boolean) => {
  lastAnswerCorrect.value = correct
  if (correct) correctCount.value++
  showResultPopup.value = true
}

const next = () => {
  showResultPopup.value = false
  if (currentIndex.value < tasks.length - 1) {
    currentIndex.value++
  } else {
    showCompletedPopup.value = true
  }
}
</script>

<template>
  <div>
    <h1>Aviskiosken</h1>

    <GameplayNewsstandNewsStandSingleton
      v-if="tasks[currentIndex]"
      :data="tasks[currentIndex]!"
      @answered="onAnswered"
    />

    <GameplaySharedSelectedPopup
      v-if="showResultPopup"
      :correct="lastAnswerCorrect"
      :is-last="currentIndex >= tasks.length - 1"
      @next="next"
    />

    <GameplaySharedCompletedPopup
      v-if="showCompletedPopup"
      :correct-count="correctCount"
      :total="tasks.length"
    />
  </div>
</template>

<style scoped>
/* Styles here */
</style>