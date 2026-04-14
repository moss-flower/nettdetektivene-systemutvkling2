<script setup lang="ts">
const route = useRoute();
const { getTeacherClassroomById, getClassroomLeaderboard } = useTeacherDashboard();

const classroomId = computed(() => String(route.query.classroomId ?? ''));

const classroom = computed(() => getTeacherClassroomById(classroomId.value));

const leaderboard = computed(() => getClassroomLeaderboard(classroomId.value));
</script>

<template>
  <section class="classroomPage">
    <header class="pageHeader">
      <NuxtLink class="backLink" to="/teacherOverview">Tilbake til hjemskjerm</NuxtLink>
      <p class="eyebrow">Klasseromsdetaljer</p>
      <h1>{{ classroom?.name ?? 'Ukjent klasserom' }}</h1>
      <p class="subtitle">Ledertavle sortert etter progresjon.</p>
    </header>

    <div v-if="classroom" class="actionGrid">
      <NuxtLink :to="`/classroom/students?classroomId=${classroom.id}`" class="actionCard">
        <span class="actionCard__title">Elever</span>
        <span class="actionCard__text">Se elevoversikt og åpne enkeltelevens progresjon.</span>
      </NuxtLink>

      <NuxtLink :to="`/classroom/notifications?classroomId=${classroom.id}`" class="actionCard">
        <span class="actionCard__title">Varslingssenter</span>
        <span class="actionCard__text">Se varsler som bare gjelder dette klasserommet.</span>
      </NuxtLink>

      <NuxtLink :to="`/classroom/tasks?classroomId=${classroom.id}`" class="actionCard">
        <span class="actionCard__title">Oppgaver</span>
        <span class="actionCard__text">Åpne oppgavene som brukes i dette klasserommet.</span>
      </NuxtLink>

      <NuxtLink to="/student/home" class="actionCard">
        <span class="actionCard__title">Test spillet</span>
        <span class="actionCard__text">Prøv spillet fra elevens perspektiv.</span>
      </NuxtLink>

      <NuxtLink :to="`/classroom/mystery?classroomId=${classroom.id}`" class="actionCard">
        <span class="actionCard__title">Ukens mysterium</span>
        <span class="actionCard__text">Se og rediger ukens mysterium for dette rommet.</span>
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
            <td>{{ student.name }}</td>
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
  padding: 1rem;
  border: 1px solid var(--border);
  border-radius: var(--radius);
  background: var(--surface);
  color: var(--text);
}

.actionCard__title {
  font-weight: 700;
  color: var(--primary);
}

.actionCard__text {
  color: var(--muted);
  line-height: 1.4;
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
