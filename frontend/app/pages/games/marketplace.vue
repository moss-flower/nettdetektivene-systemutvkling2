<script setup lang="ts">
import MarketPlaceExercise from '~/components/gameplay/marketplace/MarketPlaceExercise.vue'
import ExerciseResultDialog from '~/components/gameplay/shared/ExerciseResultDialog.vue'
import SelectedPopup from '~/components/gameplay/shared/SelectedPopup.vue'
import type { MarketplaceTask } from '~/composables/types/TaskTypes'
import { sampleAndShuffle } from '~/composables/utils/gameUtils'

const mockData: MarketplaceTask = {
  id: "1",
  kind: "marketplace",
  description: "Hva bør du gjøre med disse produktene?",
  listings: [
    { imageUrl: "https://placehold.co/600x400?text=Product+1", expectedChoice: "buy", price: 100 },
    { imageUrl: "https://placehold.co/600x400?text=Product+2", expectedChoice: "reviewMore", price: 200 },
    { imageUrl: "https://placehold.co/600x400?text=Product+3", expectedChoice: "dontBuy", price: 300 },
  ]
}

const tasks = sampleAndShuffle(mockData.listings, mockData.listings.length)
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
    <h1>Markedsplassen</h1>

    <MarketPlaceExercise
      v-if="tasks[currentIndex]"
      :listing="mockData.listings[currentIndex]!"
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
