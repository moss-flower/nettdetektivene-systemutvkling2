<script setup lang="ts">

//Temporary hardcoded level values
const levels = ref<string[]>([
  "Passordbanken",
  "Sosiale møteplassen",
  "Nyhetskvartalet",
  "Markedsplassen",
  "Postkontoret",
  "Fotografen",
  "Datasenteret",
]);

//Active filter, string or null (unfiltered)
const props = defineProps<{
  activeFilter: string | null;
}>();

//Emits select + selected filter in string or null
const emit = defineEmits<{
  (e: "select", level: string | null): void;
}>();

//Called on filter button click. If current active filter is the same as clicked, return null, otherwise, return filter. 
function selectFilter(level: string) {
  const nextValue = props.activeFilter === level ? null : level;
  emit("select", nextValue);
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
          { activeFilter: props.activeFilter === level }
        ]"
        @click="selectFilter(level)"
      >
        {{ level }}
      </button>
    </div>
  </div>
</template>

<style scoped>
.title {
  font-size: 2rem;
}

.sideFilter {
  display: flex;
  flex-direction: column;
  width: 100%;
  align-items: center;
  justify-content: center;
}

.filterOptions {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 1rem;
  gap: 4px;
}

.filterButton {
  display: flex;
  width: 100%;
  align-items: center;
  justify-content: center;
  padding: 0.8rem; 
  margin: auto;
  font-size: 1rem;
  border-radius: 10px;
}


</style>