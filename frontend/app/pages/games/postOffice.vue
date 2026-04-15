<script setup lang="ts">
import PostOfficeExercise from '~/components/gameplay/postoffice/PostOfficeExercise.vue'
import CompletedPopup from '~/components/gameplay/shared/CompletedPopup.vue'
import SelectedPopup from '~/components/gameplay/shared/SelectedPopup.vue'
import type { PostOfficeTask } from '~/composables/types/TaskTypes'
import { sampleAndShuffle } from '~/composables/utils/gameUtils'

const mockData: PostOfficeTask = {
  id: "1",
  kind: "postOffice",
  description: "Which emails are scams?",
  mails: [
    { avatarImageUrl: "https://placehold.co/100x100?text=A1", sender: "bank@sparebanken.no", subject: "Viktig melding om kontoen din", content: "Vi trenger at du bekrefter kontoinformasjonen din.", isValid: true },
    { avatarImageUrl: "https://placehold.co/100x100?text=A2", sender: "prince@nigeria.com", subject: "Du har vunnet!", content: "Send oss dine bankdetaljer for å motta premien.", isValid: false },
    { avatarImageUrl: "https://placehold.co/100x100?text=A3", sender: "support@microsoft.com.ru", subject: "Kontoen din er hacket", content: "Klikk her for å sikre kontoen din umiddelbart.", isValid: false },
  ]
}

const tasks = sampleAndShuffle(mockData.mails, mockData.mails.length)
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
    <h1>Postkontoret</h1>

    <PostOfficeExercise
      v-if="tasks[currentIndex]"
      :mails="tasks[currentIndex]!"
      @answered="onAnswered"
    />

    <SelectedPopup
      v-if="showResultPopup"
      :correct="lastAnswerCorrect"
      :is-last="currentIndex >= tasks.length - 1"
      @next="next"
    />

    <CompletedPopup
      v-if="showCompletedPopup"
      :correct-count="correctCount"
      :total="tasks.length"
    />
  </div>
</template>


<style scoped>
/* Styles here */
</style>