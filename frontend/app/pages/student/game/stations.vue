<script setup lang="ts">
import StationCard from "~/components/station/station.vue";
import { World } from "~/composables/types/WorldTypes";
import { Station } from "~/composables/types/StationType";
import { useCompletedExerciseUids } from "~/composables/utils/gameProgress";

const { student } = useClassrooms();
const { data: completedExerciseUids } = await useAsyncData(
  "student-completed-exercises",
  () => useCompletedExerciseUids(),
);

const { data: classroom } = await useAsyncData("student-classroom", () => student.getMyClassroom().catch(() => null));

const worldUid = computed(() => classroom.value?.worldUid?.trim() ?? "");

const { data: world } = await useAsyncData(
  "student-world",
  () => (worldUid.value ? World.backendFetchWorldByUid(worldUid.value) : null),
  {
    watch: [worldUid],
  },
);

const { data: stations } = await useAsyncData(
  "student-stations",
  async () => {
    const stationUids = world.value?.stationUids ?? [];

    if (stationUids.length === 0) {
      return [];
    }

    const loadedStations = await Promise.all(stationUids.map((stationUid) => Station.backendFetchStationByUid(stationUid)));

    return loadedStations.filter((station): station is Station => station !== null);
  },
  {
    watch: [world],
  },
);

const pageTitle = computed(() => world.value?.title || "Stasjonskart");

const stationCards = computed(() => {
  const completedUids = completedExerciseUids.value ?? [];
  let previousStationCompleted = true;

  return (stations.value ?? []).map((station) => {
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
      <template v-for="(entry, index) in stationCards" :key="entry.station.uid">
        <div :class="['stationPath__node', `stationPath__node--${index % 2 === 0 ? 'left' : 'right'}`]">
          <StationCard
            :station="entry.station"
            :is-unlocked="entry.isUnlocked"
            :completed-exercises="entry.completedExercises"
          />
        </div>
        <div
          v-if="index < stationCards.length - 1"
          :class="['stationPath__trail', `stationPath__trail--${index % 2 === 0 ? 'right' : 'left'}`]"
          aria-hidden="true"
        />
      </template>
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
  gap: var(--space-3);
}

.stationPath__node {
  position: relative;
  z-index: 1;
  width: min(100%, 18rem);
}

.stationPath__trail {
  position: relative;
  width: min(48%, 26rem);
  height: clamp(3.5rem, 7vw, 5.5rem);
  margin-block: calc(var(--space-2) * -1);
  pointer-events: none;
}

.stationPath__trail::before {
  content: "";
  position: absolute;
  inset: 0;
  border-bottom: 0.45rem dotted rgba(230, 180, 96, 0.82);
  filter: drop-shadow(0 0.18rem 0 rgba(44, 28, 18, 0.35));
}

.stationPath__trail--right {
  justify-self: center;
  transform: translateX(8%) rotate(9deg);
}

.stationPath__trail--left {
  justify-self: center;
  transform: translateX(-8%) rotate(-9deg);
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

  .stationPath__trail {
    justify-self: center;
    width: 0;
    height: var(--space-6);
    margin-block: calc(var(--space-2) * -1);
    transform: none;
  }

  .stationPath__trail::before {
    left: 50%;
    width: 0;
    border-bottom: 0;
    border-left: 0.45rem dotted rgba(230, 180, 96, 0.82);
    transform: translateX(-50%);
  }
}

</style>
