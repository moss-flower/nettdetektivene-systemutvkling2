<script setup lang="ts">
import BossCharacter from '~/components/gameplay/shared/BossCharacter.vue'
import BossVictoryDialog from '~/components/gameplay/shared/BossVictoryDialog.vue'
import EmailHotspotsTemplate from '~/components/gameplay/templates/EmailHotspotsTemplate.vue'
import ExerciseResultDialog from '~/components/gameplay/shared/ExerciseResultDialog.vue'
import LevelupPopup from '~/components/gameplay/shared/LevelupPopup.vue'
import PasswordStrengthTemplate from '~/components/gameplay/templates/PasswordStrengthTemplate.vue'
import SortIntoBucketsTemplate from '~/components/gameplay/templates/SortIntoBucketsTemplate.vue'
import TaskResultDialog from '~/components/gameplay/shared/TaskResultDialog.vue'
import MultipleChoiceTemplate from '~/components/gameplay/templates/MultipleChoiceTemplate.vue'
import XOrYTemplate from '~/components/gameplay/templates/XOrYTemplate.vue'
import { markExerciseCompleted, useCompletedExerciseUids } from '~/composables/utils/gameProgress'
import { routePaths } from '~/composables/utils/routePaths'
import { Exercise } from '~/composables/types/ExerciseType'
import { Station } from '~/composables/types/StationType'
import { useAudioSettings } from '~/composables/useAudioSettings'
import { useSound } from '@vueuse/sound'
import celebrationSfx from '~/assets/sound/celebration-sfx.mp3'
import { useStudentProfile } from '~/composables/useStudentProfile'
import { getLevel } from '~/composables/utils/studentProgress'

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

const station = await Station.backendFetchStationByUid(requestedStationUid)

if (!station) {
  throw createError({
    statusCode: 404,
    statusMessage: 'Fant ikke denne stasjonen.',
  })
}

const { celebrateCorrectAnswer, stop } = useGameConfetti()
const { getMyProfile } = useStudentProfile()
const completedExerciseUids = await useCompletedExerciseUids()
const completionError = ref('')

const activeExerciseUid = station.exerciseUids.includes(requestedExerciseUid)
  ? requestedExerciseUid
  : station.exerciseUids[0] ?? null

if (activeExerciseUid) {
  const activeExerciseIndex = station.exerciseUids.findIndex((exerciseUid) => exerciseUid === activeExerciseUid)
  const previousExerciseUid = activeExerciseIndex > 0 ? station.exerciseUids[activeExerciseIndex - 1] : null

  if (previousExerciseUid && !completedExerciseUids.includes(previousExerciseUid)) {
    await navigateTo({
      path: routePaths.studentExerciseDifficultySelect,
      query: { uid: station.uid },
    })
  }
}

const activeExercise = activeExerciseUid ? await Exercise.backendFetchExerciseByUid(activeExerciseUid) : null

if (!activeExercise) {
  await navigateTo({
    path: routePaths.studentExerciseDifficultySelect,
    query: { uid: station.uid },
  })

  throw createError({
    statusCode: 404,
    statusMessage: 'Fant ikke denne oppgaven.',
  })
}

const tasks = activeExercise
  ? sampleAndShuffle(activeExercise.tasks, activeExercise.tasks.length)
  : []
const currentIndex = ref(0)
const showExerciseResultDialog = ref(false)
const showLevelupPopup = ref(false)
const leveledUpTo = ref(1)
const correctCount = ref(0)
const answerState = ref<{ correct: boolean, choice: string, missingStartingPassword?: boolean } | null>(null)

const isExerciseComplete = computed(() => tasks.length > 0 && correctCount.value === tasks.length)

const isBossStation = station.isBoss
const { bossfightActive, effectsGain } = useAudioSettings()
const { play: playCelebration, sound: celebrationSound } = useSound(celebrationSfx, {
  volume: effectsGain.value,
})
watch(effectsGain, (gain) => {
  celebrationSound.value?.volume(gain)
})

if (isBossStation) {
  bossfightActive.value = true
}

onBeforeUnmount(() => {
  bossfightActive.value = false
})
const bossMaxHp = 100
const bossHp = computed(() => {
  if (tasks.length === 0) return bossMaxHp
  if (correctCount.value >= tasks.length) return 0
  return Math.max(0, bossMaxHp - Math.floor((correctCount.value * bossMaxHp) / tasks.length))
})
const bossDamaged = ref(false)
let bossDamageTimeout: ReturnType<typeof setTimeout> | null = null

