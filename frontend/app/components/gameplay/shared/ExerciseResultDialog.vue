<script setup lang="ts">
import DialogTemplate from '~/components/gameplay/shared/DialogTemplate.vue'

const props = withDefaults(defineProps<{
  correctCount: number
  total: number
  msgCompleted?: string
  msgFailed?: string
}>(), {
  msgCompleted: 'Ferdig, du klarte det!',
  msgFailed: 'Ikke helt, bedre lykke neste gang!',
})

const isComplete = computed(() => props.correctCount === props.total)

const emit = defineEmits<{
  (e: 'next'): void
}>()
</script>

<template>
  <DialogTemplate :aria-label="isComplete ? msgCompleted : msgFailed">
    <template #badge>
      <p
        class="exercise-result-dialog__badge"
        :class="{
          'exercise-result-dialog__badge--complete': isComplete,
          'exercise-result-dialog__badge--incomplete': !isComplete,
        }"
      >
        {{ isComplete ? 'Øvelse fullført' : 'Prøv igjen' }}
      </p>
    </template>

    <h2 class="exercise-result-dialog__title">{{ isComplete ? msgCompleted : msgFailed }}</h2>
    <p class="exercise-result-dialog__message">Du fikk {{ correctCount }} / {{ total }} riktig.</p>

    <template #actions>
      <button class="exercise-result-dialog__button" @click="emit('next')">Gå Tilbake</button>
    </template>
  </DialogTemplate>
</template>

<style scoped>
.exercise-result-dialog__badge {
  justify-self: start;
  padding: 0.35rem 0.7rem;
  border-radius: 999px;
  font-size: 0.88rem;
  font-weight: 700;
}

.exercise-result-dialog__badge--complete {
  background: #dcfce7;
  color: #166534;
}

.exercise-result-dialog__badge--incomplete {
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
  .exercise-result-dialog__button {
    width: 100%;
  }
}
</style>
