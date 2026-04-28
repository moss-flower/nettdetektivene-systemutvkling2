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
  /* cork board surface tokens */
  --trophy-canvas: var(--palette-cork-700);
  --trophy-canvas-edge: var(--palette-cork-800);
  --trophy-highlight: var(--palette-overlay-warm-glow);
  --trophy-shadow: var(--palette-overlay-dark-deep);
  --trophy-grain-ink: var(--palette-overlay-dark-medium);

  /* wall behind cork board (fills page) */
  --trophy-wall: var(--palette-wood-900);
  --trophy-wall-seam: var(--palette-overlay-warm-glow);

  /* shared text tokens */
  --trophy-title-depth: var(--palette-overlay-dark-strong);
  --trophy-text: var(--palette-cream-100);
  --trophy-text-muted: var(--palette-cream-300);
  --trophy-rule: var(--color-accent-reward);

  position: relative;
  height: 100%;
  width: 100%;
  padding: var(--space-7) var(--space-6);
  border-radius: var(--radius-2);
  display: grid;
  grid-template-columns: 1.65fr 1fr;
  background: var(--color-bg-canvas);
  gap: var(--space-7);
  background:
    repeating-linear-gradient(
      90deg,
      transparent 0,
      transparent 200px,
      var(--trophy-wall-seam) 200px,
      var(--trophy-wall-seam) 201px
    ),
    var(--trophy-wall);
  color: var(--trophy-text);
  overflow: hidden;
}

.achievementsSection {
  position: relative;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  align-items: stretch;
  gap: var(--space-5);
  padding: var(--space-6) var(--space-5);
  background:
    radial-gradient(ellipse 75% 55% at 28% 18%, var(--trophy-highlight), transparent 62%),
    radial-gradient(ellipse 70% 90% at 95% 95%, var(--trophy-shadow), transparent 65%),
    var(--trophy-canvas);
  border-radius: var(--radius-1);
  box-shadow: 0 var(--space-2) var(--space-5) var(--palette-overlay-dark-deep);
  overflow: hidden;
  isolation: isolate;
}

.achievementsSection::before {
  content: "";
  position: absolute;
  inset: 0;
  background-image: radial-gradient(circle at 1px 1px, var(--trophy-grain-ink) 1px, transparent 0);
  background-size: 3px 3px;
  opacity: 0.35;
  mix-blend-mode: multiply;
  pointer-events: none;
  z-index: -1;
}

.achievementsSection::after {
  content: "";
  position: absolute;
  inset: 0;
  background: radial-gradient(ellipse at center, transparent 48%, var(--trophy-canvas-edge) 100%);
  opacity: 0.55;
  pointer-events: none;
  z-index: -1;
}

.specialSection {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: var(--space-6);
  padding: var(--space-6) var(--space-5);
  border-radius: var(--radius-1);
  background:
    radial-gradient(ellipse 75% 55% at 28% 18%, var(--trophy-highlight), transparent 62%),
    radial-gradient(ellipse 70% 90% at 95% 95%, var(--trophy-shadow), transparent 65%),
    var(--trophy-canvas);
  box-shadow: 0 var(--space-2) var(--space-5) var(--palette-overlay-dark-deep);
}


.pageLabel {
  margin: 0 0 var(--space-3);
  color: var(--trophy-text-muted);
  font-family: var(--font-family-body), sans-serif;
  font-size: var(--font-size-14);
  line-height: var(--font-leading-14);
  font-weight: var(--font-weight-medium);
  text-transform: uppercase;
  letter-spacing: var(--font-tracking-wider);
}

.sectionTitle {
  margin: 0 0 var(--space-5);
  font-family: var(--font-family-serif), sans-serif;
  font-size: var(--font-size-36);
  line-height: var(--font-leading-36);
  font-weight: var(--font-weight-regular);
  color: var(--trophy-text);
  text-align: center;
  letter-spacing: var(--font-tracking-normal);
  text-shadow: 0 1px 2px var(--trophy-title-depth);
}

.sectionTitle::after {
  content: "";
  display: block;
  width: var(--space-6);
  height: var(--border-width-hairline);
  margin: var(--space-2) auto 0;
  background: var(--trophy-rule);
  opacity: 0.85;
}
</style>
