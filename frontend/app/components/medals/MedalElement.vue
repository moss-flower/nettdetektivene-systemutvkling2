<script setup lang="ts">
import type { Medal } from "~/composables/types/MedalTypes";
import filledStar from "~/assets/images/star/spStarFilled.png";
import emptyStar from "~/assets/images/star/spStarEmpty.png";

const paths = ["~/assets/images/star/spStarFilled.png", "~/assets/images/star/spStarEmpty.png"]

const props = defineProps<{
  medal: Medal;
}>();

const categoryLabel = computed(() => {
  if (props.medal.category === "stop") return "Stoppested";
  if (props.medal.category === "mystery") return "Ukens mysterium";
  return "Spesial";
});

const medalImage = (): string => {
  return props.medal.unlocked ? filledStar : emptyStar;
};

</script>

<template>
  <div :class="['medalElement', { locked: !props.medal.unlocked }]">
    <img class="medalImage" :src="medalImage()" :alt="props.medal.title" />
    <span v-if="props.medal.unlocked" class="title">
      {{ props.medal.title }}
    </span>
  </div>
</template>

<style scoped>
.medalElement {
  --medal-chip-bg: var(--palette-navy-900);
  --medal-chip-text: var(--palette-cream-100);
  --medal-chip-lift: var(--palette-overlay-dark-strong);

  position: relative;
  width: 100%;
  aspect-ratio: 1 / 1;

  display: flex;
  align-items: center;
  justify-content: center;
}

.medalImage {
  width: 100%;
  height: 100%;
  object-fit: contain;
  display: block;
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