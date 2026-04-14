<template>
  <div>
    <h1>Markedsplassen</h1>

    <GameplayMarketplaceMarketplaceSingleton
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
import { TaskMarketplaceDTO, MarketplaceChoices } from '~/composables/types/TaskTypes'

const mockData: TaskMarketplaceDTO = {
  taskId: "1",
  description: "Hva bør du gjøre med disse produktene?",
  tasks: [
    { imageurl: "https://placehold.co/600x400?text=Product+1", type: MarketplaceChoices.Buy },
    { imageurl: "https://placehold.co/600x400?text=Product+2", type: MarketplaceChoices.ReviewMore },
    { imageurl: "https://placehold.co/600x400?text=Product+3", type: MarketplaceChoices.DontBuy },
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

<style scoped>
/* Styles here */
</style>