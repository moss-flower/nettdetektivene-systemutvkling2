<template>
  <div>
    <h1>Sosiale Medier</h1>

    <GameplaySocialmediaSocialMediaSingleton
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
import { TaskSocialMediaDTO, SocialMediaChoices } from '~/composables/types/TaskTypes'
import { sampleAndShuffle } from '~/composables/utils/gameUtils'

const allPosts: TaskSocialMediaDTO[] = [
  { taskId: "1", description: "Hva gjør du med dette innlegget?", taskAmount: 2, imageurl: "https://placehold.co/600x400?text=Post+1", choice: SocialMediaChoices.Share },
  { taskId: "2", description: "Hva gjør du med dette innlegget?", taskAmount: 2, imageurl: "https://placehold.co/600x400?text=Post+2", choice: SocialMediaChoices.CheckSources },
  { taskId: "3", description: "Hva gjør du med dette innlegget?", taskAmount: 2, imageurl: "https://placehold.co/600x400?text=Post+3", choice: SocialMediaChoices.AskAdult },
]

const tasks = sampleAndShuffle(allPosts, 2)
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