<script setup lang="ts">
import { World } from '~/composables/types/WorldTypes';
const route = useRoute();
import { routePaths, routePathsWithString, routePathsWithUid } from '~/composables/utils/routePaths';

const { teacher } = useClassrooms();

const classroomId = computed(() => String(route.query.classroomId ?? ''));

const { data: classroom } = await useAsyncData(
  'teacher-classroom-overview',
  () => (classroomId.value ? teacher.getClassroomById(classroomId.value).catch(() => null) : null),
  { watch: [classroomId] },
);

const worldUid = computed(() => classroom.value?.worldUid ?? '');

const { data: world } = await useAsyncData(
  'teacher-classroom-world',
  () => (worldUid.value ? World.backendFetchWorldByUid(worldUid.value) : null),
  { watch: [worldUid] },
);
</script>

<template>
  <section class="classroomPage">
    <header class="pageHeader">
      <NuxtLink class="backLink" :to="routePaths.teacherOverview">Tilbake til hjemskjerm</NuxtLink>
      <p class="eyebrow">Klasseromsdetaljer</p>
      <h1>{{ classroom?.name ?? 'Ukjent klasserom' }}</h1>
      <p class="subtitle">Viser klasserommet slik det ligger i backend akkurat nå.</p>
    </header>

    <div v-if="classroom" class="actionGrid">
      <NuxtLink :to="routePathsWithString(routePaths.teacherClassroomNotifications, `?classroomId=${classroom.uid}`)" class="actionCard">
        <span class="actionCard__title">Varslingssenter</span>
        <img src="/images/placeholders/action-card.svg" alt="Varslingssenter" class="actionCard__image">
      </NuxtLink>

      <NuxtLink :to="routePathsWithUid(routePaths.teacherFactoryWorld, classroom.worldUid)" class="actionCard">
        <span class="actionCard__title">Verden</span>
        <img src="/images/placeholders/action-card.svg" alt="Verden" class="actionCard__image">
      </NuxtLink>

      <NuxtLink :to="routePaths.studentHome" class="actionCard">
        <span class="actionCard__title">Test spillet</span>
        <img src="/images/placeholders/action-card.svg" alt="Test spillet" class="actionCard__image">
      </NuxtLink>

      <NuxtLink :to="routePathsWithString(routePaths.teacherClassroomMystery, `?classroomId=${classroom.uid}`)" class="actionCard">
        <span class="actionCard__title">Ukens mysterium</span>
        <img src="/images/placeholders/action-card.svg" alt="Ukens mysterium" class="actionCard__image">
      </NuxtLink>
    </div>

    <div v-if="classroom" class="summaryRow">
      <article class="summaryCard">
        <span class="summaryCard__label">Elever</span>
        <strong>{{ classroom.members.length }}</strong>
      </article>

      <article class="summaryCard">
        <span class="summaryCard__label">Eiere</span>
        <strong>{{ classroom.owners.length }}</strong>
      </article>

      <article class="summaryCard">
        <span class="summaryCard__label">Kode</span>
        <strong>{{ classroom.code }}</strong>
      </article>
    </div>

    <section v-if="classroom" class="pendingCard">
      <div class="pendingCard__header">
        <h2>Verden</h2>
        <p>Klasserommet bruker verdenen som er koblet til i backend.</p>
      </div>

      <p class="pendingEmpty">{{ world?.title ?? classroom.worldUid }}</p>
    </section>

    <div v-if="classroom" class="tableCard">
      <table>
        <thead>
          <tr>
            <th>Elev</th>
            <th>Bruker-ID</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="student in classroom.members" :key="student.publicUuid">
            <td>
              <NuxtLink :to="routePathsWithString(routePaths.teacherClassroomStudentProgress, `?classroomId=${classroomId}&studentId=${student.publicUuid}`)" class="studentLink">
                {{ student.username }}
              </NuxtLink>
            </td>
            <td>{{ student.publicUuid }}</td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-else class="emptyState">
      <h2>Fant ikke klasserommet</h2>
      <p>Velg et klasserom fra lærerhjemsiden.</p>
    </div>
  </section>
</template>

<style scoped>
.classroomPage {
  width: 100%;
  max-width: 1000px;
  margin: 0 auto;
  padding: var(--space-4);
}

.pageHeader {
  margin-bottom: var(--space-4);
}

.backLink {
  display: inline-block;
  margin-bottom: 0.75rem;
  color: var(--primary);
  font-weight: 600;
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

.summaryRow {
  display: grid;
  gap: 1rem;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  margin-bottom: 1rem;
}

.actionGrid {
  display: grid;
  gap: 1rem;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  margin-bottom: 1rem;
}

.actionCard {
  display: grid;
  gap: 0.5rem;
  justify-items: center;
  padding: 1rem;
  border: 1px solid var(--border);
  border-radius: var(--radius);
  background: var(--surface);
  color: var(--text);
}

.actionCard__title {
  font-weight: 700;
  color: var(--primary);
  text-align: center;
}

.actionCard__image {
  display: block;
  width: 4.5rem;
  height: 4.5rem;
  border-radius: 0.9rem;
  object-fit: cover;
}

.summaryCard {
  display: grid;
  gap: 0.35rem;
  padding: 1rem;
  border: 1px solid var(--border);
  border-radius: var(--radius);
  background: var(--surface);
}

.summaryCard__label {
  color: var(--muted);
  font-size: 0.9rem;
}

.summaryCard strong {
  font-size: 1.6rem;
  color: var(--primary);
}

.pendingCard {
  display: grid;
  gap: 1rem;
  margin-bottom: 1rem;
  padding: 1rem;
  border: 1px solid var(--border);
  border-radius: var(--radius);
  background: var(--surface);
}

.pendingCard__header {
  display: grid;
  gap: 0.35rem;
}

.pendingCard__header h2,
.pendingStudent strong {
  margin: 0;
}

.pendingCard__header p,
.pendingEmpty,
.pendingStudent span {
  margin: 0;
  color: var(--muted);
}

.pendingList {
  display: grid;
  gap: 0.75rem;
}

.pendingStudent {
  display: grid;
  gap: 0.25rem;
  padding: 0.85rem 1rem;
  border: 1px solid var(--border);
  border-radius: calc(var(--radius) - 0.2rem);
  background: rgba(255, 255, 255, 0.03);
}

.tableCard {
  border: 1px solid var(--border);
  border-radius: var(--radius);
  background: var(--surface);
  overflow: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
  min-width: 650px;
}

th,
td {
  text-align: left;
  padding: 0.85rem 1rem;
  border-bottom: 1px solid var(--border);
}

thead th {
  background: var(--bg);
  font-weight: 700;
}

.studentLink {
  color: var(--primary);
  font-weight: 700;
  text-decoration: none;
}

.studentLink:hover {
  text-decoration: underline;
}

.emptyState {
  padding: 1.25rem;
  border: 1px solid var(--border);
  border-radius: var(--radius);
  background: var(--surface);
}

@media (max-width: 800px) {
  .summaryRow {
    grid-template-columns: 1fr;
  }
}
</style>
