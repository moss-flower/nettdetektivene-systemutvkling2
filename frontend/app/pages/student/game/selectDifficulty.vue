<script setup lang="ts">
import datatyvenSprite from "~/assets/images/datatyven.png";
import PopupNotYetUnlocked from "~/components/shared/PopupNotYetUnlocked.vue";
import DifficultySelect from "~/components/station/difficultySelect.vue";
import { useGamePreview } from "~/composables/useGamePreview";
import { useStations } from "~/composables/useStations";

type ExerciseOption = {
  uid: string;
  title: string;
  difficultyText: string;
  description: string;
  isUnlocked: boolean;
  isCompleted: boolean;
  to: {
    path: string;
    query: Record<string, string>;
  };
};

const route = useRoute();
const { getStationDataByUid } = useStations();
const { isPreview, classroomId } = useGamePreview();
const showNotYetUnlockedPopup = ref(false);

const stationUid = computed(() =>
  typeof route.query.uid === "string" ? route.query.uid : "",
);
const previewWorldUid = computed(() => typeof route.query.worldUid === "string" ? route.query.worldUid : undefined);

const previewQuery = computed(() => {
  if (isPreview.value && classroomId.value) {
    return { preview: "1", classroomId: classroomId.value };
  }

  if (previewWorldUid.value) {
    return { worldUid: previewWorldUid.value };
  }

  return {};
});

const { data: station } = await useAsyncData(
  () => `station-${stationUid.value}`,
  async () => {
    if (!stationUid.value) {
      return null;
    }

    return await getStationDataByUid(stationUid.value, previewWorldUid.value);
  },
  { watch: [stationUid, previewWorldUid] },
);

const exerciseOptions = computed<ExerciseOption[]>(() => {
  if (!station.value) {
    return [];
  }

  const currentStation = station.value;

  return currentStation.exercises.map((exercise) => ({
    uid: exercise.uid,
    title: exercise.title,
    difficultyText: exercise.difficultyText,
    description: exercise.description ?? currentStation.description ?? "",
    isCompleted: exercise.isCompleted,
    isUnlocked: exercise.isUnlocked,
    to: {
      path: `/games/${currentStation.uid}`,
      query: { exerciseUid: exercise.uid, ...previewQuery.value },
    },
  }));
});
</script>

<template>
  <section class="difficultyPage">
    <template v-if="station">
      <header class="difficultyHero">
        <div class="difficultyHero__content">
          <p class="difficultyEyebrow">Velg oppgave</p>
          <h1 class="difficultyTitle">{{ station.title }}</h1>
          <p class="difficultySummary">{{ station.description }}</p>
        </div>
        <img
          v-if="station.isBoss"
          :src="datatyvenSprite"
          alt="Datatyven"
          class="difficultyHero__boss"
        >
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
          @locked-click="showNotYetUnlockedPopup = true"
          :show-star="!station.isTeacherPreview"
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

    <PopupNotYetUnlocked
      v-if="showNotYetUnlockedPopup"
      title="Denne oppgaven er fortsatt låst"
      description="Fullfør oppgaven før denne for å låse opp neste vanskelighetsgrad."
      @close="showNotYetUnlockedPopup = false"
    />
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

.difficultyHero {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  align-items: center;
  gap: var(--space-4);
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

.difficultyHero__content {
  display: grid;
  gap: var(--space-3);
}

.difficultyHero__boss {
  width: clamp(5.5rem, 12vw, 8rem);
  height: auto;
  justify-self: end;
  align-self: end;
  filter: drop-shadow(0 0.5rem 0.75rem rgba(0, 0, 0, 0.22));
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
  .difficultyHero {
    grid-template-columns: 1fr;
  }

  .difficultyHero__boss {
    justify-self: center;
    align-self: center;
  }

  .difficultyGrid {
    grid-template-columns: 1fr;
  }
}
</style>
