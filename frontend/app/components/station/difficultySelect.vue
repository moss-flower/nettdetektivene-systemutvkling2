<script setup lang="ts">
import { NuxtLink } from "#components";
import starEmpty from "~/assets/images/star/spStarEmpty.png";
import starFilled from "~/assets/images/star/spStarFilled.png";
import type { RouteLocationRaw } from "vue-router";

const emit = defineEmits<{
  (event: "locked-click"): void;
}>();

const props = defineProps<{
  title: string;
  difficultyText: string;
  description: string;
  isCompleted: boolean;
  isUnlocked: boolean;
  to?: RouteLocationRaw;
  showStar?: boolean;
}>();

const cardTag = computed(() => (props.isUnlocked && props.to ? NuxtLink : "button"));
const showStar = computed(() => props.showStar ?? true);
const star = computed(() => ({
  src: props.isCompleted ? starFilled : starEmpty,
  alt: props.isCompleted ? `${props.difficultyText} fullført` : `${props.difficultyText} ikke fullført`,
}));

const handleClick = () => {
  if (!props.isUnlocked) {
    emit("locked-click");
  }
};
</script>

<template>
  <component
    :is="cardTag"
    class="difficultyCard"
    :class="{ difficultyCardLocked: !isUnlocked }"
    :to="isUnlocked ? to : undefined"
    :type="isUnlocked ? undefined : 'button'"
    @click="handleClick"
  >
    <p class="difficultyLabel">{{ title }}</p>
    <h2 class="difficultyHeading">{{ difficultyText }}</h2>
    <p class="difficultyDescription">{{ description }}</p>
    <p class="difficultyState">
      {{ isUnlocked ? (isCompleted ? "Spill igjen" : "Start oppgave") : "Låst" }}
    </p>
    <div v-if="showStar" class="difficultyStars" aria-hidden="true">
      <img :src="star.src" :alt="star.alt" class="difficultyStarImage">
    </div>
    <p v-if="!isUnlocked" class="difficultyLock">🔒</p>
  </component>
</template>

<style scoped>
.difficultyCard {
  position: relative;
  min-height: 220px;
  padding: var(--space-6);
  display: grid;
  gap: var(--space-3);
  border: var(--border-width-hairline) solid color-mix(in srgb, var(--color-border-default) 65%, transparent);
  border-radius: 1.25rem;
  text-decoration: none;
  text-align: left;
  color: #1d2433;
  font: inherit;
  background: linear-gradient(180deg, #f9f5ea 0%, #efe2bf 100%);
  box-shadow: var(--shadow-1);
  cursor: pointer;
  transition:
    transform 160ms ease,
    box-shadow 160ms ease,
    border-color 160ms ease;
}

.difficultyCard:hover {
  transform: translateY(-4px);
  box-shadow: 0 1rem 2rem rgb(0 0 0 / 0.12);
}

.difficultyCard:focus-visible {
  outline: var(--border-width-thick) solid color-mix(in srgb, var(--color-brand-primary) 65%, white);
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
  position: absolute;
  right: var(--space-4);
  bottom: var(--space-4);
  display: flex;
  align-items: center;
  gap: var(--space-1);
}

.difficultyStarImage {
  width: 28px;
  height: 28px;
}

.difficultyState {
  margin: auto 0 0;
  padding-right: 2.75rem;
  font-weight: 700;
}

.difficultyLock {
  position: absolute;
  top: var(--space-4);
  right: var(--space-4);
  margin: 0;
  font-size: 2rem;
}

.difficultyCardLocked {
  filter: grayscale(0.2);
  opacity: 0.7;
}

</style>
