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
  bottom: 0;
  width: 100%;

  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;

  padding: 0.5rem;
  box-sizing: border-box;

  background-color: red;
  color: white;
  font-size: 3rem;
  font-weight: 600;
}

.locked {
  opacity: 0.5;
}
</style>