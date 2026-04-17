<script setup lang="ts">
import { NuxtLink } from "#components";
import starEmpty from "~/assets/images/star/spStarEmpty.png";
import starFilled from "~/assets/images/star/spStarFilled.png";
import type { RouteLocationRaw } from "vue-router";

const props = defineProps<{
  title: string;
  difficultyText: string;
  description: string;
  isCompleted: boolean;
  isUnlocked: boolean;
  to?: RouteLocationRaw;
}>();

const cardTag = computed(() => (props.isUnlocked && props.to ? NuxtLink : "div"));
const star = computed(() => ({
  src: props.isCompleted ? starFilled : starEmpty,
  alt: props.isCompleted ? `${props.difficultyText} fullført` : `${props.difficultyText} ikke fullført`,
}));
</script>

<template>
  <component
    :is="cardTag"
    class="difficultyCard"
    :class="{ difficultyCardLocked: !isUnlocked }"
    :to="isUnlocked ? to : undefined"
  >
    <p class="difficultyLabel">{{ title }}</p>
    <h2 class="difficultyHeading">{{ difficultyText }}</h2>
    <div class="difficultyStars" aria-hidden="true">
      <img :src="star.src" :alt="star.alt" class="difficultyStarImage">
    </div>
    <p class="difficultyDescription">{{ description }}</p>
    <p class="difficultyState">
      {{ isUnlocked ? (isCompleted ? "Spill igjen" : "Start oppgave") : "Låst" }}
    </p>
    <p v-if="!isUnlocked" class="difficultyLock">🔒</p>
  </component>
</template>

<style scoped>
.difficultyCard {
  position: relative;
  min-height: 220px;
  padding: 1.5rem;
  display: grid;
  gap: 0.75rem;
  border: 1px solid color-mix(in srgb, var(--border) 65%, transparent);
  border-radius: 1.25rem;
  text-decoration: none;
  color: #1d2433;
  background: linear-gradient(180deg, #f9f5ea 0%, #efe2bf 100%);
  box-shadow: var(--shadow);
  transition:
    transform 160ms ease,
    box-shadow 160ms ease,
    border-color 160ms ease;
}

a.difficultyCard {
  cursor: pointer;
}

a.difficultyCard:hover {
  transform: translateY(-4px);
  box-shadow: 0 1rem 2rem rgb(0 0 0 / 0.12);
}

a.difficultyCard:focus-visible {
  outline: 3px solid color-mix(in srgb, var(--primary) 65%, white);
  outline-offset: 0.2rem;
}

.difficultyLabel {
  margin: 0;
  font-size: 0.85rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  opacity: 0.7;
}

.difficultyHeading {
  margin: 0;
  font-size: clamp(2rem, 4vw, 2.6rem);
  text-transform: capitalize;
}

.difficultyDescription {
  margin: 0;
  max-width: 26ch;
  line-height: 1.45;
}

.difficultyStars {
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

.difficultyStarImage {
  width: 24px;
  height: 24px;
}

.difficultyState {
  margin: auto 0 0;
  font-weight: 700;
}

.difficultyLock {
  position: absolute;
  top: 1rem;
  right: 1rem;
  margin: 0;
  font-size: 2rem;
}

.difficultyCardLocked {
  filter: grayscale(0.2);
  opacity: 0.7;
}

@media (max-width: 700px) {
  .difficultyCard {
    min-height: 180px;
  }
}
</style>
