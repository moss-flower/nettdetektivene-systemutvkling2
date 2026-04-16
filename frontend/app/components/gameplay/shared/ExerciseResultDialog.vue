<script setup lang="ts">
defineProps<{
  title: string
  message: string
  isLast: boolean
  correct: boolean
}>()

defineEmits<{
  (e: 'next'): void
}>()
</script>

<template>
  <div class="exercise-result-dialog">
    <div class="exercise-result-dialog__backdrop" />
    <div
      class="exercise-result-dialog__panel"
      role="dialog"
      aria-modal="true"
      :aria-label="title"
    >
      <p
        class="exercise-result-dialog__badge"
        :class="{
          'exercise-result-dialog__badge--correct': correct,
          'exercise-result-dialog__badge--incorrect': !correct,
        }"
      >
        {{ correct ? 'Riktig svar' : 'Ikke helt' }}
      </p>
      <h2 class="exercise-result-dialog__title">{{ title }}</h2>
      <p class="exercise-result-dialog__message">{{ message }}</p>
      <button
        class="exercise-result-dialog__button"
        type="button"
        @click="$emit('next')"
      >
        {{ isLast ? 'Fullfør' : 'Neste sak' }}
      </button>
    </div>
  </div>
</template>

<style scoped>
.exercise-result-dialog {
  position: fixed;
  inset: 0;
  z-index: 40;
  display: grid;
  place-items: center;
  padding: 1.5rem;
}

.exercise-result-dialog__backdrop {
  position: absolute;
  inset: 0;
  background: rgba(17, 24, 39, 0.34);
  backdrop-filter: blur(3px);
}

.exercise-result-dialog__panel {
  position: relative;
  width: min(100%, 32rem);
  display: grid;
  gap: 0.9rem;
  padding: 1.4rem;
  border-radius: 1.2rem;
  background: #fffdf9;
  box-shadow: 0 24px 70px rgba(15, 23, 42, 0.22);
}

.exercise-result-dialog__badge {
  justify-self: start;
  padding: 0.35rem 0.7rem;
  border-radius: 999px;
  font-size: 0.88rem;
  font-weight: 700;
}

.exercise-result-dialog__badge--correct {
  background: #dcfce7;
  color: #166534;
}

.exercise-result-dialog__badge--incorrect {
  background: #fee2e2;
  color: #991b1b;
}

.exercise-result-dialog__title {
  font-size: 1.4rem;
  line-height: 1.1;
  color: #172033;
}

.exercise-result-dialog__message {
  font-size: 1rem;
  line-height: 1.5;
  color: #374151;
}

.exercise-result-dialog__button {
  justify-self: end;
  padding: 0.85rem 1.2rem;
  border: 0;
  border-radius: 999px;
  background: #1f3657;
  color: #f9fbff;
  font-weight: 700;
  transition: transform 0.2s ease, background-color 0.2s ease;
}

.exercise-result-dialog__button:hover,
.exercise-result-dialog__button:focus-visible {
  background: #27456f;
  transform: translateY(-1px);
  outline: none;
}

@media (max-width: 640px) {
  .exercise-result-dialog {
    padding: 1rem;
  }

  .exercise-result-dialog__panel {
    padding: 1.2rem;
  }

  .exercise-result-dialog__button {
    width: 100%;
    justify-self: stretch;
  }
}
</style>
