<script setup lang="ts">
import MedalElement from "~/components/medals/MedalElement.vue";
import type { AchievementResponse } from "~/composables/types/AchievementTypes";

const props = defineProps<{
  medals: AchievementResponse[];
}>();

const emit = defineEmits<{
  (e: "select", medal: AchievementResponse): void;
}>();
</script>

<template>
  <section class="medalGrid">
    <MedalElement
      v-for="medal in props.medals"
      :key="medal.UUID"
      :medal="medal"
      @select="emit('select', $event)"
    />
  </section>
</template> 

<style scoped>
.medalGrid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--space-5);
  width: 100%;
}
</style>
