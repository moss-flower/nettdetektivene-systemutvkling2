<script setup lang="ts">
import {
  routePaths,
  routePathsWithString,
} from "~/composables/utils/routePaths";
import { useAuth } from "~/composables/useAuth";
import { toClassroomSummaries } from "~/composables/useTeacherDashboard";

definePageMeta({ layout: 'teacher' });

const { teacher } = useClassrooms();

const { data: ownedClassrooms, pending: classroomsPending } =
  await useAsyncData("teacher-owned-classrooms", () =>
    teacher.getOwnedClassrooms().catch(() => []),
  );

const classroomSummaries = computed(() =>
  toClassroomSummaries(ownedClassrooms.value ?? []),
);
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
  <section class="overviewShell">
    <div class="overviewPage">
      <header class="overviewHeader">
        <div class="overviewHeader__content">
          <p class="eyebrow">Læreroversikt</p>
          <h1>Hjemskjerm</h1>
          <p class="subtitle">Administrer klasserom og følg aktivitet.</p>
        </div>
        <button
          class="logoutButton"
          type="button"
          :disabled="isLoggingOut"
          @click="handleLogout"
        >
          {{ isLoggingOut ? "Logger ut..." : "Logg ut" }}
        </button>
      </header>

      <div class="dashboardGrid">
        <aside class="panel classesPanel">
          <h2>Dine klasserom</h2>
          <NuxtLink
            class="buttonLink createClassroomButton"
            :to="routePaths.teacherClassroomCreate"
          >
            + Lag klasserom
          </NuxtLink>
          <NuxtLink
            class="buttonLink joinClassroomButton"
            :to="routePaths.join"
          >
            Bli med i klasserom
          </NuxtLink>

          <div class="classroomCards">
            <p v-if="classroomsPending" class="panelMessage">
              Laster klasserom...
            </p>
            <p v-else-if="classroomSummaries.length === 0" class="panelMessage">
              Du har ingen klasserom ennå.
            </p>
            <NuxtLink
              v-for="(classroom, index) in classroomSummaries"
              :key="classroom.id"
              class="classroomCard"
              :class="`classroomCard${(index % 3) + 1}`"
              :to="
                routePathsWithString(
                  routePaths.teacherClassroomOverview,
                  `?classroomId=${classroom.id}`,
                )
              "
            >
              <span class="classroomName">{{ classroom.name }}</span>
              <span class="classScore"
                >{{ classroom.studentCount }} elever</span
              >
            </NuxtLink>
          </div>
        </aside>

        <div class="topActionRow">
          <NuxtLink
            class="panel settingsButton"
            :to="routePaths.teacherSettings"
          >
            <img
              src="https://placehold.co/64x64"
              alt="Innstillinger ikon"
              class="settingsIcon"
            />
            <span class="settingsButtonLabel">Innstillinger</span>
          </NuxtLink>

          <NuxtLink
            class="panel settingsButton settingsButtonNotifications"
            :to="routePaths.teacherNotifications"
          >
            <img
              src="https://placehold.co/64x64"
              alt="Varslinger ikon"
              class="settingsIcon"
            />
            <span class="settingsButtonLabel">Varslinger</span>
            <span class="notificationBadge">1</span>
          </NuxtLink>
        </div>

        <article class="panel panelHighlight managementPanel">
          <details class="advancedDetails">
            <summary class="advancedSummary">
              <h2>Avanserte funksjoner</h2>
            </summary>
            <p>Lag nye ressurser som kan brukes i klasserom og stasjonskart.</p>

            <h3 class="advancedSubheading">Lag ny</h3>

            <div class="factoryActions factoryActionsGrid">
              <NuxtLink
                class="buttonLink buttonLinkSecondary"
                :to="{
                  path: routePaths.teacherFactoryWorld,
                  query: {
                    returnTo: routePaths.teacherOverview,
                  },
                }"
              >
                + Stasjonskart
              </NuxtLink>
              <NuxtLink
                class="buttonLink buttonLinkSecondary"
                :to="{
                  path: routePaths.teacherFactoryStation,
                  query: {
                    returnTo: routePaths.teacherOverview,
                  },
                }"
              >
                + Stasjon
              </NuxtLink>
              <NuxtLink
                class="buttonLink buttonLinkSecondary"
                :to="{
                  path: routePaths.teacherFactoryExercise,
                  query: {
                    returnTo: routePaths.teacherOverview,
                  },
                }"
              >
                + Oppgavesett
              </NuxtLink>
            </div>
          </details>
        </article>
      </div>
    </div>
    <button
      class="logoutButton"
      type="button"
      :disabled="isLoggingOut"
      @click="handleLogout"
    >
      {{ isLoggingOut ? "Logger ut..." : "Logg ut" }}
    </button>

    <div class="dashboardGrid">
      <aside class="panel classesPanel">
        <h2>Dine klasserom</h2>
        <NuxtLink
          class="buttonLink createClassroomButton"
          :to="routePaths.teacherClassroomCreate"
        >
          + Lag klasserom
        </NuxtLink>
        <NuxtLink class="buttonLink joinClassroomButton" :to="routePaths.join">
          Bli med i klasserom
        </NuxtLink>

        <div class="classroomCards">
          <p v-if="classroomsPending" class="panelMessage">
            Laster klasserom...
          </p>
          <p v-else-if="classroomSummaries.length === 0" class="panelMessage">
            Du har ingen klasserom ennå.
          </p>
          <NuxtLink
            v-for="(classroom, index) in classroomSummaries"
            :key="classroom.id"
            class="classroomCard"
            :class="`classroomCard${(index % 3) + 1}`"
            :to="
              routePathsWithString(
                routePaths.teacherClassroomOverview,
                `?classroomId=${classroom.id}`,
              )
            "
          >
            <span class="classroomName">{{ classroom.name }}</span>
            <span class="classScore">{{ classroom.studentCount }} elever</span>
          </NuxtLink>
        </div>
      </aside>

      <div class="topActionRow">
        <NuxtLink class="panel settingsButton" :to="routePaths.teacherSettings">
          <img
            src="https://placehold.co/64x64"
            alt="Innstillinger ikon"
            class="settingsIcon"
          />
          <span class="settingsButtonLabel">Innstillinger</span>
        </NuxtLink>

        <NuxtLink
          class="panel settingsButton settingsButtonNotifications"
          :to="routePaths.teacherNotifications"
        >
          <img
            src="https://placehold.co/64x64"
            alt="Varslinger ikon"
            class="settingsIcon"
          />
          <span class="settingsButtonLabel">Varslinger</span>
          <span class="notificationBadge">1</span>
        </NuxtLink>
      </div>

      <article class="panel panelHighlight managementPanel">
        <details class="advancedDetails">
          <summary class="advancedSummary">
            <h2>Avanserte funksjoner</h2>
          </summary>
          <p>Lag nye ressurser som kan brukes i klasserom og stasjonskart.</p>

          <h3 class="advancedSubheading">Lag ny</h3>

          <div class="factoryActions factoryActionsGrid">
            <NuxtLink
              class="buttonLink buttonLinkSecondary"
              :to="{
                path: routePaths.teacherFactoryWorld,
                query: { returnTo: routePaths.teacherOverview },
              }"
            >
              + Stasjonskart
            </NuxtLink>
            <NuxtLink
              class="buttonLink buttonLinkSecondary"
              :to="{
                path: routePaths.teacherFactoryStation,
                query: { returnTo: routePaths.teacherOverview },
              }"
            >
              + Stasjon
            </NuxtLink>
            <NuxtLink
              class="buttonLink buttonLinkSecondary"
              :to="{
                path: routePaths.teacherFactoryExercise,
                query: { returnTo: routePaths.teacherOverview },
              }"
            >
              + Oppgavesett
            </NuxtLink>
          </div>
        </details>
      </article>
    </div>
  </section>
