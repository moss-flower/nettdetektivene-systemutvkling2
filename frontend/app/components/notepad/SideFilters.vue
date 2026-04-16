<script setup lang="ts">
const levels = ref<string[]>([
  "Passordbanken",
  "Sosiale møteplassen",
  "Nyhetskvartalet",
  "Markedsplassen",
  "Postkontoret",
  "Fotografen",
  "Datasenteret",
]);

const active = ref<string | null>(null);

const emit = defineEmits<{
  (e: "select", level: string | null): void;
}>();

function selectFilter(level: string) {
  active.value = active.value === level ? null : level;
  emit("select", active.value);
}
</script>

<template>
  <div class="sideFilter">
    <h4 class="title">Filters</h4>

    <div class="filterOptions">
      <button
        v-for="level in levels"
        :key="level"
        :class="[
          'filterButton',
          { activeFilter: active === level }
        ]"
        @click="selectFilter(level)"
      >
        {{ level }}
      </button>
    </div>
  </div>
</template>