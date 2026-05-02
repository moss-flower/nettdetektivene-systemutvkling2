<script setup lang="ts">
import { routePaths, routePathsWithString } from '~/composables/utils/routePaths';
import { useNotepad } from '~/composables/useNotepad';
import { mapNotepadEntryFromResponse } from '~/composables/useNotepad';
import type { NotepadEntryResponse } from '~/composables/types/NotepadTypes';

const route = useRoute();
const { teacher } = useClassrooms();
const { getStudentNotes } = useNotepad();

const classroomId = computed(() => String(route.query.classroomId ?? ''));
const studentId = computed(() => String(route.query.studentId ?? ''));

const { data: classroom } = await useAsyncData(
  'teacher-classroom-for-student-notes',
  () => classroomId.value ? teacher.getClassroomById(classroomId.value).catch(() => null) : Promise.resolve(null),
  { watch: [classroomId] },
);

const { data: notesPage, error: notesError } = await useAsyncData(
  'teacher-student-notes',
  () => {
    if (!studentId.value) return Promise.resolve(null);
    return getStudentNotes(studentId.value);
  },
  { watch: [studentId] },
);

const notes = computed(() => {
  if (!notesPage.value?.content) return [];
  return notesPage.value.content.map(mapNotepadEntryFromResponse);
});
</script>

<template>
  <section class="studentNotesPage">
    <header class="pageHeader">
      <NuxtLink 
        class="backLink"
        :to="routePathsWithString(routePaths.teacherClassroomStudentProgress, { classroomId: classroomId, studentId: studentId })"
      >
        ← Tilbake til elevprogresjon
      </NuxtLink>
      <h1>Notater for elev</h1>
      <p class="subtitle">{{ classroom?.name ?? 'Ukjent klasserom' }}</p>
    </header>

    <p v-if="notesError" class="errorMessage">
      Kunne ikke laste notater. Prøv igjen senere.
    </p>

    <p v-else-if="notes.length === 0" class="emptyMessage">
      Denne eleven har ingen notater ennå.
    </p>

    <div v-else class="notesList">
      <article v-for="note in notes" :key="note.publicUuid" class="noteCard">
        <header class="noteHeader">
          <h2 class="noteTitle">{{ note.title }}</h2>
          <span class="noteType">{{ note.type }}</span>
        </header>
        <p class="noteSource">{{ note.source }}</p>
        <p class="noteDate">{{ new Date(note.date).toLocaleDateString('nb-NO') }}</p>
        <p class="noteContent">{{ note.content }}</p>
      </article>
    </div>
  </section>
</template>

<style scoped>
.studentNotesPage {
  --notes-bg-top: var(--palette-cream-100);
  --notes-bg-bottom: var(--palette-cream-200);
  --notes-border: var(--palette-overlay-cork-shadow);
  --notes-eyebrow: var(--palette-cork-580);
  --notes-title: var(--palette-cork-900);
  --notes-summary: var(--palette-cork-700);
  --notes-missing-bg: var(--palette-red-100);
  --notes-missing-border: var(--palette-red-200);

  display: grid;
  gap: var(--space-5);
  /* fluid outer padding, deliberate exception (token system)*/
  min-height: 100dvh;
  width: 100%;
  display: grid;
  place-items: center;
  max-width: var(--layout-max-width);
  margin: 0 auto;
  padding: var(--layout-page-padding);
  color: var(--color-text-primary);
  background: var(--color-bg-canvas);
}

.pageHeader {
  margin-bottom: var(--space-4);
  background: var(--color-bg-surface);
  border: var(--border-width-hairline) solid var(--color-border-default);
  border-top: var(--border-width-thick) solid var(--color-accent-reward);
  box-shadow: var(--shadow-1);
  border-radius: var(--radius-3);
  padding: var(--space-5);
  display: grid;
  gap: var(--space-2);
}

.backLink {
  display: inline-block;
  margin-bottom: var(--space-3);
  color: var(--color-brand-primary);
  font-weight: var(--font-weight-semibold);
  transition: color var(--transition-fast);
}

.backLink:hover {
  text-decoration: underline;
}

h1 {
  margin: 0;
  font-family: var(--font-family-serif), sans-serif;
  font-size: var(--font-size-28);
  line-height: var(--font-leading-28);
  color: var(--color-text-primary);
}

.eyebrow {
  color: var(--notes-eyebrow);
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  margin-bottom: var(--space-2);
}

h1 {
  margin: 0 0 var(--space-2) 0;
  color: var(--notes-title);
}

.subtitle {
  margin: 0;
  color: var(--notes-summary);
}

.errorMessage,
.emptyMessage {
  padding: var(--space-5);
  border-radius: var(--radius-3);
  background: var(--notes-missing-bg);
  border: var(--border-width-hairline) solid var(--notes-missing-border);
}

.notesList {
  display: grid;
  gap: var(--space-4);
  width: 100%;
  max-width: 1180px;
}

.noteCard {
  padding: var(--space-4);
  border: var(--border-width-hairline) solid var(--color-border-default);
  border-radius: var(--radius-3);
  background: var(--color-bg-surface);
  box-shadow: var(--shadow-1);
}

.noteHeader {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--space-3);
  margin-bottom: var(--space-2);
}

.noteTitle {
  margin: 0;
  font-size: var(--font-size-18);
  color: var(--color-brand-primary);
}

.noteType {
  padding: var(--space-1) var(--space-3);
  border-radius: var(--radius-pill);
  background: var(--palette-ice-200); /* exception: palette-direct, type badge differentiates from surface */
  color: var(--color-brand-primary);
  font-size: var(--font-size-12);
  font-weight: var(--font-weight-bold);
}

.noteSource {
  margin: 0 0 var(--space-2);
  font-size: var(--font-size-14);
  color: var(--color-text-secondary);
}

.noteDate {
  margin: 0 0 var(--space-3);
  font-size: var(--font-size-12);
  color: var(--color-text-secondary);
}

.noteContent {
  margin: var(--space-3) 0 0;
  line-height: 1.6; /* deliberate: reading-optimised line-height for note body */
  color: var(--color-text-primary);
}
</style>
