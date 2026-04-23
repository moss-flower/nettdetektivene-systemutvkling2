<script setup lang="ts">
import MedalGrid from "~/components/medals/MedalGrid.vue";
import MedalElement from "~/components/medals/MedalElement.vue";
import type { AchievementResponse } from "~/composables/types/AchievementTypes";
import { useAchievements } from "~/composables/useAchievements";

const { student } = useAchievements();

const medals = ref<AchievementResponse[]>([]);
const loading = ref(true);
const formError = ref("");

const regularMedals = computed(() =>
  medals.value.filter((medal) => medal.category !== "special"),
);

const specialMedal = computed(() =>
  medals.value.find((medal) => medal.category === "special") ?? null,
);

onMounted(async () => {
  loading.value = true;
  formError.value = "";

  try {
    medals.value = await student.getUnlockedAchievements();
  } catch (error: any) {
    formError.value =
      error?.data?.message ||
      error?.data?.error ||
      error?.statusMessage ||
      "Klarte ikke å hente achievements.";
  } finally {
    loading.value = false;
  }
});
</script>

<template>
  <section class="medalsPage">
    <template v-if="loading">
      <div class="achievementsSection">
        <h1 class="sectionTitle">Achievements</h1>
        <p>Laster achievements...</p>
      </div>
    </template>

    <template v-else-if="formError">
      <div class="achievementsSection">
        <h1 class="sectionTitle">Achievements</h1>
        <p>{{ formError }}</p>
      </div>
    </template>

    <template v-else>
      <div class="achievementsSection">
        <h1 class="sectionTitle">Achievements</h1>
        <MedalGrid :medals="regularMedals" />
      </div>

      <div class="specialSection">
        <h1 class="sectionTitle">Special achievement</h1>
        <MedalElement v-if="specialMedal" class="specialMedal" :medal="specialMedal" />
      </div>
    </template>
  </section>
</template>

<style scoped>
.medalsPage {
  height: 100%;
  width: 100%;
  background: rgb(94, 37, 0);
  display: grid;
  grid-template-columns: 64% 34%;
  gap: 2%;
  padding: var(--space-6);
}

.pageLabel {
  margin: 0 0 0.75rem;
  color: #8e8e93;
  font-size: 0.85rem;
}

.achievementsSection,
.specialSection {
  display: flex;
  flex-direction: column;
  height: 100%;
  justify-content: space-around;
  align-items: center;
}

.sectionTitle {
  font-size: 3.5rem;
  font-weight: 400;
  color: white;
  font-family: Georgia, "Times New Roman", serif;
  text-align: center;
  display: flex;
  padding: var(--space-4);
}
</style>