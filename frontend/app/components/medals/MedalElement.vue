<script setup lang="ts">
import type { Medal, MedalCategory } from "~/composables/types/MedalTypes";

const props = defineProps<{
  medal: Medal;
}>();

const categoryLabel = computed(() => {
  if (props.medal.category === "stop") return "Stoppested";
  if (props.medal.category === "mystery") return "Ukens mysterium";
  return "Spesial";
});
</script>

<template>
  <div :class="['medalElement', { locked: !props.medal.unlocked }]">
    <img
      class="medalImage"
      :src="props.medal.imageUrl"
      :alt="props.medal.title"
    />
    <span v-if="!props.medal.unlocked" class="title">{{
      props.medal.title
    }}</span>
  </div>
</template>

<style scoped>
.medalElement {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.medalImage {
  width: 100%;
  height: 100%;
  display: block;
}

.title {
  display: flex;
  width: fit-content;
  align-items: center;
  justify-content: center;
  padding: 0.5rem;
  background-color: red;
  color: white;
  font-size: 1rem;
}
</style>
