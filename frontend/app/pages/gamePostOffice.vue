<template>
  <div>
    <h1>Post Kontoret</h1>

    <GameplayPostofficePostSingelton
      v-if="mockData.tasks[currentIndex]"
      :data="mockData.tasks[currentIndex]!"
      @answered="onAnswered"
    />

    <GameplaySharedSelectedPopup
      v-if="showResultPopup"
      :correct="lastAnswerCorrect"
      :is-last="currentIndex >= mockData.tasks.length - 1"
      @next="next"
    />

    <GameplaySharedCompletedPopup
      v-if="showCompletedPopup"
      :correct-count="correctCount"
      :total="mockData.tasks.length"
    />
  </div>
</template>

<script setup lang="ts">
import { TaskPostOfficeDTO } from '~/composables/types/TaskTypes'

const mockData: TaskPostOfficeDTO = {
  taskId: "1",
  description: "Which emails are scams?",
  tasks: [
    { imageurl: "https://placehold.co/600x400?text=Email+1", isValid: true },
    { imageurl: "https://placehold.co/600x400?text=Email+2", isValid: false },
    { imageurl: "https://placehold.co/600x400?text=Email+3", isValid: false },
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
  if (currentIndex.value < mockData.tasks.length - 1) {
    currentIndex.value++
  } else {
    showCompletedPopup.value = true
  }
}
</script>