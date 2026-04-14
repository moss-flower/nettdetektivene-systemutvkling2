<template>
  <div>
    <h1>Markedsplassen</h1>

    <GameplayMarketplaceSingleton
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

<script setup lang="ts">
import { TaskMarketplaceDTO, MarketplaceChoices } from '~/composables/types/TaskTypes'
import { sampleAndShuffle } from '~/composables/utils/gameUtils'

const mockData: TaskMarketplaceDTO = {
  taskId: "1",
  description: "Hva bør du gjøre med disse produktene?",
  taskAmount: 2,
  listing: [
    { imageurl: "https://placehold.co/600x400?text=Product+1", type: MarketplaceChoices.Buy, price: 100 },
    { imageurl: "https://placehold.co/600x400?text=Product+2", type: MarketplaceChoices.ReviewMore, price: 200 },
    { imageurl: "https://placehold.co/600x400?text=Product+3", type: MarketplaceChoices.DontBuy, price: 300 },
  ]
}

const tasks = sampleAndShuffle(mockData.listing, mockData.taskAmount)
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
  if (currentIndex.value < tasks.length - 1) {
    currentIndex.value++
  } else {
    showCompletedPopup.value = true
  }
}
</script>

<style scoped>
/* Styles here */
</style>