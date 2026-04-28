<script setup lang="ts">
import NewNote from "~/components/notepad/NewNote.vue";
import NotepadFilters from "~/components/notepad/NotepadFilters.vue";
import NotepadFrame from "~/components/notepad/NotepadFrame.vue";
import { routePaths } from "~/composables/utils/routePaths";
import { mapNotepadEntryFromResponse, useNotepad } from "~/composables/useNotepad";

//Holds selected filter as string
const selectedFilter = ref<string | null>(null);
const { getMyNotes } = useNotepad();

const { data: notesPage, error: notesError } = await useAsyncData(
  "student-notes",
  () => getMyNotes(),
);

const allEntries = computed(() => {
  return (notesPage.value?.content ?? []).map(mapNotepadEntryFromResponse);
});

const availableSources = computed(() => {
  return [...new Set(allEntries.value.map((entry) => entry.source))].sort();
});

//Dynamically updates entries based on current selectedFilter value
const filteredEntries = computed(() => {
  if (!selectedFilter.value) {
    return allEntries.value;
  }

  return allEntries.value.filter((entry) => entry.source === selectedFilter.value);
});

//Called on filter button click, updates selectedFilter value
function handleFilterSelect(level: string | null) {
  selectedFilter.value = level;
}

function handleNewNoteClick() {
  void navigateTo(routePaths.studentNewNote);
}
</script>

<template>
  <section class="notepadPage">
    <div class="leftColumn">
      <AvatarElement />
    </div>

    <div class="notepadWrapper">
      <p v-if="notesError" class="statusMessage">
        Could not load notes right now.
      </p>
      <NotepadFrame :notes="filteredEntries" />
    </div>

    <div class="rightColumn">
      <NewNote @click="handleNewNoteClick" />

      <NotepadFilters
        :active-filter="selectedFilter"
        :levels="availableSources"
        @select="handleFilterSelect"
      />
    </div>
  </section>
</template>

<style scoped>
.notepadPage {
  /* desk surface */
  --notepad-desk: var(--palette-wood-900);
  --notepad-desk-glow: var(--palette-overlay-warm-glow);
  --notepad-desk-seam: var(--palette-overlay-dark-strong);
  --notepad-desk-bevel: var(--palette-overlay-warm-glow);

  /* shared text on desk*/
  --notepad-text-onDesk: var(--palette-cream-100);
  --notepad-text-onDesk-muted: var(--palette-cream-300);

  display: grid;
  grid-template-columns: minmax(0, 0.22fr) minmax(0, 1fr) minmax(0, 0.22fr);
  gap: var(--space-4);
  height: 100%;
  min-height: 0;
  padding: var(--layout-page-padding);
  box-sizing: border-box;
  color: var(--notepad-text-onDesk);
  background:
      linear-gradient(
        135deg,
        var(--notepad-desk-glow) 0%,
        transparent 40%,
        transparent 60%,
        var(--palette-overlay-dark-strong) 100%
      ),
      repeating-linear-gradient(
        0deg,
        transparent 0,
        transparent 117px,
        var(--notepad-desk-seam) 117px,
        var(--notepad-desk-seam) 120px,
        var(--notepad-desk-bevel) 120px,
        var(--notepad-desk-bevel) 122px
      ),
      var(--notepad-desk);
  overflow: hidden;
}

.notepadWrapper {
  display: grid;
  gap: var(--space-3);
  min-width: 0;
  min-height: 0;
  height: 100%;
}

.statusMessage {
  margin: 0;
  color: var(--palette-cream-100);
  font-family: var(--font-family-body), sans-serif;
}

/* side rails, transparent; widgets inside carry their own surfaces */
.leftColumn,
.rightColumn {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
  height: 100%;
  min-height: 0;
}

.leftColumn {
  /* three children: top spacer + profile placeholder + back to menu arrow
     space between = arrow pushed to bottom of rail + lets placeholder sit in the middle */
  justify-content: center;
}

.rightColumn {
  justify-content: space-around;
}

.arrowButton {
  width: 70%;
  margin: 0 auto 15%;
}
</style>