</template>

<style scoped>
.overviewShell {
  min-height: 100dvh;
  width: 100%;
  display: grid;
  place-items: center;
  padding: clamp(1rem, 3vw, 2rem);
}

.overviewPage {
  width: 100%;
  max-width: 1180px;
  padding: clamp(1.25rem, 3vw, 2rem);
  background:
    radial-gradient(
      circle at top left,
      rgba(245, 202, 46, 0.22),
      transparent 28%
    ),
    radial-gradient(
      circle at 85% 18%,
      rgba(42, 63, 106, 0.14),
      transparent 30%
    ),
    linear-gradient(
      180deg,
      rgba(255, 255, 255, 0.94),
      rgba(245, 239, 226, 0.97)
    );
  border: var(--border-width-thin) solid rgba(255, 255, 255, 0.68);
  border-radius: var(--radius-4);
  box-shadow:
    0 24px 60px rgba(27, 42, 74, 0.2),
    inset 0 1px 0 rgba(255, 255, 255, 0.7);
}

.overviewHeader {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: var(--space-4);
  margin-bottom: var(--space-4);
  padding: var(--space-5);
  border: var(--border-width-hairline) solid var(--color-border-default);
  border-radius: var(--radius-3);
  background:
    linear-gradient(135deg, rgba(42, 63, 106, 0.06), rgba(232, 182, 0, 0.08)),
    var(--color-bg-surface);
  box-shadow: var(--shadow-1);
}

