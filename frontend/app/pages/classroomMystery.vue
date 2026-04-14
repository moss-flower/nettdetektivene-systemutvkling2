<script setup lang="ts">
const route = useRoute();
const { getTeacherClassroomById, getClassroomMystery } = useTeacherDashboard();

const classroomId = computed(() => String(route.query.classroomId ?? ''));
const classroom = computed(() => getTeacherClassroomById(classroomId.value));
const mystery = computed(() => getClassroomMystery(classroomId.value));
</script>

<template>
  <section class="pageShell">
    <header class="pageHeader">
      <NuxtLink class="backLink" :to="`/classroomOverview?classroomId=${classroomId}`">Tilbake til klasserommet</NuxtLink>
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
.backLink { display: inline-block; margin-bottom: 0.75rem; color: var(--primary); font-weight: 600; }
.eyebrow { color: var(--primary); font-weight: 700; letter-spacing: 0.08em; text-transform: uppercase; margin-bottom: 0.5rem; }
.card { padding: 1rem; border: 1px solid var(--border); border-radius: var(--radius); background: var(--surface); }
.mysteryHeader { display: flex; justify-content: space-between; gap: 1rem; align-items: baseline; margin-bottom: 0.75rem; }
button { margin-top: 1rem; padding: 0.65rem 1rem; border: 1px solid var(--primary); border-radius: var(--radius); background: var(--primary); color: var(--bg); font-weight: 600; }
</style>
