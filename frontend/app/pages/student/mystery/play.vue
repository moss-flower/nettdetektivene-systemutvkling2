<script setup lang="ts">
import EmailHotspotsTemplate from '~/components/gameplay/templates/EmailHotspotsTemplate.vue'
import MultipleChoiceTemplate from '~/components/gameplay/templates/MultipleChoiceTemplate.vue'
import PasswordStrengthTemplate from '~/components/gameplay/templates/PasswordStrengthTemplate.vue'
import SortIntoBucketsTemplate from '~/components/gameplay/templates/SortIntoBucketsTemplate.vue'
import XOrYTemplate from '~/components/gameplay/templates/XOrYTemplate.vue'
import ExerciseResultDialog from '~/components/gameplay/shared/ExerciseResultDialog.vue'
import TaskResultDialog from '~/components/gameplay/shared/TaskResultDialog.vue'
import { useStudentMysteryPlay } from '~/composables/useStudentMysteryPlay'
import { routePaths } from '~/composables/utils/routePaths'

definePageMeta({
  layout: 'game',
})

import type {
  EmailHotspotResult,
  MultipleChoiceOptionKey,
  MultipleChoiceResult,
  PasswordStrengthResult,
  SortIntoBucketsResult,
  XOrYChoice,
  XOrYResult,
} from '~/composables/types/TaskTypes'

const { loadMysteryPlaySession } = useStudentMysteryPlay()
const mysteryPlaySession = await loadMysteryPlaySession()

if (!mysteryPlaySession) {
  throw createError({
    statusCode: 404,
    statusMessage: 'Ukens mysterium er ikke klart for spilling ennå.',
  })
}

const { celebrateCorrectAnswer, celebrateLevelComplete, stop } = useGameConfetti()

const tasks = mysteryPlaySession.tasks
const currentIndex = ref(0)
const correctCount = ref(0)
const showMysteryResultDialog = ref(false)
const answerState = ref<{
  correct: boolean
  choice: string
  missingStartingPassword?: boolean
} | null>(null)

const currentTask = computed(() => tasks[currentIndex.value] ?? null)
const currentOptions = computed(() => {
  if (!currentTask.value || currentTask.value.templateKind !== 'x-or-y') {
    return null
  }

  return currentTask.value.options
})

const progressLabel = computed(() => `Ledetråd ${currentIndex.value + 1} av ${tasks.length}`)
const isMysteryComplete = computed(() => tasks.length > 0 && correctCount.value === tasks.length)

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
    : currentTask.value.templateKind === 'password-strength' && answerState.value.missingStartingPassword
      ? 'Det nye passordet må inneholde startpassordet som en del av svaret ditt.'
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

function onAnsweredEmailHotspots(result: EmailHotspotResult) {
  answerState.value = {
    correct: result.correct,
    choice: result.selectedTargetIds.join(','),
  }

  if (result.correct) {
    correctCount.value += 1
    celebrateCorrectAnswer()
  }
}

function onAnsweredSortIntoBuckets(result: SortIntoBucketsResult) {
  answerState.value = {
    correct: result.correct,
    choice: Object.values(result.placements).flat().join(','),
  }

  if (result.correct) {
    correctCount.value += 1
    celebrateCorrectAnswer()
  }
}

function onAnsweredPasswordStrength(result: PasswordStrengthResult) {
  answerState.value = {
    correct: result.correct,
    choice: result.password,
    missingStartingPassword: result.missingStartingPassword,
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

  if (isMysteryComplete.value) {
    celebrateLevelComplete()
  }

  showMysteryResultDialog.value = true
}
</script>

<template>
  <section class="mysteryPlayPage">
    <header class="playPageHero">
      <NuxtLink class="playPageBack" :to="routePaths.studentMystery">
        Tilbake til mysteriet
      </NuxtLink>

      <div class="playPageMeta">
        <p class="playPageEyebrow">{{ mysteryPlaySession.weekLabel }}</p>
        <p class="playPageProgress">{{ progressLabel }}</p>
      </div>

      <h1>{{ mysteryPlaySession.title }}</h1>
      <p class="playPageSummary">{{ mysteryPlaySession.summary }}</p>
    </header>

    <div class="playPageBoard">
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
.mysteryPlayPage {
  max-width: 72rem;
  margin: 0 auto;
  display: grid;
  gap: var(--space-4);
  color: #111827;
}

.playPageHero {
  display: grid;
  gap: 0.85rem;
}

.playPageBack {
  width: fit-content;
  color: var(--color-brand-primary);
  font-weight: var(--font-weight-semibold);
  text-decoration: none;
}

.playPageMeta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--space-4);
  flex-wrap: wrap;
}

.playPageEyebrow,
.playPageProgress {
  margin: 0;
  font-size: 0.9rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.playPageEyebrow {
  color: #6f4c2f;
}

.playPageProgress {
  color: var(--color-brand-primary);
}

.mysteryPlayPage h1 {
  margin: 0;
  font-size: clamp(1.9rem, 4vw, 3rem);
  line-height: 0.98;
  color: #1b2b4f;
}

.playPageSummary {
  margin: 0;
  max-width: 54ch;
  color: #52607a;
  font-size: 1.05rem;
  line-height: 1.45;
}

.playPageBoard {
  display: grid;
}

</style>
