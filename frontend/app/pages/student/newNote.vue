<script setup lang="ts">
import { routePaths } from "~/composables/utils/routePaths";
import { useStations } from "~/composables/useStations";
import { useNotepad } from "~/composables/useNotepad";

const { getMyStations } = useStations();
const { createNote } = useNotepad();

type NewNoteForm = {
  title: string;
  source: string;
  content: string;
};

const form = ref<NewNoteForm>({
  title: "",
  source: "",
  content: "",
});

const errorMessage = ref("");
const isSaving = ref(false);

const { data: stationData } = await useAsyncData(
  "student-note-stations",
  () => getMyStations(),
);

const stationOptions = computed(() => stationData.value?.stations ?? []);

const isFormValid = computed(() => {
  return Boolean(
    form.value.title.trim() &&
      form.value.source.trim() &&
      form.value.content.trim(),
  );
});

async function handleSubmit() {
  errorMessage.value = "";

  if (!isFormValid.value) {
    errorMessage.value = "Fill in title, source, and content before saving.";
    return;
  }

  isSaving.value = true;

  try {
    await createNote({
      title: form.value.title.trim(),
      source: form.value.source.trim(),
      content: form.value.content.trim(),
      type: "OBSERVATION",
    });

    await navigateTo(routePaths.studentNotes);
  } catch {
    errorMessage.value = "Could not save note right now.";
  } finally {
    isSaving.value = false;
  }
}
</script>

<template>
  <section class="newNotePage">
    <header class="newNoteHeader">
      <p class="eyebrow">Notepad</p>
      <h1>Create New Note</h1>
      <p class="intro">
        Write down a title, where the note came from, and the note content.
      </p>
    </header>

    <form class="newNoteCard" @submit.prevent="handleSubmit">
      <label class="field">
        <span class="fieldLabel">Title</span>
        <input v-model="form.title" class="fieldInput" type="text" name="title" />
      </label>

      <label class="field">
        <span class="fieldLabel">Source</span>
        <select v-model="form.source" class="fieldInput" name="source">
          <option disabled value="">Choose a station</option>
          <option
            v-for="station in stationOptions"
            :key="station.uid"
            :value="station.title"
          >
            {{ station.title }}
          </option>
        </select>
      </label>

      <label class="field">
        <span class="fieldLabel">Content</span>
        <textarea
          v-model="form.content"
          class="fieldInput fieldTextarea"
          name="content"
          rows="8"
        ></textarea>
      </label>

      <p v-if="errorMessage" class="errorText">{{ errorMessage }}</p>
      <p v-else-if="stationOptions.length === 0" class="errorText">
        No stations available to use as a source.
      </p>

      <div class="actions">
        <NuxtLink class="secondaryButton" :to="routePaths.studentNotes">
          Cancel
        </NuxtLink>
        <button class="primaryButton" type="submit" :disabled="!isFormValid || isSaving">
          Save note
        </button>
      </div>
    </form>
  </section>
</template>

<style scoped>
.newNotePage {
  height: 100%;
  width: 100%;
  display: grid;
  gap: var(--space-6);
  align-content: start;
  padding: var(--layout-page-padding);
  background:
    radial-gradient(ellipse 70% 50% at 50% 20%, var(--palette-overlay-warm-glow), transparent 70%),
    repeating-linear-gradient(
      0deg,
      transparent 0,
      transparent 117px,
      var(--palette-overlay-dark-strong) 117px,
      var(--palette-overlay-dark-strong) 120px,
      var(--palette-overlay-warm-glow) 120px,
      var(--palette-overlay-warm-glow) 122px
    ),
    var(--palette-wood-900);
}

.newNoteHeader,
.newNoteCard {
  width: min(100%, 760px);
  margin: 0 auto;
}

.newNoteHeader {
  display: grid;
  gap: var(--space-2);
  color: var(--palette-cream-100);
}

.backButton {
  width: fit-content;
  padding: var(--space-3) var(--space-4);
  border-radius: var(--radius-pill);
  background: var(--palette-white);
  color: var(--palette-navy-900);
}

.eyebrow {
  margin: 0;
  color: var(--palette-gold-500);
  font-size: var(--font-size-14);
  font-weight: var(--font-weight-bold);
  letter-spacing: var(--font-tracking-wider);
  text-transform: uppercase;
}

h1 {
  margin: 0;
}

.intro {
  margin: 0;
  color: var(--palette-cream-300);
}

.newNoteCard {
  display: grid;
  gap: var(--space-4);
  padding: clamp(1.25rem, 2vw, 2rem);
  border-radius: var(--radius-3);
  background: linear-gradient(180deg, var(--palette-cream-100) 0%, var(--palette-cream-200) 100%);
  box-shadow: 0 var(--space-4) var(--space-6) var(--palette-overlay-dark-deep);
}

.field {
  display: grid;
  gap: var(--space-2);
}

.fieldLabel {
  color: var(--palette-cork-900);
  font-weight: var(--font-weight-bold);
}

.fieldInput {
  width: 100%;
  padding: 0.9rem 1rem;
  border: var(--border-width-hairline) solid var(--palette-gray-300);
  border-radius: var(--radius-2);
  background: var(--palette-white);
  color: var(--palette-cork-900);
  font: inherit;
  box-sizing: border-box;
}

.fieldInput:focus {
  outline: none;
  border-color: var(--palette-gold-600);
  box-shadow: 0 0 0 3px var(--palette-overlay-warm-glow);
}

.fieldTextarea {
  min-height: 12rem;
  resize: vertical;
}

.errorText {
  margin: 0;
  color: var(--palette-red-700);
  font-weight: var(--font-weight-semibold);
}

.actions {
  display: flex;
  justify-content: flex-end;
  gap: var(--space-3);
  flex-wrap: wrap;
}

.secondaryButton,
.primaryButton {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 9rem;
  padding: 0.85rem 1.15rem;
  border-radius: var(--radius-pill);
  font: inherit;
  font-weight: var(--font-weight-bold);
  text-decoration: none;
  cursor: pointer;
}

.secondaryButton {
  border: var(--border-width-hairline) solid var(--palette-cork-700);
  background: transparent;
  color: var(--palette-cork-900);
}

.primaryButton {
  border: 0;
  background: var(--palette-gold-600);
  color: var(--palette-cork-900);
}

.primaryButton:disabled {
  cursor: not-allowed;
  opacity: 0.6;
}

@media (max-width: 720px) {
  .actions {
    justify-content: stretch;
  }

  .secondaryButton,
  .primaryButton {
    flex: 1 1 100%;
  }
}
</style>
