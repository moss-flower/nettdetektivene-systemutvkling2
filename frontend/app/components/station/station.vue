<script setup lang="ts">
import { NuxtLink } from "#components";
import starEmpty from "~/assets/images/star/spStarEmpty.png";
import starFilled from "~/assets/images/star/spStarFilled.png";
import { routePaths } from "~/composables/routePaths";
import type { Station } from "~/composables/types/StationType";

const props = defineProps<{
  station: Station;
}>();

const stationStatus = computed(() => props.station.getMockStatus());
const stationIsUnlocked = computed(() => props.station.isUnlocked());

const stationPath = computed(() => ({
  path: routePaths.studentExerciseDifficultySelect,
  query: { uid: props.station.uid },
}));

const totalExercises = computed(() => {
  if (props.station.exerciseUids.length > 0) {
    return props.station.exerciseUids.length;
  }

  return stationStatus.value.completedExercises.length;
});

const stars = computed(() =>
  Array.from({ length: totalExercises.value }, (_, index) => {
    const isCompleted = stationStatus.value.completedExercises[index] ?? false;

    return {
      id: `${props.station.uid}-star-${index}`,
      src: isCompleted ? starFilled : starEmpty,
      alt: isCompleted ? `Completed star ${index + 1}` : `Empty star ${index + 1}`,
    };
  }),
);
</script>

<template>
    <component
  :is="stationIsUnlocked ? NuxtLink : 'div'"
    class="levelCard"
  :class="{ lockedCard: !stationIsUnlocked }"
  :to="stationIsUnlocked ? stationPath : undefined"
  >
    <img :src="station.image" :alt="station.name" class="levelImage">

    <h4 class="levelName">{{ station.name }}</h4>
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
  gap: 0.75rem;
  width: 100%;
  max-width: 220px;
  padding: 1rem;
  border: 1px solid #d6d6d6;
  border-radius: 12px;
  background: #ffffff;
  color: #1f2937;
  text-decoration: none;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.lockedCard {
  opacity: 0.5;
}

.levelImage {
  width: 100%;
  max-width: 140px;
  height: auto;
  border-radius: 8px;
  object-fit: cover;
}

.levelName {
  margin: 0;
  text-align: center;
  font-size: 1rem;
}

.starRow {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.25rem;
}

.starImage {
  width: 24px;
  height: 24px;
}

.lock {
  margin: 0;
  font-size: 1.25rem;
}

</style>
