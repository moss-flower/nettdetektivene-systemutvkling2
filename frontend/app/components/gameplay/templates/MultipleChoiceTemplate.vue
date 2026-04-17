<script setup lang="ts">
import type {
  MultipleChoiceOptionContent,
  MultipleChoiceOptionKey,
  MultipleChoicePromptContent,
  MultipleChoiceResult,
} from '~/composables/types/TaskTypes'

const props = defineProps<{
  prompt: MultipleChoicePromptContent
  options: MultipleChoiceOptionContent[]
  correctChoice: MultipleChoiceOptionKey
  disabled?: boolean
  selectedChoice?: MultipleChoiceOptionKey | null
}>()

const emit = defineEmits<{
  (e: 'answered', payload: MultipleChoiceResult): void
}>()

const choose = (choice: MultipleChoiceOptionKey) => {
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
  <section class="three-option-image-template">
    <header v-if="props.prompt.title || props.prompt.text" class="three-option-image-template__header">
      <h2 v-if="props.prompt.title" class="three-option-image-template__title">
        {{ props.prompt.title }}
      </h2>
      <p v-if="props.prompt.text" class="three-option-image-template__text">
        {{ props.prompt.text }}
      </p>
    </header>

    <div class="three-option-image-template__image-frame">
      <img
        :src="props.prompt.imageUrl"
        :alt="props.prompt.imageAlt"
        class="three-option-image-template__image"
      >
    </div>

    <div class="three-option-image-template__options">
      <button
        v-for="option in props.options"
        :key="option.key"
        class="three-option-image-template__option"
        :class="{
          'three-option-image-template__option--selected': props.selectedChoice === option.key,
          'three-option-image-template__option--locked': props.disabled && props.selectedChoice !== option.key,
        }"
        type="button"
        :disabled="props.disabled"
        @click="choose(option.key)"
      >
        <span class="three-option-image-template__option-badge">{{ option.key.toUpperCase() }}</span>
        <span class="three-option-image-template__option-copy">
          <span class="three-option-image-template__option-title">{{ option.title }}</span>
        </span>
      </button>
    </div>
  </section>
</template>

<style scoped>
.three-option-image-template {
  display: grid;
  gap: clamp(1rem, 2vw, 1.5rem);
  color: #1f2937;
}

.three-option-image-template__header {
  display: grid;
  gap: 0.45rem;
}

.three-option-image-template__title {
  font-family: Georgia, "Times New Roman", serif;
  font-size: clamp(1.5rem, 3vw, 2.4rem);
  line-height: 1;
  color: #201716;
}

.three-option-image-template__text {
  max-width: 58rem;
  font-size: 1rem;
  line-height: 1.5;
  color: #4b5563;
}

.three-option-image-template__image-frame {
  padding: 0.85rem;
  border-radius: 1.6rem;
  background: #fffdfa;
  border: 2px solid #d6d3ce;
  box-shadow: 0 14px 26px rgba(47, 39, 28, 0.08);
}

.three-option-image-template__image {
  width: 100%;
  max-height: min(48dvh, 34rem);
  object-fit: cover;
  display: block;
  border-radius: 1rem;
}

.three-option-image-template__options {
  display: grid;
  gap: 0.9rem;
}

.three-option-image-template__option {
  display: grid;
  grid-template-columns: auto minmax(0, 1fr);
  gap: 1rem;
  align-items: center;
  width: 100%;
  padding: 1rem 1.15rem;
  border: 2px solid #d8dde5;
  border-radius: 1.2rem;
  background: #fbfcff;
  color: #1f2937;
  text-align: left;
  box-shadow: 0 10px 18px rgba(53, 75, 108, 0.07);
  transition: transform 0.2s ease, border-color 0.2s ease, box-shadow 0.2s ease, background-color 0.2s ease;
}

.three-option-image-template__option:hover:not(:disabled),
.three-option-image-template__option:focus-visible {
  transform: translateY(-2px);
  border-color: #9db7d6;
  box-shadow: 0 14px 24px rgba(53, 75, 108, 0.12);
  outline: none;
}

.three-option-image-template__option-badge {
  display: grid;
  place-items: center;
  width: 2.25rem;
  height: 2.25rem;
  border-radius: 999px;
  background: #e6edf7;
  color: #27456f;
  font-weight: 800;
  font-size: 0.95rem;
}

.three-option-image-template__option-copy {
  display: grid;
  align-self: center;
}

.three-option-image-template__option-title {
  font-size: 1.05rem;
  font-weight: 700;
  line-height: 1.35;
}

.three-option-image-template__option--selected {
  border-color: #4c7bb0;
  background: #eef5ff;
  box-shadow: 0 16px 28px rgba(65, 104, 150, 0.16);
}

.three-option-image-template__option--selected .three-option-image-template__option-badge {
  background: #4c7bb0;
  color: #f8fbff;
}

.three-option-image-template__option--locked {
  opacity: 0.82;
}

.three-option-image-template__option:disabled {
  cursor: default;
}

@media (max-width: 760px) {
  .three-option-image-template__option {
    padding: 0.95rem 1rem;
  }
}
</style>
