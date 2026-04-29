<script setup lang="ts">
import DifficultySelect from "~/components/station/difficultySelect.vue";
import { useGamePreview } from "~/composables/useGamePreview";
import { useCompletedExerciseUids } from "~/composables/utils/gameProgress";
import { routePaths } from "~/composables/utils/routePaths";
import { Exercise } from "~/composables/types/ExerciseType";
import { Station } from "~/composables/types/StationType";

type ExerciseOption = {
  uid: string;
  title: string;
  difficultyText: string;
  description: string;
  isUnlocked: boolean;
  isCompleted: boolean;
  to: {
    path: string;
    query: {
      exerciseUid: string;
      preview?: string;
      classroomId?: string;
    };
  };
};

const route = useRoute();
const { isPreview, classroomId, buildPreviewRoute } = useGamePreview();

const stationUid = computed(() =>
  typeof route.query.uid === "string" ? route.query.uid : "",
);

const { data: station } = await useAsyncData(
  () => `station-${stationUid.value}`,
  () => (stationUid.value ? Station.backendFetchStationByUid(stationUid.value) : null),
  { watch: [stationUid] },
);

const hydratedStation = computed(() =>
  station.value ? Station.fromBackend(station.value) : null,
);

const { data: completedExerciseUids } = await useAsyncData(
  "completed-exercises",
  () => (isPreview.value ? [] : useCompletedExerciseUids()),
  {
    watch: [isPreview],
  },
);

const { data: stationExercises } = await useAsyncData(
  () => `station-exercises-${stationUid.value}`,
  () => (station.value ? Exercise.backendFetchExercisesByUids(station.value.exerciseUids) : []),
  { watch: [station] },
);

const stationStatus = computed(() => ({
  completedExercises: (station.value?.exerciseUids ?? []).map((exerciseUid) =>
    (completedExerciseUids.value ?? []).includes(exerciseUid),
  ),
}));

const backRoute = computed(() =>
  isPreview.value ? buildPreviewRoute(routePaths.studentLevels) : routePaths.studentLevels,
);

const exerciseOptions = computed<ExerciseOption[]>(() => {
  if (!station.value) {
    return [];
  }

  const currentStation = hydratedStation.value;

  if (!currentStation) {
    return [];
  }

  const currentExercises = stationExercises.value ?? [];

  return currentExercises.map((exercise, index) => ({
    uid: exercise.uid,
    title: exercise.title,
    difficultyText: exercise.entryName,
    description: exercise.description ?? currentStation.description ?? "",
    isCompleted: stationStatus.value.completedExercises[index] ?? false,
    isUnlocked: isPreview.value || index === 0 || Boolean(stationStatus.value.completedExercises[index - 1]),
    to: {
      path: `/games/${currentStation.uid}`,
      query: {
        exerciseUid: exercise.uid,
        ...(isPreview.value
          ? {
              preview: "1",
              classroomId: classroomId.value,
            }
          : {}),
      },
    },
  }));
});
</script>

<template>
  <section class="difficultyPage">
    <NuxtLink class="backLink" :to="backRoute">
      Tilbake til stasjoner
    </NuxtLink>

    <template v-if="hydratedStation">
      <header class="difficultyHero">
        <p class="difficultyEyebrow">Velg oppgave</p>
        <h1 class="difficultyTitle">{{ hydratedStation.title }}</h1>
        <p class="difficultySummary">{{ hydratedStation.description }}</p>
      </header>

      <div v-if="exerciseOptions.length > 0" class="difficultyGrid">
        <DifficultySelect
          v-for="option in exerciseOptions"
          :key="option.uid"
          :title="option.title"
          :difficulty-text="option.difficultyText"
          :description="option.description"
          :is-completed="option.isCompleted"
          :is-unlocked="option.isUnlocked"
          :to="option.to"
        />
      </div>

      <section v-else class="missingStation">
        <h1>Ingen oppgaver klare ennå</h1>
        <p>Denne stasjonen har ikke noen tilgjengelige øvelser enda.</p>
      </section>
    </template>

    <section v-else class="missingStation">
      <h1>Fant ikke stasjonen</h1>
      <p>Prøv å gå tilbake og velg en stasjon på nytt.</p>
    </section>
  </section>
</template>

<style scoped>
.difficultyPage {
  --difficulty-hero-bg-top: var(--palette-cream-100);
  --difficulty-hero-bg-bottom: var(--palette-cream-200);
  --difficulty-hero-border: var(--palette-overlay-cork-shadow);
  --difficulty-eyebrow: var(--palette-cork-580);
  --difficulty-title: var(--palette-cork-900);
  --difficulty-summary: var(--palette-cork-700);
  --difficulty-missing-bg: var(--palette-red-100);
  --difficulty-missing-border: var(--palette-red-200);

  display: grid;
  gap: var(--space-5);
  /* fluid outer padding, deliberate exception (token system)*/
  padding: var(--layout-page-padding-comfortable);
}

.backLink {
  width: fit-content;
  padding: var(--space-3) var(--space-4);
  border: var(--border-width-hairline) solid var(--palette-gray-200);
  border-radius: var(--radius-pill);
  background: var(--palette-white);
  font-weight: var(--font-weight-bold);
  color: var(--palette-navy-900);
  text-decoration: none;
}

.difficultyHero {
  display: grid;
  gap: var(--space-3);
  /* fluid inner padding, deliberate exception (token system) */
  padding: clamp(1.25rem, 3vw, 2rem);
  border: var(--border-width-hairline) solid var(--difficulty-hero-border);
  border-radius: var(--radius-4);
  background: linear-gradient(
      135deg,
      var(--difficulty-hero-bg-top) 0%,
      var(--difficulty-hero-bg-bottom) 100%
  );
  box-shadow: var(--shadow-2);
}

.difficultyEyebrow {
  margin: 0;
  font-size: var(--font-size-14);
  line-height: var(--font-leading-14);
  font-weight: var(--font-weight-bold);
  text-transform: uppercase;
  letter-spacing: var(--font-tracking-wider);
  color: var(--difficulty-eyebrow);
}

.difficultyTitle {
  margin: 0;
  /* fluid display-scale, deliberate exception again */
  font-size: clamp(2rem, 5vw, 3.25rem);
  color: var(--difficulty-title);
}

.difficultySummary {
  margin: 0;
  max-width: 60ch;
  line-height: var(--font-leading-base);
  color: var(--difficulty-summary);
}

.difficultyGrid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: var(--space-4);
}

.missingStation {
  padding: var(--space-5);
  border-radius: var(--radius-3);
  background: var(--difficulty-missing-bg);
  border: var(--border-width-hairline) solid var(--difficulty-missing-border);
}

.missingStation h1,
.missingStation p {
  margin: 0;
}

.missingStation p {
  margin-top: var(--space-3);
}

@media (max-width: 900px) {
  .difficultyGrid {
    grid-template-columns: 1fr;
  }
}
</style>
