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
.sideFilter {
  /* Filing-cabinet index, sits on dark desk in the right rail */
  --filter-title-color: var(--palette-cream-100);
  --filter-title-depth: var(--palette-overlay-dark-strong);
  --filter-rule: var(--palette-gold-500);

  /* tab buttons (cream paper on cork border) */
  --filter-button-bg: var(--palette-cream-100);
  --filter-button-text: var(--palette-cork-900);
  --filter-button-border: var(--palette-cork-900);
  --filter-button-shadow: var(--palette-overlay-dark-deep);
  --filter-button-hover-ring: var(--palette-gold-500);

  /* pressed / active states, brass-stamped index tab */
  --filter-active-bg: var(--palette-navy-900);
  --filter-active-text: var(--palette-cream-100);
  --filter-active-ring: var(--palette-gold-500);

  display: flex;
  flex-direction: column;
  width: 100%;
  align-items: center;
  gap: var(--space-4);
}

.title {
  margin: 0;
  color: var(--filter-title-color);
  font-family: var(--font-family-serif), sans-serif;
  font-size: var(--font-size-28);
  line-height: var(--font-leading-28);
  font-weight: var(--font-weight-semibold);
  text-align: center;
  text-shadow: 0 1px 2px var(--filter-title-depth);
}

.title::after {
  content: "";
  display: block;
  width: var(--space-6);
  height: var(--border-width-hairline);
  margin: var(--space-2) auto 0;
  background: var(--filter-rule);
  opacity: 0.85;
}

.filterOptions {
  display: flex;
  flex-direction: column;
  align-items: stretch;
  gap: var(--space-2);
  width: 100%;
  padding: 0;
}

.filterButton {
  display: flex;
  width: 100%;
  align-items: center;
  justify-content: center;
  min-height: var(--size-control-md);
  padding: var(--space-2) var(--space-3);
  border: var(--border-width-hairline) solid var(--filter-button-border);
  border-radius: var(--radius-pill);
  background: var(--filter-button-bg);
  color: var(--filter-button-text);
  font-family: var(--font-family-body), sans-serif;
  font-size: var(--font-size-14);
  line-height: var(--font-leading-14);
  font-weight: var(--font-weight-semibold);
  cursor: pointer;
  box-shadow: 0 var(--space-1) var(--space-2) var(--filter-button-shadow);
  transition:
    transform var(--transition-fast),
    box-shadow var(--transition-fast),
    background var(--transition-fast),
    color var(--transition-fast);
}

.filterButton:hover {
  transform: translateY(-1px);
  box-shadow:
    0 0 0 var(--border-width-thin) var(--filter-button-hover-ring),
    0 var(--space-1) var(--space-3) var(--filter-button-shadow);
}

.filterButton.active,
.filterButton[aria-pressed="true"] {
  background: var(--filter-active-bg);
  color: var(--filter-active-text);
  border-color: var(--filter-active-ring);
  transform: translateY(0);
  box-shadow:
    inset 0 var(--space-1) var(--space-2) var(--palette-overlay-dark-deep),
    0 0 0 var(--border-width-thin) var(--filter-active-ring);
}
</style>