<script setup lang="ts">
import StudentHomeMenuItem from '~/components/student/StudentHomeMenuItem.vue';
import mapImage from "~/assets/images/studentHome/Map.png";
import helpImage from "~/assets/images/studentHome/Help.png";
import profileImage from "~/assets/images/studentHome/Profile.png";
import leaderboardImage from "~/assets/images/studentHome/Leaderboard.png";
import medalsImage from "~/assets/images/studentHome/Medals.png";
import mysteryImage from "~/assets/images/studentHome/Mystery.png";
import notebookImage from "~/assets/images/studentHome/Notebook.png";
import settingsImage from "~/assets/images/studentHome/Settings.png";

type StudentHomeItem = {
  id: string;
  title: string;
  description: string;
  route: string;
  badge?: string;
  imagePath?: string;
  rotation?: number;
  tooltipPlacement?: "top" | "bottom";
};

const items: StudentHomeItem[] = [
  {
    id: 'help',
    title: 'Hjelp',
    description: 'Få tips hvis du står fast eller lurer på hva du skal gjøre.',
    route: '/student/help',
    badge: '?',
    imagePath: helpImage,
    rotation: 4,
    tooltipPlacement: "bottom",
  },
  {
    id: 'avatar',
    title: 'Avatar',
    description: 'Se profilen din og tilpass hvordan detektiven din ser ut.',
    route: '/student/profile',
    badge: 'A',
    imagePath: profileImage,
    rotation: -4,
    tooltipPlacement: "top",
  },
  {
    id: 'leaderboard',
    title: 'Ledertavle',
    description: 'Se poeng, plassering og hvem som har samlet flest spor.',
    route: '',
    badge: '#',
    imagePath: leaderboardImage,
    rotation: -2,
    tooltipPlacement: "bottom",
  },
  {
    id: 'map',
    title: 'Kartet',
    description: 'Her foregår spillet. Trykk her for å gå til stoppesteder og oppgaver.',
    route: '/levels',
    badge: 'K',
    imagePath: mapImage,
    rotation: -1,
    tooltipPlacement: "bottom",
  },
  {
    id: 'mystery',
    title: 'Ukens mysterium',
    description: 'Et eget mysterium med ferske spor og en ny sak å utforske.',
    route: '',
    badge: '!',
    imagePath: mysteryImage,
    rotation: 1,
    tooltipPlacement: "bottom",
  },
  {
    id: 'medals',
    title: 'Medaljer',
    description: 'Se medaljene du har vunnet og hvilke du kan samle senere.',
    route: '/student/achievements',
    badge: '*',
    imagePath: medalsImage,
    rotation: 2,
    tooltipPlacement: "top",
  },
  {
    id: 'notebook',
    title: 'Notatblokk',
    description: 'Skriv ned spor, tanker og ting du vil huske til senere.',
    route: '',
    badge: 'N',
    imagePath: notebookImage,
    rotation: 3,
    tooltipPlacement: "top",
  }, 
  {
    id: 'settings',
    title: 'Innstillinger',
    description: 'Endre innstillinger for spillet.',
    route: '',
    badge: 'S',
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
  min-height: 100%;
  display: grid;
  gap: clamp(1.5rem, 2vw, 2.25rem);
  padding: clamp(1rem, 2vw, 2rem);
}

.student-home__board {
  position: relative;
  min-height: clamp(32rem, 64vw, 44rem);
  aspect-ratio: 16 / 9;
  overflow: hidden;
  border: 0.45rem solid #a66f43;
  border-radius: 1.75rem;
  background:
    linear-gradient(180deg, #b57d4d 0%, #a86f42 100%);
  box-shadow:
    0 1.4rem 3rem rgb(74 39 15 / 0.18),
    inset 0 0 0 0.12rem rgb(111 66 33 / 0.22);
}

.student-home__board::before {
  content: "";
  position: absolute;
  inset: 0;
  background:
    linear-gradient(90deg, rgb(255 255 255 / 0.05) 0, transparent 35%),
    repeating-linear-gradient(
      135deg,
      rgb(123 73 34 / 0.03) 0,
      rgb(123 73 34 / 0.03) 7px,
      transparent 7px,
      transparent 20px
    );
  pointer-events: none;
}

.student-home__board::after {
  content: "";
  position: absolute;
  inset: 1rem;
  border: 0.18rem solid rgb(125 79 42 / 0.52);
  border-radius: 1.1rem;
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
  width: 37%;
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
    gap: 1rem;
    padding: 1.25rem;
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
    padding: 1rem;
  }
}
</style>
