<script setup lang="ts">
import { routePaths, routePathsWithString } from '~/composables/utils/routePaths';

import { getClassroomExercises } from '~/composables/useTeacherDashboard';
const route = useRoute();
const { teacher } = useClassrooms();

const classroomId = computed(() => String(route.query.classroomId ?? ''));

const { data: classroom } = await useAsyncData(
  'teacher-classroom-tasks',
  () => (classroomId.value ? teacher.getClassroomById(classroomId.value).catch(() => null) : null),
  { watch: [classroomId] },
);

const { data: exercises } = await useAsyncData(
  'teacher-classroom-exercises',
  () => getClassroomExercises(classroom.value ?? null),
  { watch: [classroom] },
);
</script>

<template>
  <section class="pageShell">
    <header class="pageHeader">
      <NuxtLink class="backLink" :to="routePathsWithString(routePaths.teacherClassroomOverview, `?classroomId=${classroomId}`)">Tilbake til klasserommet</NuxtLink>
      <p class="eyebrow">Oppgaver</p>
      <h1>{{ classroom?.name ?? 'Ukjent klasserom' }}</h1>
    </header>

    <div class="stack">
      <article v-if="(exercises ?? []).length === 0" class="card">
        <h2>Ingen oppgaver tilgjengelig</h2>
        <p>Fant ingen oppgaver via klasserommets stasjonskart og stasjoner.</p>
      </article>
      <article v-for="exercise in exercises" :key="exercise.uid" class="card">
        <h2>{{ exercise.title }}</h2>
        <p>{{ exercise.stationTitle }}</p>
        <span>{{ exercise.difficultyName }} · {{ exercise.taskCount }} oppgaver</span>
      </article>
    </div>
  </section>
</template>

<style scoped>
.pageShell { width: 100%; max-width: 1000px; margin: 0 auto; padding: var(--space-4); color: var(--color-text-primary); background: linear-gradient(180deg, rgba(255, 255, 255, 0.78), rgba(255, 255, 255, 0.88)), linear-gradient(180deg, var(--palette-gray-50) 0%, var(--palette-cream-100) 100%); border-radius: var(--radius-4); }
.pageHeader { margin-bottom: var(--space-4); }
.backLink { display: inline-block; margin-bottom: 0.75rem; color: var(--color-brand-primary); font-weight: 600; }
.eyebrow { color: var(--color-brand-primary); font-weight: 700; letter-spacing: 0.08em; text-transform: uppercase; margin-bottom: 0.5rem; }
.stack { display: grid; gap: 1rem; }
.card { padding: 1rem; border: 1px solid var(--color-border-default); border-radius: var(--radius-3); background: var(--color-bg-surface); box-shadow: var(--shadow-1); }
.card h2 { margin: 0 0 0.4rem 0; }
.card p { margin: 0 0 0.75rem 0; color: var(--color-text-secondary); }
.card span { color: var(--color-brand-primary); font-weight: 700; }
</style>
