<script setup lang="ts">
import MedalElement from "~/components/medals/MedalElement.vue";
import type { AchievementResponse } from "~/composables/types/AchievementTypes";

const props = defineProps<{
  medals: AchievementResponse[];
}>();
</script>

<template>
  <section class="medalGrid">
    <MedalElement
      v-for="medal in props.medals"
      :key="medal.UUID"
      :medal="medal"
    />
  </section>
</template> 

<style scoped>
.medalGrid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 30px;
  width: 100%;
}
</style>