<script setup lang="ts">
import type { Level, LevelState } from "~/composables/types/local";
import placeholderImage from "~/assets/images/placeholder.jpeg";

//Props, temporary hardcoded content
const props = withDefaults(
  defineProps<{
    levels: Level[];
  }>(),

  {
    levels: () => [
      {
        name: "Nyhetskvartalet",
        description:
          "Lær å skille mellom ekte nyheter, clickbait og emosjonelt språk.",
        image: placeholderImage,
        locked: false,
        state: {
          completed: false,
          stars: 0,
        },
      },
      {
        name: "Fotografen",
        description:
          "Lær å kjenne igjen ekte, KI-genererte og manipulerte bilder.",
        image: placeholderImage,
        locked: true,
        state: {
          completed: false,
          stars: 0,
        },
      },
      {
        name: "Postkontoret",
        description:
          "Øv på å avsløre falske e-poster, mistenkelige lenker og rare avsendere.",
        image: placeholderImage,
        locked: true,
        state: {
          completed: false,
          stars: 0,
        },
      },
      {
        name: "Passordbanken",
        description:
          "Lær hva som gjør et passord sterkt, trygt og lett å huske.",
        image: placeholderImage,
        locked: true,
        state: {
          completed: false,
          stars: 0,
        },
      },
      {
        name: "Markedsplassen",
        description:
          "Lær å vurdere om nettsider og nettbutikker er trygge eller mistenkelige.",
        image: placeholderImage,
        locked: true,
        state: {
          completed: false,
          stars: 0,
        },
      },
      {
        name: "Den sosiale møteplassen",
        description:
          "Lær å tenke før du deler, og å oppdage manipulering og gruppepress.",
        image: placeholderImage,
        locked: true,
        state: {
          completed: false,
          stars: 0,
        },
      },
      {
        name: "Datasenteret",
        description:
          "Finalebanen der du bruker det du har lært for å bekjempe datatyven.",
        image: placeholderImage,
        locked: true,
        state: {
          completed: false,
          stars: 0,
        },
      },
    ],
  },
);

//Dynamically creates empty or full stars based on given filled-star number
const createStarString = (number: number): string => {
  if (number > 3) {
    throw new Error("Stars must be below three");
  }

  return "★".repeat(number) + "☆".repeat(3 - number);
};
</script>

<template>
  <div class="levelGrid">
    <div
      v-for="level in props.levels"
      :key="level.name"
      class="levelCard"
      :class="{ lockedCard: level.locked }"
    >
      <img :src="level.image" alt="" class="levelImage">

      <h4 class="levelName">{{ level.name }}</h4>
      <p class="stars">{{ createStarString(level.state.stars) }}</p>
      <p v-if="level.locked" class="lock">🔒</p>
    </div>
  </div>
</template>

<style scoped>
.levelGrid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 1rem;
}

.levelCard {
  position: relative;
  min-height: 220px;
  overflow: hidden;
  border-radius: 1rem;
  padding: 1rem;
  display: flex;
  flex-direction: column;
  justify-content: end;
  border: 1px solid var(--border);
  box-shadow: var(--shadow);
}

.levelImage {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  z-index: 0;
}

.levelCard::after {
  content: "";
  position: absolute;
  inset: 0;
  z-index: 1;
  background: linear-gradient(
    to top,
    rgba(0, 0, 0, 0.75) 0%,
    rgba(0, 0, 0, 0.25) 55%,
    rgba(0, 0, 0, 0.05) 100%
  );
}

.levelName {
  position: relative;
  z-index: 2;
  font-size: 2rem;
  color: white;
  margin: 0;
}

.stars {
  position: relative;
  z-index: 2;
  font-size: 1rem;
  color: white;
  margin: 0.25rem 0 0 0;
}

.lock {
  position: absolute;
  top: 0.75rem;
  right: 0.75rem;
  z-index: 3;
  font-size: 3rem;
}

/* Locked state */
.lockedCard {
  color: #d1d5db;
}

.lockedCard .levelImage {
  filter: grayscale(100%) brightness(0.65);
}

.lockedCard .levelName,
.lockedCard .stars {
  color: #d1d5db;
}

@media (max-width: 1000px) {
  .levelGrid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 750px) {
  .levelGrid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 500px) {
  .levelGrid {
    grid-template-columns: 1fr;
  }
}
</style>