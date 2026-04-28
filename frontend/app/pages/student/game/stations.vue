<script setup lang="ts">
import StationCard from "~/components/station/station.vue";
import { useCompletedExerciseUids } from "~/composables/utils/gameProgress";
import { useStations } from "~/composables/useStations";

const { getMyStations } = useStations();
const { data: completedExerciseUids } = await useAsyncData(
  "student-completed-exercises",
  () => useCompletedExerciseUids(),
);

const { data: stationData } = await useAsyncData(
  "student-world",
  () => getMyStations(),
);

const pageTitle = computed(() => stationData.value?.world?.title || "Stasjonskart");

const stationCards = computed(() => {
  const completedUids = completedExerciseUids.value ?? [];
  let previousStationCompleted = true;

  return (stationData.value?.stations ?? []).map((station) => {
    const completedExercises = station.exerciseUids.map((exerciseUid) => completedUids.includes(exerciseUid));
    const isUnlocked = previousStationCompleted;
    const isCompleted = station.exerciseUids.length > 0 && completedExercises.every(Boolean);

    previousStationCompleted = isCompleted;

    return {
      station,
      completedExercises,
      isUnlocked,
    };
  });
});

const hasStations = computed(() => stationCards.value.length > 0);

</script>

<template>
  <section class="stationsPage">
    <header class="pageIntro">
      <p class="pageEyebrow">Stasjonskart</p>
      <h1 class="pageTitle">{{ pageTitle }}</h1>
      <p class="pageSummary">
        Start på den første åpne stasjonen og jobb deg videre bortover banen.
      </p>
    </header>

    <section v-if="hasStations" class="stationPath" aria-label="Stasjonsoversikt">
      <div
        v-for="(entry, index) in stationCards"
        :key="entry.station.uid"
        :class="['stationPath__node', `stationPath__node--${index % 2 === 0 ? 'left' : 'right'}`]"
      >
        <StationCard
          :station="entry.station"
          :is-unlocked="entry.isUnlocked"
          :completed-exercises="entry.completedExercises"
        />
      </div>
    </section>

    <section v-else class="emptyState">
      <h2>Ingen stasjoner tilgjengelig</h2>
      <p>Denne verdenen har ingen stasjoner ennå, eller du er ikke koblet til et klasserom.</p>
    </section>
  </section>
</template>

<style scoped>
.stationsPage {
  --stations-eyebrow: var(--palette-cork-580);
  --stations-title: var(--palette-cream-200);
  --stations-summary: var(--palette-gray-300);

  display: grid;
  gap: var(--space-5);
  padding: var(--layout-page-padding);
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

.stationPath {
  display: grid;
  gap: var(--space-4);
}

.stationPath__node {
  width: min(100%, 18rem);
}

.stationPath__node--left {
  justify-self: start;
  margin-left: max(0rem, 10%);
}

.stationPath__node--right {
  justify-self: end;
  margin-right: max(0rem, 10%);
}

.emptyState {
  display: grid;
  gap: var(--space-2);
  padding: var(--space-5);
  border: var(--border-width-hairline) solid var(--palette-gray-200);
  border-radius: var(--radius-3);
  background: rgba(255, 255, 255, 0.04);
}

.emptyState h2,
.emptyState p {
  margin: 0;
}

@media (max-width: 800px) {
  .stationPath__node,
  .stationPath__node--left,
  .stationPath__node--right {
    width: 100%;
    justify-self: stretch;
    margin-left: 0;
    margin-right: 0;
  }
}

</style>
