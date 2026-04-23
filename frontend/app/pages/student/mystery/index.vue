<script setup lang="ts">
import { routePaths } from '~/composables/utils/routePaths';
import { useStudentMystery } from '~/composables/useStudentMystery';

definePageMeta({
  layout: 'game',
});

const selectedFile = ref<File | null>(null);
const fileInputKey = ref(0);
const studentMessage = ref('');
const errorMessage = ref('');
const successMessage = ref('');
const previewUrl = ref('');
const showPreviousMysteries = ref(false);
const showSuggestionDialog = ref(false);

function handleDialogKeydown(event: KeyboardEvent) {
  if (event.key === 'Escape') {
    showPreviousMysteries.value = false;
    showSuggestionDialog.value = false;
  }
}

const {
  currentMystery,
  previousMysteries,
  submissionHistory,
  submitMysteryExample,
} = useStudentMystery();

const canSubmit = computed(() => {
  return selectedFile.value !== null && studentMessage.value.trim().length >= 8;
});

const anyDialogOpen = computed(() => showPreviousMysteries.value || showSuggestionDialog.value);

watch(selectedFile, (file, previousFile) => {
  if (previewUrl.value) {
    URL.revokeObjectURL(previewUrl.value);
    previewUrl.value = '';
  }

  if (file && file.type.startsWith('image/')) {
    previewUrl.value = URL.createObjectURL(file);
  }
});

onBeforeUnmount(() => {
  if (previewUrl.value) {
    URL.revokeObjectURL(previewUrl.value);
  }

  if (import.meta.client) {
    window.removeEventListener('keydown', handleDialogKeydown);
    document.body.style.overflow = '';
  }
});

watch(anyDialogOpen, (isOpen) => {
  if (import.meta.client) {
    document.body.style.overflow = isOpen ? 'hidden' : '';
  }
});

onMounted(() => {
  window.addEventListener('keydown', handleDialogKeydown);
});

function handleFileChange(event: Event) {
  const inputElement = event.target as HTMLInputElement;
  const [firstFile] = inputElement.files ?? [];
  selectedFile.value = firstFile ?? null;
  errorMessage.value = '';
  successMessage.value = '';
}

function clearForm() {
  selectedFile.value = null;
  studentMessage.value = '';
  fileInputKey.value += 1;
}

function sendMysterySubmission() {
  errorMessage.value = '';
  successMessage.value = '';

  if (!selectedFile.value) {
    errorMessage.value = 'Legg ved et bilde før du sender inn.';
    return;
  }

  if (studentMessage.value.trim().length < 8) {
    errorMessage.value = 'Skriv en kort forklaring (minst 8 tegn).';
    return;
  }

  submitMysteryExample(studentMessage.value, selectedFile.value.name);
  successMessage.value = 'Innleveringen er sendt til lærer for verifisering.';
  clearForm();
}
</script>

