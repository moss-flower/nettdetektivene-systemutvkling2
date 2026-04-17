<script setup lang="ts">
import MedalElement from "~/components/medals/MedalElement.vue";
import type { Medal } from "~/composables/types/MedalTypes";

const props = defineProps<{
  medals: Medal[];
}>();
</script>

<template>
  <section class="medalGrid">
    <MedalElement
      v-for="medal in props.medals"
      :key="medal.id"
      :medal="medal"
    />
  </section>
</template>

<style scoped>
.medalGrid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  margin: auto;
  gap: 5rem;
  width: 100%;
  min-width: 70%;
}
</style>