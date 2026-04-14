<template>
  <div>
    <h1>Passordbanken</h1>

    <template v-if="currentTask">
      <GameplayPasswordbankPasswordBankGuess
        v-if="currentTask instanceof TaskPasswordBankGuessSingleton"
        :data="currentTask"
        @answered="onAnswered"
      />
      <GameplayPasswordbankPasswordBankImprove
        v-else-if="currentTask instanceof TaskPasswordBankImproveSingleton"
        :data="currentTask"
        @answered="onAnswered"
      />
      <GameplayPasswordbankPasswordBankMakePassword
        v-else-if="currentTask instanceof TaskPasswordBankMakePasswordSingleton"
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

<script setup lang="ts">
import {
  TaskPasswordBankDTO,
  TaskPasswordBankGuessSingleton,
  TaskPasswordBankImproveSingleton,
  TaskPasswordBankMakePasswordSingleton,
} from '~/composables/types/TaskTypes'
import { sampleAndShuffle } from '~/composables/utils/gameUtils'

const mockData: TaskPasswordBankDTO = {
  taskId: "1",
  description: "Passordutfordringer",
  taskAmount: 3,
  passwordTasks: [
    Object.assign(new TaskPasswordBankGuessSingleton(), {
      taskId: "1a",
      description: "Hvilke passord er svake og hvilke er sterke?",
      passwordWeak: ["123456", "passord"],
      passwordStrong: ["K7$mP!xQ9z", "Tr0ub4dor&3"],
    }),
    Object.assign(new TaskPasswordBankImproveSingleton(), {
      taskId: "1b",
      description: "Forbedre dette passordet",
      passwordStart: "hund123",
    }),
    Object.assign(new TaskPasswordBankMakePasswordSingleton(), {
      taskId: "1c",
      description: "Lag et sterkt passord",
      password: "",
    }),
  ]
}

const tasks = sampleAndShuffle(mockData.passwordTasks, mockData.taskAmount)
const currentIndex = ref(0)
const currentTask = computed(() => tasks[currentIndex.value])
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