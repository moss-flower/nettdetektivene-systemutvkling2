<script setup lang="ts">
import StudentHomeMenuItem from '~/components/student/StudentHomeMenuItem.vue';
import { routePaths } from '~/composables/utils/routePaths';
import mapImage from "~/assets/images/studentHome/map.png";
import helpImage from "~/assets/images/studentHome/help.png";
import profileImage from "~/assets/images/studentHome/profile.png";
import leaderboardImage from "~/assets/images/studentHome/leaderboard.png";
import medalsImage from "~/assets/images/studentHome/medals.png";
import mysteryImage from "~/assets/images/studentHome/mystery.png";
import notebookImage from "~/assets/images/studentHome/notebook.png";
import settingsImage from "~/assets/images/studentHome/settings.png";

definePageMeta( { layout: false })

type StudentHomeItem = {
  id: string;
  title: string;
  description: string;
  route: string;
  imagePath?: string;
  rotation?: number;
  tooltipPlacement?: "top" | "bottom";
};

const items: StudentHomeItem[] = [
  {
    id: 'help',
    title: 'Hjelp',
    description: 'Få tips hvis du står fast eller lurer på hva du skal gjøre.',
    route: routePaths.studentHelp,
    imagePath: helpImage,
    rotation: 4,
    tooltipPlacement: "bottom",
  },
  {
    id: 'avatar',
    title: 'Avatar',
    description: 'Se profilen din og tilpass hvordan detektiven din ser ut.',
    route: routePaths.studentProfile,
    imagePath: profileImage,
    rotation: -4,
    tooltipPlacement: "top",
  },
  {
    id: 'leaderboard',
    title: 'Ledertavle',
    description: 'Se poeng, plassering og hvem som har samlet flest spor.',
    route: routePaths.studentLeaderboard,
    imagePath: leaderboardImage,
    rotation: -2,
    tooltipPlacement: "bottom",
  },
  {
    id: 'map',
    title: 'Kartet',
    description: 'Her foregår spillet. Trykk her for å gå til stoppesteder og oppgaver.',
    route: routePaths.studentLevels,
    imagePath: mapImage,
    rotation: -1,
    tooltipPlacement: "bottom",
  },
  {
    id: 'mystery',
    title: 'Ukens mysterium',
    description: 'Et eget mysterium med ferske spor og en ny sak å utforske.',
    route: routePaths.studentMystery,
    imagePath: mysteryImage,
    rotation: 1,
    tooltipPlacement: "bottom",
  },
  {
    id: 'medals',
    title: 'Medaljer',
    description: 'Se medaljene du har vunnet og hvilke du kan samle senere.',
    route: routePaths.studentAchievements,
    imagePath: medalsImage,
    rotation: 2,
    tooltipPlacement: "top",
  },
  {
    id: 'notebook',
    title: 'Notatblokk',
    description: 'Skriv ned spor, tanker og ting du vil huske til senere.',
    route: routePaths.studentNotes,
    imagePath: notebookImage,
    rotation: 3,
    tooltipPlacement: "top",
  }, 
  {
    id: 'settings',
    title: 'Innstillinger',
    description: 'Endre innstillinger for spillet.',
    route: routePaths.studentSettings,
    imagePath: settingsImage,
    rotation: 1,
    tooltipPlacement: "bottom",
  },
];

const router = useRouter();

function handleClick(item: StudentHomeItem) {
  if (!item.route) {
    return;
  }

  router.push(item.route);
}
</script>

<template>
  <section class="student-home">
    <div class="student-home__board" aria-label="Elevens hjemskjerm">
      <StudentHomeMenuItem
        v-for="item in items"
        :key="item.id"
        :image-path="item.imagePath"
        :alt="item.title"
        :tooltip-text="item.description"
        :tooltip-placement="item.tooltipPlacement"
        :rotation="item.rotation"
        :onClick="() => handleClick(item)"
        :class="['student-home__item', `student-home__item--${item.id}`]"
        />
    </div>
  </section>