<template>
  <section class="student-mystery">
    <div class="student-mystery__content">
      <header class="student-mystery__header">
        <NuxtLink class="student-mystery__back" :to="routePaths.studentHome">Tilbake til hjem</NuxtLink>
        <p class="student-mystery__eyebrow">Ukens mysterium</p>
        <div class="student-mystery__titleRow">
          <h1>{{ currentMystery.title }}</h1>
          <p class="student-mystery__week student-mystery__week--hero">{{ currentMystery.weekLabel }}</p>
        </div>
        <p class="student-mystery__intro">
          Spill ukens mysterium, eller tips læreren om en senere sak.
        </p>
      </header>

      <section class="student-mystery__section student-mystery__section--feature" aria-labelledby="current-mystery-heading">
        <div class="student-mystery__playCard">
          <h2 id="current-mystery-heading" class="student-mystery__playTitle">Denne ukens sak</h2>

          <div class="student-mystery__actionsRow">
            <NuxtLink :to="routePaths.studentMysteryPlay" class="student-mystery__playAction">
              Spill ukens mysterium
            </NuxtLink>

            <button
              type="button"
              class="student-mystery__secondaryAction"
              @click="showPreviousMysteries = true"
            >
              Se tidligere mysterier
            </button>

            <button
              type="button"
              class="student-mystery__secondaryAction"
              @click="showSuggestionDialog = true"
            >
              Tips læreren om neste uke
            </button>
          </div>
        </div>
      </section>
    </div>

    <div
      v-if="showSuggestionDialog"
      class="student-mystery__dialog"
      @click.self="showSuggestionDialog = false"
    >
      <div
        class="student-mystery__dialogPanel"
        role="dialog"
        aria-modal="true"
        aria-labelledby="suggestion-dialog-title"
      >
        <div class="student-mystery__dialogHeader">
          <div class="student-mystery__sectionHeader">
            <p class="student-mystery__eyebrow">Neste uke</p>
            <h2 id="suggestion-dialog-title">Tips læreren om et nytt mysterium</h2>
            <p class="student-mystery__helper">{{ currentMystery.prompt }}</p>
            <p class="student-mystery__helper">Send inn et bilde og en kort forklaring. Læreren kan bruke det i et senere mysterium.</p>
            <p class="student-mystery__meta"><strong>Tips:</strong> {{ currentMystery.clue }}</p>
          </div>

          <button
            type="button"
            class="student-mystery__dialogClose"
            aria-label="Lukk dialog"
            @click="showSuggestionDialog = false"
          >
            Lukk
          </button>
        </div>

        <div class="student-mystery__dialogBody">
          <label class="student-mystery__field student-mystery__field--upload">
            <span>Bilde</span>
            <span class="student-mystery__uploadControl">
              <input
                :key="fileInputKey"
                class="student-mystery__fileInput"
                type="file"
                accept="image/*"
                @change="handleFileChange"
              >
              <span class="student-mystery__uploadMain">
                <span class="student-mystery__uploadButton">Velg bilde</span>
                <span class="student-mystery__uploadText">{{ selectedFile ? selectedFile.name : 'Ingen fil valgt ennå' }}</span>
              </span>
              <img
                v-if="previewUrl"
                :src="previewUrl"
                alt="Forhåndsvisning av valgt bilde"
                class="student-mystery__uploadPreview"
              >
            </span>
          </label>

          <label class="student-mystery__field">
            <span>Kort melding</span>
            <textarea
              v-model="studentMessage"
              rows="4"
              maxlength="280"
              placeholder="For eksempel: Overskriften virker overdrevet og prøver å gjøre meg redd."
            />
          </label>

          <div class="student-mystery__formFooter">
            <p class="student-mystery__meta">{{ studentMessage.length }} / 280</p>
            <button
              type="button"
              class="student-mystery__submit"
              :disabled="!canSubmit"
              @click="sendMysterySubmission"
            >
              Send til lærer
            </button>
          </div>

          <p v-if="errorMessage" class="student-mystery__error">{{ errorMessage }}</p>
          <p v-if="successMessage" class="student-mystery__success">{{ successMessage }}</p>
        </div>

        <section
          v-if="submissionHistory.length > 0"
          class="student-mystery__section student-mystery__section--compact"
          aria-labelledby="submissions-heading"
        >
          <div class="student-mystery__sectionHeader">
            <h2 id="submissions-heading">Siste innsendinger</h2>
          </div>

          <ul class="student-mystery__list">
            <li v-for="submission in submissionHistory.slice(0, 3)" :key="submission.id" class="student-mystery__listItem">
              <div class="student-mystery__statusRow">
                <strong>{{ submission.status }}</strong>
                <span>{{ submission.submittedAt }}</span>
              </div>
              <p>{{ submission.message }}</p>
              <p class="student-mystery__meta">{{ submission.screenshotName }}</p>
            </li>
          </ul>
        </section>
      </div>
    </div>

    <div
      v-if="showPreviousMysteries"
      class="student-mystery__dialog"
      @click.self="showPreviousMysteries = false"
    >
      <div
        class="student-mystery__dialogPanel"
        role="dialog"
        aria-modal="true"
        aria-labelledby="previous-mysteries-dialog-title"
      >
        <div class="student-mystery__dialogHeader">
          <div class="student-mystery__sectionHeader">
            <p class="student-mystery__eyebrow">Tidligere mysterier</p>
            <h2 id="previous-mysteries-dialog-title">Tidligere ukers saker</h2>
            <p class="student-mystery__helper">Gamle mysterier du kan bla tilbake til senere.</p>
          </div>

          <button
            type="button"
            class="student-mystery__dialogClose"
            aria-label="Lukk dialog"
            @click="showPreviousMysteries = false"
          >
            Lukk
          </button>
        </div>

        <ul class="student-mystery__dialogList">
          <li
            v-for="mystery in previousMysteries"
            :key="mystery.id"
            class="student-mystery__dialogCard"
          >
            <div class="student-mystery__dialogCardHeader">
              <p class="student-mystery__week">{{ mystery.weekLabel }}</p>
              <h3>{{ mystery.title }}</h3>
            </div>
            <p>{{ mystery.prompt }}</p>
            <p class="student-mystery__meta"><strong>Tips:</strong> {{ mystery.clue }}</p>
          </li>
        </ul>
      </div>
    </div>
  </section>
