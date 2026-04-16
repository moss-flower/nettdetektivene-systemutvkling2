<script setup lang="ts">
import type { XOrYChoice, XOrYOptionContent, XOrYPair, XOrYResult } from '~/composables/types/TaskTypes'

const props = defineProps<{
  options: XOrYPair<XOrYOptionContent>
  correctChoice: XOrYChoice
  disabled?: boolean
  selectedChoice?: XOrYChoice | null
}>()

const emit = defineEmits<{
  (e: 'answered', payload: XOrYResult): void
}>()

const choose = (choice: XOrYChoice) => {
  if (props.disabled) {
    return
  }

  emit('answered', {
    correct: choice === props.correctChoice,
    choice,
  })
}
</script>

<template>
  <div class="x-or-y-exercise">
    <article
      class="x-or-y-exercise__option"
      :class="{
        'x-or-y-exercise__option--selected': props.selectedChoice === 'x',
        'x-or-y-exercise__option--locked': props.disabled && props.selectedChoice !== 'x',
      }"
    >
      <button
        class="x-or-y-exercise__surface"
        type="button"
        :disabled="props.disabled"
        @click="choose('x')"
      >
        <div class="x-or-y-exercise__card">
          <img
            :src="props.options.x.imageUrl"
            :alt="props.options.x.imageAlt"
            class="x-or-y-exercise__image"
          >
          <div class="x-or-y-exercise__content">
            <p v-if="props.options.x.eyebrow" class="x-or-y-exercise__eyebrow">
              {{ props.options.x.eyebrow }}
            </p>
            <h2 class="x-or-y-exercise__title">{{ props.options.x.title }}</h2>
            <p class="x-or-y-exercise__text">{{ props.options.x.text }}</p>
          </div>
        </div>
      </button>
    </article>

    <article
      class="x-or-y-exercise__option"
      :class="{
        'x-or-y-exercise__option--selected': props.selectedChoice === 'y',
        'x-or-y-exercise__option--locked': props.disabled && props.selectedChoice !== 'y',
      }"
    >
      <button
        class="x-or-y-exercise__surface"
        type="button"
        :disabled="props.disabled"
        @click="choose('y')"
      >
        <div class="x-or-y-exercise__card">
          <img
            :src="props.options.y.imageUrl"
            :alt="props.options.y.imageAlt"
            class="x-or-y-exercise__image"
          >
          <div class="x-or-y-exercise__content">
            <p v-if="props.options.y.eyebrow" class="x-or-y-exercise__eyebrow">
              {{ props.options.y.eyebrow }}
            </p>
            <h2 class="x-or-y-exercise__title">{{ props.options.y.title }}</h2>
            <p class="x-or-y-exercise__text">{{ props.options.y.text }}</p>
          </div>
        </div>
      </button>
    </article>
  </div>
</template>

<style scoped>
.x-or-y-exercise {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: clamp(1rem, 3vw, 2.5rem);
  align-items: start;
}

.x-or-y-exercise__option {
  display: grid;
}

.x-or-y-exercise__surface {
  width: 100%;
  padding: 0;
  border: 0;
  background: transparent;
  text-align: left;
}

.x-or-y-exercise__surface:focus-visible {
  outline: none;
}

.x-or-y-exercise__card {
  min-height: 100%;
  border-radius: 1.6rem;
  padding: 0.8rem;
  background: #fffdfa;
  border: 2px solid #d6d3ce;
  box-shadow: 0 14px 26px rgba(47, 39, 28, 0.08);
  transition: transform 0.2s ease, box-shadow 0.2s ease, border-color 0.2s ease;
}

.x-or-y-exercise__surface:hover:not(:disabled) .x-or-y-exercise__card,
.x-or-y-exercise__surface:focus-visible .x-or-y-exercise__card {
  transform: translateY(-3px);
  border-color: #8ea7c4;
  box-shadow: 0 18px 32px rgba(47, 39, 28, 0.12);
}

.x-or-y-exercise__image {
  width: 100%;
  aspect-ratio: 1.1 / 0.8;
  object-fit: cover;
  border-radius: 1rem 1rem 0 0;
  display: block;
}

.x-or-y-exercise__content {
  display: grid;
  gap: 0.75rem;
  padding: 1rem 1rem 1.2rem;
}

.x-or-y-exercise__eyebrow {
  font-size: 0.85rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: #6b7280;
}

.x-or-y-exercise__title {
  font-family: Georgia, "Times New Roman", serif;
  font-size: clamp(1.8rem, 3vw, 3.35rem);
  line-height: 0.95;
  color: #241713;
  text-wrap: balance;
}

.x-or-y-exercise__text {
  font-size: 1rem;
  line-height: 1.5;
  color: #47372e;
}

.x-or-y-exercise__option--selected .x-or-y-exercise__card {
  border-color: #4c7bb0;
  box-shadow: 0 18px 34px rgba(65, 104, 150, 0.18);
}

.x-or-y-exercise__option--locked {
  opacity: 0.82;
}

.x-or-y-exercise__surface:disabled {
  cursor: default;
}

@media (max-width: 900px) {
  .x-or-y-exercise__title {
    font-size: clamp(1.6rem, 5vw, 2.6rem);
  }
}

@media (max-width: 760px) {
  .x-or-y-exercise {
    grid-template-columns: 1fr;
  }
}
</style>
