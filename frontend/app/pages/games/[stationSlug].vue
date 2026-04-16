<script setup lang="ts">
import CompletedPopup from '~/components/gameplay/shared/CompletedPopup.vue'
import ExerciseResultDialog from '~/components/gameplay/shared/ExerciseResultDialog.vue'
import ThreeOptionImageExercise from '~/components/gameplay/shared/ThreeOptionImageExercise.vue'
import XOrYExercise from '~/components/gameplay/shared/XOrYExercise.vue'
import { getStationMockBySlug } from '~/composables/mocks/stations'
import type {
  StationDifficulty,
  ThreeOptionChoice,
  ThreeOptionResult,
  XOrYChoice,
  XOrYResult,
} from '~/composables/types/TaskTypes'
import { sampleAndShuffle } from '~/composables/utils/gameUtils'

definePageMeta({
  layout: 'game',
})

const route = useRoute()
const requestedStationSlug = Array.isArray(route.params.stationSlug)
  ? route.params.stationSlug[0] ?? ''
  : route.params.stationSlug ?? ''
const station = getStationMockBySlug(requestedStationSlug)

if (!station) {
  throw createError({
    statusCode: 404,
    statusMessage: 'Fant ikke denne stasjonen.',
  })
}

const { celebrateCorrectAnswer, stop } = useGameConfetti()

const activeDifficulty: StationDifficulty = 'easy'
const activeExercises = station.exercisesByDifficulty[activeDifficulty]
const tasks = sampleAndShuffle(activeExercises, activeExercises.length)
const currentIndex = ref(0)
const showCompletedPopup = ref(false)
const correctCount = ref(0)
const answerState = ref<{ correct: boolean, choice: string } | null>(null)

const currentTask = computed(() => tasks[currentIndex.value] ?? null)
const currentOptions = computed(() => {
  if (!currentTask.value || currentTask.value.templateKind !== 'x-or-y') {
    return null
  }

  return currentTask.value.options
})

const selectedChoice = computed<XOrYChoice | null>(() => {
  if (answerState.value?.choice === 'x') {
    return 'x'
  }

  if (answerState.value?.choice === 'y') {
    return 'y'
  }

  return null
})

const selectedThreeOptionChoice = computed<ThreeOptionChoice | null>(() => {
  if (
    answerState.value?.choice === 'a'
    || answerState.value?.choice === 'b'
    || answerState.value?.choice === 'c'
  ) {
    return answerState.value.choice
  }

  return null
})

const progressLabel = computed(() => {
  if (tasks.length === 0) {
    return 'Ingen oppgaver ennå'
  }

  return `Sak ${currentIndex.value + 1} av ${tasks.length}`
})

const feedbackTitle = computed(() => {
  if (!currentTask.value) {
    return 'Ingen oppgaver ennå'
  }

  if (!answerState.value) {
    return currentTask.value.templateKind === 'three-option-image'
      ? 'Hva avslører den falske saken?'
      : 'Velg avisen du stoler mest på'
  }

  return answerState.value.correct ? 'Riktig svar' : 'Ikke helt'
})

const feedbackText = computed(() => {
  if (!currentTask.value) {
    return 'Denne stasjonen har ikke fått noen oppgaver enda.'
  }

  if (!answerState.value) {
    return currentTask.value.introText
  }

  if (answerState.value.correct) {
    return currentTask.value.feedbackCorrect
  }

  return currentTask.value.feedbackIncorrect
})

const showAnswerDialog = computed(() => Boolean(answerState.value))

const onAnswered = (result: XOrYResult) => {
  answerState.value = {
    correct: result.correct,
    choice: result.choice,
  }

  if (result.correct) {
    correctCount.value++
    celebrateCorrectAnswer()
  }
}

const onAnsweredThreeOption = (result: ThreeOptionResult) => {
  answerState.value = {
    correct: result.correct,
    choice: result.choice,
  }

  if (result.correct) {
    correctCount.value++
    celebrateCorrectAnswer()
  }
}

const next = () => {
  stop()
  answerState.value = null

  if (currentIndex.value < tasks.length - 1) {
    currentIndex.value++
  } else {
    showCompletedPopup.value = true
  }
}
</script>

<template>
  <section class="station-page">
    <header class="station-page__hero">
      <div class="station-page__header">
        <div class="station-page__meta">
          <p class="station-page__eyebrow">{{ station.title }}</p>
          <p class="station-page__progress-pill">{{ progressLabel }}</p>
        </div>
        <p class="station-page__description">
          {{ station.description }}
        </p>
      </div>
    </header>

    <div class="station-page__board">
      <XOrYExercise
        v-if="currentTask?.templateKind === 'x-or-y' && currentOptions"
        :prompt="currentTask.prompt"
        :options="currentOptions"
        :correct-choice="currentTask.correctChoice"
        :disabled="Boolean(answerState)"
        :selected-choice="selectedChoice"
        @answered="onAnswered"
      />
      <ThreeOptionImageExercise
        v-else-if="currentTask?.templateKind === 'three-option-image'"
        :prompt="currentTask.prompt"
        :options="currentTask.options"
        :correct-choice="currentTask.correctChoice"
        :disabled="Boolean(answerState)"
        :selected-choice="selectedThreeOptionChoice"
        @answered="onAnsweredThreeOption"
      />
    </div>

    <ExerciseResultDialog
      v-if="showAnswerDialog && answerState"
      :title="feedbackTitle"
      :message="feedbackText"
      :is-last="currentIndex >= tasks.length - 1"
      :correct="answerState.correct"
      @next="next"
    />

    <CompletedPopup
      v-if="showCompletedPopup"
      :correct-count="correctCount"
      :total="tasks.length"
    />
  </section>
</template>

<style scoped>
.station-page {
  max-width: 72rem;
  margin: 0 auto;
  display: grid;
  gap: 1rem;
  color: #111827;
}

.station-page__hero {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 1.5rem;
  width: 100%;
}

.station-page__header {
  display: grid;
  gap: 0.6rem;
  width: 100%;
}

.station-page__meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
}

.station-page__eyebrow {
  color: #1d4f91;
  font-size: 1.15rem;
  font-weight: 700;
  letter-spacing: 0.04em;
  text-transform: uppercase;
}

.station-page__progress-pill {
  flex-shrink: 0;
  padding: 0.45rem 0.85rem;
  border-radius: 999px;
  background: #dbeafe;
  color: #1e3a5f;
  font-size: 0.95rem;
  font-weight: 700;
}

.station-page__description {
  max-width: 46rem;
  font-size: 0.9rem;
  line-height: 1.5;
  color: #374151;
}

.station-page__board {
  padding: clamp(0.8rem, 1.4vw, 1.1rem);
  border-radius: 1.6rem;
  background: #f7fbff;
}

@media (max-width: 780px) {
  .station-page__hero {
    flex-direction: column;
    align-items: stretch;
  }

  .station-page__meta {
    align-items: flex-start;
    flex-direction: column;
  }

  .station-page__board {
    padding: 0.85rem;
  }
}
</style>