</template>

<style scoped>
.student-home {
  --board-border: var(--palette-cork-470);
  --board-gradient-top: var(--palette-cork-450);
  --board-gradient-bottom: var(--palette-cork-470);
  --board-inner-frame: var(--palette-overlay-cork-strong);
  --board-hatch: var(--palette-overlay-cork-soft);
  --board-highlight: var(--palette-overlay-light-soft);
  --board-inset-ring: var(--palette-overlay-cork-medium);
  --board-shadow: var(--palette-overlay-cork-shadow);

  min-height: 100dvh;
  display: grid;
  place-items: center;
  /* fluid sizing (deliberate exception to token system),
     scales cleanly across devices */
  gap: clamp(1.5rem, 2vw, 2.25rem);
  padding: var(--layout-page-padding);
  overflow: hidden;
  box-sizing: border-box;
}

.student-home__board {
  position: relative;
  width: 100%;
  /* fluid aspect-constrained max-width (see note on .student-home) */
  max-width: calc((100dvh - 2 * clamp(1rem, 2vw, 2rem)) * 16 / 9);
  aspect-ratio: 16 / 9;
  overflow: hidden;
  border: var(--border-width-chunky) solid var(--board-border);
  border-radius: var(--radius-4);
  background:
    linear-gradient(
      180deg,
      var(--board-gradient-top) 0%,
      var(--board-gradient-bottom) 100%
    );
  box-shadow:
    0 var(--space-5) var(--space-8) var(--board-shadow),
    inset 0 0 0 var(--border-width-thin) var(--board-inset-ring);
}

.student-home__board::before {
  content: "";
  position: absolute;
  inset: 0;
  /* decorative left-edge gloss + diagonal cork hatch */
  background:
    linear-gradient(90deg, var(--board-highlight) 0, transparent 35%),
    repeating-linear-gradient(
      135deg,
      var(--board-hatch) 0,
      var(--board-hatch) 7px,
      transparent 7px,
      transparent 20px
    );
  pointer-events: none;
}

.student-home__board::after {
  content: "";
  position: absolute;
  inset: var(--space-4);
  border: var(--border-width-thick) solid var(--board-inner-frame);
  border-radius: var(--radius-3);
  pointer-events: none;
}

.student-home__item {
  position: absolute;
  display: block;
}

.student-home__item:hover {
  z-index: 100;
  transform: scale(1.15);
}

.student-home__item--leaderboard {
  top: 5%;
  left: 3%;
  width: 16%;
  z-index: 3;
}

.student-home__item--avatar {
  left: 3%;
  bottom: 19%;
  width: 21%;
  z-index: 4;
}

.student-home__item--medals {
  left: 6%;
  bottom: 4.5%;
  width: 14%;
  z-index: 2;
}

.student-home__item--map {
  top: 19%;
  left: 29%;
  width: 39%;
  z-index: 8;
}

.student-home__item--mystery {
  top: 6%;
  right: 19%;
  width: 22%;
  z-index: 5;
}

.student-home__item--help {
  top: 5%;
  right: 5%;
  width: 16%;
  z-index: 6;
}

.student-home__item--notebook {
  right: 3.5%;
  bottom: 3.5%;
  width: 15%;
  z-index: 4;
}

.student-home__item--settings {
  right: 3.5%;
  bottom: 35%;
  width: 20%;
  z-index: 4;
}

@media (max-width: 980px) {
  .student-home__board {
    min-height: auto;
    aspect-ratio: auto;
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: var(--space-4);
    padding: var(--space-4);
  }

  .student-home__item {
    position: relative;
    inset: auto;
    width: 100%;
  }

  .student-home__item--map {
    grid-column: 1 / -1;
  }
}

@media (max-width: 560px) {
  .student-home__board {
    grid-template-columns: 1fr;
    padding: var(--space-4);
  }
}
</style>
