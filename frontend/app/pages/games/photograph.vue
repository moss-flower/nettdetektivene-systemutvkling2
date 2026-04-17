<script setup lang="ts">
import ImageRecognitionExercise from '~/components/gameplay/imagerecognition/ImageRecognitionExercise.vue'
import ExerciseResultDialog from '~/components/gameplay/shared/ExerciseResultDialog.vue'
import SelectedPopup from '~/components/gameplay/shared/SelectedPopup.vue'
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
const showTaskResultDialog = ref(false)
const showExerciseResultDialog = ref(false)
const lastAnswerCorrect = ref(false)
const correctCount = ref(0)

const onAnswered = (correct: boolean) => {
  lastAnswerCorrect.value = correct
  if (correct) correctCount.value++
  showTaskResultDialog.value = true
}

const next = () => {
  showTaskResultDialog.value = false
  if (currentIndex.value < tasks.length - 1) {
    currentIndex.value++
  } else {
    showExerciseResultDialog.value = true
  }
}
</script>

<template>
  <div>
    <h1>Bildegjenkjenning</h1>

    <ImageRecognitionExercise
      v-if="tasks[currentIndex]"
      :images="tasks[currentIndex]!"
      @answered="onAnswered"
    />

    <SelectedPopup
      v-if="showTaskResultDialog"
      :correct="lastAnswerCorrect"
      :is-last="currentIndex >= tasks.length - 1"
      @next="next"
    />

    <ExerciseResultDialog
      v-if="showExerciseResultDialog"
      :correct-count="correctCount"
      :total="tasks.length"
    />
  </div>
</template>

<style scoped>
/* Styles here */
</style>
