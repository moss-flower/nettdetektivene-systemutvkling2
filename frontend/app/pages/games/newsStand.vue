<script setup lang="ts">
import CompletedPopup from '~/components/gameplay/shared/CompletedPopup.vue'
import XOrYExercise from '~/components/gameplay/shared/XOrYExercise.vue'
import type { NewsStandTask, XOrYChoice, XOrYOptionContent, XOrYPair, XOrYResult } from '~/composables/types/TaskTypes'
import { sampleAndShuffle } from '~/composables/utils/gameUtils'

definePageMeta({
  layout: 'game',
})

const { celebrateCorrectAnswer, stop } = useGameConfetti()

const mockData: NewsStandTask = {
  id: "1",
  kind: "newsStand",
  description: "Hvilke nyheter er ekte og hvilke er falske?",
  articles: [
    {
      correct: { author: "NRK", title: "Ekte nyhet 1", imageUrl: "https://placehold.co/600x400?text=Real+1", text: "Dette er en ekte nyhetsartikkel." },
      fake: { author: "Ukjent", title: "Falsk nyhet 1", imageUrl: "https://placehold.co/600x400?text=Fake+1", text: "Dette er en falsk nyhetsartikkel." },
    },
    {
      correct: { author: "VG", title: "Ekte nyhet 2", imageUrl: "https://placehold.co/600x400?text=Real+2", text: "Dette er også en ekte nyhetsartikkel." },
      fake: { author: "Tull", title: "Falsk nyhet 2", imageUrl: "https://placehold.co/600x400?text=Fake+2", text: "Dette er også en falsk nyhetsartikkel." },
    },
  ]
}

const tasks = sampleAndShuffle(mockData.articles, mockData.articles.length)
const currentIndex = ref(0)
const showCompletedPopup = ref(false)
const correctCount = ref(0)
const answerState = ref<{ correct: boolean, choice: 'correct' | 'fake' } | null>(null)

const currentTask = computed(() => tasks[currentIndex.value] ?? null)
const currentOptions = computed<XOrYPair<XOrYOptionContent> | null>(() => {
  if (!currentTask.value) {
    return null
  }

  return {
    x: {
      imageUrl: currentTask.value.correct.imageUrl,
      imageAlt: `Bilde til artikkelen ${currentTask.value.correct.title}`,
      eyebrow: currentTask.value.correct.author,
      title: currentTask.value.correct.title,
      text: currentTask.value.correct.text,
    },
    y: {
      imageUrl: currentTask.value.fake.imageUrl,
      imageAlt: `Bilde til artikkelen ${currentTask.value.fake.title}`,
      eyebrow: currentTask.value.fake.author,
      title: currentTask.value.fake.title,
      text: currentTask.value.fake.text,
    },
  }
})
const selectedChoice = computed<XOrYChoice | null>(() => {
  if (answerState.value?.choice === 'correct') {
    return 'x'
  }

  if (answerState.value?.choice === 'fake') {
    return 'y'
  }

  return null
})
const progressLabel = computed(() => `Sak ${currentIndex.value + 1} av ${tasks.length}`)
const feedbackTitle = computed(() => {
  if (!answerState.value) {
    return 'Velg avisen du stoler mest på'
  }

  return answerState.value.correct ? 'Riktig svar' : 'Ikke helt'
})

const feedbackText = computed(() => {
  if (!answerState.value || !currentTask.value) {
    return 'Se etter hvem som er avsender, om overskriften prøver å lure deg, og om innholdet virker troverdig.'
  }

  if (answerState.value.correct) {
    return `Bra observert. Artikkelen fra ${currentTask.value.correct.author} virker mest troverdig her, fordi avsenderen og presentasjonen ser mer seriøse ut.`
  }

  return `Denne gangen var det artikkelen fra ${currentTask.value.correct.author} som var mest troverdig. Se ekstra etter om overskriften virker overdrevet eller om avsenderen føles mindre pålitelig.`
})

