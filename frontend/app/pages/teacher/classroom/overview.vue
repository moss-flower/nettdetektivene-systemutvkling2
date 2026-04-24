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
        <span class="actionCardTitle">Varslingssenter</span>
        <img src="https://placehold.co/128x128?text=VS" alt="Varslingssenter" class="actionCardImage">
      </NuxtLink>

      <NuxtLink :to="routePathsWithUid(routePaths.teacherFactoryWorld, classroom.worldUid)" class="actionCard">
        <span class="actionCardTitle">Verden</span>
        <img src="https://placehold.co/128x128?text=VE" alt="Verden" class="actionCardImage">
      </NuxtLink>

      <NuxtLink :to="routePaths.studentHome" class="actionCard">
        <span class="actionCardTitle">Test spillet</span>
        <img src="https://placehold.co/128x128?text=TS" alt="Test spillet" class="actionCardImage">
      </NuxtLink>

      <NuxtLink :to="routePathsWithString(routePaths.teacherClassroomMystery, `?classroomId=${classroom.id}`)" class="actionCard">
        <span class="actionCardTitle">Ukens mysterium</span>
        <img src="https://placehold.co/128x128?text=UM" alt="Ukens mysterium" class="actionCardImage">
      </NuxtLink>
    </div>

    <div v-if="classroom" class="summaryRow">
      <article class="summaryCard">
        <span class="summaryCardLabel">Klassescore</span>
        <strong>{{ classroom.totalScore }}</strong>
      </article>

      <article class="summaryCard">
        <span class="summaryCardLabel">Elever</span>
        <strong>{{ classroom.studentCount }}</strong>
      </article>

      <article class="summaryCard">
        <span class="summaryCardLabel">Gjennomsnittlig progresjon</span>
        <strong>{{ classroom.averageProgressPercent }} %</strong>
      </article>
    </div>

    <section v-if="classroom" class="pendingCard">
      <div class="pendingCardHeader">
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
  margin-bottom: var(--space-3);
  color: var(--color-brand-primary);
  font-weight: 600;
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

.summaryRow {
  display: grid;
  gap: var(--space-4);
  grid-template-columns: repeat(3, minmax(0, 1fr));
  margin-bottom: var(--space-4);
}

.actionGrid {
  display: grid;
  gap: var(--space-4);
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  margin-bottom: var(--space-4);
}

.actionCard {
  display: grid;
  gap: var(--space-2);
  justify-items: center;
  padding: var(--space-4);
  border: var(--border-width-hairline) solid var(--color-border-default);
  border-radius: var(--radius-3);
  background: var(--color-bg-surface);
  color: var(--color-text-primary);
  box-shadow: var(--shadow-1);
}

.actionCardTitle {
  font-weight: 700;
  color: var(--color-brand-primary);
  text-align: center;
}

.actionCardImage {
  display: block;
  width: 4.5rem;
  height: 4.5rem;
  border-radius: 0.9rem;
  object-fit: cover;
}

.summaryCard {
  display: grid;
  gap: 0.35rem;
  padding: var(--space-4);
  border: var(--border-width-hairline) solid var(--color-border-default);
  border-radius: var(--radius-3);
  background: var(--color-bg-surface);
  box-shadow: var(--shadow-1);
}

.summaryCardLabel {
  color: var(--color-text-secondary);
  font-size: 0.9rem;
}

.summaryCard strong {
  font-size: 1.6rem;
  color: var(--color-brand-primary);
}

.pendingCard {
  display: grid;
  gap: var(--space-4);
  margin-bottom: var(--space-4);
  padding: var(--space-4);
  border: var(--border-width-hairline) solid var(--color-border-default);
  border-radius: var(--radius-3);
  background: var(--color-bg-surface);
  box-shadow: var(--shadow-1);
}

.pendingCardHeader {
  display: grid;
  gap: 0.35rem;
}

.pendingCardHeader h2,
.pendingStudent strong {
  margin: 0;
}

.pendingCardHeader p,
.pendingEmpty,
.pendingStudent span {
  margin: 0;
  color: var(--color-text-secondary);
}

.pendingList {
  display: grid;
  gap: var(--space-3);
}

.pendingStudent {
  display: grid;
  gap: var(--space-1);
  padding: 0.85rem var(--space-4);
  border: var(--border-width-hairline) solid var(--color-border-default);
  border-radius: calc(var(--radius-3) - 0.2rem);
  background: color-mix(in srgb, var(--color-bg-surface) 94%, var(--color-semantic-info) 6%);
}

.tableCard {
  border: var(--border-width-hairline) solid var(--color-border-default);
  border-radius: var(--radius-3);
  background: var(--color-bg-surface);
  overflow: auto;
  box-shadow: var(--shadow-1);
}

table {
  width: 100%;
  border-collapse: collapse;
  min-width: 650px;
}

th,
td {
  text-align: left;
  padding: 0.85rem var(--space-4);
  border-bottom: var(--border-width-hairline) solid var(--color-border-default);
}

thead th {
  background: color-mix(in srgb, var(--color-bg-surface) 88%, var(--color-semantic-info) 12%);
  font-weight: 700;
}

.studentLink {
  color: var(--color-brand-primary);
  font-weight: 700;
  text-decoration: none;
}

.studentLink:hover {
  text-decoration: underline;
}

.emptyState {
  padding: 1.25rem;
  border: var(--border-width-hairline) solid var(--color-border-default);
  border-radius: var(--radius-3);
  background: var(--color-bg-surface);
  box-shadow: var(--shadow-1);
}

@media (max-width: 800px) {
  .summaryRow {
    grid-template-columns: 1fr;
  }
}
</style>
