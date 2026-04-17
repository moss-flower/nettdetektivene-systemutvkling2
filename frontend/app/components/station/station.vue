<script setup lang="ts">
import { NuxtLink } from "#components";
import starEmpty from "~/assets/images/star/spStarEmpty.png";
import starFilled from "~/assets/images/star/spStarFilled.png";
import { routePaths } from "~/composables/routePaths";
import type { StationBase } from "~/composables/types/StationType";

const props = defineProps<{
  station: StationBase;
}>();

const stationPath = computed(() => ({
  path: routePaths.studentTaskDifficultySelect,
  query: { id: props.station.id },
}));

const stars = computed(() =>
  Array.from({ length: 3 }, (_, index) => {
    const isCompleted = props.station.completedTasks[index] ?? false;

    return {
      id: `${props.station.id}-star-${index}`,
      src: isCompleted ? starFilled : starEmpty,
      alt: isCompleted ? `Completed star ${index + 1}` : `Empty star ${index + 1}`,
    };
  }),
);
</script>

<template>
  <component
    :is="station.isUnlocked ? NuxtLink : 'div'"
    class="levelCard"
    :class="{ lockedCard: !station.isUnlocked }"
    :to="station.isUnlocked ? stationPath : undefined"
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
    <p v-if="!station.isUnlocked" class="lock">🔒</p>
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