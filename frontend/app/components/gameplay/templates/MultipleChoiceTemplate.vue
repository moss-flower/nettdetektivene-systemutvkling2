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
  <section class="multiple-choice-template">
    <header v-if="props.prompt.title || props.prompt.text" class="multiple-choice-template__header">
      <h2 v-if="props.prompt.title" class="multiple-choice-template__title">
        {{ props.prompt.title }}
      </h2>
      <p v-if="props.prompt.text" class="multiple-choice-template__text">
        {{ props.prompt.text }}
      </p>
    </header>

    <div class="multiple-choice-template__image-frame">
      <img
        :src="props.prompt.imageUrl"
        :alt="props.prompt.imageAlt"
        class="multiple-choice-template__image"
      >
    </div>

    <div class="multiple-choice-template__options">
      <button
        v-for="option in props.options"
        :key="option.key"
        class="multiple-choice-template__option"
        :class="{
          'multiple-choice-template__option--selected': props.selectedChoice === option.key,
          'multiple-choice-template__option--locked': props.disabled && props.selectedChoice !== option.key,
        }"
        type="button"
        :disabled="props.disabled"
        @click="choose(option.key)"
      >
        <span class="multiple-choice-template__option-badge">{{ option.key.toUpperCase() }}</span>
        <span class="multiple-choice-template__option-copy">
          <span class="multiple-choice-template__option-title">{{ option.title }}</span>
        </span>
      </button>
    </div>
  </section>
</template>

<style scoped>
.multiple-choice-template {
  display: grid;
  gap: clamp(1rem, 2vw, 1.5rem);
  color: #1f2937;
}

.multiple-choice-template__header {
  display: grid;
  gap: 0.45rem;
}

.multiple-choice-template__title {
  font-family: Georgia, "Times New Roman", serif;
  font-size: clamp(1.5rem, 3vw, 2.4rem);
  line-height: 1;
  color: #201716;
}

.multiple-choice-template__text {
  max-width: 58rem;
  font-size: 1rem;
  line-height: 1.5;
  color: #4b5563;
}

.multiple-choice-template__image-frame {
  padding: 0.85rem;
  border-radius: 1.6rem;
  background: #fffdfa;
  border: 2px solid #d6d3ce;
  box-shadow: 0 14px 26px rgba(47, 39, 28, 0.08);
}

.multiple-choice-template__image {
  width: 100%;
  max-height: min(48dvh, 34rem);
  object-fit: cover;
  display: block;
  border-radius: 1rem;
}

.multiple-choice-template__options {
  display: grid;
  gap: 0.9rem;
}

.multiple-choice-template__option {
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

.multiple-choice-template__option:hover:not(:disabled),
.multiple-choice-template__option:focus-visible {
  transform: translateY(-2px);
  border-color: #9db7d6;
  box-shadow: 0 14px 24px rgba(53, 75, 108, 0.12);
  outline: none;
}

.multiple-choice-template__option-badge {
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

.multiple-choice-template__option-copy {
  display: grid;
  align-self: center;
}

.multiple-choice-template__option-title {
  font-size: 1.05rem;
  font-weight: 700;
  line-height: 1.35;
}

.multiple-choice-template__option--selected {
  border-color: #4c7bb0;
  background: #eef5ff;
  box-shadow: 0 16px 28px rgba(65, 104, 150, 0.16);
}

.multiple-choice-template__option--selected .multiple-choice-template__option-badge {
  background: #4c7bb0;
  color: #f8fbff;
}

.multiple-choice-template__option--locked {
  opacity: 0.82;
}

.multiple-choice-template__option:disabled {
  cursor: default;
}

@media (max-width: 760px) {
  .multiple-choice-template__option {
    padding: 0.95rem 1rem;
  }
}
</style>
