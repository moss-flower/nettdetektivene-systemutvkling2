<script setup lang="ts">
import type { NotepadEntry } from "~/composables/types/NotepadTypes.ts";

//Takes in a list of NotepadEntry; to be dynamically allocated into NotepadEntry elements
const props = defineProps<{
  notes: NotepadEntry[];
}>();
</script>

<template>
  <div class="notepadFrame">
    <div class="notesScroll">
      <NotepadEntry
        v-for="note in props.notes"
        :key="`${note.date}-${note.title}`"
        :entry="note"
      />
    </div>
  </div>
</template>

<style scoped>
.notepadFrame {
  height: 100%;
  min-height: 0;
  padding: 7% 7% 5%;
  box-sizing: border-box;
  overflow: hidden;

  display: flex;
  flex-direction: column;

  background-image: url("~/assets/images/notepad.png");
  background-repeat: no-repeat;
  background-position: center;
  background-size: 100% 100%;
}

.notesScroll {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  overflow-x: hidden;

  display: flex;
  flex-direction: column;
  gap: 1rem;
}
</style>