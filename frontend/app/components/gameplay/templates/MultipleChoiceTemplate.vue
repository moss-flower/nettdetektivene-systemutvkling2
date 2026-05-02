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
  hideImages?: boolean
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
  <section class="multipleChoiceTemplate">
    <header v-if="props.prompt.title || props.prompt.text" class="choiceTemplateHeader">
      <h2 v-if="props.prompt.title" class="choiceTemplateTitle">
        {{ props.prompt.title }}
      </h2>
      <p v-if="props.prompt.text" class="choiceTemplateText">
        {{ props.prompt.text }}
      </p>
    </header>
    <div v-if="!props.hideImages" class="templateImageFrame">
      <img
        :src="props.prompt?.imageUrl ?? ''"
        :alt="props.prompt?.imageAlt ?? ''"
        class="choiceTemplateImage"
      >
    </div>

    <div class="choiceTemplateOptions">
      <button
        v-for="option in props.options"
        :key="option.key"
        class="choiceTemplateOption"
        :class="{
          'templateOptionSelected': props.selectedChoice === option.key,
          'templateOptionLocked': props.disabled && props.selectedChoice !== option.key,
        }"
        type="button"
        :disabled="props.disabled"
        @click="choose(option.key)"
      >
        <span class="templateOptionBadge">{{ option.key.toUpperCase() }}</span>
        <span class="templateOptionCopy">
          <span class="templateOptionTitle">{{ option.title }}</span>
        </span>
      </button>
    </div>
  </section>
</template>

<style scoped>
.multipleChoiceTemplate {
  display: grid;
  gap: clamp(var(--space-4), 2vw, var(--space-6));
  color: var(--palette-gray-900);
}

.choiceTemplateHeader {
  display: grid;
  gap: 0.45rem;
}

.choiceTemplateTitle {
  font-family: Georgia, "Times New Roman", serif;
  font-size: clamp(1.5rem, 3vw, 2.4rem);
  line-height: 1;
  color: var(--palette-cork-900);
}

.choiceTemplateText {
  max-width: 58rem;
  font-size: var(--font-size-16);
  line-height: 1.5;
  color: var(--color-text-secondary);
}

.templateImageFrame {
  padding: 0.85rem;
  border-radius: 1.6rem;
  background: var(--color-bg-surface);
  border: var(--border-width-thin) solid var(--color-border-default);
  box-shadow: 0 14px 26px rgba(47, 39, 28, 0.08);
}

.choiceTemplateImage {
  width: 100%;
  max-height: min(48dvh, 34rem);
  object-fit: contain;
  background: #fff;
  display: block;
  border-radius: var(--radius-2);
}

.choiceTemplateOptions {
  display: grid;
  gap: 0.9rem;
}

.choiceTemplateOption {
  display: grid;
  grid-template-columns: auto minmax(0, 1fr);
  gap: var(--space-4);
  align-items: center;
  width: 100%;
  padding: var(--space-4) 1.15rem;
  border: var(--border-width-thin) solid var(--color-border-default);
  border-radius: 1.2rem;
  background: var(--color-bg-surface);
  color: var(--palette-gray-900);
  text-align: left;
  box-shadow: 0 10px 18px rgba(53, 75, 108, 0.07);
  transition: transform var(--transition-base) ease, border-color var(--transition-base) ease, box-shadow var(--transition-base) ease, background-color var(--transition-base) ease;
}

.choiceTemplateOption:hover:not(:disabled),
.choiceTemplateOption:focus-visible {
  transform: translateY(-2px);
  border-color: #9db7d6;
  box-shadow: 0 14px 24px rgba(53, 75, 108, 0.12);
  outline: none;
}

.templateOptionBadge {
  display: grid;
  place-items: center;
  width: 2.25rem;
  height: 2.25rem;
  border-radius: var(--radius-pill);
  background: var(--color-semantic-info);
  color: var(--palette-navy-700);
  font-weight: 800;
  font-size: 0.95rem;
}

.templateOptionCopy {
  display: grid;
  align-self: center;
}

.templateOptionTitle {
  font-size: 1.05rem;
  font-weight: 700;
  line-height: 1.35;
}

.templateOptionSelected {
  border-color: #4c7bb0;
  background: var(--color-semantic-info);
  box-shadow: 0 16px 28px rgba(65, 104, 150, 0.16);
}

.templateOptionSelected .templateOptionBadge {
  background: #4c7bb0;
  color: var(--palette-white);
}

.templateOptionLocked {
  opacity: 0.82;
}

.choiceTemplateOption:disabled {
  cursor: default;
}

</style>
