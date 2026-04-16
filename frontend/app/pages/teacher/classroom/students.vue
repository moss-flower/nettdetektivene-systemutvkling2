<script setup lang="ts">
const route = useRoute();
const { getTeacherClassroomById, getClassroomStudents } = useTeacherDashboard();

const classroomId = computed(() => String(route.query.classroomId ?? ''));
const classroom = computed(() => getTeacherClassroomById(classroomId.value));
const students = computed(() => getClassroomStudents(classroomId.value));
</script>

<template>
  <section class="pageShell">
    <header class="pageHeader">
      <NuxtLink class="backLink" :to="routePathsWithString(routePaths.classroomOverview, `?classroomId=${classroomId}`)">Tilbake til klasserommet</NuxtLink>
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
              <NuxtLink :to="routePathsWithString(routePaths.classroomStudentProgress, `?classroomId=${classroomId}&studentId=${student.id}`)">{{ student.name }}</NuxtLink>
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

.tableCard {
  border: 1px solid var(--border);
  border-radius: var(--radius);
  background: var(--surface);
  overflow: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
  min-width: 680px;
}

th,
td {
  text-align: left;
  padding: 0.85rem 1rem;
  border-bottom: 1px solid var(--border);
}

thead th {
  background: var(--bg);
}

tbody a {
  color: var(--primary);
  font-weight: 700;
}
</style>
