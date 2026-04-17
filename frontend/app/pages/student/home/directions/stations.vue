<script setup lang="ts">
import StationCard from "~/components/station/station.vue";
import { routePaths } from "~/composables/routePaths";
import { Station } from "~/composables/types/StationType";

//Props, temporary hardcoded content
const mockData = Station.mockData();
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

    <header class="pageIntro">
      <p class="pageEyebrow">Stasjonskart</p>
      <h1 class="pageTitle">Info here</h1>
      <p class="pageSummary">
        Start på den første åpne stasjonen og jobb deg videre bortover banen.
      </p>
    </header>

    <div class="stationGrid">
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
  gap: 1.5rem;
  padding: clamp(1rem, 2vw, 2rem);
}

.goBack {
  width: fit-content;
  padding: 0.6rem 1rem;
  border: 1px solid #d6d6d6;
  border-radius: 999px;
  background: #ffffff;
  color: #1f2937;
}

.pageIntro {
  display: grid;
  gap: 0.75rem;
}

.pageEyebrow {
  margin: 0;
  font-size: 0.9rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: #86562b;
}

.pageTitle {
  margin: 0;
  color: #f3f3f3;
}

.pageSummary {
  margin: 0;
  max-width: 60ch;
  color: #d1d5db;
}

.stationGrid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 1rem;
  justify-items: center;
}

</style>
