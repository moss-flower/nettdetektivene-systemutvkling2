<script setup lang="ts">
import type { NewsStandPair } from '~/composables/types/TaskTypes'

const props = defineProps<{
  data: NewsStandPair
  disabled?: boolean
  selectedChoice?: 'correct' | 'fake' | null
}>()

const emit = defineEmits<{
  (e: 'answered', payload: { correct: boolean, choice: 'correct' | 'fake' }): void
}>()

function answer(choice: 'correct' | 'fake') {
  if (props.disabled) {
    return
  }

  emit('answered', { correct: choice === 'correct', choice })
}
</script>

<template>
  <div class="newsstand-board">
    <article
      class="news-card"
      :class="{
        'news-card--selected': props.selectedChoice === 'correct',
        'news-card--locked': props.disabled && props.selectedChoice !== 'correct',
      }"
    >
      <button class="news-card__surface" type="button" :disabled="props.disabled" @click="answer('correct')">
        <div class="news-card__paper">
          <img
            :src="props.data.correct.imageUrl"
            :alt="`Bilde til artikkelen ${props.data.correct.title}`"
            class="news-card__image"
          />
          <div class="news-card__content">
            <p class="news-card__source">{{ props.data.correct.author }}</p>
            <h2 class="news-card__headline">{{ props.data.correct.title }}</h2>
            <p class="news-card__summary">{{ props.data.correct.text }}</p>
          </div>
        </div>
      </button>
      <button class="news-card__action" type="button" :disabled="props.disabled" @click="answer('correct')">
        Denne er ekte
      </button>
    </article>

    <article
      class="news-card"
      :class="{
        'news-card--selected': props.selectedChoice === 'fake',
        'news-card--locked': props.disabled && props.selectedChoice !== 'fake',
      }"
    >
      <button class="news-card__surface" type="button" :disabled="props.disabled" @click="answer('fake')">
        <div class="news-card__paper">
          <img
            :src="props.data.fake.imageUrl"
            :alt="`Bilde til artikkelen ${props.data.fake.title}`"
            class="news-card__image"
          />
          <div class="news-card__content">
            <p class="news-card__source">{{ props.data.fake.author }}</p>
            <h2 class="news-card__headline">{{ props.data.fake.title }}</h2>
            <p class="news-card__summary">{{ props.data.fake.text }}</p>
          </div>
        </div>
      </button>
      <button class="news-card__action" type="button" :disabled="props.disabled" @click="answer('fake')">
        Denne er ekte
      </button>
    </article>
  </div>
</template>

<style scoped>
.newsstand-board {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: clamp(1rem, 3vw, 2.5rem);
  align-items: start;
}

.news-card {
  display: grid;
  gap: 1rem;
}

.news-card__surface {
  width: 100%;
  padding: 0;
  border: 0;
  background: transparent;
  text-align: left;
}

.news-card__paper {
  min-height: 100%;
  border-radius: 1.6rem;
  padding: 0.8rem;
  background: #fffdfa;
  border: 2px solid #d6d3ce;
  box-shadow: 0 14px 26px rgba(47, 39, 28, 0.08);
  transition: transform 0.2s ease, box-shadow 0.2s ease, border-color 0.2s ease;
}

.news-card__surface:hover:not(:disabled) .news-card__paper,
.news-card__surface:focus-visible .news-card__paper {
  transform: translateY(-3px);
  border-color: #8ea7c4;
  box-shadow: 0 18px 32px rgba(47, 39, 28, 0.12);
}

.news-card__surface:focus-visible {
  outline: none;
}

.news-card__image {
  width: 100%;
  aspect-ratio: 1.1 / 0.8;
  object-fit: cover;
  border-radius: 1rem 1rem 0 0;
  display: block;
}

.news-card__content {
  display: grid;
  gap: 0.75rem;
  padding: 1rem 1rem 1.2rem;
}

.news-card__source {
  font-size: 0.85rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: #6b7280;
}

.news-card__headline {
  font-family: Georgia, "Times New Roman", serif;
  font-size: clamp(1.8rem, 3vw, 3.35rem);
  line-height: 0.95;
  color: #241713;
  text-wrap: balance;
}

.news-card__summary {
  font-size: 1rem;
  line-height: 1.5;
  color: #47372e;
}

.news-card__action {
  justify-self: center;
  min-width: min(100%, 13rem);
  padding: 0.9rem 1.25rem;
  border: 0;
  background: #d9d9d9;
  color: #1f2937;
  font-size: 1.05rem;
  border-radius: 0;
  box-shadow: inset 0 -2px 0 rgba(0, 0, 0, 0.08);
  transition: background-color 0.2s ease, transform 0.2s ease;
}

.news-card__action:hover:not(:disabled),
.news-card__action:focus-visible {
  background: #cdd5df;
  transform: translateY(-1px);
  outline: none;
}

.news-card--selected .news-card__paper {
  border-color: #4c7bb0;
  box-shadow: 0 18px 34px rgba(65, 104, 150, 0.18);
}

.news-card--selected .news-card__action {
  background: #bdd1ea;
}

.news-card--locked {
  opacity: 0.82;
}

.news-card__surface:disabled,
.news-card__action:disabled {
  cursor: default;
}

@media (max-width: 900px) {
  .news-card__headline {
    font-size: clamp(1.6rem, 5vw, 2.6rem);
  }
}

@media (max-width: 760px) {
  .newsstand-board {
    grid-template-columns: 1fr;
  }

  .news-card__action {
    width: 100%;
  }
}
</style>