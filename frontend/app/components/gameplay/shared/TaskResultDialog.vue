<script setup lang="ts">
import DialogTemplate from '~/components/gameplay/shared/DialogTemplate.vue'

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
  <DialogTemplate :aria-label="title">
    <template #badge>
      <p
        class="task-result-dialog__badge"
        :class="{
          'task-result-dialog__badge--correct': correct,
          'task-result-dialog__badge--incorrect': !correct,
        }"
      >
        {{ correct ? 'Riktig svar' : 'Ikke helt' }}
      </p>
    </template>

    <h2 class="task-result-dialog__title">{{ title }}</h2>
    <p class="task-result-dialog__message">{{ message }}</p>

    <template #actions>
      <button
        class="task-result-dialog__button"
        type="button"
        @click="$emit('next')"
      >
        {{ isLast ? 'Fullfør' : 'Neste sak' }}
      </button>
    </template>
  </DialogTemplate>
</template>

<style scoped>
.task-result-dialog__badge {
  justify-self: start;
  padding: 0.35rem 0.7rem;
  border-radius: 999px;
  font-size: 0.88rem;
  font-weight: 700;
}

.task-result-dialog__badge--correct {
  background: #dcfce7;
  color: #166534;
}

.task-result-dialog__badge--incorrect {
  background: #fee2e2;
  color: #991b1b;
}

.task-result-dialog__title {
  font-size: 1.4rem;
  line-height: 1.1;
  color: #172033;
}

.task-result-dialog__message {
  font-size: 1rem;
  line-height: 1.5;
  color: #374151;
}

.task-result-dialog__button {
  padding: 0.85rem 1.2rem;
  border: 0;
  border-radius: 999px;
  background: #1f3657;
  color: #f9fbff;
  font-weight: 700;
  transition: transform 0.2s ease, background-color 0.2s ease;
}

.task-result-dialog__button:hover,
.task-result-dialog__button:focus-visible {
  background: #27456f;
  transform: translateY(-1px);
  outline: none;
}

@media (max-width: 640px) {
  .task-result-dialog__button {
    width: 100%;
  }
}
</style>
