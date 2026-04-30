<script setup lang="ts">
import { mapNotepadEntryFromResponse, useNotepad } from "~/composables/useNotepad";
import { routePaths } from "~/composables/utils/routePaths";

const selectedFilter = ref<string | null>(null);
const deleteError = ref("");
const deletingUuid = ref("");
const { getMyNotes, deleteNote } = useNotepad();

const { data: notesPage, error: notesError, refresh } = await useAsyncData(
  "teacher-notes",
  () => getMyNotes({ size: 100 }),
);

const allEntries = computed(() => {
  return (notesPage.value?.content ?? []).map(mapNotepadEntryFromResponse);
});

const availableSources = computed(() => {
  return [...new Set(allEntries.value.map((entry) => entry.source))].sort();
});

const filteredEntries = computed(() => {
  if (!selectedFilter.value) {
    return allEntries.value;
  }

  return allEntries.value.filter((entry) => entry.source === selectedFilter.value);
});

function handleFilterSelect(source: string | null) {
  selectedFilter.value = source;
}

function handleCreateNote() {
  void navigateTo({
    path: routePaths.teacherNoteEditor,
    query: { returnTo: routePaths.teacherNotes },
  });
}

function handleEditNote(uuid: string) {
  void navigateTo({
    path: routePaths.teacherNoteEditor,
    query: { uid: uuid, returnTo: routePaths.teacherNotes },
  });
}

async function handleDeleteNote(uuid: string) {
  deleteError.value = "";

  if (import.meta.client && !window.confirm("Vil du slette dette notatet?")) {
    return;
  }

  deletingUuid.value = uuid;

  try {
    await deleteNote(uuid);
    await refresh();
  } catch {
    deleteError.value = "Kunne ikke slette notatet akkurat nå.";
  } finally {
    deletingUuid.value = "";
  }
}
</script>

<template>
  <section class="teacherNotesPage">
    <header class="teacherNotesHeader">
      <div>
        <p class="eyebrow">Lærerverktøy</p>
        <h1>Notatoversikt</h1>
        <p class="intro">Opprett, rediger og slett notater fra ett sted.</p>
      </div>

      <div class="headerActions">
        <button class="createButton" type="button" @click="handleCreateNote">
          + Nytt notat
        </button>
      </div>
    </header>

    <div class="teacherNotesLayout">
      <aside class="sidePanel">
        <NotepadFilters
          :active-filter="selectedFilter"
          :levels="availableSources"
          @select="handleFilterSelect"
        />
      </aside>

      <section class="notesPanel">
        <p v-if="notesError" class="statusMessage">Kunne ikke laste notater akkurat nå.</p>
        <p v-else-if="deleteError" class="statusMessage statusMessageError">{{ deleteError }}</p>
        <p v-else-if="filteredEntries.length === 0" class="statusMessage">Ingen notater å vise.</p>

        <div v-else class="notesList">
          <article v-for="note in filteredEntries" :key="note.publicUuid" class="noteCard">
            <NotepadEntry :entry="note" />

            <div class="noteActions">
              <button class="actionButton" type="button" @click="handleEditNote(note.publicUuid)">
                Rediger
              </button>
              <button
                class="actionButton actionButtonDanger"
                type="button"
                :disabled="deletingUuid === note.publicUuid"
                @click="handleDeleteNote(note.publicUuid)"
              >
                {{ deletingUuid === note.publicUuid ? "Sletter..." : "Slett" }}
              </button>
            </div>
          </article>
        </div>
      </section>
    </div>
  </section>
</template>

<style scoped>
.teacherNotesPage {
  width: 100%;
  max-width: 1320px;
  margin: 0 auto;
  padding: var(--space-4);
  display: grid;
  gap: var(--space-4);
}

.teacherNotesHeader,
.sidePanel,
.notesPanel {
  border: var(--border-width-hairline) solid var(--color-border-default);
  border-radius: var(--radius-3);
  background: var(--color-bg-surface);
  box-shadow: var(--shadow-1);
}

.teacherNotesHeader {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: var(--space-4);
  padding: var(--space-5);
}

.eyebrow,
h1,
.intro,
.statusMessage {
  margin: 0;
}

.eyebrow {
  margin-bottom: var(--space-2);
  color: var(--color-brand-primary);
  font-size: var(--font-size-14);
  font-weight: var(--font-weight-bold);
  letter-spacing: var(--font-tracking-wider);
  text-transform: uppercase;
}

.intro {
  margin-top: var(--space-2);
  color: var(--color-text-secondary);
}

.createButton,
.actionButton {
  border-radius: var(--radius-pill);
  font: inherit;
  font-weight: var(--font-weight-bold);
  cursor: pointer;
}

.createButton {
  border: 0;
  padding: 0.9rem 1.2rem;
  background: var(--color-brand-primary);
  color: var(--palette-navy-900);
}

.teacherNotesLayout {
  display: grid;
  grid-template-columns: minmax(15rem, 18rem) minmax(0, 1fr);
  gap: var(--space-4);
  align-items: start;
}

.sidePanel,
.notesPanel {
  padding: var(--space-4);
}

.notesList {
  display: grid;
  gap: var(--space-4);
}

.noteCard {
  display: grid;
  gap: var(--space-3);
}

.noteActions {
  display: flex;
  justify-content: flex-end;
  gap: var(--space-3);
  flex-wrap: wrap;
}

.actionButton {
  min-width: 8rem;
  padding: 0.8rem 1rem;
  border: var(--border-width-hairline) solid var(--color-border-strong);
  background: transparent;
  color: var(--color-text-primary);
}

.actionButtonDanger {
  border-color: var(--palette-red-700);
  color: var(--palette-red-700);
}

.actionButton:disabled {
  cursor: not-allowed;
  opacity: 0.6;
}

.statusMessage {
  color: var(--color-text-secondary);
}

.statusMessageError {
  color: var(--palette-red-700);
}

@media (max-width: 900px) {
  .teacherNotesLayout {
    grid-template-columns: 1fr;
  }

  .teacherNotesHeader {
    flex-direction: column;
    align-items: stretch;
  }
}
</style>
