<script setup lang="ts">
import {
  routePaths,
  routePathsWithString,
} from "~/composables/utils/routePaths";
import { useAuth } from "~/composables/useAuth";
import { toClassroomSummaries } from "~/composables/useTeacherDashboard";
import { Settings, Bell } from 'lucide-vue-next';

definePageMeta({ layout: 'teacher' });

const { teacher } = useClassrooms();

const { data: ownedClassrooms, pending: classroomsPending } =
  await useAsyncData("teacher-owned-classrooms", () =>
    teacher.getOwnedClassrooms().catch(() => []),
  );

const classroomSummaries = computed(() =>
  toClassroomSummaries(ownedClassrooms.value ?? []),
);
const { notificationCount } = useNotifications();
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
          Logg ut
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

        <div class="rightColumn">
          <div class="topActionRow">
            <NuxtLink
              class="panel settingsButton"
              :to="routePaths.teacherSettings"
            >
              <Settings :size="48" class="settingsIcon" />
              <span class="settingsButtonLabel">Innstillinger</span>
            </NuxtLink>

            <NuxtLink
              class="panel settingsButton settingsButtonNotifications"
              :to="routePaths.teacherNotifications"
            >
              <Bell :size="48" class="settingsIcon" />
              <span class="settingsButtonLabel">Varslinger</span>
              <span v-if="notificationCount > 0" class="notificationBadge">
                {{ notificationCount }}
              </span>
            </NuxtLink>
          </div>

          <article class="panel panelHighlight">
            <h2>Hurtig tilgang</h2>
            <p>Lag nye ressurser som kan brukes i klasserom og stasjonskart.</p>

            <h3 class="quickAccessSubheading">Lag ny</h3>

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
          </article>
        </div>
      </div>
    </div>
  </section>
</template>

<style scoped>
.overviewShell {
  min-height: 100dvh;
  width: 100%;
  display: grid;
  place-items: center;
  padding: var(--layout-page-padding);
}

.overviewPage {
  width: 100%;
  max-width: var(--layout-max-width);
  padding: var(--layout-page-padding);
  background: var(--color-bg-canvas);
}

.overviewHeader {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: var(--space-4);
  margin-bottom: var(--space-4);
  padding: var(--space-5);
  border: var(--border-width-hairline) solid var(--color-border-default);
  border-top: var(--border-width-thick) solid var(--color-accent-reward);
  border-radius: var(--radius-3);
  background: var(--color-bg-surface);
  box-shadow: var(--shadow-folder);
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
  color: var(--color-text-secondary);
  font-weight: var(--font-weight-bold);
  letter-spacing: var(--font-tracking-wider);
  text-transform: uppercase;
  margin-bottom: var(--space-2);
}

h1 {
  margin: 0 0 var(--space-2) 0;
  color: var(--color-text-primary);
  font-family: var(--font-family-serif), sans-serif;
  font-size: var(--font-size-28);
  line-height: var(--font-leading-28);
}

.subtitle {
  margin: 0;
  color: var(--color-text-secondary);
}

.logoutButton {
  flex: 0 0 auto;
  border: var(--border-width-hairline) solid var(--color-border-default);
  border-radius: var(--radius-1);
  padding: var(--space-3) var(--space-4);
  background: var(--color-bg-surface);
  color: var(--color-text-primary);
  font: inherit;
  font-weight: var(--font-weight-bold);
  cursor: pointer;
  transition: transform var(--transition-fast), border-color var(--transition-fast), color var(--transition-fast);
}

.logoutButton:hover:not(:disabled) {
  transform: translateY(-1px);
  border-color: var(--color-brand-primary);
  color: var(--color-brand-primary);
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
  transition: transform var(--transition-fast), border-color var(--transition-fast);
}

.settingsButton:hover {
  transform: translateY(-1px);
  border-color: var(--color-brand-primary);
}

.settingsIcon {
  /* deliberate exception, decorative icon, no token covers 56px */
  width: 3.5rem;
  height: 3.5rem;
  border-radius: var(--radius-1);
}

.settingsButtonLabel {
  font-size: var(--font-size-16);
  font-weight: var(--font-weight-bold);
  color: var(--color-text-primary);
}

.notificationBadge {
  position: absolute;
  top: var(--space-4);
  right: var(--space-4);
  display: grid;
  place-items: center;
  width: var(--size-icon-md);
  height: var(--size-icon-md);
  border-radius: var(--radius-pill);
  background: var(--color-semantic-error);
  color: var(--color-text-onDark);
  font-size: var(--font-size-12);
  font-weight: var(--font-weight-semibold);
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
  color: var(--color-text-secondary);
}

.classroomCard {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  gap: var(--space-3);
  /* deliberate exception, card-aspect anchor */
  min-height: 96px;
  padding: var(--space-4);
  border: var(--border-width-hairline) solid var(--color-border-default);
  border-radius: var(--radius-2);
  text-decoration: none;
  /* deliberate, navy-family tint */
  background: var(--palette-navy-600);
  transition: transform var(--transition-fast), border-color var(--transition-fast);
}

.classroomCard:hover {
  transform: translateY(-1px);
  border-color: var(--color-brand-primary);
}

.classroomName {
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-onDark);
  font-size: var(--font-size-16);
}

.classScore {
  color: var(--color-text-onDark);
  font-size: var(--font-size-14);
  opacity: 0.87;
  white-space: nowrap;
}

.panelHighlight {
  background: var(--color-bg-surface);
  border: var(--border-width-thick) solid var(--color-accent-reward);
  border-radius: var(--radius-4);
  box-shadow: var(--shadow-folder);
}

.factoryActions {
  display: flex;
  flex-wrap: wrap;
  gap: var(--space-3);
  margin-top: var(--space-3);
}

.factoryActionsGrid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
}

.quickAccessSubheading {
  margin: var(--space-4) 0 0;
  color: var(--color-text-primary);
  font-size: var(--font-size-16);
}

.rightColumn {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
  min-width: 0;
}

.buttonLink {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: var(--size-control-lg);
  padding: var(--space-4) var(--space-5);
  border: var(--border-width-hairline) solid var(--color-brand-primary);
  border-radius: var(--radius-1);
  background: var(--color-brand-primary);
  color: var(--color-text-onDark);
  font-size: var(--font-size-16);
  font-weight: var(--font-weight-bold);
  text-decoration: none;
  box-shadow: var(--shadow-1);
  transition: tansform var(--transition-fast), box-shadow var(--transition-fast), border-color var(--transition-fast);
}

.buttonLink:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-2);
}

.buttonLink:active {
  transform: translateY(0);
  box-shadow: var(--shadow-pressed);
}

.buttonLinkSecondary {
  background: var(--color-bg-surface);
  border-color: var(--color-border-default);
  color: var(--color-brand-primary);
}

.buttonLinkSecondary:hover {
  border-color: var(--color-brand-primary);
}

.joinClassroomButton {
  width: 100%;
  min-height: var(--size-control-lg);
}

.createClassroomButton {
  width: 100%;
  min-height: var(--size-control-lg);
}

@media (max-width: 900px) {
  .dashboardGrid {
    grid-template-columns: 1fr;
  }

  .factoryActionsGrid {
    grid-template-columns: 1fr;
  }
}
</style>
