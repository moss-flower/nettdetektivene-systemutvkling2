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
  /* card surface */
  --mc-card-bg-top: var(--palette-cream-100);
  --mc-card-border: var(--palette-overlay-cork-medium);
  --mc-card-shadow: var(--palette-overlay-dark-soft);
  --mc-card-text: var(--palette-cork-900);
  --mc-card-muted: var(--palette-cork-580);

  /* image frame */
  --mc-frame-bg: var(--palette-cream-100);
  --mc-frame-border: var(--palette-overlay-cork-medium);
  --mc-frame-shadow: var(--palette-overlay-dark-soft);

  /* option pill, idle */
  --mc-option-bg: var(--palette-white);
  --mc-option-bg-hover: var(--palette-cream-100);
  --mc-option-border: var(--palette-cork-700);
  --mc-option-text: var(--palette-cork-900);
  --mc-option-shadow: var(--palette-overlay-dark-soft);

  /* option pill, selected */
  --mc-option-bg-active: var(--palette-navy-900);
  --mc-option-border-active: var(--palette-gold-500);
  --mc-option-text-active: var(--palette-cream-100);

  /* badge (A/B/C/D) */
  --mc-badge-bg: var(--palette-navy-900);
  --mc-badge-text: var(--palette-cream-100);
  --mc-badge-bg-active: var(--palette-gold-500);
  --mc-badge-text-active: var(--palette-navy-900);

  /* locked state */
  --mc-option-locked-opacity: 0.6;

  display: grid;
  gap: var(--space-5);
  padding: var(--space-5);
  border: var(--border-width-thin) solid var(--mc-card-border);
  border-radius: var(--radius-2);
  background: var(--mc-card-bg-top);
  box-shadow: var(--space-2) var(--space-4) var(--space-6) var(--mc-card-shadow);
  color: var(--mc-card-text);
}

.choiceTemplateHeader {
  display: grid;
  gap: var(--space-1);
}

.choiceTemplateTitle {
  font-family: var(--font-family-serif), sans-serif;
  /* fluid hero scale, deliberate exception */
  font-size: clamp(1.5rem, 3vw, 2.4rem);
  line-height: 1;
  color: var(--mc-card-text);
}

.choiceTemplateText {
  max-width: 58ch;
  font-size: var(--font-size-16);
  line-height: var(--font-leading-16);
  color: var(--mc-card-muted);
}

.templateImageFrame {
  padding: var(--space-3);
  border-radius: var(--radius-3);
  background: var(--mc-frame-bg);
  border: var(--border-width-thin) solid var(--mc-frame-border);
  box-shadow: 0 var(--space-4) var(--space-5) var(--mc-frame-shadow);
}

.choiceTemplateImage {
  width: 100%;
  max-height: 48dvh;
  object-fit: cover;
  display: block;
  border-radius: var(--radius-2);
}

.choiceTemplateOptions {
  display: grid;
  gap: var(--space-2);
}

.choiceTemplateOption {
  display: grid;
  grid-template-columns: auto minmax(0, 1fr);
  gap: var(--space-4);
  align-items: center;
  width: 100%;
  padding: var(--space-4) var(--space-3);
  border: var(--border-width-thin) solid var(--mc-option-border);
  border-radius: var(--radius-2);
  background: var(--mc-option-bg);
  color: var(--mc-option-text);
  text-align: left;
  box-shadow: 0 var(--space-2) var(--space-4) var(--mc-option-shadow);
  transition: transform var(--transition-base), border-color var(--transition-base), box-shadow var(--transition-base), background-color var(--transition-base);
}

.choiceTemplateOption:hover:not(:disabled),
.choiceTemplateOption:focus-visible {
  transform: translateY(calc(var(--border-width-thin) * -1));
  border-color: var(--palette-navy-200);
  background: var(--mc-option-bg-hover);
  box-shadow: 0 var(--space-3) var(--space-5) var(--palette-overlay-dark-soft);
  outline: none;
}

.templateOptionBadge {
  display: grid;
  place-items: center;
  width: var(--size-control-sm);
  height: var(--size-control-sm);
  border-radius: var(--radius-pill);
  background: var(--mc-badge-bg);
  color: var(--mc-badge-text);
  font-weight: var(--font-weight-bold);
  font-size: var(--font-size-14);
}

.templateOptionCopy {
  display: grid;
  align-self: center;
}

.templateOptionTitle {
  font-size: var(--font-size-16);
  font-weight: var(--font-weight-bold);
  line-height: var(--font-leading-16);
}

.templateOptionSelected {
  border-color: var(--mc-option-border-active);
  background: var(--mc-option-bg-active);
  color: var(--mc-option-text-active);
  box-shadow: 0 var(--space-4) var(--space-6) var(--palette-overlay-dark-medium);
}

.templateOptionSelected .templateOptionBadge {
  background: var(--mc-badge-bg-active);
  color: var(--mc-badge-text-active);
}

.templateOptionLocked {
  opacity: var(--mc-option-locked-opacity);
}

.choiceTemplateOption:disabled {
  cursor: default;
}
</style>
