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
  () => (classroomId.value ? teacher.getClassroomById(classroomId.value).catch(() => null) : null),
  { watch: [classroomId] },
);

const { data: notesPage, error: notesError } = await useAsyncData(
  'teacher-student-notes',
  () => {
    if (!studentId.value) return null;
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
  width: 100%;
  max-width: 900px;
  margin: 0 auto;
  padding: var(--layout-page-padding);
  color: var(--color-text-primary);
}

.pageHeader {
  margin-bottom: var(--space-5);
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

.subtitle {
  margin: 0;
  color: var(--color-text-secondary);
}

.errorMessage,
.emptyMessage {
  padding: var(--space-4);
  border-radius: var(--radius-3);
  background: var(--color-bg-surface);
  color: var(--color-text-secondary);
}

.notesList {
  display: grid;
  gap: var(--space-4);
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
