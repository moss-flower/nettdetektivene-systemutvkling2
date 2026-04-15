<script setup lang="ts">
import StudentHomeMenuItem from '~/components/ui/StudentHomeMenuItem.vue';
import mapImage from "~/assets/images/Map.png";
import helpImage from "~/assets/images/help.png";
import profileImage from "~/assets/images/profile.png";
import leaderboardImage from "~/assets/images/Leaderboard.png";
import medalsImage from "~/assets/images/medals.png";
import mysteryImage from "~/assets/images/mystery.png";
import notebookImage from "~/assets/images/notebook.png";

type StudentHomeItem = {
  id: string;
  title: string;
  description: string;
  route: string;
  badge?: string;
  imagePath?: string;
  rotation?: number;
};

const items: StudentHomeItem[] = [
  {
    id: 'help',
    title: 'Hjelp',
    description: 'Få tips hvis du står fast eller lurer på hva du skal gjøre.',
    route: '',
    badge: '?',
    imagePath: helpImage,
    rotation: 4,
  },
  {
    id: 'avatar',
    title: 'Avatar',
    description: 'Se profilen din og etter hvert tilpass hvordan detektiven din ser ut.',
    route: '',
    badge: 'A',
    imagePath: profileImage,
    rotation: -4,
  },
  {
    id: 'leaderboard',
    title: 'Ledertavle',
    description: 'Se poeng, plassering og hvem som har samlet flest spor.',
    route: '',
    badge: '#',
    imagePath: leaderboardImage,
    rotation: -2,
  },
  {
    id: 'map',
    title: 'Kartet',
    description: 'Her foregår spillet. Trykk her for å gå til stoppesteder og oppgaver.',
    route: '/levels',
    badge: 'K',
    imagePath: mapImage,
    rotation: -1,
  },
  {
    id: 'mystery',
    title: 'Ukens mysterium',
    description: 'Et eget mysterium med ferske spor og en ny sak å utforske.',
    route: '',
    badge: '!',
    imagePath: mysteryImage,
    rotation: 5,
  },
  {
    id: 'medals',
    title: 'Medaljer',
    description: 'Se medaljene du har vunnet og hvilke du kan samle senere.',
    route: '',
    badge: '*',
    imagePath: medalsImage,
    rotation: 2,
  },
  {
    id: 'notebook',
    title: 'Notatblokk',
    description: 'Skriv ned spor, tanker og ting du vil huske til senere.',
    route: '',
    badge: 'N',
    imagePath: notebookImage,
    rotation: 3,
  }
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
    <header class="student-home__header">
      <p class="student-home__eyebrow">Detektivtavla</p>
      <h2>Hva vil du utforske i dag?</h2>
      <p class="student-home__intro">
        Trykk på tingene på tavla for å utforske spor, samle medaljer og følge veien videre i
        oppdraget. Kartet i midten er hovedveien din gjennom saken.
      </p>
    </header>

    <div class="student-home__board" aria-label="Elevens hjemskjerm">
      <StudentHomeMenuItem
        v-for="item in items"
        :key="item.id"
        :image-path="item.imagePath"
        :alt="item.title"
        :rotation="item.rotation"
        :onClick="() => handleClick(item)"
        :class="['student-home__item', `student-home__item--${item.id}`]"
        :title="item.description"
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

.student-home__header {
  display: grid;
  gap: 0.75rem;
  max-width: 54rem;
}

.student-home__eyebrow {
  color: var(--primary);
  font-size: 0.82rem;
  font-weight: 800;
  letter-spacing: 0.14em;
  text-transform: uppercase;
  margin: 0;
}

.student-home__header h2 {
  margin: 0;
  font-size: clamp(1.9rem, 4vw, 3rem);
  line-height: 1.05;
  color: var(--text);
}

.student-home__intro {
  max-width: 60ch;
  margin: 0;
  color: color-mix(in srgb, var(--text) 72%, #744d2b);
  line-height: 1.6;
}

.student-home__board {
  position: relative;
  min-height: clamp(32rem, 64vw, 44rem);
  aspect-ratio: 16 / 9;
  overflow: hidden;
  border: 0.45rem solid #a66f43;
  border-radius: 1.75rem;
  background:
    radial-gradient(circle at 20% 20%, rgb(255 232 198 / 0.16), transparent 20%),
    radial-gradient(circle at 80% 15%, rgb(255 240 222 / 0.12), transparent 18%),
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
  z-index: 9;
}

.student-home__item--leaderboard {
  top: 5%;
  left: 3%;
  width: 16%;
  z-index: 3;
}

.student-home__item--avatar {
  left: 2%;
  bottom: 17%;
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
  z-index: 1;
}

.student-home__item--mystery {
  top: 6%;
  right: 19%;
  width: 18%;
  z-index: 5;
}

.student-home__item--help {
  top: 6%;
  right: 4.5%;
  width: 11.5%;
  z-index: 6;
}

.student-home__item--notebook {
  right: 3.5%;
  bottom: 3.5%;
  width: 24%;
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
