<script setup lang="ts">
import type { XOrYChoice, XOrYOptionContent, XOrYPair, XOrYResult } from '~/composables/types/TaskTypes'

const props = defineProps<{
  prompt?: {
    title?: string
    text?: string
  }
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
  <div class="x-or-y-template">
    <header
      v-if="props.prompt?.title || props.prompt?.text"
      class="x-or-y-template__header"
    >
      <h2 v-if="props.prompt?.title" class="x-or-y-template__prompt-title">
        {{ props.prompt.title }}
      </h2>
      <p v-if="props.prompt?.text" class="x-or-y-template__prompt-text">
        {{ props.prompt.text }}
      </p>
    </header>

    <article
      class="x-or-y-template__option"
      :class="{
        'x-or-y-template__option--selected': props.selectedChoice === 'x',
        'x-or-y-template__option--locked': props.disabled && props.selectedChoice !== 'x',
      }"
    >
      <button
        class="x-or-y-template__surface"
        type="button"
        :disabled="props.disabled"
        @click="choose('x')"
      >
        <div class="x-or-y-template__card">
          <img
            :src="props.options.x.imageUrl"
            :alt="props.options.x.imageAlt"
            class="x-or-y-template__image"
          >
          <div class="x-or-y-template__content">
            <p v-if="props.options.x.eyebrow" class="x-or-y-template__eyebrow">
              {{ props.options.x.eyebrow }}
            </p>
            <h2 class="x-or-y-template__title">{{ props.options.x.title }}</h2>
            <p class="x-or-y-template__text">{{ props.options.x.text }}</p>
          </div>
        </div>
      </button>
    </article>

    <article
      class="x-or-y-template__option"
      :class="{
        'x-or-y-template__option--selected': props.selectedChoice === 'y',
        'x-or-y-template__option--locked': props.disabled && props.selectedChoice !== 'y',
      }"
    >
      <button
        class="x-or-y-template__surface"
        type="button"
        :disabled="props.disabled"
        @click="choose('y')"
      >
        <div class="x-or-y-template__card">
          <img
            :src="props.options.y.imageUrl"
            :alt="props.options.y.imageAlt"
            class="x-or-y-template__image"
          >
          <div class="x-or-y-template__content">
            <p v-if="props.options.y.eyebrow" class="x-or-y-template__eyebrow">
              {{ props.options.y.eyebrow }}
            </p>
            <h2 class="x-or-y-template__title">{{ props.options.y.title }}</h2>
            <p class="x-or-y-template__text">{{ props.options.y.text }}</p>
          </div>
        </div>
      </button>
    </article>
  </div>
</template>

<style scoped>
.x-or-y-template {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: clamp(0.9rem, 2vw, 1.5rem);
  align-items: start;
}

.x-or-y-template__header {
  grid-column: 1 / -1;
  display: grid;
  gap: 0.35rem;
}

.x-or-y-template__prompt-title {
  font-size: clamp(1.15rem, 1.8vw, 1.45rem);
  line-height: 1.15;
  color: #172033;
}

.x-or-y-template__prompt-text {
  max-width: 48rem;
  font-size: 0.95rem;
  line-height: 1.45;
  color: #4b5563;
}

.x-or-y-template__option {
  display: grid;
}

.x-or-y-template__surface {
  width: 100%;
  padding: 0;
  border: 0;
  background: transparent;
  text-align: left;
}

.x-or-y-template__surface:focus-visible {
  outline: none;
}

.x-or-y-template__card {
  min-height: 100%;
  border-radius: 1.6rem;
  padding: 0.7rem;
  background: #fffdfa;
  border: 2px solid #d6d3ce;
  box-shadow: 0 14px 26px rgba(47, 39, 28, 0.08);
  transition: transform 0.2s ease, box-shadow 0.2s ease, border-color 0.2s ease;
}

.x-or-y-template__surface:hover:not(:disabled) .x-or-y-template__card,
.x-or-y-template__surface:focus-visible .x-or-y-template__card {
  transform: translateY(-3px);
  border-color: #8ea7c4;
  box-shadow: 0 18px 32px rgba(47, 39, 28, 0.12);
}

.x-or-y-template__image {
  width: 100%;
  aspect-ratio: 1.2 / 0.68;
  max-height: min(25vh, 13rem);
  object-fit: cover;
  border-radius: 1rem 1rem 0 0;
  display: block;
}

.x-or-y-template__content {
  display: grid;
  gap: 0.55rem;
  padding: 0.8rem 0.85rem 0.95rem;
}

.x-or-y-template__eyebrow {
  font-size: 0.8rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: #6b7280;
}

.x-or-y-template__title {
  font-family: Georgia, "Times New Roman", serif;
  font-size: clamp(1.35rem, 2.2vw, 2.2rem);
  line-height: 1;
  color: #241713;
  text-wrap: balance;
}

.x-or-y-template__text {
  font-size: 0.94rem;
  line-height: 1.4;
  color: #47372e;
}

.x-or-y-template__option--selected .x-or-y-template__card {
  border-color: #4c7bb0;
  box-shadow: 0 18px 34px rgba(65, 104, 150, 0.18);
}

.x-or-y-template__option--locked {
  opacity: 0.82;
}

.x-or-y-template__surface:disabled {
  cursor: default;
}

@media (max-width: 900px) {
  .x-or-y-template__title {
    font-size: clamp(1.2rem, 4vw, 1.9rem);
  }
}

@media (max-width: 760px) {
  .x-or-y-template {
    grid-template-columns: 1fr;
  }
}
</style>