const triggerBossDamage = () => {
  if (bossDamageTimeout) {
    clearTimeout(bossDamageTimeout)
  }
  bossDamaged.value = false
  requestAnimationFrame(() => {
    bossDamaged.value = true
    bossDamageTimeout = setTimeout(() => {
      bossDamaged.value = false
    }, 400)
  })
}

const currentTask = computed(() => tasks[currentIndex.value] ?? null)

const bossTaskImages = computed(() => {
  const task = currentTask.value
  if (!task) return []

  if (task.templateKind === 'multiple-choice' && task.prompt?.imageUrl) {
    return [{
      src: task.prompt.imageUrl,
      alt: task.prompt.imageAlt ?? '',
    }]
  }

  if (task.templateKind === 'x-or-y') {
    return [
      {
        src: task.options.x.imageUrl,
        alt: task.options.x.imageAlt,
        label: task.options.x.eyebrow ?? task.options.x.title,
      },
      {
        src: task.options.y.imageUrl,
        alt: task.options.y.imageAlt,
        label: task.options.y.eyebrow ?? task.options.y.title,
      },
    ]
  }

  return []
})
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

const bossVictoryRoute = computed(() => ({
  path: routePaths.studentLevels,
}))

const showBossVictoryDialog = computed(() =>
  isBossStation && showExerciseResultDialog.value && isExerciseComplete.value
)

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
const canAutoSolve = computed(() => Boolean(currentTask.value) && !answerState.value)
const taskResultActionLabel = computed(() => {
  if (!answerState.value) {
    return 'Neste sak'
  }

  if (!answerState.value.correct) {
    return 'Tilbake til stasjonen'
  }

  return currentIndex.value >= tasks.length - 1 ? 'Fullfor' : 'Neste sak'
})

const autoSolveCurrentTask = () => {
  if (!currentTask.value || answerState.value) {
    return
  }

  if (currentTask.value.templateKind === 'multiple-choice') {
    onAnsweredMultipleChoice({
      correct: true,
      choice: currentTask.value.correctChoice,
    })
    return
  }

  if (currentTask.value.templateKind === 'x-or-y') {
    onAnswered({
      correct: true,
      choice: currentTask.value.correctChoice,
    })
    return
  }

  if (currentTask.value.templateKind === 'email-hotspots') {
    onAnsweredEmailHotspots({
      correct: true,
      selectedTargetIds: currentTask.value.targets
        .filter((target) => target.isSuspicious)
        .map((target) => target.id),
    })
    return
  }

  if (currentTask.value.templateKind === 'sort-into-buckets') {
    const placements = currentTask.value.items.reduce<Record<string, string[]>>((accumulator, item) => {
      if (!accumulator[item.correctBucketKey]) {
        accumulator[item.correctBucketKey] = []
      }

      accumulator[item.correctBucketKey].push(item.id)
      return accumulator
    }, {})

    onAnsweredSortIntoBuckets({
      correct: true,
      placements,
    })
    return
  }

  const fallbackPassword = currentTask.value.mode === 'improve'
    ? `${currentTask.value.startingPassword ?? 'TryggStart'}!9X`
    : 'TryggMaanebase!9X'

  onAnsweredPasswordStrength({
    correct: true,
    password: fallbackPassword,
    score: currentTask.value.minScoreToPass,
  })
}

watch(correctCount, (next, prev) => {
  if (isBossStation && next > prev) {
    triggerBossDamage()
  }
})

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

const fetchCurrentLevel = async () => {
  const profile = await getMyProfile()

  return getLevel(profile.score).level
}

const closeLevelupPopup = () => {
  showLevelupPopup.value = false
  showExerciseResultDialog.value = true
}

const next = async () => {
  stop()

  if (answerState.value && !answerState.value.correct) {
    answerState.value = null
    completionError.value = ''
    await navigateTo(exerciseSelectRoute.value)
    return
  }

  answerState.value = null
  completionError.value = ''

  if (currentIndex.value < tasks.length - 1) {
    currentIndex.value++
  } else {
    if (
      isExerciseComplete.value
      && activeExercise
    ) {
      let levelBeforeCompletion: number | null = null

      try {
        try {
          levelBeforeCompletion = await fetchCurrentLevel()
        } catch (error) {
          console.error('Error fetching student level before completion:', error)
        }

        await markExerciseCompleted(activeExercise.uid)

        if (levelBeforeCompletion !== null) {
          try {
            const levelAfterCompletion = await fetchCurrentLevel()

            if (levelAfterCompletion > levelBeforeCompletion) {
              leveledUpTo.value = levelAfterCompletion
              showLevelupPopup.value = true
            }
          } catch (error) {
            console.error('Error fetching student level after completion:', error)
          }
        }

        if (!completedExerciseUids.includes(activeExercise.uid)) {
          completedExerciseUids.push(activeExercise.uid)
        }
      } catch (error: any) {
        completionError.value =
          error?.data?.detail
          || error?.data?.message
          || error?.statusMessage
          || 'Kunne ikke lagre at oppgaven er fullført.'
      }
    }
    
    if (isExerciseComplete.value) {
      bossfightActive.value = false
      celebrationSound.value?.volume(effectsGain.value)
      playCelebration()
    }

    showExerciseResultDialog.value = true
    if (!showLevelupPopup.value) {
      showExerciseResultDialog.value = true
    }
  }
}
</script>

