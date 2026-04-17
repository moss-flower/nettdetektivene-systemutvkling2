<script setup lang="ts">
import DifficultySelect from "~/components/station/difficultySelect.vue";
import { routePaths } from "~/composables/routePaths";
import { Exercise } from "~/composables/types/ExerciseType";
import { Station } from "~/composables/types/StationType";

type ExerciseOption = {
  id: string;
  title: string;
  difficultyText: string;
  description: string;
  isUnlocked: boolean;
  isCompleted: boolean;
  to: {
    path: string;
    query: {
      exerciseId: string;
    };
  };
};

const route = useRoute();

const stationId = computed(() =>
  typeof route.query.id === "string" ? route.query.id : "",
);

const station = computed(() =>
  stationId.value ? Station.backendFetchStationById(stationId.value) : null,
);
const stationStatus = computed(() => station.value?.getMockStatus() ?? {
  completedExercises: [],
});
const stationIsUnlocked = computed(() => station.value?.isUnlocked() ?? false);

const stationExercises = computed(() =>
  station.value ? Exercise.backendFetchExercisesByIds(station.value.exerciseIds) : [],
);

const exerciseOptions = computed<ExerciseOption[]>(() => {
  if (!station.value) {
    return [];
  }

  const currentStation = station.value;

  return stationExercises.value.map((exercise, index) => ({
    id: exercise.id,
    title: exercise.title,
    difficultyText: exercise.entryName,
    description: exercise.description ?? currentStation.description ?? "",
    isCompleted: stationStatus.value.completedExercises[index] ?? false,
    isUnlocked: currentStation.isExerciseUnlocked(exercise.id),
    to: {
      path: `/games/${currentStation.slug}`,
      query: { exerciseId: exercise.id },
    },
  }));
});
</script>

<template>
  <section class="difficultyPage">
    <NuxtLink class="backLink" :to="routePaths.studentLevels">
      Tilbake til stasjoner
    </NuxtLink>

    <template v-if="station">
      <header class="difficultyHero">
        <p class="difficultyEyebrow">Velg oppgave</p>
        <h1 class="difficultyTitle">{{ station.name }}</h1>
        <p class="difficultySummary">{{ station.description }}</p>
      </header>

      <div v-if="exerciseOptions.length > 0" class="difficultyGrid">
        <DifficultySelect
          v-for="option in exerciseOptions"
          :key="option.id"
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
  display: grid;
  gap: 1.5rem;
  padding: clamp(1rem, 2vw, 2rem);
}

.backLink {
  width: fit-content;
  font-weight: 700;
  color: var(--primary);
  text-decoration: none;
}

.difficultyHero {
  display: grid;
  gap: 0.75rem;
  padding: clamp(1.25rem, 3vw, 2rem);
  border: 1px solid color-mix(in srgb, var(--border) 70%, transparent);
  border-radius: 1.5rem;
  background: linear-gradient(135deg, #fff7e8 0%, #f6ead1 100%);
  box-shadow: var(--shadow);
}

.difficultyEyebrow {
  margin: 0;
  font-size: 0.9rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  color: #86562b;
}

.difficultyTitle {
  margin: 0;
  font-size: clamp(2rem, 5vw, 3.25rem);
  color: #3e2a18;
}

.difficultySummary {
  margin: 0;
  max-width: 60ch;
  line-height: 1.55;
  color: #5b4633;
}

.difficultyGrid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 1rem;
}

.missingStation {
  padding: 1.5rem;
  border-radius: 1.25rem;
  background: #fff1ef;
  border: 1px solid #f3c4bc;
}

.missingStation h1,
.missingStation p {
  margin: 0;
}

.missingStation p {
  margin-top: 0.75rem;
}

@media (max-width: 900px) {
  .difficultyGrid {
    grid-template-columns: 1fr;
  }
}
</style>
