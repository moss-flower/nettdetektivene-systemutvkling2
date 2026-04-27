<script setup lang="ts">
import pencil from "~/assets/images/pencil.png";

//Emits plain click event
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
  flex-direction: column;
  align-items: center;
  font-size: 2rem;
  gap: 0.5rem;
}

.newNoteImage {
  width: 80%;
  object-fit: contain;
}

.newNoteButton {
  width: fit-content;
  background-color: white;
}
</style>