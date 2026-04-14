<template>
  <div>
    <h1>Bildegjenkjenning</h1>

    <GameplayImagerecognitionImageRecognitionSingleton
      v-if="mockData.images[currentIndex]"
      :data="mockData.images[currentIndex]!"
      @answered="onAnswered"
    />

    <GameplaySharedSelectedPopup
      v-if="showResultPopup"
      :correct="lastAnswerCorrect"
      :is-last="currentIndex >= mockData.images.length - 1"
      @next="next"
    />

    <GameplaySharedCompletedPopup
      v-if="showCompletedPopup"
      :correct-count="correctCount"
      :total="mockData.images.length"
    />
  </div>
</template>

<script setup lang="ts">
import { TaskImageRecognitionDTO, ImageRecognitionType } from '~/composables/types/TaskTypes'

const mockData: TaskImageRecognitionDTO = {
  taskId: "1",
  description: "Hvilken form for bilde er dette?",
  images: [
    { imageurl: "https://placehold.co/600x400?text=Image+1", type: ImageRecognitionType.Real },
    { imageurl: "https://placehold.co/600x400?text=Image+2", type: ImageRecognitionType.AI },
    { imageurl: "https://placehold.co/600x400?text=Image+3", type: ImageRecognitionType.Manipulated },
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
  if (currentIndex.value < mockData.images.length - 1) {
    currentIndex.value++
  } else {
    showCompletedPopup.value = true
  }
}
</script>

<style scoped>
/* Styles here */
</style>
