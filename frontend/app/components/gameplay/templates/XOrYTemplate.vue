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
  hideImages?: boolean
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
  <div class="orYTemplate">
    <header
      v-if="props.prompt?.title || props.prompt?.text"
      class="yTemplateHeader"
    >
      <h2 v-if="props.prompt?.title" class="templatePromptTitle">
        {{ props.prompt.title }}
      </h2>
      <p v-if="props.prompt?.text" class="templatePromptText">
        {{ props.prompt.text }}
      </p>
    </header>

    <article
      class="yTemplateOption"
      :class="{
        'templateOptionSelected': props.selectedChoice === 'x',
        'templateOptionLocked': props.disabled && props.selectedChoice !== 'x',
      }"
    >
      <button
        class="yTemplateSurface"
        type="button"
        :disabled="props.disabled"
        @click="choose('x')"
      >
        <div class="yTemplateCard">
          <img
            v-if="!props.hideImages"
            :src="props.options.x.imageUrl"
            :alt="props.options.x.imageAlt"
            class="yTemplateImage"
          >
          <div class="yTemplateContent">
            <p v-if="props.options.x.eyebrow" class="yTemplateEyebrow">
              {{ props.options.x.eyebrow }}
            </p>
            <h2 class="yTemplateTitle">{{ props.options.x.title }}</h2>
            <p class="yTemplateText">{{ props.options.x.text }}</p>
          </div>
        </div>
      </button>
    </article>

    <article
      class="yTemplateOption"
      :class="{
        'templateOptionSelected': props.selectedChoice === 'y',
        'templateOptionLocked': props.disabled && props.selectedChoice !== 'y',
      }"
    >
      <button
        class="yTemplateSurface"
        type="button"
        :disabled="props.disabled"
        @click="choose('y')"
      >
        <div class="yTemplateCard">
          <img
            v-if="!props.hideImages"
            :src="props.options.y.imageUrl"
            :alt="props.options.y.imageAlt"
            class="yTemplateImage"
          >
          <div class="yTemplateContent">
            <p v-if="props.options.y.eyebrow" class="yTemplateEyebrow">
              {{ props.options.y.eyebrow }}
            </p>
            <h2 class="yTemplateTitle">{{ props.options.y.title }}</h2>
            <p class="yTemplateText">{{ props.options.y.text }}</p>
          </div>
        </div>
      </button>
    </article>
  </div>
</template>

<style scoped>
.orYTemplate {
  /* header */
  --xy-title: var(--palette-cork-900);
  --xy-muted: var(--palette-cork-580);

  /* card surface */
  --xy-card-bg: var(--palette-cream-100);
  --xy-card-border: var(--palette-overlay-cork-medium);
  --xy-card-shadow: var(--palette-overlay-dark-soft);
  --xy-card-shadow-hover: var(--palette-overlay-dark-medium);

  /* card text */
  --xy-eyebrow: var(--palette-cork-580);
  --xy-card-title: var(--palette-cork-900);
  --xy-card-text: var(--palette-cork-700);

  /* selected state */
  --xy-card-border-active: var(--palette-gold-500);
  --xy-card-shadow-active: var(--palette-overlay-dark-medium);

  /* locked state */
  --xy-locked-opacity: 0.6;

  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: var(--space-5);
  align-items: start;
}

.yTemplateHeader {
  grid-column: 1 / -1;
  display: grid;
  gap: var(--space-1);
}

.templatePromptTitle {
  /* fluid title scale, deliberate exception */
  font-size: clamp(1.15rem, 1.8vw, 1.45rem);
  line-height: 1.15;
  color: var(--xy-title);
}

.templatePromptText {
  max-width: 48ch;
  font-size: var(--font-size-14);
  line-height: var(--font-leading-14);
  color: var(--xy-muted);
}

.yTemplateOption {
  display: grid;
}

.yTemplateSurface {
  width: 100%;
  padding: 0;
  border: 0;
  background: transparent;
  text-align: left;
}

.yTemplateSurface:focus-visible {
  outline: none;
}

.yTemplateCard {
  min-height: 100%;
  border-radius: var(--radius-3);
  padding: var(--space-2);
  background: var(--xy-card-bg);
  border: var(--border-width-thin) solid var(--xy-card-border);
  box-shadow: 0 var(--space-3) var(--space-5) var(--xy-card-shadow);
  transition: transform var(--transition-base), box-shadow var(--transition-base), border-color var(--transition-base);
}

.yTemplateSurface:hover:not(:disabled) .yTemplateCard,
.yTemplateSurface:focus-visible .yTemplateCard {
  transform: translateY(calc(var(--border-width-thick) * -1));
  border-color: var(--palette-navy-200);
  box-shadow: 0 var(--space-4) var(--space-6) var(--xy-card-shadow-hover);
}

.yTemplateImage {
  width: 100%;
  aspect-ratio: 1.2 / 0.68;
  max-height: 25vh;
  object-fit: cover;
  border-radius: var(--radius-2) var(--radius-2) 0 0;
  display: block;
}

.yTemplateContent {
  display: grid;
  gap: var(--space-2);
  padding: var(--space-2) var(--space-3) var(--space-3);
}

.yTemplateEyebrow {
  font-size: var(--font-size-12);
  font-weight: var(--font-weight-bold);
  letter-spacing: var(--font-tracking-wider);
  text-transform: uppercase;
  color: var(--xy-eyebrow);
}

.yTemplateTitle {
  font-family: var(--font-family-serif), sans-serif;
  /* fluid hero scale, deliberate exception */
  font-size: clamp(1.35rem, 2.2vw, 2.2rem);
  line-height: 1;
  color: var(--xy-card-title);
  text-wrap: balance;
}

.yTemplateText {
  font-size: var(--font-size-14);
  line-height: var(--font-leading-14);
  color: var(--xy-card-text);
}

.templateOptionSelected .yTemplateCard {
  border-color: var(--xy-card-border-active);
  box-shadow: 0 var(--space-4) var(--space-6) var(--xy-card-shadow-active);
}

.templateOptionLocked {
  opacity: var(--xy-locked-opacity);
}

.yTemplateSurface:disabled {
  cursor: default;
}

@media (max-width: 900px) {
  .yTemplateTitle {
    /* fluid mobile scale, deliberate exception */
    font-size: clamp(1.2rem, 4vw, 1.9rem);
  }
}

</style>
