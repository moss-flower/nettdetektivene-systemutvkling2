<script setup lang="ts">
import { routePaths, routePathsWithString } from '~/composables/utils/routePaths';
import { getClassroomMemberById } from '~/composables/useTeacherDashboard';
const route = useRoute();
const { teacher } = useClassrooms();

const classroomId = computed(() => String(route.query.classroomId ?? ''));
const studentId = computed(() => String(route.query.studentId ?? ''));

const { data: classroom } = await useAsyncData(
  'teacher-classroom-student-progress',
  () => (classroomId.value ? teacher.getClassroomById(classroomId.value).catch(() => null) : null),
  { watch: [classroomId] },
);

const student = computed(() => getClassroomMemberById(classroom.value, studentId.value));
</script>

<template>
  <section class="pageShell">
    <header class="pageHeader">
      <NuxtLink class="backLink" :to="routePathsWithString(routePaths.teacherClassroomStudents, `?classroomId=${classroomId}`)">Tilbake til elevoversikt</NuxtLink>
      <p class="eyebrow">Enkeltelev</p>
      <h1>{{ student?.username ?? 'Ukjent elev' }}</h1>
      <p class="subtitle">{{ classroom?.name ?? 'Ukjent klasserom' }}</p>
    </header>

    <div v-if="student" class="summaryGrid">
      <article class="summaryCard">
        <span class="summaryLabel">Elev</span>
        <strong>{{ student.username }}</strong>
      </article>
      <article class="summaryCard">
        <span class="summaryLabel">Bruker-ID</span>
        <strong>{{ student.publicUuid }}</strong>
      </article>
      <article class="summaryCard">
        <span class="summaryLabel">Klasserom</span>
        <strong>{{ classroom?.name }}</strong>
      </article>
    </div>

    <div v-if="student" class="detailCard">
      <h2>Elevdata</h2>
      <p>Denne siden viser nå ekte medlemsdata fra backend.</p>
      <p>Detaljert progresjon per elev finnes ikke som lærer-endepunkt i backend ennå.</p>
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
