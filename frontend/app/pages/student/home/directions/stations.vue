<script setup lang="ts">
import StationCard from "~/components/station/station.vue";
import { routePaths } from "~/composables/routePaths";
import { StationFactory } from "~/composables/types/StationType";

//Props, temporary hardcoded content
const mockData = StationFactory.mockData();
const router = useRouter();

function goBack() {
  router.push(routePaths.studentHome);
}

</script>

<template>
  <section class="stationsPage">
    <button class="goBack" type="button" @click="goBack">
      ← Tilbake
    </button>

    <div class="levelGrid">
      <StationCard
        v-for="station in mockData"
        :key="station.id"
        :station="station"
      />
    </div>
  </section>
</template>

<style scoped>
.stationsPage {
  display: grid;
  gap: 1rem;
}

.goBack {
  width: fit-content;
  padding: 0.75rem 1rem;
  border: 1px solid var(--border);
  border-radius: 999px;
  background: white;
  color: var(--text);
  font-weight: 700;
  cursor: pointer;
  box-shadow: var(--shadow);
}

.goBack:hover {
  transform: translateY(-2px);
}

.goBack:focus-visible {
  outline: 3px solid color-mix(in srgb, var(--primary) 65%, white);
  outline-offset: 0.2rem;
}

.levelGrid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 1rem;
}

@media (max-width: 1000px) {
  .levelGrid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 750px) {
  .levelGrid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 500px) {
  .levelGrid {
    grid-template-columns: 1fr;
  }
}
</style>