<script setup lang="ts">
import type { ImageRecognitionTask, ImageRecognitionType } from '~/composables/types/TaskTypes'
import { sampleAndShuffle } from '~/composables/utils/gameUtils'

const mockData: ImageRecognitionTask = {
  id: "1",
  kind: "imageRecognition",
  description: "Hvilken form for bilde er dette?",
  images: [
    { imageUrl: "https://placehold.co/600x400?text=Image+1", type: "real" },
    { imageUrl: "https://placehold.co/600x400?text=Image+2", type: "AI" },
    { imageUrl: "https://placehold.co/600x400?text=Image+3", type: "manipulated" },
  ]
}

const tasks = sampleAndShuffle(mockData.images, mockData.images.length)
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
    <h1>Bildegjenkjenning</h1>

    <GameplayImagerecognitionImageRecognitionSingleton
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
