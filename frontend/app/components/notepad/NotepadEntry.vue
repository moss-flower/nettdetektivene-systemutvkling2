<script setup lang="ts">
import type { NotepadEntry } from '~/composables/types/local';

//Takes in a single NotepadEntry
const props = defineProps<{
  entry: NotepadEntry
}>();

//Changes title masking based on entry type
const titlePrefix = computed(() => {
  if (props.entry.type === "tip") return "Tip:";
  if (props.entry.type === "info") return "Info:";
  if (props.entry.type === "observation") return "Observation:";
  return "";
});

</script>

<template>
  <article
    :class="[
      'notepadEntry',
      {
        tip: props.entry.type === 'tip',
        info: props.entry.type === 'info',
        observation: props.entry.type === 'observation',
      },
    ]"
  >
    <div class="meta">
      <h3 class="date">{{ entry.date }}</h3>
      <h3 class="source">{{ entry.source }}</h3>
    </div>
    <h4 class="title">{{ titlePrefix }} {{ props.entry.title }}</h4>
    <p class="content">{{ props.entry.content }}</p>
  </article>
</template>

<style scoped>
.notepadEntry {
  width: 100%;
  border-radius: 1.75rem;
  padding: 1.25rem 1.5rem 1.25rem 1.5rem;
  box-sizing: border-box;
  color: black;
}

.meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  margin-bottom: 1rem;
}

.date,
.source {
  margin: 0;
  font-size: 2rem;
}

.title {
  font-size: 1rem;
  font-weight: 700;
}

.content {
  margin: 0;
  font-size: 1rem;
}

.tip {
  border: 3px limegreen solid;
  background-color: rgb(169, 228, 153, 0.3);
}

.info {
  border: 3px crimson solid;
  background-color: rgba(228, 153, 153, 0.3);
}

.observation {
  border: 3px gray solid;
  background-color: rgba(218, 218, 218, 0.3);
}
</style>
