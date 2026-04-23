<script setup lang="ts">
import StationCard from "~/components/station/station.vue";
import { routePaths } from "~/composables/utils/routePaths";
import { Station } from "~/composables/types/StationType";

const stations = Station.backendFetchAllStations();
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
        v-for="station in stations"
        :key="station.uid"
        :station="station"
      />
    </div>
  </section>
</template>

<style scoped>
.stationsPage {
  --stations-eyebrow: var(--palette-cork-580);
  --stations-title: var(--palette-cream-200);
  --stations-summary: var(--palette-gray-300);
  --stations-back-bg: var(--palette-white);
  --stations-back-border: var(--palette-gray-200);
  --stations-back-text: var(--palette-navy-900);

  display: grid;
  gap: var(--space-5);
  padding: var(--layout-page-padding);
}

.goBack {
  width: fit-content;
  padding: var(--space-3) var(--space-4);
  border: var(--border-width-hairline) solid var(--stations-back-border);
  border-radius: var(--radius-pill);
  background: var(--stations-back-bg);
  color: var(--stations-back-text);
}

.pageIntro {
  display: grid;
  gap: var(--space-3);
}

.pageEyebrow {
  margin: 0;
  font-family: var(--font-family-body), sans-serif;
  font-size: var(--font-size-14);
  line-height: var(--font-leading-14);
  font-weight: var(--font-weight-bold);
  letter-spacing: var(--font-tracking-wider);
  text-transform: uppercase;
  color: var(--stations-eyebrow);
}

.pageTitle {
  margin: 0;
  color: var(--stations-title);
}

.pageSummary {
  margin: 0;
  max-width: 60ch;
  color: var(--stations-summary);
}

.stationGrid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: var(--space-4);
  justify-items: center;
}

</style>
