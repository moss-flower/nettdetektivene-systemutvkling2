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
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: clamp(0.9rem, 2vw, var(--space-6));
  align-items: start;
}

.yTemplateHeader {
  grid-column: 1 / -1;
  display: grid;
  gap: 0.35rem;
}

.templatePromptTitle {
  font-size: clamp(1.15rem, 1.8vw, 1.45rem);
  line-height: 1.15;
  color: var(--color-text-primary);
}

.templatePromptText {
  max-width: 48rem;
  font-size: 0.95rem;
  line-height: 1.45;
  color: var(--color-text-secondary);
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
  border-radius: 1.6rem;
  padding: 0.7rem;
  background: var(--color-bg-surface);
  border: var(--border-width-thin) solid var(--color-border-default);
  box-shadow: 0 14px 26px rgba(47, 39, 28, 0.08);
  transition: transform var(--transition-base) ease, box-shadow var(--transition-base) ease, border-color var(--transition-base) ease;
}

.yTemplateSurface:hover:not(:disabled) .yTemplateCard,
.yTemplateSurface:focus-visible .yTemplateCard {
  transform: translateY(-3px);
  border-color: #8ea7c4;
  box-shadow: 0 18px 32px rgba(47, 39, 28, 0.12);
}

.yTemplateImage {
  width: 100%;
  aspect-ratio: 1.2 / 0.68;
  max-height: min(25vh, 13rem);
  object-fit: contain;
  background: #fff;
  border-radius: var(--radius-2) var(--radius-2) 0 0;
  display: block;
}

.yTemplateContent {
  display: grid;
  gap: 0.55rem;
  padding: 0.8rem 0.85rem 0.95rem;
}

.yTemplateEyebrow {
  font-size: 0.8rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: var(--color-text-secondary);
}

.yTemplateTitle {
  font-family: Georgia, "Times New Roman", serif;
  font-size: clamp(1.35rem, 2.2vw, 2.2rem);
  line-height: 1;
  color: var(--palette-cork-900);
  text-wrap: balance;
}

.yTemplateText {
  font-size: 0.94rem;
  line-height: 1.4;
  color: #47372e;
}

.templateOptionSelected .yTemplateCard {
  border-color: #4c7bb0;
  box-shadow: 0 18px 34px rgba(65, 104, 150, 0.18);
}

.templateOptionLocked {
  opacity: 0.82;
}

.yTemplateSurface:disabled {
  cursor: default;
}

@media (max-width: 900px) {
  .yTemplateTitle {
    font-size: clamp(1.2rem, 4vw, 1.9rem);
  }
}

</style>
