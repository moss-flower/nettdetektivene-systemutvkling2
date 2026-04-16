<script setup lang="ts">
import { NuxtLink } from "#components";
import { routePaths } from "~/composables/routePaths";
import type { StationBase } from "~/composables/types/StationType";

const props = defineProps<{
  station: StationBase;
}>();

const stationPath = computed(() => ({
  path: routePaths.studentTaskDifficultySelect,
  query: { id: props.station.id },
}));
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
    <p v-if="!station.isUnlocked" class="lock">🔒</p>
  </component>
</template>

<style scoped>
.levelCard {
  position: relative;
  min-height: 220px;
  overflow: hidden;
  border-radius: 1rem;
  padding: 1rem;
  text-decoration: none;
  display: flex;
  flex-direction: column;
  justify-content: end;
  border: 1px solid var(--border);
  box-shadow: var(--shadow);
  transition:
    transform 160ms ease,
    box-shadow 160ms ease;
}

a.levelCard {
  cursor: pointer;
}

a.levelCard:hover {
  transform: translateY(-4px);
  box-shadow: 0 1rem 2rem rgb(0 0 0 / 0.18);
}

a.levelCard:focus-visible {
  outline: 3px solid color-mix(in srgb, var(--primary) 65%, white);
  outline-offset: 0.2rem;
}

.levelImage {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  z-index: 0;
}

.levelCard::after {
  content: "";
  position: absolute;
  inset: 0;
  z-index: 1;
  background: linear-gradient(
    to top,
    rgba(0, 0, 0, 0.75) 0%,
    rgba(0, 0, 0, 0.25) 55%,
    rgba(0, 0, 0, 0.05) 100%
  );
}

.levelName {
  position: relative;
  z-index: 2;
  font-size: 2rem;
  color: white;
  margin: 0;
}

.lock {
  position: absolute;
  top: 0.75rem;
  right: 0.75rem;
  z-index: 3;
  font-size: 3rem;
}

.lockedCard {
  color: #d1d5db;
  cursor: default;
}

.lockedCard .levelImage {
  filter: grayscale(100%) brightness(0.65);
}

.lockedCard .levelName {
  color: #d1d5db;
}
</style>