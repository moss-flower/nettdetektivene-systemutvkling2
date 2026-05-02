<script setup lang="ts">
import type { AchievementResponse } from "~/composables/types/AchievementTypes";
import filledMedal from "~/assets/images/star/Medal.png";
import emptyMedal from "~/assets/images/star/emptymedal.png";
import specialMedal from "~/assets/images/star/special medal.png";

const props = defineProps<{
  medal: AchievementResponse;
}>();

const emit = defineEmits<{
  (e: "select", medal: AchievementResponse): void;
}>();

const medalImage = (): string => {
  if (props.medal.category === "special") {
    return props.medal.unlocked ? specialMedal : emptyMedal;
  }

  return props.medal.unlocked ? filledMedal : emptyMedal;
};

</script>

<template>
  <button
    type="button"
    :class="['medalElement', { locked: !props.medal.unlocked }]"
    :aria-label="`${props.medal.title} (${categoryLabel})`"
    @click="emit('select', props.medal)"
  >
    <img class="medalImage" :src="medalImage()" :alt="props.medal.title" />
    <span class="title">
      {{ props.medal.title }}
    </span>
  </button>
</template>

<style scoped>
.medalElement {
  --medal-chip-bg: var(--palette-navy-900);
  --medal-chip-text: var(--palette-cream-100);
  --medal-chip-lift: var(--palette-overlay-dark-strong);

  position: relative;
  width: 100%;
  height: 100%;

  display: flex;
  align-items: center;
  justify-content: center;
  padding: var(--space-3);
  border: 0;
  background: transparent;
  cursor: pointer;
}

.medalElement.special {
  --medal-chip-bg: var(--palette-blue-800);
  --medal-chip-lift: color-mix(in srgb, var(--palette-blue-900) 75%, transparent);
}

.medalImage {
  width: 82%;
  height: 82%;
  object-fit: contain;
  display: block;
}

.medalElement.special .medalImage {
  filter: hue-rotate(185deg) saturate(1.35) brightness(1.02);
}

.title {
  position: absolute;
  left: 50%;
  bottom: 0;
  transform: translateX(-50%);
  max-width: 110%;

  padding: var(--space-1) var(--space-3);
  background-color: var(--medal-chip-bg);
  color: var(--medal-chip-text);
  font-family: var(--font-family-body), sans-serif;
  font-size: var(--font-size-22);
  line-height: var(--font-leading-22);
  font-weight: var(--font-weight-semibold);
  text-align: center;
  border-radius: var(--radius-pill);
  box-shadow: 0 var(--space-1) var(--space-2) var(--medal-chip-lift);
}

.locked {
  opacity: 0.5;
}
</style>
