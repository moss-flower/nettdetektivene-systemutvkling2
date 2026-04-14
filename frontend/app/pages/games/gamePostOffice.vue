<template>
  <div>
    <h1>Post Kontoret</h1>

    <GameplayPostofficePostSingleton
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
import { TaskPostOfficeDTO } from '~/composables/types/TaskTypes'
import { sampleAndShuffle } from '~/composables/utils/gameUtils'

const mockData: TaskPostOfficeDTO = {
  taskId: "1",
  description: "Which emails are scams?",
  taskAmount: 2,
  mails: [
    { avatarImageurl: "https://placehold.co/100x100?text=A1", sender: "bank@sparebanken.no", subject: "Viktig melding om kontoen din", content: "Vi trenger at du bekrefter kontoinformasjonen din.", isValid: true },
    { avatarImageurl: "https://placehold.co/100x100?text=A2", sender: "prince@nigeria.com", subject: "Du har vunnet!", content: "Send oss dine bankdetaljer for å motta premien.", isValid: false },
    { avatarImageurl: "https://placehold.co/100x100?text=A3", sender: "support@microsoft.com.ru", subject: "Kontoen din er hacket", content: "Klikk her for å sikre kontoen din umiddelbart.", isValid: false },
  ]
}

const tasks = sampleAndShuffle(mockData.mails, mockData.taskAmount)
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