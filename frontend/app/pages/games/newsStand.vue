<script setup lang="ts">
import NewsStandExercise from '~/components/gameplay/newsstand/NewsStandExercise.vue'
import CompletedPopup from '~/components/gameplay/shared/CompletedPopup.vue'
import SelectedPopup from '~/components/gameplay/shared/SelectedPopup.vue'
import type { NewsStandTask } from '~/composables/types/TaskTypes'
import { sampleAndShuffle } from '~/composables/utils/gameUtils'

definePageMeta({
  layout: 'game',
})

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
  <section class="newsstand-page">
    <header class="newsstand-page__header">
      <p class="newsstand-page__eyebrow">Nyhetskvartalet</p>
      <h1>Aviskiosken</h1>
      <p class="newsstand-page__description">
        Se på de to artiklene og velg hvilken som virker mest troverdig.
      </p>
    </header>

    <div class="newsstand-page__board">
      <NewsStandExercise
        v-if="tasks[currentIndex]"
        :data="mockData.articles[currentIndex]!"
        @answered="onAnswered"
      />
    </div>

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
  </section>
</template>

<style scoped>
.newsstand-page {
  max-width: 72rem;
  margin: 0 auto;
  display: grid;
  gap: 1.5rem;
  color: #111827;
}

.newsstand-page__header {
  display: grid;
  gap: 0.5rem;
}

.newsstand-page__eyebrow {
  color: #1d4f91;
  font-size: 0.95rem;
  font-weight: 700;
  letter-spacing: 0.04em;
  text-transform: uppercase;
}

.newsstand-page h1 {
  font-size: clamp(2rem, 4vw, 3rem);
  line-height: 1.1;
}

.newsstand-page__description {
  max-width: 44rem;
  font-size: 1.05rem;
  line-height: 1.5;
}

.newsstand-page__board {
  padding: 1.5rem;
  border: 1px solid #d7e5f6;
  border-radius: 1.5rem;
  background: #f8fbff;
}

@media (max-width: 780px) {
  .newsstand-page__board {
    padding: 1rem;
  }
}
</style>