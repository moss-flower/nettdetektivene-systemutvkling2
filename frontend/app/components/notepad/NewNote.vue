<script setup lang="ts">
import pencil from "~/assets/images/image.png";

const emit = defineEmits<{
  (e: "click"): void;
}>();
</script>

<template>
  <div class="newNote">
    <img class="newNoteImage" :src="pencil" alt="Pencil" />
    <button class="newNoteButton" @click="emit('click')">New note</button>
  </div>
</template>

<style scoped>
.newNote {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.newNoteImage {
  width: 2rem;
  height: 2rem;
  object-fit: contain;
}

.newNoteButton {
  width: fit-content;
}
</style>