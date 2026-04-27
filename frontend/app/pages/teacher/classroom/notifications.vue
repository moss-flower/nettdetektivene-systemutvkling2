<script setup lang="ts">
import { routePaths, routePathsWithString } from '~/composables/utils/routePaths';
const route = useRoute();
const { teacher } = useClassrooms();

const classroomId = computed(() => String(route.query.classroomId ?? ''));
const { data: classroom } = await useAsyncData(
  'teacher-classroom-notifications',
  () => (classroomId.value ? teacher.getClassroomById(classroomId.value).catch(() => null) : null),
  { watch: [classroomId] },
);
</script>

<template>
  <section class="pageShell">
    <header class="pageHeader">
      <NuxtLink class="backLink" :to="routePathsWithString(routePaths.teacherClassroomOverview, `?classroomId=${classroomId}`)">Tilbake til klasserommet</NuxtLink>
      <p class="eyebrow">Varslingssenter</p>
      <h1>{{ classroom?.name ?? 'Ukjent klasserom' }}</h1>
    </header>

    <div class="stack">
      <article class="card">
        <h2>Ingen klasseromsvarsler ennå</h2>
        <p>Frontend bruker ikke lenger mockede varsler her.</p>
        <span>Backend har foreløpig ingen lese-endepunkt for klasseromsvarsler.</span>
      </article>
    </div>
  </section>
</template>

<style scoped>
.pageShell { width: 100%; max-width: 1000px; margin: 0 auto; padding: var(--space-4); }
.pageHeader { margin-bottom: var(--space-4); }
.backLink { display: inline-block; margin-bottom: 0.75rem; color: var(--primary); font-weight: 600; }
.eyebrow { color: var(--primary); font-weight: 700; letter-spacing: 0.08em; text-transform: uppercase; margin-bottom: 0.5rem; }
.stack { display: grid; gap: 1rem; }
.card { padding: 1rem; border: 1px solid var(--border); border-radius: var(--radius); background: var(--surface); }
.card h2 { margin: 0 0 0.5rem 0; }
.card p { margin: 0 0 0.75rem 0; color: var(--text); }
.card span { color: var(--muted); font-size: 0.9rem; }
</style>
