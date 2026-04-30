<script setup lang="ts">
import { routePaths } from "~/composables/utils/routePaths";
import { useNotepad } from "~/composables/useNotepad";
import type { BackendNotepadEntryType } from "~/composables/types/NotepadTypes";

const route = useRoute();
const { createNote } = useNotepad();

type TeacherNoteForm = {
  title: string;
  source: string;
  type: BackendNotepadEntryType;
  content: string;
};

const form = ref<TeacherNoteForm>({
  title: "",
  source: "",
  type: "TIP",
  content: "",
});

const isSaving = ref(false);
const errorMessage = ref("");
const returnTo = computed(() => typeof route.query.returnTo === "string" ? route.query.returnTo : routePaths.teacherOverview);

const noteTypes: Array<{ value: BackendNotepadEntryType; label: string }> = [
  { value: "TIP", label: "Tips" },
  { value: "INFO", label: "Info" },
  { value: "OBSERVATION", label: "Observasjon" },
];

const isFormValid = computed(() => {
  return Boolean(
    form.value.title.trim()
      && form.value.source.trim()
      && form.value.content.trim(),
  );
});

async function handleSubmit() {
  errorMessage.value = "";

  if (!isFormValid.value) {
    errorMessage.value = "Fyll inn tittel, kilde, type og innhold før du lagrer.";
    return;
  }

  isSaving.value = true;

  try {
    await createNote({
      title: form.value.title.trim(),
      source: form.value.source.trim(),
      type: form.value.type,
      content: form.value.content.trim(),
    });

    await navigateTo(returnTo.value);
  } catch {
    errorMessage.value = "Kunne ikke lagre notatet akkurat nå.";
  } finally {
    isSaving.value = false;
  }
}

function handleCancel() {
  void navigateTo(returnTo.value);
}
</script>

<template>
  <section class="teacherNotePage">
    <header class="teacherNoteHeader">
      <button class="backButton" type="button" @click="handleCancel">Tilbake</button>
      <p class="eyebrow">Lærerverktøy</p>
      <h1>Lag nytt notat</h1>
      <p class="intro">
        Fyll inn feltene under. Dato settes automatisk når notatet lagres.
      </p>
    </header>

    <form class="teacherNoteCard" @submit.prevent="handleSubmit">
      <label class="field">
        <span class="fieldLabel">Tittel</span>
        <input v-model="form.title" class="fieldInput" type="text" name="title" />
      </label>

      <div class="fieldRow">
        <label class="field">
          <span class="fieldLabel">Kilde</span>
          <input v-model="form.source" class="fieldInput" type="text" name="source" />
        </label>

        <label class="field">
          <span class="fieldLabel">Type</span>
          <select v-model="form.type" class="fieldInput" name="type">
            <option v-for="option in noteTypes" :key="option.value" :value="option.value">
              {{ option.label }}
            </option>
          </select>
        </label>
      </div>

      <label class="field">
        <span class="fieldLabel">Notatinnhold</span>
        <textarea
          v-model="form.content"
          class="fieldInput fieldTextarea"
          name="content"
          rows="9"
        />
      </label>

      <p v-if="errorMessage" class="errorText">{{ errorMessage }}</p>

      <div class="actions">
        <button class="secondaryButton" type="button" @click="handleCancel">
          Avbryt
        </button>
        <button class="primaryButton" type="submit" :disabled="!isFormValid || isSaving">
          {{ isSaving ? "Lagrer..." : "Lagre notat" }}
        </button>
      </div>
    </form>
  </section>
</template>

<style scoped>
.teacherNotePage {
  width: 100%;
  max-width: 960px;
  margin: 0 auto;
  padding: var(--space-4);
  display: grid;
  gap: var(--space-4);
}

.teacherNoteHeader,
.teacherNoteCard {
  border: var(--border-width-hairline) solid var(--color-border-default);
  border-radius: var(--radius-3);
  background: var(--color-bg-surface);
  box-shadow: var(--shadow-1);
}

.teacherNoteHeader {
  display: grid;
  gap: var(--space-2);
  padding: var(--space-5);
  background:
    radial-gradient(circle at top right, rgba(245, 202, 46, 0.16), transparent 25%),
    linear-gradient(180deg, rgba(42, 63, 106, 0.04), rgba(42, 63, 106, 0)),
    var(--color-bg-surface);
}

.backButton {
  width: fit-content;
  padding: 0;
  border: 0;
  background: transparent;
  color: var(--color-brand-primary);
  font: inherit;
  font-weight: 600;
  cursor: pointer;
}

.backButton:hover {
  text-decoration: underline;
}

.eyebrow {
  margin: 0;
  color: var(--color-brand-primary);
  font-size: var(--font-size-14);
  font-weight: var(--font-weight-bold);
  letter-spacing: var(--font-tracking-wider);
  text-transform: uppercase;
}

h1,
.intro,
.errorText {
  margin: 0;
}

.intro {
  color: var(--color-text-secondary);
}

.teacherNoteCard {
  display: grid;
  gap: var(--space-4);
  padding: clamp(1.25rem, 2vw, 2rem);
}

.field,
.fieldRow {
  display: grid;
  gap: var(--space-2);
}

.fieldRow {
  grid-template-columns: minmax(0, 1fr) minmax(14rem, 18rem);
  gap: var(--space-4);
}

.fieldLabel {
  color: var(--color-text-primary);
  font-weight: var(--font-weight-bold);
}

.fieldInput {
  width: 100%;
  padding: 0.9rem 1rem;
  border: var(--border-width-hairline) solid var(--color-border-default);
  border-radius: var(--radius-2);
  background: var(--palette-white);
  color: var(--color-text-primary);
  font: inherit;
  box-sizing: border-box;
}

.fieldInput:focus {
  outline: none;
  border-color: var(--color-brand-primary);
  box-shadow: 0 0 0 3px rgba(245, 202, 46, 0.18);
}

.fieldTextarea {
  min-height: 14rem;
  resize: vertical;
}

.errorText {
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
  cursor: pointer;
}

.secondaryButton {
  border: var(--border-width-hairline) solid var(--color-border-strong);
  background: transparent;
  color: var(--color-text-primary);
}

.primaryButton {
  border: 0;
  background: var(--color-brand-primary);
  color: var(--palette-navy-900);
}

.primaryButton:disabled,
.secondaryButton:disabled {
  cursor: not-allowed;
  opacity: 0.6;
}

@media (max-width: 720px) {
  .teacherNotePage {
    padding: var(--space-3);
  }

  .fieldRow {
    grid-template-columns: 1fr;
  }

  .actions {
    justify-content: stretch;
  }

  .secondaryButton,
  .primaryButton {
    width: 100%;
  }
}
</style>
