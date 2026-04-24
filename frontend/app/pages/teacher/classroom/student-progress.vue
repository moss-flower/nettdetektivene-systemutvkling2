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
}

.subtitle {
  margin: 0;
  color: var(--color-text-secondary);
}

.summaryGrid {
  display: grid;
  gap: var(--space-4);
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  margin-bottom: var(--space-4);
}

.summaryCard,
.detailCard {
  padding: var(--space-4);
  border: var(--border-width-hairline) solid var(--color-border-default);
  border-radius: var(--radius-3);
  background: var(--color-bg-surface);
  box-shadow: var(--shadow-1);
}

.summaryLabel {
  display: block;
  color: var(--color-text-secondary);
  margin-bottom: 0.35rem;
}

.summaryCard strong {
  color: var(--color-brand-primary);
  font-size: 1.4rem;
}
</style>
