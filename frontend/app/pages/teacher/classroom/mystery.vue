<script setup lang="ts">
import { routePaths, routePathsWithString } from '~/composables/utils/routePaths';
const route = useRoute();
const { teacher } = useClassrooms();

const classroomId = computed(() => String(route.query.classroomId ?? ''));
const { data: classroom } = await useAsyncData(
  'teacher-classroom-mystery',
  () => (classroomId.value ? teacher.getClassroomById(classroomId.value).catch(() => null) : null),
  { watch: [classroomId] },
);
</script>

<template>
  <section class="pageShell">
    <header class="pageHeader">
      <NuxtLink class="backLink" :to="routePathsWithString(routePaths.teacherClassroomOverview, `?classroomId=${classroomId}`)">Tilbake til klasserommet</NuxtLink>
      <p class="eyebrow">Ukens mysterium</p>
      <h1>{{ classroom?.name ?? 'Ukjent klasserom' }}</h1>
    </header>

    <div class="card">
      <div class="mysteryHeader">
        <h2>Ingen publisert mysteriedata ennå</h2>
      </div>
      <p>Frontend viser ikke lenger mockede mysterier for klasserom.</p>
      <p>Backend har foreløpig ikke et endepunkt for å hente ukens mysterium per klasserom.</p>
    </div>
  </section>
</template>

<style scoped>
.pageShell { width: 100%; max-width: 1000px; margin: 0 auto; padding: var(--space-4); }
.pageHeader { margin-bottom: var(--space-4); }
.backLink { display: inline-block; margin-bottom: 0.75rem; color: var(--primary); font-weight: 600; }
.eyebrow { color: var(--primary); font-weight: 700; letter-spacing: 0.08em; text-transform: uppercase; margin-bottom: 0.5rem; }
.card { padding: 1rem; border: 1px solid var(--border); border-radius: var(--radius); background: var(--surface); }
.mysteryHeader { display: flex; justify-content: space-between; gap: 1rem; align-items: baseline; margin-bottom: 0.75rem; }
button { margin-top: 1rem; padding: 0.65rem 1rem; border: 1px solid var(--primary); border-radius: var(--radius); background: var(--primary); color: var(--bg); font-weight: 600; }
</style>
