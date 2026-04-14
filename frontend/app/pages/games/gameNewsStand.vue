<template>
  <div>
    <h1>Aviskiosken</h1>

    <GameplayNewsstandNewsStandSingleton
      v-if="mockData.articles[currentIndex]"
      :data="mockData.articles[currentIndex]!"
      @answered="onAnswered"
    />

    <GameplaySharedSelectedPopup
      v-if="showResultPopup"
      :correct="lastAnswerCorrect"
      :is-last="currentIndex >= mockData.articles.length - 1"
      @next="next"
    />

    <GameplaySharedCompletedPopup
      v-if="showCompletedPopup"
      :correct-count="correctCount"
      :total="mockData.articles.length"
    />
  </div>
</template>

<script setup lang="ts">
import { TaskNewsStandDTO } from '~/composables/types/TaskTypes'

const mockData: TaskNewsStandDTO = {
  taskId: "1",
  description: "Hvilke nyheter er ekte og hvilke er falske?",
  articles: [
    {
      correct: { author: "NRK", title: "Ekte nyhet 1", imageurl: "https://placehold.co/600x400?text=Real+1", text: "Dette er en ekte nyhetsartikkel." },
      fake: { author: "Ukjent", title: "Falsk nyhet 1", imageurl: "https://placehold.co/600x400?text=Fake+1", text: "Dette er en falsk nyhetsartikkel." },
    },
    {
      correct: { author: "VG", title: "Ekte nyhet 2", imageurl: "https://placehold.co/600x400?text=Real+2", text: "Dette er også en ekte nyhetsartikkel." },
      fake: { author: "Tull", title: "Falsk nyhet 2", imageurl: "https://placehold.co/600x400?text=Fake+2", text: "Dette er også en falsk nyhetsartikkel." },
    },
  ]
}

const currentIndex = ref(0)
const showResultPopup = ref(false)
const showCompletedPopup = ref(false)
const lastAnswerCorrect = ref(false)
const correctCount = ref(0)

function onAnswered(correct: boolean) {
  lastAnswerCorrect.value = correct
  if (correct) correctCount.value++
  showResultPopup.value = true
}

function next() {
  showResultPopup.value = false
  if (currentIndex.value < mockData.articles.length - 1) {
    currentIndex.value++
  } else {
    showCompletedPopup.value = true
  }
}
</script>

<style scoped>
/* Styles here */
</style>