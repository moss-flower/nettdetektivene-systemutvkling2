<script setup lang="ts">
const route = useRoute();
const { getTeacherClassroomById, getStudentById } = useTeacherDashboard();

const classroomId = computed(() => String(route.query.classroomId ?? ''));
const studentId = computed(() => String(route.query.studentId ?? ''));

const classroom = computed(() => getTeacherClassroomById(classroomId.value));
const student = computed(() => getStudentById(classroomId.value, studentId.value));
const progressPercent = computed(() => {
  if (!student.value) {
    return 0;
  }

  return Math.round((student.value.completedLevels / student.value.totalLevels) * 100);
});
</script>

<template>
  <section class="pageShell">
    <header class="pageHeader">
      <NuxtLink class="backLink" :to="`/classroom/students?classroomId=${classroomId}`">Tilbake til elevoversikt</NuxtLink>
      <p class="eyebrow">Enkeltelev</p>
      <h1>{{ student?.name ?? 'Ukjent elev' }}</h1>
      <p class="subtitle">{{ classroom?.name ?? 'Ukjent klasserom' }}</p>
    </header>

    <div v-if="student" class="summaryGrid">
      <article class="summaryCard">
        <span class="summaryLabel">Progresjon</span>
        <strong>{{ progressPercent }} %</strong>
      </article>
      <article class="summaryCard">
        <span class="summaryLabel">Fullførte nivåer</span>
        <strong>{{ student.completedLevels }} / {{ student.totalLevels }}</strong>
      </article>
      <article class="summaryCard">
        <span class="summaryLabel">Poeng</span>
        <strong>{{ student.points }}</strong>
      </article>
      <article class="summaryCard">
        <span class="summaryLabel">Medaljer</span>
        <strong>{{ student.badges }}</strong>
      </article>
    </div>

    <div v-if="student" class="detailCard">
      <h2>Logg</h2>
      <p>Sist aktiv: {{ student.lastActive }}</p>
      <p>Her kan vi kanskje ha en tabell med spillhistorikken til eleven.</p>
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
}

.subtitle {
  margin: 0;
  color: var(--muted);
}

.summaryGrid {
  display: grid;
  gap: 1rem;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  margin-bottom: 1rem;
}

.summaryCard,
.detailCard {
  padding: 1rem;
  border: 1px solid var(--border);
  border-radius: var(--radius);
  background: var(--surface);
}

.summaryLabel {
  display: block;
  color: var(--muted);
  margin-bottom: 0.35rem;
}

.summaryCard strong {
  color: var(--primary);
  font-size: 1.4rem;
}
</style>
