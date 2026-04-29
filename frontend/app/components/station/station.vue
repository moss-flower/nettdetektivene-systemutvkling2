<script setup lang="ts">
import { NuxtLink } from "#components";
import starEmpty from "~/assets/images/star/spStarEmpty.png";
import starFilled from "~/assets/images/star/spStarFilled.png";
import { routePaths } from "~/composables/utils/routePaths";
import type { Station } from "~/composables/types/StationTypes";

const emit = defineEmits<{
  (event: "locked-click"): void;
}>();

const props = defineProps<{
  station: Station;
  isUnlocked?: boolean;
  completedExercises?: boolean[];
}>();

const stationIsUnlocked = computed(() => props.isUnlocked ?? true);
const completedExercises = computed(() => props.completedExercises ?? []);

const stationPath = computed(() => ({
  path: routePaths.studentExerciseDifficultySelect,
  query: { uid: props.station.uid },
}));

const totalExercises = computed(() => {
  if (props.station.exerciseUids.length > 0) {
    return props.station.exerciseUids.length;
  }

  return completedExercises.value.length;
});

const stars = computed(() =>
  Array.from({ length: totalExercises.value }, (_, index) => {
    const isCompleted = completedExercises.value[index] ?? false;

    return {
      id: `${props.station.uid}-star-${index}`,
      src: isCompleted ? starFilled : starEmpty,
      alt: isCompleted ? `Completed star ${index + 1}` : `Empty star ${index + 1}`,
    };
  }),
);

const cardTag = computed(() => (stationIsUnlocked.value ? NuxtLink : "button"));

const handleClick = () => {
  if (!stationIsUnlocked.value) {
    emit("locked-click");
  }
};
</script>

<template>
  <component
    :is="cardTag"
    class="levelCard"
    :class="{ lockedCard: !stationIsUnlocked }"
    :to="stationIsUnlocked ? stationPath : undefined"
    :type="stationIsUnlocked ? undefined : 'button'"
    @click="handleClick"
  >
    <img :src="station.image" :alt="station.title" class="levelImage">

    <h4 class="levelName">{{ station.title }}</h4>
    <div class="starRow">
      <img
        v-for="star in stars"
        :key="star.id"
        :src="star.src"
        :alt="star.alt"
        class="starImage"
      >
    </div>
    <p v-if="!stationIsUnlocked" class="lock">🔒</p>
  </component>
</template>

<style scoped>
.levelCard {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-3);
  width: 100%;
  /* 220px matches minmax(220px, 1fr) minimum on .stationGrid */
  max-width: 220px;
  padding: var(--space-4);
  border: var(--border-width-hairline) solid var(--palette-gray-200);
  border-radius: var(--radius-2);
  background: var(--palette-white);
  color: var(--palette-navy-900);
  font: inherit;
  text-decoration: none;
  box-shadow: var(--shadow-card);
  cursor: pointer;
}

.lockedCard {
  opacity: 0.5;
}

.levelCard:focus-visible {
  outline: var(--border-width-thick) solid color-mix(in srgb, var(--color-brand-primary) 65%, white);
  outline-offset: 0.2rem;
}

.levelImage {
  width: 100%;
  /* thumbnail ceiling inside the 220px card */
  max-width: 140px;
  height: auto;
  border-radius: var(--radius-1);
  object-fit: cover;
}

.levelName {
  margin: 0;
  text-align: center;
  font-size: var(--font-size-16);
  line-height: var(--font-leading-16);
}

.starRow {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-1);
}

.starImage {
  width: var(--size-icon-md);
  height: var(--size-icon-md);
}

.lock {
  margin: 0;
  font-size: 1.25rem;
}

</style>
