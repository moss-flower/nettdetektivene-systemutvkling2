<script setup lang="ts">
const route = useRoute();
const { getTeacherClassroomById, getClassroomLeaderboard, getTeacherClassroomPendingStudents } = useTeacherDashboard();
import { routePaths, routePathsWithString, routePathsWithUid } from '~/composables/utils/routePaths';

const classroomId = computed(() => String(route.query.classroomId ?? ''));

const classroom = computed(() => getTeacherClassroomById(classroomId.value));

const leaderboard = computed(() => getClassroomLeaderboard(classroomId.value));
const pendingStudents = computed(() => getTeacherClassroomPendingStudents(classroomId.value));
</script>

<template>
  <section class="classroomPage">
    <header class="pageHeader">
      <NuxtLink class="backLink" :to="routePaths.teacherOverview">Tilbake til hjemskjerm</NuxtLink>
      <p class="eyebrow">Klasseromsdetaljer</p>
      <h1>{{ classroom?.name ?? 'Ukjent klasserom' }}</h1>
      <p class="subtitle">Ledertavle sortert etter progresjon.</p>
    </header>

    <div v-if="classroom" class="actionGrid">
      <NuxtLink :to="routePathsWithString(routePaths.teacherClassroomNotifications, `?classroomId=${classroom.id}`)" class="actionCard">
        <span class="actionCard__title">Varslingssenter</span>
        <img src="https://placehold.co/128x128?text=VS" alt="Varslingssenter" class="actionCard__image">
      </NuxtLink>

      <NuxtLink :to="routePathsWithUid(routePaths.teacherFactoryWorld, classroom.worldUid)" class="actionCard">
        <span class="actionCard__title">Verden</span>
        <img src="https://placehold.co/128x128?text=VE" alt="Verden" class="actionCard__image">
      </NuxtLink>

      <NuxtLink :to="routePaths.studentHome" class="actionCard">
        <span class="actionCard__title">Test spillet</span>
        <img src="https://placehold.co/128x128?text=TS" alt="Test spillet" class="actionCard__image">
      </NuxtLink>

      <NuxtLink :to="routePathsWithString(routePaths.teacherClassroomMystery, `?classroomId=${classroom.id}`)" class="actionCard">
        <span class="actionCard__title">Ukens mysterium</span>
        <img src="https://placehold.co/128x128?text=UM" alt="Ukens mysterium" class="actionCard__image">
      </NuxtLink>
    </div>

    <div v-if="classroom" class="summaryRow">
      <article class="summaryCard">
        <span class="summaryCard__label">Klassescore</span>
        <strong>{{ classroom.totalScore }}</strong>
      </article>

      <article class="summaryCard">
        <span class="summaryCard__label">Elever</span>
        <strong>{{ classroom.studentCount }}</strong>
      </article>

      <article class="summaryCard">
        <span class="summaryCard__label">Gjennomsnittlig progresjon</span>
        <strong>{{ classroom.averageProgressPercent }} %</strong>
      </article>
    </div>

    <section v-if="classroom" class="pendingCard">
      <div class="pendingCard__header">
        <h2>Ventende elever</h2>
        <p>Elever som venter på å bli godkjent inn i klasserommet.</p>
      </div>

      <div v-if="pendingStudents.length > 0" class="pendingList">
        <article v-for="student in pendingStudents" :key="student.id" class="pendingStudent">
          <strong>{{ student.name }}</strong>
          <span>Sendte forespørsel {{ student.requestedAt }}</span>
        </article>
      </div>

      <p v-else class="pendingEmpty">Ingen elever venter på godkjenning.</p>
    </section>

    <div v-if="classroom" class="tableCard">
      <table>
        <thead>
          <tr>
            <th>Rang</th>
            <th>Elev</th>
            <th>Progresjon</th>
            <th>Fullførte nivåer</th>
            <th>Poeng</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(student, index) in leaderboard" :key="student.id">
            <td>{{ index + 1 }}</td>
            <td>
              <NuxtLink :to="routePathsWithString(routePaths.teacherClassroomStudentProgress, `?classroomId=${classroomId}&studentId=${student.id}`)" class="studentLink">
                {{ student.name }}
              </NuxtLink>
            </td>
            <td>{{ student.progressPercent }} %</td>
            <td>{{ student.completedLevels }} / {{ student.totalLevels }}</td>
            <td>{{ student.points }}</td>
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
