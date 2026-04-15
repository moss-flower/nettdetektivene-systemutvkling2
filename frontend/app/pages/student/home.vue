<script setup lang="ts">
type StudentHomeItem = {
  id: string;
  title: string;
  description: string;
  route: string;
  badge?: string;
};

const items: StudentHomeItem[] = [
  {
    id: 'hjelp',
    title: 'Hjelp',
    description: 'Få tips hvis du står fast eller lurer på hva du skal gjøre.',
    route: routePaths.studentHelp,
    badge: '?'
  },
  {
    id: 'avatar',
    title: 'Avatar',
    description: 'Se profilen din og etter hvert tilpass hvordan detektiven din ser ut.',
    route: routePaths.studentProfile,
    badge: 'A'
  },
  {
    id: 'ledertavle',
    title: 'Ledertavle',
    description: 'Se poeng, plassering og hvem som har samlet flest spor.',
    route: routePathsWithString(routePaths.notYetImplemented, `?backPath=${routePaths.studentHome}`),
    badge: '#'
  },
  {
    id: 'kart',
    title: 'Kartet',
    description: 'Her foregår spillet. Trykk her for å gå til stoppesteder og oppgaver.',
    route: routePaths.levels,
    badge: 'K'
  },
  {
    id: 'mysterium',
    title: 'Ukens mysterium',
    description: 'Et eget mysterium med ferske spor og en ny sak å utforske.',
    route: routePathsWithString(routePaths.notYetImplemented, `?backPath=${routePaths.studentHome}`),
    badge: '!'
  },
  {
    id: 'medaljer',
    title: 'Medaljer',
    description: 'Se medaljene du har vunnet og hvilke du kan samle senere.',
    route: routePaths.studentAchievements,
    badge: '*'
  },
  {
    id: 'notatblokk',
    title: 'Notatblokk',
    description: 'Skriv ned spor, tanker og ting du vil huske til senere.',
    route: routePathsWithString(routePaths.notYetImplemented, `?backPath=${routePaths.studentHome}`),
    badge: 'N'
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
      <p class="student-home__eyebrow">Elevens hjemskjerm</p>
      <h2>En tittel her kanskje</h2>
      <p class="student-home__intro">
        Noe beskrivelse elns. her, kanskje en liten miniveiledning
      </p>
    </header>

    <div class="student-home__board">
      <button
        v-for="item in items"
        :key="item.id"
        type="button"
        class="board-item"
        :class="[
          `board-item--${item.id}`,
          { 'board-item--map': item.id === 'kart' }
        ]"
        :aria-label="`${item.title}: ${item.description}`"
        @click="handleClick(item)"
      >
        <span class="board-item__badge">{{ item.badge }}</span>
        <span class="board-item__title">{{ item.title }}</span>
        <span class="board-item__tooltip">{{ item.description }}</span>
      </button>
    </div>
  </section>
</template>

<style scoped>
.student-home {
  min-height: 100%;
  display: grid;
  gap: var(--space-3);
  padding: var(--space-3);
}

.student-home__header {
  display: grid;
  gap: 0.75rem;
}

.student-home__eyebrow {
  color: var(--primary);
  font-size: 0.9rem;
  font-weight: 800;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.student-home__intro {
  max-width: 65ch;
  color: var(--muted);
  line-height: 1.5;
}

.student-home__board {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  grid-template-areas:
    'hjelp avatar ledertavle mysterium'
    'medaljer kart kart notatblokk';
  gap: 1rem;
}

.board-item {
  position: relative;
  display: grid;
  gap: 0.5rem;
  min-height: 9rem;
  padding: 1rem;
  border: 2px solid var(--border);
  border-radius: var(--radius);
  background: var(--surface);
  color: var(--text);
  text-align: left;
  transition:
    transform var(--transition),
    border-color var(--transition),
    background-color var(--transition);
}

.board-item:hover,
.board-item:focus-visible {
  transform: translateY(-2px);
  border-color: var(--primary);
  background: var(--surface);
  outline: none;
}

.board-item__badge {
  width: 2.5rem;
  height: 2.5rem;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 999px;
  background: var(--bg);
  color: var(--primary);
  font-weight: 800;
}

.board-item__title {
  font-size: 1.15rem;
  font-weight: 800;
}

.board-item__tooltip {
  position: absolute;
  left: 1rem;
  right: 1rem;
  bottom: calc(100% + 0.5rem);
  z-index: 1;
  padding: 0.65rem 0.8rem;
  border: 1px solid var(--border);
  border-radius: var(--radius);
  background: var(--bg);
  color: var(--text);
  line-height: 1.4;
  opacity: 0;
  pointer-events: none;
  transform: translateY(4px);
  transition:
    opacity var(--transition),
    transform var(--transition);
}

.board-item:hover .board-item__tooltip,
.board-item:focus-visible .board-item__tooltip {
  opacity: 1;
  transform: translateY(0);
}

.board-item--hjelp {
  grid-area: hjelp;
}

.board-item--avatar {
  grid-area: avatar;
}

.board-item--ledertavle {
  grid-area: ledertavle;
}

.board-item--mysterium {
  grid-area: mysterium;
}

.board-item--medaljer {
  grid-area: medaljer;
}

.board-item--notatblokk {
  grid-area: notatblokk;
}

.board-item--map {
  grid-area: kart;
  min-height: 16rem;
  align-content: center;
  justify-items: center;
  text-align: center;
  border-color: var(--primary);
  background: var(--surface);
}

.board-item--map .board-item__badge {
  width: 4rem;
  height: 4rem;
  font-size: 1.2rem;
}

.board-item--map .board-item__title {
  font-size: 1.7rem;
}

@media (max-width: 900px) {
  .student-home__board {
    grid-template-columns: repeat(2, minmax(0, 1fr));
    grid-template-areas:
      'kart kart'
      'hjelp avatar'
      'ledertavle mysterium'
      'medaljer notatblokk';
  }
}

@media (max-width: 560px) {
  .student-home {
    padding: var(--space-2);
  }

  .student-home__board {
    grid-template-columns: 1fr;
    grid-template-areas:
      'kart'
      'hjelp'
      'avatar'
      'ledertavle'
      'mysterium'
      'medaljer'
      'notatblokk';
  }

  .board-item,
  .board-item--map {
    min-height: 10rem;
  }
}
</style>
