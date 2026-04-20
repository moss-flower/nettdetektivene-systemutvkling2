<script setup lang="ts">
import EmailHotspotsTemplate from '~/components/gameplay/templates/EmailHotspotsTemplate.vue'
import ExerciseResultDialog from '~/components/gameplay/shared/ExerciseResultDialog.vue'
import PasswordStrengthTemplate from '~/components/gameplay/templates/PasswordStrengthTemplate.vue'
import SortIntoBucketsTemplate from '~/components/gameplay/templates/SortIntoBucketsTemplate.vue'
import TaskResultDialog from '~/components/gameplay/shared/TaskResultDialog.vue'
import MultipleChoiceTemplate from '~/components/gameplay/templates/MultipleChoiceTemplate.vue'
import XOrYTemplate from '~/components/gameplay/templates/XOrYTemplate.vue'
import { routePaths } from '~/composables/routePaths'
import { Exercise } from '~/composables/types/ExerciseType'
import { Station } from '~/composables/types/StationType'

import type {
  EmailHotspotResult,
  MultipleChoiceOptionKey,
  MultipleChoiceResult,
  PasswordStrengthResult,
  SortIntoBucketsResult,
  XOrYChoice,
  XOrYResult,
} from '~/composables/types/TaskTypes'
import { sampleAndShuffle } from '~/composables/utils/gameUtils'

definePageMeta({
  layout: 'game',
})

const route = useRoute()
const requestedStationUid = Array.isArray(route.params.stationUid)
  ? route.params.stationUid[0] ?? ''
  : route.params.stationUid ?? ''
const requestedExerciseUid = typeof route.query.exerciseUid === 'string'
  ? route.query.exerciseUid
  : ''
const station = Station.backendFetchStationByUid(requestedStationUid)

if (!station) {
  throw createError({
    statusCode: 404,
    statusMessage: 'Fant ikke denne stasjonen.',
  })
}

const { celebrateCorrectAnswer, stop } = useGameConfetti()

const activeExerciseUid = station.exerciseUids.includes(requestedExerciseUid)
  ? requestedExerciseUid
  : station.exerciseUids[0] ?? null
const activeExercise = activeExerciseUid ? Exercise.backendFetchExerciseByUid(activeExerciseUid) : null
const tasks = activeExercise
  ? sampleAndShuffle(activeExercise.tasks, activeExercise.tasks.length)
  : []
const currentIndex = ref(0)
const showExerciseResultDialog = ref(false)
const correctCount = ref(0)
const answerState = ref<{ correct: boolean, choice: string, missingStartingPassword?: boolean } | null>(null)

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

const selectedMultipleChoiceOption = computed<MultipleChoiceOptionKey | null>(() => {
  if (currentTask.value?.templateKind === 'multiple-choice' && answerState.value) {
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

const exerciseSelectRoute = computed(() => ({
  path: routePaths.studentExerciseDifficultySelect,
  query: { uid: station.uid },
}))

const feedbackTitle = computed(() => {
  if (!currentTask.value) {
    return 'Ingen oppgaver ennå'
  }

  if (!answerState.value) {
    if (currentTask.value.templateKind === 'multiple-choice') {
      return 'Hva avslører den falske saken?'
    }

    if (currentTask.value.templateKind === 'email-hotspots') {
      return 'Trykk på det som virker mistenkelig'
    }

    if (currentTask.value.templateKind === 'sort-into-buckets') {
      return 'Sorter alle passordene'
    }

    if (currentTask.value.templateKind === 'password-strength') {
      return 'Bygg et tryggere passord'
    }

    return 'Velg avisen du stoler mest på'
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

  if (
    currentTask.value.templateKind === 'password-strength'
    && answerState.value.missingStartingPassword
  ) {
    return 'Det nye passordet ma inneholde startpassordet som en del av svaret ditt.'
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

const onAnsweredMultipleChoice = (result: MultipleChoiceResult) => {
  answerState.value = {
    correct: result.correct,
    choice: result.choice,
  }

  if (result.correct) {
    correctCount.value++
    celebrateCorrectAnswer()
  }
}

const onAnsweredEmailHotspots = (result: EmailHotspotResult) => {
  answerState.value = {
    correct: result.correct,
    choice: result.selectedTargetIds.join(','),
  }

  if (result.correct) {
    correctCount.value++
    celebrateCorrectAnswer()
  }
}

const onAnsweredSortIntoBuckets = (result: SortIntoBucketsResult) => {
  answerState.value = {
    correct: result.correct,
    choice: Object.values(result.placements).flat().join(','),
  }

  if (result.correct) {
    correctCount.value++
    celebrateCorrectAnswer()
  }
}

const onAnsweredPasswordStrength = (result: PasswordStrengthResult) => {
  answerState.value = {
    correct: result.correct,
    choice: result.password,
    missingStartingPassword: result.missingStartingPassword,
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
    showExerciseResultDialog.value = true
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
      </div>
    </header>

    <div class="station-page__board">
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
        :key="`${currentTask.id}-three-option`"
        :prompt="currentTask.prompt"
        :options="currentTask.options"
        :correct-choice="currentTask.correctChoice"
        :disabled="Boolean(answerState)"
        :selected-choice="selectedMultipleChoiceOption"
        @answered="onAnsweredMultipleChoice"
      />
      <EmailHotspotsTemplate
        v-else-if="currentTask?.templateKind === 'email-hotspots'"
        :key="`${currentTask.id}-email-hotspots`"
        :prompt="currentTask.prompt"
        :email="currentTask.email"
        :targets="currentTask.targets"
        :disabled="Boolean(answerState)"
        @answered="onAnsweredEmailHotspots"
      />
      <SortIntoBucketsTemplate
        v-else-if="currentTask?.templateKind === 'sort-into-buckets'"
        :key="`${currentTask.id}-sort-into-buckets`"
        :prompt="currentTask.prompt"
        :items="currentTask.items"
        :buckets="currentTask.buckets"
        :unplaced-label="currentTask.unplacedLabel"
        :disabled="Boolean(answerState)"
        @answered="onAnsweredSortIntoBuckets"
      />
      <PasswordStrengthTemplate
        v-else-if="currentTask?.templateKind === 'password-strength'"
        :key="`${currentTask.id}-password-strength`"
        :task="currentTask"
        :disabled="Boolean(answerState)"
        @answered="onAnsweredPasswordStrength"
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
      v-if="showExerciseResultDialog"
      :correct-count="correctCount"
      :total="tasks.length"
      :to="exerciseSelectRoute"
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

.station-page__exercise-title {
  font-size: 1.1rem;
  font-weight: 700;
  color: #1f2937;
}

.station-page__board {
  padding: clamp(0.8rem, 1.4vw, 1.1rem);
  border-radius: 1.6rem;
  background: #f5f5f4;
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