.overviewHeader__content {
  min-width: 0;
}

@media (max-width: 720px) {
  .overviewShell {
    place-items: stretch;
  }

  .overviewPage {
    border-radius: var(--radius-4);
  }

  .overviewHeader {
    flex-direction: column;
    align-items: stretch;
  }

  .logoutButton {
    align-self: flex-start;
  }
}

.eyebrow {
  color: var(--color-brand-primary);
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  margin-bottom: var(--space-2);
}

h1 {
  margin: 0 0 var(--space-2) 0;
  color: var(--color-text-primary);
}

.subtitle {
  margin: 0;
  color: var(--color-text-secondary);
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
  transition:
    transform 0.15s ease,
    border-color 0.15s ease,
    color 0.15s ease;
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
  border: var(--border-width-hairline) solid var(--color-border-default);
  border-radius: var(--radius-3);
  background: var(--color-bg-surface);
  padding: var(--space-4);
  box-shadow: var(--shadow-1);
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
  gap: var(--space-4);
  color: inherit;
  text-decoration: none;
  transition:
    transform var(--transition-fast) ease,
    border-color var(--transition-fast) ease;
}

.settingsButton:hover {
  transform: translateY(-1px);
  border-color: var(--color-brand-primary);
}

.settingsIcon {
  width: 3.5rem;
  height: 3.5rem;
  border-radius: 0.75rem;
}

.settingsButtonLabel {
  font-size: 1.05rem;
  font-weight: 700;
  color: var(--color-text-primary);
}

.notificationBadge {
  position: absolute;
  top: var(--space-4);
  right: var(--space-4);
  display: grid;
  place-items: center;
  width: 2rem;
  height: 2rem;
  border-radius: var(--radius-pill);
  background: #ef4444;
  color: white;
  font-size: 0.95rem;
  font-weight: 700;
}

.classesPanel {
  display: grid;
  gap: var(--space-4);
}

h2 {
  margin: 0 0 var(--space-3) 0;
}

.classroomCards {
  display: grid;
  gap: var(--space-3);
}

.panelMessage {
  margin: 0;
  color: var(--muted);
}

.classroomCard {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  gap: var(--space-3);
  min-height: 96px;
  padding: var(--space-4);
  border: var(--border-width-hairline) solid var(--color-border-default);
  border-radius: var(--radius-2);
  text-decoration: none;
  transition:
    transform var(--transition-fast) ease,
    border-color var(--transition-fast) ease;
}

.classroomCard1 {
  background: rgba(101, 240, 175, 0.08);
}

.classroomCard2 {
  background: rgba(96, 165, 250, 0.1);
}

.classroomCard3 {
  background: rgba(244, 114, 182, 0.1);
}

.classroomCard:hover {
  transform: translateY(-1px);
  border-color: var(--color-brand-primary);
}

.classroomName {
  font-weight: 700;
  color: var(--color-brand-primary);
  font-size: 1.05rem;
}

.classScore {
  color: var(--color-text-secondary);
  font-size: 0.95rem;
  white-space: nowrap;
}

.panelHighlight {
  background: color-mix(
    in srgb,
    var(--color-bg-surface) 90%,
    var(--color-semantic-info) 10%
  );
}

.managementPanel {
  grid-column: 2;
}

.advancedDetails {
  display: grid;
  gap: var(--space-3);
}

.advancedSummary {
  cursor: pointer;
}

.advancedSummary h2 {
  display: inline;
}

.factoryActions {
  display: flex;
  flex-wrap: wrap;
  gap: var(--space-3);
  margin-top: 0.9rem;
}

.factoryActionsGrid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
}

.advancedSubheading {
  margin: var(--space-4) 0 0;
  color: var(--color-text-primary);
  font-size: 1rem;
}

.buttonLink {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: 4.5rem;
  padding: var(--space-4) 1.25rem;
  border: var(--border-width-hairline) solid var(--color-brand-primary);
  border-radius: var(--radius-2);
  background: var(--color-brand-primary);
  color: var(--color-text-onDark);
  font-size: 1.05rem;
  font-weight: 700;
  text-decoration: none;
}

.buttonLinkSecondary {
  background: color-mix(
    in srgb,
    var(--color-bg-surface) 92%,
    var(--color-semantic-info) 8%
  );
  color: var(--color-brand-primary);
}

.joinClassroomButton {
  width: 100%;
  min-height: 3.5rem;
}

.createClassroomButton {
  width: 100%;
  min-height: 3.5rem;
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

  .factoryActionsGrid {
    grid-template-columns: 1fr;
  }
}
</style>
