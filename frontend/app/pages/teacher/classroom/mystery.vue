<script setup lang="ts">
import { routePaths, routePathsWithString } from '~/composables/utils/routePaths';

const route = useRoute();
const { getTeacherClassroomById, getteacherClassroomMystery } = useTeacherDashboard();

const classroomId = computed(() => String(route.query.classroomId ?? ''));
const classroom = computed(() => getTeacherClassroomById(classroomId.value));
const mystery = computed(() => getteacherClassroomMystery(classroomId.value));
</script>

<template>
  <section class="pageShell">
    <header class="pageHeader">
      <NuxtLink class="backLink" :to="routePathsWithString(routePaths.teacherClassroomOverview, `?classroomId=${classroomId}`)">Tilbake til klasserommet</NuxtLink>
      <p class="eyebrow">Ukens mysterium</p>
      <h1>{{ classroom?.name ?? 'Ukjent klasserom' }}</h1>
    </header>

    <div v-if="mystery" class="card">
      <div class="mysteryHeader">
        <h2>{{ mystery.title }}</h2>
        <span>{{ mystery.status }}</span>
      </div>
      <p>{{ mystery.description }}</p>
      <button type="button">Rediger mysterium</button>
    </div>
  </section>
</template>

<style scoped>
.pageShell { width: 100%; max-width: 1000px; margin: 0 auto; padding: var(--space-4); }
.pageHeader { margin-bottom: var(--space-4); }
.backLink { display: inline-block; margin-bottom: 0.75rem; color: var(--color-brand-primary); font-weight: 600; }
.eyebrow { color: var(--color-brand-primary); font-weight: 700; letter-spacing: 0.08em; text-transform: uppercase; margin-bottom: 0.5rem; }
.card { padding: 1rem; border: 1px solid var(--color-border-default); border-radius: var(--radius-3); background: var(--color-bg-surface); box-shadow: var(--shadow-1); }
.mysteryHeader { display: flex; justify-content: space-between; gap: 1rem; align-items: baseline; margin-bottom: 0.75rem; }
button { margin-top: 1rem; padding: 0.65rem 1rem; border: 1px solid var(--color-brand-primary); border-radius: var(--radius-2); background: var(--color-brand-primary); color: var(--color-text-onDark); font-weight: 600; }
</style>
