<script setup lang="ts">
import { NuxtLink } from "#components";
import datatyvenSprite from "~/assets/images/datatyven.png";
import { useGamePreview } from "~/composables/useGamePreview";
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

const route = useRoute();
const stationIsUnlocked = computed(() => props.isUnlocked ?? true);
const completedExercises = computed(() => props.completedExercises ?? []);
const { isPreview, classroomId } = useGamePreview();

const stationPath = computed(() => {
  const worldUid = typeof route.query.worldUid === "string" ? route.query.worldUid : undefined;
  const previewQuery = isPreview.value && classroomId.value
    ? { preview: "1", classroomId: classroomId.value }
    : worldUid
      ? { worldUid }
      : {};

  return {
    path: routePaths.studentExerciseDifficultySelect,
    query: { uid: props.station.uid, ...previewQuery },
  };
});

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
  
    <div class="levelBubble" :style="{ '--station-image': `url(${station.image})` }">
      <div class="levelImageWrap">
        <img :src="station.image" :alt="station.title" class="levelImage">
      </div>

      <h4 class="levelName">{{ station.title }}</h4>
    </div>

    <div class="starTray" :class="{ 'starTray--boss': station.isBoss }" aria-label="Oppgavestjerner">
      <img
        v-if="station.isBoss"
        :src="datatyvenSprite"
        alt=""
        aria-hidden="true"
        class="bossSprite"
      >
      <div class="starRow">
        <img
          v-for="star in stars"
          :key="star.id"
          :src="star.src"
          :alt="star.alt"
          class="starImage"
        >
      </div>
    </div>
    <p v-if="!stationIsUnlocked" class="lock">🔒</p>
  </component>
</template>

<style scoped>
.levelCard {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0;
  width: 100%;
  max-width: 15.5rem;
  padding: 0 0 var(--space-2);
  border: 0;
  background: transparent;
  color: var(--palette-navy-900);
  font: inherit;
  text-decoration: none;
  cursor: pointer;
}

.lockedCard {
  cursor: not-allowed;
  filter: grayscale(0.65) saturate(0.75);
}

.levelCard:focus-visible {
  outline: none;
}

.levelCard:focus-visible .levelBubble {
  outline: var(--border-width-thick) solid color-mix(in srgb, var(--color-focus-ring) 75%, white);
  outline-offset: 0.22rem;
}

.levelBubble {
  position: relative;
  display: grid;
  justify-items: center;
  align-content: start;
  gap: var(--space-1);
  width: min(100%, 13.5rem);
  aspect-ratio: 1;
  padding: 1.35rem 1.2rem 2.65rem;
  border: 0.36rem solid var(--palette-cream-300);
  border-radius: 50%;
  background:
    linear-gradient(180deg, rgba(245, 239, 226, 0.92) 0%, rgba(217, 206, 181, 0.82) 100%),
    var(--station-image) center / cover no-repeat,
    var(--palette-cream-100);
  background-blend-mode: soft-light, normal, normal;
  box-shadow:
    0 0.4rem 0 var(--palette-cork-400),
    0 0.85rem 1.2rem rgba(42, 26, 11, 0.18),
    inset 0 -0.45rem 0 rgba(122, 78, 42, 0.16),
    inset 0 0.4rem 0 rgba(255, 255, 255, 0.32);
  transition: transform 150ms ease, filter 150ms ease;
}

.levelCard:hover .levelBubble {
  transform: translateY(-0.14rem) scale(1.01);
  filter: brightness(1.02);
}

.levelImage {
  width: 0;
  height: 0;
  opacity: 0;
  border: 0.2rem solid rgba(255, 255, 255, 0.9);
  border-radius: 999rem;
  object-fit: cover;
  box-shadow:
    0 0.24rem 0 rgba(122, 62, 42, 0.28),
    0 0.45rem 0.7rem rgba(38, 18, 52, 0.22);
}

.levelImageWrap {
  position: relative;
  display: flex;
  justify-content: center;
  width: min(82%, 9.2rem);
  min-height: 3.1rem;
}

.bossSprite {
  position: absolute;
  bottom: calc(100% - 0.08rem);
  left: 50%;
  z-index: 2;
  width: 4.25rem;
  height: auto;
  transform: translateX(-50%);
  pointer-events: none;
  filter: drop-shadow(0 0.35rem 0.5rem rgba(42, 26, 11, 0.2));
}

.levelName {
  margin: -0.25rem 0 0;
  max-width: 9.8rem;
  text-align: center;
  font-size: clamp(0.9rem, 2.6vw, 1.08rem);
  line-height: 1.05;
  font-weight: var(--font-weight-bold);
  color: var(--palette-navy-900);
  text-shadow: 0 0.08rem 0 rgba(255, 255, 255, 0.5);
}

.starTray {
  position: relative;
  z-index: 1;
  min-width: min(78%, 10.8rem);
  min-height: 3.2rem;
  margin-top: -1.85rem;
  padding: 0.65rem 0.85rem 0.5rem;
  border: 0.22rem solid var(--palette-cream-300);
  border-radius: 1.15rem;
  background: linear-gradient(180deg, rgba(246, 234, 209, 0.98) 0%, rgba(217, 206, 181, 0.94) 100%);
  box-shadow:
    0 0.32rem 0 var(--palette-cork-400),
    0 0.58rem 0.9rem rgba(42, 26, 11, 0.16),
    inset 0 -0.18rem 0 rgba(122, 78, 42, 0.14);
}

.starTray--boss {
  margin-top: 0.25rem;
}

.starRow {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.18rem;
  min-height: 1.65rem;
}

.starImage {
  width: clamp(1.45rem, 4.5vw, 1.85rem);
  height: clamp(1.45rem, 4.5vw, 1.85rem);
  filter: drop-shadow(0 0.12rem 0 rgba(74, 46, 23, 0.2));
}

.lock {
  position: absolute;
  top: 0.75rem;
  right: 1.1rem;
  z-index: 2;
  display: grid;
  place-items: center;
  width: 2.25rem;
  height: 2.25rem;
  margin: 0;
  border-radius: 50%;
  background: var(--palette-cream-200);
  font-size: 1.1rem;
  box-shadow: 0 0.22rem 0 rgba(74, 46, 23, 0.22);
}

</style>
