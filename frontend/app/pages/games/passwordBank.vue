<script setup lang="ts">
import PasswordBankGuessExercise from '~/components/gameplay/passwordbank/PasswordBankGuessExercise.vue'
import PasswordBankImproveExercise from '~/components/gameplay/passwordbank/PasswordBankImproveExercise.vue'
import PasswordBankMakePasswordExercise from '~/components/gameplay/passwordbank/PasswordBankMakePasswordExercise.vue'
import CompletedPopup from '~/components/gameplay/shared/CompletedPopup.vue'
import SelectedPopup from '~/components/gameplay/shared/SelectedPopup.vue'
import type {
  PasswordBankTask,
} from '~/composables/types/TaskTypes'
import { sampleAndShuffle } from '~/composables/utils/gameUtils'

const mockData: PasswordBankTask = {
  id: "1",
  kind: "passwordBank",
  description: "Passordutfordringer",
  items: [
    {
      itemId: "1a",
      guide: "Hvilke passord er svake og hvilke er sterke?",
      kind: "guess",
      weakPasswords: ["123456", "passord"],
      strongPasswords: ["K7$mP!xQ9z", "Tr0ub4dor&3"],
    },
    {
      itemId: "1b",
      guide: "Forbedre dette passordet",  
      kind: "improvePassword",
      passwordStart: "hund123"
      
    },
    {
      itemId: "1c",
      guide: "Lag et sterkt passord",
      kind: "makePassword",
      inputPassword: ""
    }
  ]
}

const tasks = sampleAndShuffle(mockData.items, mockData.items.length)
const currentIndex = ref(0)
const currentTask = computed(() => tasks[currentIndex.value])
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
    <h1>Passordbanken</h1>

    <template v-if="currentTask">
      <PasswordBankGuessExercise
        v-if="currentTask.kind === 'guess'"
        :taskInfo="currentTask"
        @answered="onAnswered"
      />
      <PasswordBankImproveExercise
        v-else-if="currentTask.kind === 'improvePassword'"
        :taskInfo="currentTask"
        @answered="onAnswered"
      />
      <PasswordBankMakePasswordExercise
        v-else-if="currentTask.kind === 'makePassword'"
        :taskInfo="currentTask"
        @answered="onAnswered"
      />
    </template>

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