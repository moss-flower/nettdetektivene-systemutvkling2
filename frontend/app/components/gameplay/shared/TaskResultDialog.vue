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
        class="resultDialogBadge"
        :class="{
          'dialogBadgeCorrect': correct,
          'dialogBadgeIncorrect': !correct,
        }"
      >
        {{ correct ? 'Riktig svar' : 'Ikke helt' }}
      </p>
    </template>

    <h2 class="resultTitle">{{ title }}</h2>
    <p class="resultMessage">{{ message }}</p>

    <template #actions>
      <button
        class="resultButton"
        type="button"
        @click="$emit('next')"
      >
        {{ isLast ? 'Fullfør' : 'Neste sak' }}
      </button>
    </template>
  </DialogTemplate>
</template>

<style scoped>
.resultDialogBadge {
  justify-self: start;
  padding: 0.35rem 0.7rem;
  border-radius: var(--radius-pill);
  font-size: 0.88rem;
  font-weight: 700;
}

.dialogBadgeCorrect {
  background: var(--palette-green-100);
  color: var(--palette-green-700);
}

.dialogBadgeIncorrect {
  background: var(--palette-red-200);
  color: var(--palette-red-700);
}

.resultTitle {
  font-size: 1.4rem;
  line-height: 1.1;
  color: var(--color-text-primary);
}

.resultMessage {
  font-size: var(--font-size-16);
  line-height: 1.5;
  color: var(--palette-gray-700);
}

.resultButton {
  padding: 0.85rem 1.2rem;
  border: 0;
  border-radius: var(--radius-pill);
  background: var(--color-brand-primary);
  color: #f9fbff;
  font-weight: 700;
  transition: transform var(--transition-base) ease, background-color var(--transition-base) ease;
}

.resultButton:hover,
.resultButton:focus-visible {
  background: var(--palette-navy-700);
  transform: translateY(-1px);
  outline: none;
}

</style>
