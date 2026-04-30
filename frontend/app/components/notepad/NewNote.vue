<script setup lang="ts">
import pencil from "~/assets/ui-icons/Pencil.png";

//Emits plain click event
const emit = defineEmits<{
  (e: "click"): void;
}>();
</script>

<template>
  <button class="newNote" @click="emit('click')">
    <img class="newNoteImage" :src="pencil" alt="Pencil" />
    <div class="newNoteButton">Nytt notat</div>
  </button>
</template>

<style scoped>
.newNote {
  display: flex;
  flex-direction: column;
  align-items: center;
  font-size: 2rem;
  font-weight: bold;
  border-radius: var(--radius-2);
  background-color: var(--palette-cream-200);
  gap: var(--space-2);
}

.newNoteImage {
  width: 70%;
  object-fit: contain;
}

.newNoteButton {
  width: fit-content;
  padding: var(--space-2);
  border-radius: var(--radius-2);
}
</style>