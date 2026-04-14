<script setup lang="ts">
const route = useRoute();
const { getTeacherClassroomById, getClassroomTasks } = useTeacherDashboard();

const classroomId = computed(() => String(route.query.classroomId ?? ''));
const classroom = computed(() => getTeacherClassroomById(classroomId.value));
const tasks = computed(() => getClassroomTasks(classroomId.value));
</script>

<template>
  <section class="pageShell">
    <header class="pageHeader">
      <NuxtLink class="backLink" :to="`/classroom/overview?classroomId=${classroomId}`">Tilbake til klasserommet</NuxtLink>
      <p class="eyebrow">Oppgaver</p>
      <h1>{{ classroom?.name ?? 'Ukjent klasserom' }}</h1>
    </header>

    <div class="stack">
      <article v-for="task in tasks" :key="task.id" class="card">
        <h2>{{ task.title }}</h2>
        <p>{{ task.level }}</p>
        <span>{{ task.status }}</span>
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
.card h2 { margin: 0 0 0.4rem 0; }
.card p { margin: 0 0 0.75rem 0; color: var(--muted); }
.card span { color: var(--primary); font-weight: 700; }
</style>
