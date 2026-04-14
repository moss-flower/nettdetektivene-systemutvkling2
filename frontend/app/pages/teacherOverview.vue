<script setup lang="ts">
const { getTeacherClassroomSummaries } = useTeacherDashboard();

const classroomSummaries = computed(() => getTeacherClassroomSummaries());
</script>

<template>
  <section class="overviewPage">
    <header class="overviewHeader">
      <p class="eyebrow">Læreroversikt</p>
      <h1>Hjemskjerm</h1>
      <p class="subtitle">Administrer klasserom og følg aktivitet.</p>
    </header>

    <div class="dashboardGrid">
      <article class="panel classesPanel">
        <h2>Dine klasserom</h2>
        <div class="classroomCards">
          <article
            v-for="(classroom, index) in classroomSummaries"
            :key="classroom.id"
            class="classroomCard"
            :class="`classroomCard--${(index % 3) + 1}`"
          >
            <NuxtLink :to="`/classroom/overview?classroomId=${classroom.id}`">{{ classroom.name }}</NuxtLink>
            <span class="classScore">{{ classroom.totalScore }} poeng</span>
          </article>
        </div>
      </article>

      <article class="panel">
        <h2>Innstillinger</h2>
        <p>Profil, språk, varslingstyper og klasseregler.</p>
      </article>

      <article class="panel">
        <h2>Varslinger</h2>
        <ul class="notificationList">
          <li>3 elever har levert nye oppgaver.</li>
        </ul>
      </article>

      <article class="panel panel--highlight">
        <h2>Opprett nytt klasserom</h2>
        <p>Start et nytt klasserom og inviter elever.</p>
        <button type="button">+ Opprett klasserom</button>
      </article>
    </div>
  </section>
</template>

<style scoped>
.overviewPage {
  width: 100%;
  max-width: 1100px;
  margin: 0 auto;
  padding: var(--space-4);
}

.overviewHeader {
  margin-bottom: var(--space-4);
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

.dashboardGrid {
  display: grid;
  gap: var(--space-4);
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.panel {
  border: 1px solid var(--border);
  border-radius: var(--radius);
  background: var(--surface);
  padding: var(--space-4);
}

.classesPanel {
  grid-column: 1 / -1;
}

h2 {
  margin: 0 0 0.75rem 0;
}

.notificationList {
  margin: 0;
  padding-left: 1rem;
  display: grid;
  gap: 0.4rem;
}

.classroomCards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 1rem;
}

.classroomCard {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  gap: 0.75rem;
  min-height: 120px;
  padding: 1rem;
  border: 1px solid var(--border);
  border-radius: var(--radius);
}

.classroomCard--1 {
  background: rgba(101, 240, 175, 0.08);
}

.classroomCard--2 {
  background: rgba(96, 165, 250, 0.1);
}

.classroomCard--3 {
  background: rgba(244, 114, 182, 0.1);
}

.classroomCard a {
  font-weight: 700;
  color: var(--primary);
  font-size: 1.05rem;
}

.classScore {
  color: var(--muted);
  font-size: 0.95rem;
  white-space: nowrap;
}

.panel--highlight {
  background: var(--surface);
}

button {
  margin-top: 0.9rem;
  padding: 0.65rem 1rem;
  border: 1px solid var(--primary);
  border-radius: var(--radius);
  background: var(--primary);
  color: var(--bg);
  font-weight: 600;
  cursor: pointer;
}

@media (max-width: 900px) {
  .dashboardGrid {
    grid-template-columns: 1fr;
  }
}
</style>