<template>
  <section class="station-page">
    <header class="station-page__hero">
      <div class="station-page__header">
        <BossCharacter
          v-if="isBossStation"
          :hp="bossHp"
          :max-hp="bossMaxHp"
          :damaged="bossDamaged"
          :images="bossTaskImages"
        />
        <div v-else class="station-page__meta">
          <p class="station-page__eyebrow">{{ station.title }}</p>
          <p class="station-page__progress-pill">{{ progressLabel }}</p>
        </div>
      </div>
    </header>

    <div class="stationPageBoard">
      <XOrYTemplate
        v-if="currentTask?.templateKind === 'x-or-y' && currentOptions"
        :key="`${currentTask.id}-xory`"
        :prompt="currentTask.prompt"
        :options="currentOptions"
        :correct-choice="currentTask.correctChoice"
        :disabled="Boolean(answerState)"
        :selected-choice="selectedChoice"
        :hide-images="isBossStation"
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
        :hide-images="isBossStation"
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

    <div class="station-page__test-tools">
      <button
        class="station-page__test-button"
        type="button"
        :disabled="!canAutoSolve"
        @click="autoSolveCurrentTask"
      >
        Autosolve
      </button>
    </div>

    <TaskResultDialog
      v-if="showAnswerDialog && answerState"
      :title="feedbackTitle"
      :message="feedbackText"
      :is-last="currentIndex >= tasks.length - 1"
      :correct="answerState.correct"
      :action-label="taskResultActionLabel"
      @next="next"
    />

    <BossVictoryDialog
      v-if="showBossVictoryDialog"
      :to="bossVictoryRoute"
    />

    <ExerciseResultDialog
      v-else-if="showExerciseResultDialog"
      :correct-count="correctCount"
      :total="tasks.length"
      msg-completed="Øvelse fullført!"
      msg-failed="Øvelse ikke fullført"
      :to="exerciseSelectRoute"
    />

    <LevelupPopup
      v-if="showLevelupPopup"
      :level="leveledUpTo"
      @close="closeLevelupPopup"
    />

    <p v-if="completionError" class="station-page__completion-error">{{ completionError }}</p>
  </section>
</template>

<style scoped>
.stationPage {
  max-width: 72rem;
  margin: 0 auto;
  display: grid;
  gap: var(--space-4);
  color: #111827;
}

.stationPageHero {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: var(--space-6);
  width: 100%;
}

.stationPageHeader {
  display: grid;
  gap: 0.6rem;
  width: 100%;
}

.stationPageMeta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--space-4);
}

.stationPageEyebrow {
  color: #1d4f91;
  font-size: 1.15rem;
  font-weight: 700;
  letter-spacing: 0.04em;
  text-transform: uppercase;
}

.pageProgressPill {
  flex-shrink: 0;
  padding: 0.45rem 0.85rem;
  border-radius: var(--radius-pill);
  background: #dbeafe;
  color: #1e3a5f;
  font-size: 0.95rem;
  font-weight: 700;
}

.stationPageDescription {
  max-width: 46rem;
  font-size: 0.9rem;
  line-height: 1.5;
  color: var(--palette-gray-700);
}

.pageExerciseTitle {
  font-size: 1.1rem;
  font-weight: 700;
  color: var(--palette-gray-900);
}

.stationPageBoard {
  padding: clamp(0.8rem, 1.4vw, 1.1rem);
  border-radius: 1.6rem;
  background: #f5f5f4;
}

.station-page__test-tools {
  display: flex;
  justify-content: flex-end;
}

.station-page__test-button {
  padding: 0.75rem 1rem;
  border: 1px dashed #1d4f91;
  border-radius: 999px;
  background: #eff6ff;
  color: #1d4f91;
  font-weight: 700;
}

.station-page__test-button:disabled {
  opacity: 0.55;
  cursor: not-allowed;
}

.station-page__completion-error {
  margin: 0;
  color: #991b1b;
  font-size: 0.95rem;
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
