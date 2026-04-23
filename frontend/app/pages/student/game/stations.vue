<script setup lang="ts">
import StationCard from "~/components/station/station.vue";
import { Station } from "~/composables/types/StationType";

const stations = Station.backendFetchAllStations();
</script>

<template>
  <section class="stationsPage">
    <UiStudentHomeBackButton class="goBack" label="Tilbake" />

    <header class="pageIntro">
      <p class="pageEyebrow">Stasjonskart</p>
      <h1 class="pageTitle">Info here</h1>
      <p class="pageSummary">
        Start på den første åpne stasjonen og jobb deg videre bortover banen.
      </p>
    </header>

    <div class="stationGrid">
      <StationCard
        v-for="station in stations"
        :key="station.uid"
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
