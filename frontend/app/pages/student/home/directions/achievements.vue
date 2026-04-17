<script setup lang="ts">
import MedalGrid from "~/components/medals/MedalGrid.vue";
import MedalElement from "~/components/medals/MedalElement.vue";
import type { Medal } from "~/composables/types/MedalTypes";

const medals: Medal[] = [
  {
    id: "passordbanken",
    title: "Passordbanken",
    description: "Fullførte Passordbanken.",
    category: "stop",
    source: "Passordbanken",
    unlocked: true,
    imageUrl: "/images/medals/gold-medal.png",
    reward: null,
  },
  {
    id: "sosiale-moteplassen",
    title: "Sosiale møteplassen",
    description: "Fullførte Sosiale møteplassen.",
    category: "stop",
    source: "Sosiale møteplassen",
    unlocked: true,
    imageUrl: "/images/medals/gold-medal.png",
    reward: null,
  },
  {
    id: "nyhetskvartalet",
    title: "Nyhetskvartalet",
    description: "Fullførte Nyhetskvartalet.",
    category: "stop",
    source: "Nyhetskvartalet",
    unlocked: true,
    imageUrl: "/images/medals/gold-medal.png",
    reward: null,
  },
  {
    id: "markedsplassen",
    title: "Markedsplassen",
    description: "Fullførte Markedsplassen.",
    category: "stop",
    source: "Markedsplassen",
    unlocked: true,
    imageUrl: "/images/medals/gold-medal.png",
    reward: null,
  },
  {
    id: "postkontoret",
    title: "Postkontoret",
    description: "Fullførte Postkontoret.",
    category: "stop",
    source: "Postkontoret",
    unlocked: true,
    imageUrl: "/images/medals/gold-medal.png",
    reward: null,
  },
  {
    id: "fotografen",
    title: "Fotografen",
    description: "Fullførte Fotografen.",
    category: "stop",
    source: "Fotografen",
    unlocked: true,
    imageUrl: "/images/medals/gold-medal.png",
    reward: null,
  },
  {
    id: "datasenteret",
    title: "Datasenteret",
    description: "Beseiret hovedbossen i Datasenteret.",
    category: "special",
    source: "Datasenteret",
    unlocked: true,
    imageUrl: "/images/medals/silver-medal.png",
    reward: "Spesialmedalje for å ha slått bossen",
  },
];

const regularMedals = computed(() =>
  medals.filter((medal) => medal.category !== "special")
);

const specialMedal = computed(() =>
  medals.find((medal) => medal.category === "special") ?? null
);
</script>

<template>
  <section class="medalsPage">
    <p class="pageLabel">Medaljer</p>

    <div class="medalsBoard">
      <div class="achievementsSection">
        <h1 class="sectionTitle">Achievements</h1>

        <MedalGrid :medals="regularMedals" />
      </div>

      <div class="specialSection">
        <h1 class="sectionTitle">Special achievement</h1>

        <div v-if="specialMedal" class="specialMedalWrapper">
          <MedalElement class="specialMedal" :medal="specialMedal" />
        </div>
      </div>
    </div>
  </section>
</template>

<style scoped>
.medalsPage {
  min-height: 100%;
  background: #1d1d1f;
  padding: 1.5rem 2rem 2rem;
  box-sizing: border-box;
}

.pageLabel {
  margin: 0 0 0.75rem;
  color: #8e8e93;
  font-size: 0.85rem;
}

.medalsBoard {
  background: #ad7a50;
  padding: 1.5rem 1.5rem 2rem;
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 2rem;
  min-height: 42rem;
  box-sizing: border-box;
}

.achievementsSection,
.specialSection {
  display: flex;
  flex-direction: column;
}

.sectionTitle {
  margin: 0 0 1.5rem;
  font-size: 3.5rem;
  font-weight: 400;
  color: white;
  font-family: Georgia, "Times New Roman", serif;
  text-align: center;
}

.specialMedalWrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  flex: 1;
}

@media (max-width: 1000px) {
  .medalsBoard {
    grid-template-columns: 1fr;
  }

  .specialSection {
    margin-top: 2rem;
  }

  .specialMedal {
    transform: scale(1.2);
  }

  .sectionTitle {
    font-size: 2.6rem;
  }
}
</style>