const onAnswered = (result: XOrYResult) => {
  answerState.value = {
    correct: result.correct,
    choice: result.choice === 'x' ? 'correct' : 'fake',
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
  <section class="newsstand-page">
    <header class="newsstand-page__hero">
      <div class="newsstand-page__header">
        <p class="newsstand-page__eyebrow">Nyhetskvartalet</p>
        <h1>Nyhetskvartalet - {{ progressLabel }}</h1>
        <p class="newsstand-page__description">
          I oppgaven under ser du to nyhetsartikler. Trykk på den som virker mest troverdig.
        </p>
      </div>
    </header>

    <div class="newsstand-page__board">
      <XOrYExercise
        v-if="currentOptions"
        :options="currentOptions"
        correct-choice="x"
        :disabled="Boolean(answerState)"
        :selected-choice="selectedChoice"
        @answered="onAnswered"
      />
    </div>

    <section class="newsstand-page__feedback" :class="{ 'newsstand-page__feedback--answered': Boolean(answerState) }">
      <div class="newsstand-page__feedback-copy">
        <p class="newsstand-page__feedback-title">{{ feedbackTitle }}</p>
        <p class="newsstand-page__feedback-text">{{ feedbackText }}</p>
      </div>
      <button
        v-if="answerState"
        class="newsstand-page__next-button"
        type="button"
        @click="next"
      >
        {{ currentIndex >= tasks.length - 1 ? 'Fullfør' : 'Neste sak' }}
      </button>
    </section>

    <CompletedPopup
      v-if="showCompletedPopup"
      :correct-count="correctCount"
      :total="tasks.length"
    />
  </section>
</template>

<style scoped>
.newsstand-page {
  max-width: 72rem;
  margin: 0 auto;
  display: grid;
  gap: 1.75rem;
  color: #111827;
}

.newsstand-page__hero {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 1.5rem;
}

.newsstand-page__header {
  display: grid;
  gap: 0.6rem;
  max-width: 48rem;
}

.newsstand-page__eyebrow {
  color: #1d4f91;
  font-size: 0.95rem;
  font-weight: 700;
  letter-spacing: 0.04em;
  text-transform: uppercase;
}

.newsstand-page h1 {
  font-size: clamp(2rem, 4vw, 3.2rem);
  line-height: 1;
  color: #171717;
}

.newsstand-page__description {
  max-width: 46rem;
  font-size: 1.08rem;
  line-height: 1.5;
  color: #374151;
}

.newsstand-page__board {
  padding: clamp(1rem, 2vw, 1.5rem);
  border-radius: 1.6rem;
  background: #fdf9f2;
}

.newsstand-page__feedback {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  padding: 1rem 1.35rem;
  background: #ede7e2;
  border-radius: 0.5rem;
  color: #34251f;
}

.newsstand-page__feedback--answered {
  background: #e5ddd7;
}

.newsstand-page__feedback-copy {
  display: grid;
  gap: 0.25rem;
}

.newsstand-page__feedback-title {
  font-weight: 700;
  font-size: 1.05rem;
}

.newsstand-page__feedback-text {
  line-height: 1.45;
}

.newsstand-page__next-button {
  flex-shrink: 0;
  padding: 0.85rem 1.2rem;
  border: 0;
  border-radius: 999px;
  background: #1f3657;
  color: #f9fbff;
  font-weight: 700;
  transition: transform 0.2s ease, background-color 0.2s ease;
}

.newsstand-page__next-button:hover,
.newsstand-page__next-button:focus-visible {
  background: #27456f;
  transform: translateY(-1px);
  outline: none;
}

@media (max-width: 780px) {
  .newsstand-page__hero,
  .newsstand-page__feedback {
    flex-direction: column;
    align-items: stretch;
  }

  .newsstand-page__progress-card {
    min-width: 0;
  }

  .newsstand-page__board {
    padding: 1rem;
  }

  .newsstand-page__next-button {
    width: 100%;
  }
}
</style>