<script setup lang="ts">
import type { AchievementResponse } from "~/composables/types/AchievementTypes";
import filledStar from "~/assets/images/star/spStarFilled.png";

const props = defineProps<{
  achievement: AchievementResponse;
}>();

const emit = defineEmits<{
  (e: "close"): void;
}>();
</script>

<template>
  <div class="popupOverlay" @click.self="emit('close')">
    <article class="popupCard" role="dialog" aria-modal="true" :aria-label="props.achievement.title">
      <div class="popupImageWrap">
        <img
          class="popupImage"
          :src="filledStar"
          :alt="props.achievement.title"
        >
      </div>

      <div class="popupBody">
        <p class="popupEyebrow">Medalje</p>
        <h2 class="popupTitle">{{ props.achievement.title }}</h2>
        <p class="popupDescription">{{ props.achievement.description }}</p>

        <button class="closeButton" type="button" @click="emit('close')">
          Close
        </button>
      </div>
    </article>
  </div>
</template>

<style scoped>
.popupOverlay {
  position: fixed;
  inset: 0;
  z-index: 200;
  display: grid;
  place-items: center;
  padding: var(--space-5);
  background: rgba(15, 23, 42, 0.62);
  backdrop-filter: blur(4px);
}

.popupCard {
  width: min(100%, 34rem);
  display: grid;
  gap: var(--space-4);
  padding: var(--space-5);
  border-radius: var(--radius-3);
  background: linear-gradient(180deg, var(--palette-cream-100) 0%, var(--palette-cream-200) 100%);
  box-shadow: 0 var(--space-4) var(--space-6) var(--palette-overlay-dark-deep);
}

.popupImageWrap {
  display: flex;
  justify-content: center;
}

.popupImage {
  width: min(12rem, 100%);
  height: auto;
  object-fit: contain;
}

.popupBody {
  display: grid;
  gap: var(--space-3);
}

.popupEyebrow,
.popupTitle,
.popupDescription {
  margin: 0;
}

.popupEyebrow {
  color: var(--palette-cork-580);
  font-size: var(--font-size-14);
  font-weight: var(--font-weight-bold);
  letter-spacing: var(--font-tracking-wider);
  text-transform: uppercase;
}

.popupTitle {
  color: var(--palette-cork-900);
}

.popupDescription {
  color: var(--palette-cork-700);
  line-height: var(--font-leading-base);
}

.closeButton {
  justify-self: end;
  padding: 0.8rem 1.1rem;
  border: 0;
  border-radius: var(--radius-pill);
  background: var(--palette-navy-900);
  color: var(--palette-cream-100);
  font: inherit;
  font-weight: var(--font-weight-bold);
  cursor: pointer;
}
</style>
