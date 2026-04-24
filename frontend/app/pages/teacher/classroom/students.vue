<script setup lang="ts">
import { routePaths, routePathsWithString } from '~/composables/utils/routePaths';
const route = useRoute();
const { getTeacherClassroomById, getteacherClassroomStudents } = useTeacherDashboard();
  
const classroomId = computed(() => String(route.query.classroomId ?? ''));
const classroom = computed(() => getTeacherClassroomById(classroomId.value));
const students = computed(() => getteacherClassroomStudents(classroomId.value));
</script>

<template>
  <section class="pageShell">
    <header class="pageHeader">
      <NuxtLink class="backLink" :to="routePathsWithString(routePaths.teacherClassroomOverview, `?classroomId=${classroomId}`)">Tilbake til klasserommet</NuxtLink>
      <p class="eyebrow">Elevoversikt</p>
      <h1>{{ classroom?.name ?? 'Ukjent klasserom' }}</h1>
      <p class="subtitle">Klikk på en elev for å se progresjonen.</p>
    </header>

    <div class="tableCard">
      <table>
        <thead>
          <tr>
            <th>Elev</th>
            <th>Progresjon</th>
            <th>Poeng</th>
            <th>Medaljer</th>
            <th>Sist aktiv</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="student in students" :key="student.id">
            <td>
              <NuxtLink :to="routePathsWithString(routePaths.teacherClassroomStudentProgress, `?classroomId=${classroomId}&studentId=${student.id}`)">{{ student.name }}</NuxtLink>
            </td>
            <td>{{ Math.round((student.completedLevels / student.totalLevels) * 100) }} %</td>
            <td>{{ student.points }}</td>
            <td>{{ student.badges }}</td>
            <td>{{ student.lastActive }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>
</template>

<style scoped>
.pageShell {
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
  min-width: 680px;
}

th,
td {
  text-align: left;
  padding: 0.85rem var(--space-4);
  border-bottom: var(--border-width-hairline) solid var(--color-border-default);
}

thead th {
  background: color-mix(in srgb, var(--color-bg-surface) 88%, var(--color-semantic-info) 12%);
}

tbody a {
  color: var(--color-brand-primary);
  font-weight: 700;
}
</style>
