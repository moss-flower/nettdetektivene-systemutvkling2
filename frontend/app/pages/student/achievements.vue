<script setup lang="ts">
import MedalGrid from "~/components/medals/MedalGrid.vue";
import MedalElement from "~/components/medals/MedalElement.vue";
import medalImage from "~/assets/images/medal.png";
import type { Medal } from "~/composables/types/MedalTypes";
import { routePaths } from "~/composables/utils/routePaths";

const medals: Medal[] = [
  {
    id: "passordbanken",
    title: "Passordbanken",
    description: "Fullførte Passordbanken.",
    category: "stop",
    source: "Passordbanken",
    unlocked: false,
    imageUrl: medalImage,
    reward: null,
  },
  {
    id: "sosiale-moteplassen",
    title: "Sosiale møteplassen",
    description: "Fullførte Sosiale møteplassen.",
    category: "stop",
    source: "Sosiale møteplassen",
    unlocked: true,
    imageUrl: medalImage,
    reward: null,
  },
  {
    id: "nyhetskvartalet",
    title: "Nyhetskvartalet",
    description: "Fullførte Nyhetskvartalet.",
    category: "stop",
    source: "Nyhetskvartalet",
    unlocked: false,
    imageUrl: medalImage,
    reward: null,
  },
  {
    id: "markedsplassen",
    title: "Markedsplassen",
    description: "Fullførte Markedsplassen.",
    category: "stop",
    source: "Markedsplassen",
    unlocked: true,
    imageUrl: medalImage,
    reward: null,
  },
  {
    id: "postkontoret",
    title: "Postkontoret",
    description: "Fullførte Postkontoret.",
    category: "stop",
    source: "Postkontoret",
    unlocked: false,
    imageUrl: medalImage,
    reward: null,
  },
  {
    id: "fotografen",
    title: "Fotografen",
    description: "Fullførte Fotografen.",
    category: "stop",
    source: "Fotografen",
    unlocked: true,
    imageUrl: medalImage,
    reward: null,
  },
  {
    id: "datasenteret",
    title: "Datasenteret",
    description: "Beseiret hovedbossen i Datasenteret.",
    category: "special",
    source: "Datasenteret",
    unlocked: true,
    imageUrl: medalImage,
    reward: "Spesialmedalje for å ha slått bossen",
  },
];

const regularMedals = computed(() =>
  medals.filter((medal) => medal.category !== "special")
);

const specialMedal = computed(() => 
  medals.find((medal) => medal.category === "special")
);
</script>

<template>
  <NuxtLink :to="routePaths.studentHome" class="returnButton">Tilbake til hjem</NuxtLink>
  <section class="medalsPage">
    <div class="achievementsSection">
      <h1 class="sectionTitle">Achievements</h1>
      <MedalGrid :medals="regularMedals" />
    </div>

    <div class="specialSection">
      <h1 class="sectionTitle">Special achievement</h1>
      <MedalElement class="specialMedal" :medal="specialMedal!" />
    </div>
  </section>
</template>

<style scoped>
.medalsPage {
  height: 100%;
  width: 100%;
  background: rgb(94, 37, 0);
  display: grid;
  grid-template-columns: 64% 34%;
  grid-template-rows: auto 1fr;
  gap: 2%;
  padding: var(--space-6);
}

.returnButton {
  grid-column: 1 / -1;
  justify-self: start;
  margin-bottom: 0.5rem;
  border: 1px solid rgba(255, 255, 255, 0.75);
  border-radius: 999px;
  background: rgba(0, 0, 0, 0.3);
  color: #fff;
  padding: 0.45rem 0.9rem;
  text-decoration: none;
  font-weight: 600;
}

.returnButton:hover {
  background: rgba(0, 0, 0, 0.5);
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
