<script setup lang="ts">
import { routePaths, routePathsWithString } from '~/composables/utils/routePaths';
import { useAuth } from '~/composables/useAuth';
import { toClassroomSummaries } from '~/composables/useTeacherDashboard';

const { teacher } = useClassrooms();

const { data: ownedClassrooms, pending: classroomsPending } = await useAsyncData(
  'teacher-owned-classrooms',
  () => teacher.getOwnedClassrooms().catch(() => []),
);

const classroomSummaries = computed(() => toClassroomSummaries(ownedClassrooms.value ?? []));
const { logout } = useAuth();
const isLoggingOut = ref(false);

const handleLogout = async () => {
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
};
</script>

<template>
  <section class="overviewPage">
    <header class="overviewHeader">
      <div class="overviewHeader__content">
        <p class="eyebrow">Læreroversikt</p>
        <h1>Hjemskjerm</h1>
        <p class="subtitle">Administrer klasserom og følg aktivitet.</p>
      </div>
      <button class="logoutButton" type="button" :disabled="isLoggingOut" @click="handleLogout">
        {{ isLoggingOut ? 'Logger ut...' : 'Logg ut' }}
      </button>
    </header>

	<div class="dashboardGrid">
	  <aside class="panel classesPanel">
        <h2>Dine klasserom</h2>
        <NuxtLink class="buttonLink createClassroomButton" :to="routePaths.teacherClassroomCreate">
          + Lag klasserom
        </NuxtLink>
        <NuxtLink class="buttonLink joinClassroomButton" :to="routePaths.join">
          Bli med i klasserom
        </NuxtLink>

        <div class="classroomCards">
          <p v-if="classroomsPending" class="panelMessage">Laster klasserom...</p>
          <p v-else-if="classroomSummaries.length === 0" class="panelMessage">Du har ingen klasserom ennå.</p>
          <NuxtLink
            v-for="(classroom, index) in classroomSummaries"
            :key="classroom.id"
            class="classroomCard"
            :class="`classroomCard--${(index % 3) + 1}`"
            :to="routePathsWithString(routePaths.teacherClassroomOverview, `?classroomId=${classroom.id}`)"
          >
            <span class="classroomName">{{ classroom.name }}</span>
            <span class="classScore">{{ classroom.studentCount }} elever</span>
          </NuxtLink>
		  </div>
		</aside>

		<div class="topActionRow">
		  <NuxtLink class="panel settingsButton" :to="routePaths.teacherSettings">
			<img src="/images/placeholders/settings.svg" alt="Innstillinger ikon" class="settingsIcon">
			<span class="settingsButton__label">Innstillinger</span>
		  </NuxtLink>

		  <NuxtLink class="panel settingsButton settingsButton--notifications" :to="routePaths.teacherNotifications">
			<img src="/images/placeholders/notifications.svg" alt="Varslinger ikon" class="settingsIcon">
			<span class="settingsButton__label">Varslinger</span>
			<span class="notificationBadge">1</span>
		  </NuxtLink>
		</div>

      <article class="panel panel--highlight managementPanel">
        <h2>Lag og rediger</h2>
        <p>Lag og/eller rediger klasserom og ressurser fra samme sted.</p>

        <div class="factoryActions factoryActions--grid">
          <NuxtLink class="buttonLink buttonLink--secondary" :to="routePaths.teacherFactoryStation">
            Stasjoner
          </NuxtLink>
          <NuxtLink class="buttonLink buttonLink--secondary" :to="routePaths.teacherFactoryExercise">
            Øvelser
          </NuxtLink>
        </div>
      </article>

      <article class="panel mysteryPanel">
        <h2>Ukens mysterium</h2>
        <button type="button" class="buttonLink buttonLink--secondary mysteryButton">
          Placeholder
        </button>
      </article>
    </div>
  </section>
</template>

<style scoped>
.overviewPage {
  width: 100%;
  max-width: 1100px;
  margin: 0 auto;
  padding: var(--space-4);
}

.overviewHeader {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: var(--space-4);
  margin-bottom: var(--space-4);
}

.overviewHeader__content {
  min-width: 0;
}

.eyebrow {
  color: var(--primary);
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  margin-bottom: 0.5rem;
}

h1 {
  margin: 0 0 0.5rem 0;
  color: var(--text);
}

.subtitle {
  margin: 0;
  color: var(--muted);
}

