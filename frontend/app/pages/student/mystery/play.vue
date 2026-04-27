<script setup lang="ts">
import MultipleChoiceTemplate from '~/components/gameplay/templates/MultipleChoiceTemplate.vue'
import XOrYTemplate from '~/components/gameplay/templates/XOrYTemplate.vue'
import ExerciseResultDialog from '~/components/gameplay/shared/ExerciseResultDialog.vue'
import TaskResultDialog from '~/components/gameplay/shared/TaskResultDialog.vue'
import { useStudentMysteryPlay } from '~/composables/useStudentMysteryPlay'
import { routePaths } from '~/composables/utils/routePaths'

import type {
  MultipleChoiceOptionKey,
  MultipleChoiceResult,
  XOrYChoice,
  XOrYResult,
} from '~/composables/types/TaskTypes'

definePageMeta({
  layout: 'game',
})

const { mysteryPlaySession } = useStudentMysteryPlay()
const { celebrateCorrectAnswer, stop } = useGameConfetti()

const tasks = mysteryPlaySession.tasks
const currentIndex = ref(0)
const correctCount = ref(0)
const showMysteryResultDialog = ref(false)
const answerState = ref<{ correct: boolean, choice: string } | null>(null)

const currentTask = computed(() => tasks[currentIndex.value] ?? null)
const currentOptions = computed(() => {
  if (!currentTask.value || currentTask.value.templateKind !== 'x-or-y') {
    return null
  }

  return currentTask.value.options
})

const progressLabel = computed(() => `Ledetråd ${currentIndex.value + 1} av ${tasks.length}`)

const selectedChoice = computed<XOrYChoice | null>(() => {
  if (answerState.value?.choice === 'x') {
    return 'x'
  }

  if (answerState.value?.choice === 'y') {
    return 'y'
  }

  return null
})

const selectedMultipleChoiceOption = computed<MultipleChoiceOptionKey | null>(() => {
  if (currentTask.value?.templateKind === 'multiple-choice' && answerState.value) {
    return answerState.value.choice
  }

  return null
})

const feedbackTitle = computed(() => {
  if (!currentTask.value) {
    return 'Ingen ledetråder ennå'
  }

  return answerState.value?.correct ? 'Riktig svar' : 'Ikke helt'
})

const feedbackText = computed(() => {
  if (!currentTask.value) {
    return 'Dette mysteriet har ingen ledetråder enda.'
  }

  if (!answerState.value) {
    return currentTask.value.introText
  }

  return answerState.value.correct
    ? currentTask.value.feedbackCorrect
    : currentTask.value.feedbackIncorrect
})

const showAnswerDialog = computed(() => Boolean(answerState.value))

const mysteryReturnRoute = computed(() => ({
  path: routePaths.studentMystery,
}))

function onAnswered(result: XOrYResult) {
  answerState.value = {
    correct: result.correct,
    choice: result.choice,
  }

  if (result.correct) {
    correctCount.value += 1
    celebrateCorrectAnswer()
  }
}

function onAnsweredMultipleChoice(result: MultipleChoiceResult) {
  answerState.value = {
    correct: result.correct,
    choice: result.choice,
  }

  if (result.correct) {
    correctCount.value += 1
    celebrateCorrectAnswer()
  }
}

function next() {
  stop()
  answerState.value = null

  if (currentIndex.value < tasks.length - 1) {
    currentIndex.value += 1
    return
  }

  showMysteryResultDialog.value = true
}
</script>

<template>
  <section class="mystery-play-page">
    <header class="mystery-play-page__hero">
      <NuxtLink class="mystery-play-page__back" :to="routePaths.studentMystery">
        Tilbake til mysteriet
      </NuxtLink>

      <div class="mystery-play-page__meta">
        <p class="mystery-play-page__eyebrow">{{ mysteryPlaySession.weekLabel }}</p>
        <p class="mystery-play-page__progress">{{ progressLabel }}</p>
      </div>

      <h1>{{ mysteryPlaySession.title }}</h1>
      <p class="mystery-play-page__summary">{{ mysteryPlaySession.summary }}</p>
    </header>

    <div class="mystery-play-page__board">
      <XOrYTemplate
        v-if="currentTask?.templateKind === 'x-or-y' && currentOptions"
        :key="`${currentTask.id}-xory`"
        :prompt="currentTask.prompt"
        :options="currentOptions"
        :correct-choice="currentTask.correctChoice"
        :disabled="Boolean(answerState)"
        :selected-choice="selectedChoice"
        @answered="onAnswered"
      />

      <MultipleChoiceTemplate
        v-else-if="currentTask?.templateKind === 'multiple-choice'"
        :key="`${currentTask.id}-multiple`"
        :prompt="currentTask.prompt"
        :options="currentTask.options"
        :correct-choice="currentTask.correctChoice"
        :disabled="Boolean(answerState)"
        :selected-choice="selectedMultipleChoiceOption"
        @answered="onAnsweredMultipleChoice"
      />
    </div>

    <TaskResultDialog
      v-if="showAnswerDialog && answerState"
      :title="feedbackTitle"
      :message="feedbackText"
      :is-last="currentIndex >= tasks.length - 1"
      :correct="answerState.correct"
      @next="next"
    />

    <ExerciseResultDialog
      v-if="showMysteryResultDialog"
      :correct-count="correctCount"
      :total="tasks.length"
      :msg-completed="mysteryPlaySession.outroSuccess"
      :msg-failed="mysteryPlaySession.outroRetry"
      :to="mysteryReturnRoute"
    />
  </section>
</template>

<style scoped>
.mystery-play-page {
  max-width: 72rem;
  margin: 0 auto;
  display: grid;
  gap: 1rem;
  color: #111827;
}

.mystery-play-page__hero {
  display: grid;
  gap: 0.85rem;
}

.mystery-play-page__back {
  width: fit-content;
  color: var(--color-brand-primary);
  font-weight: var(--font-weight-semibold);
  text-decoration: none;
}

.mystery-play-page__meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  flex-wrap: wrap;
}

.mystery-play-page__eyebrow,
.mystery-play-page__progress {
  margin: 0;
  font-size: 0.9rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.mystery-play-page__eyebrow {
  color: #6f4c2f;
}

.mystery-play-page__progress {
  color: var(--color-brand-primary);
}

.mystery-play-page h1 {
  margin: 0;
  font-size: clamp(1.9rem, 4vw, 3rem);
  line-height: 0.98;
  color: #1b2b4f;
}

.mystery-play-page__summary {
  margin: 0;
  max-width: 54ch;
  color: #52607a;
  font-size: 1.05rem;
  line-height: 1.45;
}

.mystery-play-page__board {
  display: grid;
}

@media (max-width: 640px) {
  .mystery-play-page {
    gap: 0.85rem;
  }

  .mystery-play-page__summary {
    font-size: 1rem;
  }
}
</style>
