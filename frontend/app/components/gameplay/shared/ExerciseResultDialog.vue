<script setup lang="ts">
import { NuxtLink } from '#components'
import type { RouteLocationRaw } from 'vue-router'
import DialogTemplate from '~/components/gameplay/shared/DialogTemplate.vue'

const props = withDefaults(defineProps<{
  correctCount: number
  total: number
  msgCompleted?: string
  msgFailed?: string
  to?: RouteLocationRaw
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
        class="resultDialogBadge"
        :class="{
          'dialogBadgeComplete': isComplete,
          'dialogBadgeIncomplete': !isComplete,
        }"
      >
        {{ isComplete ? 'Øvelse fullført' : 'Prøv igjen' }}
      </p>
    </template>

    <h2 class="resultTitle">{{ isComplete ? msgCompleted : msgFailed }}</h2>
    <p class="resultMessage">Du fikk {{ correctCount }} / {{ total }} riktig.</p>

    <template #actions>
      <component
        :is="to ? NuxtLink : 'button'"
        class="resultButton"
        :to="to"
        :type="to ? undefined : 'button'"
        @click="to ? undefined : emit('next')"
      >
        Gå tilbake
      </component>
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

.dialogBadgeComplete {
  background: var(--palette-green-100);
  color: var(--palette-green-700);
}

.dialogBadgeIncomplete {
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
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 0.85rem 1.2rem;
  border: 0;
  border-radius: var(--radius-pill);
  background: var(--color-brand-primary);
  color: #f9fbff;
  font-weight: 700;
  text-decoration: none;
  transition: transform var(--transition-base) ease, background-color var(--transition-base) ease;
}

.resultButton:hover,
.resultButton:focus-visible {
  background: var(--palette-navy-700);
  transform: translateY(-1px);
  outline: none;
}

</style>