.logoutButton {
  flex: 0 0 auto;
  border: 1px solid var(--border);
  border-radius: 999px;
  padding: 0.75rem 1rem;
  background: transparent;
  color: var(--text);
  font: inherit;
  font-weight: 700;
  cursor: pointer;
  transition: transform 0.15s ease, border-color 0.15s ease, color 0.15s ease;
}

.logoutButton:hover:not(:disabled) {
  transform: translateY(-1px);
  border-color: var(--primary);
  color: var(--primary);
}

.logoutButton:disabled {
  cursor: wait;
  opacity: 0.65;
}

.dashboardGrid {
  display: grid;
  gap: var(--space-4);
  grid-template-columns: minmax(260px, 320px) minmax(0, 1fr);
  align-items: start;
}

.panel {
  border: 1px solid var(--border);
  border-radius: var(--radius);
  background: var(--surface);
  padding: var(--space-4);
}

.topActionRow {
  grid-column: 2;
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: var(--space-4);
}

.settingsButton {
  position: relative;
  display: grid;
  justify-items: start;
  gap: 1rem;
  color: inherit;
  text-decoration: none;
  transition: transform 0.15s ease, border-color 0.15s ease;
}

.settingsButton:hover {
  transform: translateY(-1px);
  border-color: var(--primary);
}

.settingsIcon {
  width: 3.5rem;
  height: 3.5rem;
  border-radius: 0.75rem;
}

.settingsButton__label {
  font-size: 1.05rem;
  font-weight: 700;
  color: var(--text);
}

.notificationBadge {
  position: absolute;
  top: 1rem;
  right: 1rem;
  display: grid;
  place-items: center;
  width: 2rem;
  height: 2rem;
  border-radius: 999px;
  background: #ef4444;
  color: white;
  font-size: 0.95rem;
  font-weight: 700;
}

.classesPanel {
  display: grid;
  gap: 1rem;
}

h2 {
  margin: 0 0 0.75rem 0;
}

.classroomCards {
  display: grid;
  gap: 0.75rem;
}

.panelMessage {
  margin: 0;
  color: var(--muted);
}

.classroomCard {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  gap: 0.75rem;
  min-height: 96px;
  padding: 1rem;
  border: 1px solid var(--border);
  border-radius: var(--radius);
  text-decoration: none;
  transition: transform 0.15s ease, border-color 0.15s ease;
}

.classroomCard--1 {
  background: rgba(101, 240, 175, 0.08);
}

.classroomCard--2 {
  background: rgba(96, 165, 250, 0.1);
}

.classroomCard--3 {
  background: rgba(244, 114, 182, 0.1);
}

.classroomCard:hover {
  transform: translateY(-1px);
  border-color: var(--primary);
}

.classroomName {
  font-weight: 700;
  color: var(--primary);
  font-size: 1.05rem;
}

.classScore {
  color: var(--muted);
  font-size: 0.95rem;
  white-space: nowrap;
}

.panel--highlight {
  background: var(--surface);
}

.managementPanel {
  grid-column: 2;
}

.mysteryPanel {
  grid-column: 2;
}

.factoryActions {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
  margin-top: 0.9rem;
}

.factoryActions--grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.buttonLink {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: 4.5rem;
  padding: 1rem 1.25rem;
  border: 1px solid var(--primary);
  border-radius: var(--radius);
  background: var(--primary);
  color: var(--bg);
  font-size: 1.05rem;
  font-weight: 700;
  text-decoration: none;
}

.buttonLink--secondary {
  background: transparent;
  color: var(--primary);
}

.joinClassroomButton {
  width: 100%;
  min-height: 3.5rem;
}

.createClassroomButton {
  width: 100%;
  min-height: 3.5rem;
}

.mysteryButton {
  margin-top: 0.9rem;
  width: 100%;
}

@media (max-width: 900px) {
  .dashboardGrid {
    grid-template-columns: 1fr;
  }

  .topActionRow {
    grid-column: auto;
  }

  .managementPanel {
    grid-column: auto;
  }

  .mysteryPanel {
    grid-column: auto;
  }
}

@media (max-width: 640px) {
  .overviewHeader {
    display: grid;
  }

  .logoutButton {
    justify-self: flex-start;
  }

  .topActionRow,
  .factoryActions--grid {
    grid-template-columns: 1fr;
  }
}
</style>
