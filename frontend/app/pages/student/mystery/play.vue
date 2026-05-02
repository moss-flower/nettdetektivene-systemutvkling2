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
  /* "Out and about" field investigation aesthetic  */
  --play-eyebrow: var(--palette-cork-700);
  --play-progress: var(--palette-navy-700);
  --play-title: var(--palette-navy-900);
  --play-intro: var(--palette-gray-700);

  /* Back pill: cream fill with navy border */
  --play-back-bg: var(--palette-cream-100);
  --play-back-bg-hover: var(--palette-white);
  --play-back-border: var(--palette-navy-900);
  --play-back-text: var(--palette-navy-900);

  --play-bg-top: var(--palette-ice-200);
  --play-bg-bottom: var(--palette-navy-200);

  position: relative;
  min-height: 100%;
  width: 100%;
  display: grid;
  gap: var(--space-6);
  align-content: start;
  padding: var(--layout-page-padding);
  border-radius: var(--radius-2);
  color: var(--play-title);
  background: linear-gradient(
    180deg,
    var(--play-bg-top) 0%,
    var(--play-bg-bottom) 100%
  );
  overflow-x: hidden;
}

.playPageHero {
  width: min(820px, 100%);
  margin: 0 auto;
  display: grid;
  gap: var(--space-3);
}

.playPageBack {
  width: fit-content;
  margin-bottom: var(--space-2);
  padding: var(--space-3) var(--space-4);
  border: var(--border-width-thin) solid var(--play-back-border);
  border-radius: var(--radius-pill);
  background: var(--play-back-bg);
  color: var(--play-back-text);
  font-family: var(--font-family-body), sans-serif;
  font-weight: var(--font-weight-semibold);
  text-decoration: none;
  transition: background var(--transition-fast);
}

.playPageBack:hover {
  background: var(--play-back-bg-hover);
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
  font-family: var(--font-family-body), sans-serif;
  font-size: var(--font-size-14);
  line-height: var(--font-leading-14);
  font-weight: var(--font-weight-bold);
  letter-spacing: var(--font-tracking-wider);
  text-transform: uppercase;
}

.playPageEyebrow {
  color: var(--play-eyebrow);
}

.playPageProgress {
  color: var(--play-progress);
}

.mysteryPlayPage h1 {
  margin: 0;
  max-width: 22ch;
  font-family: var(--font-family-serif), sans-serif;
  font-size: clamp(1.7rem, 3vw, 2.5rem);
  line-height: 1.05;
  font-weight: var(--font-weight-regular);
  color: var(--play-title);
}

.playPageSummary {
  margin: 0;
  max-width: 60ch;
  color: var(--play-intro);
  font-family: var(--font-family-body), sans-serif;
  font-size: var(--font-size-16);
  line-height: var(--font-leading-16);
}

.playPageBoard {
  width: min(820px, 100%);
  margin: 0 auto;
  display: grid;
  gap: var(--space-5);
}

@media (max-width: 560px) {
  .mysteryPlayPage {
    padding: var(--space-5) var(--space-4);
  }

  .playPageMeta {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