</template>

<style scoped>
.student-mystery {
  min-height: 100%;
  padding: clamp(1rem, 2.5vw, 2rem);
  border-radius: 0;
  background: transparent;
}

.student-mystery__content {
  width: min(820px, 100%);
  margin: 0 auto;
  display: grid;
  gap: clamp(1.25rem, 2vw, 2rem);
}

.student-mystery__header {
  display: grid;
  gap: var(--space-2);
  padding: 0;
}

.student-mystery__titleRow {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: var(--space-3);
}

.student-mystery__back {
  width: fit-content;
  color: var(--color-brand-primary);
  font-weight: var(--font-weight-semibold);
  text-decoration: none;
  line-height: 1;
}

.student-mystery__eyebrow {
  color: var(--color-brand-primary);
  font-size: var(--font-size-14);
  font-weight: var(--font-weight-bold);
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.student-mystery__header h1 {
  margin: 0;
  max-width: 14ch;
  font-size: clamp(1.7rem, 3vw, 2.4rem);
  line-height: 1.02;
}

.student-mystery__intro {
  max-width: 58ch;
  color: var(--color-text-secondary);
  font-size: clamp(1rem, 1.5vw, 1.15rem);
}

.student-mystery__secondaryAction {
  width: fit-content;
  min-height: var(--size-control-sm);
  padding: 0.55rem 0.9rem;
  border: 1px solid color-mix(in srgb, var(--color-brand-primary) 18%, var(--color-border-default));
  border-radius: var(--radius-pill);
  background: color-mix(in srgb, var(--color-bg-surface) 88%, var(--color-semantic-info) 12%);
  color: var(--color-brand-primary);
  font: inherit;
  font-weight: var(--font-weight-semibold);
  cursor: pointer;
}

.student-mystery__playCard {
  display: grid;
  gap: var(--space-3);
  padding: clamp(1rem, 2vw, 1.35rem);
  border: 1px solid color-mix(in srgb, var(--color-brand-primary) 14%, var(--color-border-default));
  border-radius: clamp(1rem, 2vw, 1.2rem);
  background: color-mix(in srgb, var(--color-bg-surface) 90%, var(--color-semantic-info) 10%);
}

.student-mystery__playTitle {
  margin: 0;
  font-size: clamp(1.35rem, 2vw, 1.65rem);
  line-height: 1.05;
}

.student-mystery__actionsRow {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  flex-wrap: wrap;
}

.student-mystery__playAction {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: var(--size-control-md);
  padding: 0.7rem 1.1rem;
  border-radius: var(--radius-2);
  background: var(--color-brand-primary);
  color: var(--color-text-onDark);
  font-weight: var(--font-weight-semibold);
  text-decoration: none;
  cursor: pointer;
}

.student-mystery__section {
  display: grid;
  gap: var(--space-3);
  padding: var(--space-5) 0;
  border-top: 1px solid var(--color-border-default);
}

.student-mystery__section--feature {
  padding: 0;
  border: 0;
  border-radius: 0;
  background: transparent;
}

.student-mystery__section:first-of-type {
  border-top: 0;
  padding-top: 0;
}

.student-mystery__sectionHeader {
  display: grid;
  gap: var(--space-1);
}

.student-mystery__section h2,
.student-mystery__section h3 {
  margin: 0;
}

.student-mystery__section h2 {
  font-size: clamp(1.5rem, 2.2vw, 2rem);
  line-height: 1.05;
}

.student-mystery__section--compact {
  gap: var(--space-2);
}

.student-mystery__week {
  width: fit-content;
  padding: 0.3rem 0.65rem;
  border-radius: var(--radius-pill);
  background: color-mix(in srgb, var(--color-brand-primary) 10%, white);
  color: var(--color-brand-primary);
  font-size: var(--font-size-12);
  font-weight: var(--font-weight-bold);
}

.student-mystery__week--hero {
  margin-top: 0.2rem;
  flex: 0 0 auto;
}

.student-mystery__helper {
  color: var(--color-text-secondary);
}

.student-mystery__field {
  display: grid;
  gap: var(--space-2);
}

.student-mystery__field span {
  font-weight: var(--font-weight-semibold);
}

.student-mystery__field--upload {
  gap: var(--space-3);
}

.student-mystery__uploadControl {
  position: relative;
  display: flex;
  align-items: center;
  gap: var(--space-3);
  justify-content: space-between;
  padding: 0.9rem 1rem;
  border: 1px dashed color-mix(in srgb, var(--color-brand-primary) 28%, var(--color-border-default));
  border-radius: var(--radius-2);
  background: color-mix(in srgb, var(--color-bg-surface) 78%, var(--color-semantic-info) 22%);
}

.student-mystery__uploadMain {
  display: grid;
  gap: var(--space-2);
  min-width: 0;
  flex: 0 1 15rem;
}

.student-mystery__fileInput {
  position: absolute;
  inset: 0;
  opacity: 0;
  cursor: pointer;
}

.student-mystery__uploadButton {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: var(--size-control-sm);
  padding: 0.55rem 0.9rem;
  border-radius: var(--radius-pill);
  background: var(--color-brand-primary);
  color: var(--color-text-onDark);
  font-size: var(--font-size-14);
  font-weight: var(--font-weight-semibold);
}

.student-mystery__uploadText {
  color: var(--color-text-secondary);
  font-size: var(--font-size-14);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.student-mystery__uploadPreview {
  width: auto;
  height: auto;
  max-width: clamp(7rem, 22vw, 10rem);
  max-height: clamp(7rem, 22vw, 10rem);
  flex: 1 1 9rem;
  object-fit: contain;
  border-radius: var(--radius-2);
  border: 1px solid color-mix(in srgb, var(--color-brand-primary) 16%, var(--color-border-default));
  background: var(--color-bg-surface);
}

.student-mystery__field input,
.student-mystery__field textarea {
  width: 100%;
  padding: 0.85rem 1rem;
  border: 1px solid var(--color-border-default);
  border-radius: var(--radius-2);
  background: color-mix(in srgb, var(--color-bg-surface) 92%, var(--color-bg-canvas));
  color: var(--color-text-primary);
  font: inherit;
}

.student-mystery__field textarea {
  resize: vertical;
  min-height: 7rem;
  line-height: 1.5;
}

.student-mystery__formFooter {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--space-3);
  flex-wrap: wrap;
}

.student-mystery__meta {
  color: var(--color-text-secondary);
  font-size: var(--font-size-14);
}

.student-mystery__error {
  color: var(--color-semantic-error);
  font-weight: var(--font-weight-semibold);
}

.student-mystery__success {
  color: var(--color-semantic-success);
  font-weight: var(--font-weight-semibold);
}

.student-mystery__submit {
  min-height: var(--size-control-md);
  padding: 0.7rem 1.1rem;
  border: 0;
  border-radius: var(--radius-2);
  background: var(--color-brand-primary);
  color: var(--color-text-onDark);
  font: inherit;
  font-weight: var(--font-weight-semibold);
  cursor: pointer;
  transition: background-color var(--transition-fast);
}

.student-mystery__submit:disabled {
  cursor: not-allowed;
  background: var(--color-disabled-bg);
  color: var(--color-disabled-text);
}

.student-mystery__submit:not(:disabled):hover {
  background: color-mix(in srgb, var(--color-brand-primary) 92%, white);
}

.student-mystery__list {
  display: grid;
  gap: var(--space-3);
  list-style: none;
  padding: 0;
  margin: 0;
}

.student-mystery__listItem {
  display: grid;
  gap: var(--space-2);
  padding-bottom: var(--space-3);
  border-bottom: 1px solid var(--color-border-default);
}

.student-mystery__listItem:last-child {
  padding-bottom: 0;
  border-bottom: 0;
}

.student-mystery__listHeader,
.student-mystery__statusRow {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: var(--space-3);
  flex-wrap: wrap;
}

.student-mystery__dialog {
  position: fixed;
  inset: 0;
  z-index: 50;
  display: grid;
  place-items: center;
  padding: clamp(1rem, 2.5vw, 2rem);
  background: rgb(15 23 42 / 0.36);
}

.student-mystery__dialogPanel {
  width: min(820px, 100%);
  max-height: 80vh;
  overflow: auto;
  display: grid;
  gap: var(--space-4);
  padding: clamp(1rem, 2vw, 1.5rem);
  border-radius: clamp(1rem, 2vw, 1.25rem);
  background: color-mix(in srgb, var(--color-bg-surface) 94%, var(--color-semantic-info) 6%);
}

.student-mystery__dialogHeader {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: var(--space-3);
}

.student-mystery__dialogBody {
  display: grid;
  gap: var(--space-3);
}

.student-mystery__dialogClose {
  min-height: var(--size-control-sm);
  padding: 0.55rem 0.9rem;
  border: 0;
  border-radius: var(--radius-pill);
  background: color-mix(in srgb, var(--color-brand-primary) 10%, white);
  color: var(--color-brand-primary);
  font: inherit;
  font-weight: var(--font-weight-semibold);
  cursor: pointer;
}

.student-mystery__dialogList {
  display: grid;
  gap: var(--space-3);
  list-style: none;
  padding: 0;
  margin: 0;
}

.student-mystery__dialogCard {
  display: grid;
  gap: var(--space-2);
  padding: 1rem;
  border: 1px solid color-mix(in srgb, var(--color-brand-primary) 12%, var(--color-border-default));
  border-radius: var(--radius-2);
  background: color-mix(in srgb, var(--color-bg-surface) 82%, var(--color-semantic-info) 18%);
}

.student-mystery__dialogCardHeader {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: var(--space-3);
  flex-wrap: wrap;
}

.student-mystery__dialogCardHeader h3 {
  max-width: 22ch;
}

@media (max-width: 560px) {
  .student-mystery {
    padding: 0.75rem;
    border-radius: 1rem;
  }

  .student-mystery__content {
    gap: var(--space-4);
  }

  .student-mystery__titleRow {
    align-items: flex-start;
    flex-wrap: wrap;
  }

  .student-mystery__dialog {
    padding: 0.75rem;
  }

  .student-mystery__dialogHeader,
  .student-mystery__dialogCardHeader {
    flex-direction: column;
  }

  .student-mystery__dialogClose {
    width: 100%;
  }

  .student-mystery__header,
  .student-mystery__section--feature {
    padding: 0;
  }

  .student-mystery__section {
    padding: var(--space-4) 0;
  }

  .student-mystery__uploadControl {
    align-items: flex-start;
    flex-wrap: wrap;
  }

  .student-mystery__uploadPreview {
    max-width: 10rem;
    max-height: 10rem;
  }

  .student-mystery__formFooter {
    align-items: stretch;
  }

  .student-mystery__submit {
    width: 100%;
  }
}
</style>
