<script setup lang="ts">
import StudentHomeMenuItem from '~/components/student/StudentHomeMenuItem.vue';
import { routePaths } from '~/composables/utils/routePaths';
import { useAuth } from '~/composables/useAuth';
import mapImage from "~/assets/ui-icons/Map-big.png";
import helpImage from "~/assets/images/studentHome/help.png";
import profileImage from "~/assets/ui-icons/ID-card.png";
import leaderboardImage from "~/assets/ui-icons/leaderboard-sign.png";
import leaderboardNamePlate from "~/assets/ui-icons/leaderboard-name-plate.png";
import medalsImage from "~/assets/ui-icons/Medal.png";
import mysteryImage from "~/assets/ui-icons/newspaper.png";
import notebookImage from "~/assets/ui-icons/Notepad.png";
import settingsImage from "~/assets/ui-icons/Radio.png";

type StudentHomeItem = {
  id: string;
  title: string;
  description: string;
  route: string;
  imagePath?: string;
  imagePaths?: string[];
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
    imagePaths: [
      leaderboardImage,
      leaderboardNamePlate,
      leaderboardNamePlate,
      leaderboardNamePlate,
    ],
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
const { logout } = useAuth();
const isLoggingOut = ref(false);

function handleClick(item: StudentHomeItem) {
  if (!item.route) {
    return;
  }

  router.push(item.route);
}

async function handleLogout() {
  if (isLoggingOut.value) {
    return;
  }

  isLoggingOut.value = true;

  try {
    await logout();
    await navigateTo(routePaths.home);
  } finally {
    isLoggingOut.value = false;
  }
}
</script>

<template>
  <section class="student-home">
    <button class="student-home__logout" type="button" :disabled="isLoggingOut" @click="handleLogout">
      {{ isLoggingOut ? 'Logger ut...' : 'Logg ut' }}
    </button>

    <div class="student-home__board" aria-label="Elevens hjemskjerm">
      <StudentHomeMenuItem
        v-for="item in items"
        :key="item.id"
        :image-path="item.imagePath"
        :image-paths="item.imagePaths"
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
  position: relative;
  width: 100%;
  height: 100%;
  overflow: hidden;
  box-sizing: border-box;
  position: relative;
}

.student-home__logout {
  position: fixed;
  top: var(--space-4);
  right: var(--space-4);
  z-index: 30;
  border: var(--border-width-thin) solid var(--palette-gray-300);
  border-radius: 999px;
  padding: 0.75rem 1rem;
  background: var(--palette-white);
  color: var(--palette-navy-900);
  font: inherit;
  font-weight: 700;
  cursor: pointer;
  box-shadow: 0 var(--space-2) var(--space-4) var(--board-shadow);
  transition: transform 0.15s ease, opacity 0.15s ease;
}

.student-home__logout:hover:not(:disabled) {
  transform: translateY(-1px);
}

.student-home__logout:disabled {
  cursor: wait;
  opacity: 0.65;
}

.student-home__board {
  position: relative;
  width: 100%;
  height: 100%;
}

.student-home__item {
  position: absolute;
  display: block;
}

.student-home__item:hover,
.student-home__item:focus-within {
  z-index: 100;
}

.student-home__item--leaderboard {
  top: 5%;
  left: 3%;
  width: 16%;
  z-index: 3;
}

.student-home__item--avatar {
  left: 3%;
  bottom: 30%;
  width: 21%;
  z-index: 4;
}

.student-home__item--medals {
  left: 5%;
  bottom: 4.5%;
  width: 10%;
  z-index: 2;
}

.student-home__item--map {
  top: 20%;
  left: 20%;
  width: 50%;
  z-index: 3;
}

.student-home__item--mystery {
  top: 2%;
  right: 15%;
  width: 20%;
  z-index: 2;
}

.student-home__item--help {
  top: 1%;
  right: 1%;
  width: 16%;
  z-index: 6;
}

.student-home__item--notebook {
  right: 0.5%;
  bottom: 3.5%;
  width: 30%;
  z-index: 4;
}

.student-home__item--settings {
  right: 40%;
  bottom: 1%;
  width: 20%;
  z-index: 1;
}

@media (max-width: 980px) {
  .student-home {
    width: 100%;
    height: 100%;
  }

  .student-home__board {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: var(--space-4);
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

</style>
