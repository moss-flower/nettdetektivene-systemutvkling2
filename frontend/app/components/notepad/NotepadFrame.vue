<script setup lang="ts">
import type { NotepadEntry } from "~/composables/types/local";

//Takes in a list of NotepadEntry; to be dynamically allocated into NotepadEntry elements
const props = defineProps<{
  notes: NotepadEntry[];
}>();
</script>

<template>
  <div class="notepadFrame">
    <NotepadEntry
      v-for="note in props.notes"
      :key="`${note.date}-${note.title}`"
      :entry="note"
    />
  </div>
</template>

<style scoped>
.notepadFrame {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  padding: 7% 7% 5%;

  height: 100%;

  background-image: url("~/assets/images/notepad.png");
  background-repeat: no-repeat;
  background-position: center;
  background-size: 100% 100%;
}
</style>