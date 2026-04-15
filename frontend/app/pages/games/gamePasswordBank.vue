<script setup lang="ts">
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
      taskId: "1a",
      guide: "Hvilke passord er svake og hvilke er sterke?",
      task: {
        kind: "guess",
        weakPasswords: ["123456", "passord"],
        strongPasswords: ["K7$mP!xQ9z", "Tr0ub4dor&3"],
      }
    },
    {
      taskId: "1b",
      guide: "Forbedre dette passordet",
      task: {
        kind: "improve",
        passwordStart: "hund123"
      }
    },
    {
      taskId: "1c",
      guide: "Lag et sterkt passord",
      task: {
        kind: "makePassword",
        inputPassword: ""
      }
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
      <GameplayPasswordbankPasswordBankGuess
        v-if="currentTask.task.kind === 'guess'"
        :data="currentTask"
        @answered="onAnswered"
      />
      <GameplayPasswordbankPasswordBankImprove
        v-else-if="currentTask.task.kind === 'improve'"
        :data="currentTask"
        @answered="onAnswered"
      />
      <GameplayPasswordbankPasswordBankMakePassword
        v-else-if="currentTask.task.kind === 'makePassword'"
        :data="currentTask"
        @answered="onAnswered"
      />
    </template>

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

<style scoped>
/* Styles here */
</style>