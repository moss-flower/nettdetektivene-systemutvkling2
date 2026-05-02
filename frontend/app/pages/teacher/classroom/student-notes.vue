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
        :to="routePathsWithString(routePaths.teacherClassroomStudentProgress, `?classroomId=${classroomId}&studentId=${studentId}`)"
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
  padding: var(--layout-page-padding-comfortable);
  min-height: 100dvh;
  width: 100%;
  display: grid;
  place-items: center;
}

.pageHeader {
  display: grid;
  gap: var(--space-3);
  width: 100%;
  max-width: 1180px;
  padding: clamp(1.25rem, 3vw, 2rem);
  border: var(--border-width-hairline) solid var(--notes-border);
  border-radius: var(--radius-4);
  background: linear-gradient(
    135deg,
    var(--notes-bg-top) 0%,
    var(--notes-bg-bottom) 100%
  );
  box-shadow: var(--shadow-2);
}

.backLink {
  display: inline-block;
  margin-bottom: var(--space-3);
  color: var(--color-brand-primary);
  font-weight: 600;
  text-decoration: none;
  transition: color 0.15s ease;
}

.backLink:hover {
  color: var(--color-brand-secondary);
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
  font-size: 1.1rem;
  color: var(--color-brand-primary);
}

.noteType {
  padding: 0.25rem 0.75rem;
  border-radius: var(--radius-pill);
  background: var(--palette-blue-100);
  color: var(--palette-blue-700);
  font-size: 0.85rem;
  font-weight: 600;
}

.noteSource {
  margin: 0 0 var(--space-2);
  font-size: 0.9rem;
  color: var(--color-text-secondary);
}

.noteDate {
  margin: 0 0 var(--space-3);
  font-size: 0.85rem;
  color: var(--color-text-muted);
}

.noteContent {
  margin: var(--space-3) 0 0;
  line-height: 1.6;
  color: var(--color-text-primary);
}
</style>
