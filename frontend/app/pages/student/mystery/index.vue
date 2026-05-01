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
const isSubmitting = ref(false);

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
  isLoading,
  loadMysteryData,
  submitMysteryExample,
} = useStudentMystery();

await useAsyncData('student-mystery-data', loadMysteryData);

const canSubmit = computed(() => {
  return currentMystery.value !== null && selectedFile.value !== null && studentMessage.value.trim().length >= 8;
});

const hasPlayableMystery = computed(() => Boolean(currentMystery.value?.exerciseUid));

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

async function sendMysterySubmission() {
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

  isSubmitting.value = true;

  try {
    await submitMysteryExample(studentMessage.value, selectedFile.value);
    successMessage.value = 'Innleveringen er sendt til lærer for vurdering.';
    clearForm();
  } catch (error) {
    errorMessage.value = error instanceof Error ? error.message : 'Kunne ikke sende inn bidraget akkurat nå.';
  } finally {
    isSubmitting.value = false;
  }
}
</script>

<template>
  <section class="studentMystery">
    <div class="studentMysteryContent">
      <header class="studentMysteryHeader">
        <p class="studentMysteryEyebrow">Ukens mysterium</p>
        <div class="mysteryTitleRow">
          <h1>{{ currentMystery?.title ?? 'Ukens mysterium' }}</h1>
          <p class="studentMysteryWeek mysteryWeekHero">{{ currentMystery?.weekLabel ?? 'Ingen sak valgt' }}</p>
        </div>
        <p class="studentMysteryIntro">
          {{ hasPlayableMystery ? 'Spill ukens mysterium, eller tips læreren om en senere sak.' : currentMystery ? 'Læreren samler forslag før spillet blir klart.' : 'Læreren har ikke valgt et mysterium for klassen ennå.' }}
        </p>
      </header>

      <section class="studentMysterySection mysterySectionFeature" aria-labelledby="current-mystery-heading">
        <div class="mysteryPlayCard">
          <h2 id="current-mystery-heading" class="mysteryPlayTitle">Denne ukens sak</h2>
          <p v-if="isLoading" class="studentMysteryHelper">Laster mysteriet...</p>
          <p class="studentMysteryHelper">{{ currentMystery?.prompt ?? 'Det er ikke publisert noen aktiv sak ennå.' }}</p>
          <p v-if="currentMystery?.clue" class="studentMysteryMeta"><strong>Tips:</strong> {{ currentMystery.clue }}</p>
          <p v-if="currentMystery && !hasPlayableMystery" class="studentMysteryMeta">Spillet blir tilgjengelig når læreren har valgt et elevforslag og laget saken ferdig.</p>

          <div class="mysteryActionsRow">
            <NuxtLink :to="routePaths.studentMysteryPlay" class="mysteryPlayAction" :aria-disabled="!hasPlayableMystery" :tabindex="hasPlayableMystery ? 0 : -1" :class="{ mysteryPlayActionDisabled: !hasPlayableMystery }">
              Spill ukens mysterium
            </NuxtLink>

            <button
              type="button"
              class="mysterySecondaryAction"
              @click="showPreviousMysteries = true"
            >
              Se tidligere mysterier
            </button>

            <button
              type="button"
              class="mysterySecondaryAction"
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
      class="studentMysteryDialog"
      @click.self="showSuggestionDialog = false"
    >
      <div
        class="mysteryDialogPanel"
        role="dialog"
        aria-modal="true"
        aria-labelledby="suggestion-dialog-title"
      >
        <div class="mysteryDialogHeader">
          <div class="mysterySectionHeader">
            <p class="studentMysteryEyebrow">Neste uke</p>
            <h2 id="suggestion-dialog-title">Tips læreren om et nytt mysterium</h2>
            <p class="studentMysteryHelper">{{ currentMystery?.prompt ?? 'Beskriv et innlegg eller bilde som kan passe til et senere mysterium.' }}</p>
            <p class="studentMysteryHelper">Send inn et bilde og en kort forklaring. Læreren kan bruke det i et senere mysterium.</p>
            <p v-if="currentMystery?.clue" class="studentMysteryMeta"><strong>Tips:</strong> {{ currentMystery.clue }}</p>
            <p v-if="!currentMystery" class="studentMysteryError">Klassen har ikke et aktivt ukens mysterium ennå. Læreren må velge en sak før du kan sende inn forslag.</p>
          </div>

          <button
            type="button"
            class="mysteryDialogClose"
            aria-label="Lukk dialog"
            @click="showSuggestionDialog = false"
          >
            Lukk
          </button>
        </div>

        <div class="mysteryDialogBody">
          <label class="studentMysteryField mysteryFieldUpload">
            <span>Bilde</span>
            <span class="mysteryUploadControl">
              <input
                :key="fileInputKey"
                class="mysteryFileInput"
                type="file"
                accept="image/*"
                :disabled="!currentMystery || isSubmitting"
                @change="handleFileChange"
              >
              <span class="mysteryUploadMain">
                <span class="mysteryUploadButton">Velg bilde</span>
                <span class="mysteryUploadText">{{ selectedFile ? selectedFile.name : 'Ingen fil valgt ennå' }}</span>
              </span>
              <img
                v-if="previewUrl"
                :src="previewUrl"
                alt="Forhåndsvisning av valgt bilde"
                class="mysteryUploadPreview"
              >
            </span>
          </label>

          <label class="studentMysteryField">
            <span>Kort melding</span>
            <textarea
              v-model="studentMessage"
              rows="4"
              maxlength="280"
              placeholder="For eksempel: Overskriften virker overdrevet og prøver å gjøre meg redd."
            />
          </label>

          <div class="mysteryFormFooter">
            <p class="studentMysteryMeta">{{ studentMessage.length }} / 280</p>
            <button
              type="button"
              class="studentMysterySubmit"
              :disabled="!canSubmit || isSubmitting"
              @click="sendMysterySubmission"
            >
              {{ isSubmitting ? 'Sender...' : 'Send til lærer' }}
            </button>
          </div>

          <p v-if="errorMessage" class="studentMysteryError">{{ errorMessage }}</p>
          <p v-if="successMessage" class="studentMysterySuccess">{{ successMessage }}</p>
        </div>

        <section
          v-if="submissionHistory.length > 0"
          class="studentMysterySection mysterySectionCompact"
          aria-labelledby="submissions-heading"
        >
          <div class="mysterySectionHeader">
            <h2 id="submissions-heading">Siste innsendinger</h2>
          </div>

          <ul class="studentMysteryList">
            <li v-for="submission in submissionHistory.slice(0, 3)" :key="submission.id" class="mysteryListItem">
              <div class="mysteryStatusRow">
                <strong>{{ submission.status }}</strong>
                <span>{{ submission.screenshotName }}</span>
              </div>
              <p>{{ submission.message }}</p>
              <a :href="submission.screenshotUrl" target="_blank" rel="noreferrer" class="studentMysteryMeta">Vis bildet</a>
            </li>
          </ul>
        </section>
      </div>
    </div>

    <div
      v-if="showPreviousMysteries"
      class="studentMysteryDialog"
      @click.self="showPreviousMysteries = false"
    >
      <div
        class="mysteryDialogPanel"
        role="dialog"
        aria-modal="true"
        aria-labelledby="previous-mysteries-dialog-title"
      >
        <div class="mysteryDialogHeader">
          <div class="mysterySectionHeader">
            <p class="studentMysteryEyebrow">Tidligere mysterier</p>
            <h2 id="previous-mysteries-dialog-title">Tidligere ukers saker</h2>
            <p class="studentMysteryHelper">Gamle mysterier du kan bla tilbake til senere.</p>
          </div>

          <button
            type="button"
            class="mysteryDialogClose"
            aria-label="Lukk dialog"
            @click="showPreviousMysteries = false"
          >
            Lukk
          </button>
        </div>

        <ul class="mysteryDialogList">
          <li v-if="previousMysteries.length === 0" class="mysteryDialogCard">
            <p>Ingen tidligere mysterier er registrert for klasserommet ennå.</p>
          </li>
          <li
            v-for="mystery in previousMysteries"
            :key="mystery.publicId"
            class="mysteryDialogCard"
          >
            <div class="dialogCardHeader">
              <p class="studentMysteryWeek">{{ mystery.weekLabel }}</p>
              <h3>{{ mystery.title }}</h3>
            </div>
            <p>{{ mystery.prompt }}</p>
            <p class="studentMysteryMeta"><strong>Tips:</strong> {{ mystery.clue }}</p>
          </li>
        </ul>
      </div>
    </div>
  </section>
</template>

<style scoped>
.studentMystery {
  min-height: 100%;
  padding: clamp(1rem, 2.5vw, 2rem);
  border-radius: 0;
  background: transparent;
}

.studentMysteryContent {
  width: min(820px, 100%);
  margin: 0 auto;
  display: grid;
  gap: clamp(1.25rem, 2vw, 2rem);
}

.studentMysteryHeader {
  display: grid;
  gap: var(--space-2);
  padding: 0;
}

.mysteryTitleRow {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: var(--space-3);
}

.studentMysteryEyebrow {
  color: var(--color-brand-primary);
  font-size: var(--font-size-14);
  font-weight: var(--font-weight-bold);
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.studentMysteryHeader h1 {
  margin: 0;
  max-width: 14ch;
  font-size: clamp(1.7rem, 3vw, 2.4rem);
  line-height: 1.02;
}

.studentMysteryIntro {
  max-width: 58ch;
  color: var(--color-text-secondary);
  font-size: clamp(var(--font-size-16), 1.5vw, 1.15rem);
}

.mysterySecondaryAction {
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

.mysteryPlayCard {
  display: grid;
  gap: var(--space-3);
  padding: clamp(1rem, 2vw, 1.35rem);
  border: 1px solid color-mix(in srgb, var(--color-brand-primary) 14%, var(--color-border-default));
  border-radius: clamp(1rem, 2vw, 1.2rem);
  background: color-mix(in srgb, var(--color-bg-surface) 90%, var(--color-semantic-info) 10%);
}

.mysteryPlayTitle {
  margin: 0;
  font-size: clamp(1.35rem, 2vw, 1.65rem);
  line-height: 1.05;
}

.mysteryActionsRow {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  flex-wrap: wrap;
}

.mysteryPlayAction {
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

.mysteryPlayActionDisabled {
  pointer-events: none;
  opacity: 0.55;
}

.studentMysterySection {
  display: grid;
  gap: var(--space-3);
  padding: var(--space-5) 0;
  border-top: 1px solid var(--color-border-default);
}

.mysterySectionFeature {
  padding: 0;
  border: 0;
  border-radius: 0;
  background: transparent;
}

.studentMysterySection:first-of-type {
  padding-top: 0;
}

.mysterySectionHeader {
  display: grid;
  gap: var(--space-1);
}

.studentMysterySection h2,
.studentMysterySection h3 {
  margin: 0;
}

.studentMysterySection h2 {
  font-size: clamp(1.5rem, 2.2vw, 2rem);
  line-height: 1.05;
}

.mysterySectionCompact {
  gap: var(--space-2);
}

.studentMysteryWeek {
  width: fit-content;
  padding: 0.3rem 0.65rem;
  border-radius: var(--radius-pill);
  background: color-mix(in srgb, var(--color-brand-primary) 10%, white);
  color: var(--color-brand-primary);
  font-size: var(--font-size-12);
  font-weight: var(--font-weight-bold);
}

.mysteryWeekHero {
  margin-top: 0.2rem;
  flex: 0 0 auto;
}

.studentMysteryHelper {
  color: var(--color-text-secondary);
}

.studentMysteryField {
  display: grid;
  gap: var(--space-2);
}

.studentMysteryField span {
  font-weight: var(--font-weight-semibold);
}

.mysteryFieldUpload {
  gap: var(--space-3);
}

.mysteryUploadControl {
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

.mysteryUploadMain {
  display: grid;
  gap: var(--space-2);
  min-width: 0;
  flex: 0 1 15rem;
}

.mysteryFileInput {
  position: absolute;
  inset: 0;
  opacity: 0;
  cursor: pointer;
}

.mysteryUploadButton {
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

.mysteryUploadText {
  color: var(--color-text-secondary);
  font-size: var(--font-size-14);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.mysteryUploadPreview {
  width: auto;
  height: auto;
  max-width: clamp(7rem, 22vw, 10rem);
  max-height: clamp(7rem, 22vw, 10rem);
  flex: 1 1 9rem;
  object-fit: contain;
  border-radius: var(--radius-2);
  border: var(--border-width-hairline) solid color-mix(in srgb, var(--color-brand-primary) 16%, var(--color-border-default));
  background: var(--color-bg-surface);
}

.studentMysteryField input,
.studentMysteryField textarea {
  width: 100%;
  padding: 0.85rem 1rem;
  border: 1px solid var(--color-border-default);
  border-radius: var(--radius-2);
  background: color-mix(in srgb, var(--color-bg-surface) 92%, var(--color-bg-canvas));
  color: var(--color-text-primary);
  font: inherit;
}

.studentMysteryField textarea {
  resize: vertical;
  min-height: 7rem;
  line-height: 1.5;
}

.mysteryFormFooter {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--space-3);
  flex-wrap: wrap;
}

.studentMysteryMeta {
  color: var(--color-text-secondary);
  font-size: var(--font-size-14);
}

.studentMysteryError {
  color: var(--color-semantic-error);
  font-weight: var(--font-weight-semibold);
}

.studentMysterySuccess {
  color: var(--color-semantic-success);
  font-weight: var(--font-weight-semibold);
}

.studentMysterySubmit {
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

.studentMysterySubmit:disabled {
  cursor: not-allowed;
  background: var(--color-disabled-bg);
  color: var(--color-disabled-text);
}

.studentMysterySubmit:not(:disabled):hover {
  background: color-mix(in srgb, var(--color-brand-primary) 92%, white);
}

.studentMysteryList {
  display: grid;
  gap: var(--space-3);
  list-style: none;
  padding: 0;
  margin: 0;
}

.mysteryListItem {
  display: grid;
  gap: var(--space-2);
  padding-bottom: var(--space-3);
  border-bottom: 1px solid var(--color-border-default);
}

.mysteryListItem:last-child {
  padding-bottom: 0;
  border-bottom: 0;
}

.mysteryListHeader,
.mysteryStatusRow {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: var(--space-3);
  flex-wrap: wrap;
}

.studentMysteryDialog {
  position: fixed;
  inset: 0;
  z-index: 50;
  display: grid;
  place-items: center;
  padding: clamp(var(--space-4), 2.5vw, var(--space-8));
  background: rgb(15 23 42 / 0.36);
}

.mysteryDialogPanel {
  width: min(820px, 100%);
  max-height: 80vh;
  overflow: auto;
  display: grid;
  gap: var(--space-4);
  padding: clamp(1rem, 2vw, 1.5rem);
  border-radius: clamp(1rem, 2vw, 1.25rem);
  border: 1px solid color-mix(in srgb, var(--color-brand-primary) 12%, var(--color-border-default));
  background: #ffffff;
  box-shadow: 0 24px 60px rgb(15 23 42 / 0.22);
}

.mysteryDialogHeader {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: var(--space-3);
}

.mysteryDialogBody {
  display: grid;
  gap: var(--space-3);
}

.mysteryDialogClose {
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

.mysteryDialogList {
  display: grid;
  gap: var(--space-3);
  list-style: none;
  padding: 0;
  margin: 0;
}

.mysteryDialogCard {
  display: grid;
  gap: var(--space-2);
  padding: 1rem;
  border: 1px solid color-mix(in srgb, var(--color-brand-primary) 12%, var(--color-border-default));
  border-radius: var(--radius-2);
  background: color-mix(in srgb, var(--color-bg-surface) 82%, var(--color-semantic-info) 18%);
}

.dialogCardHeader {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: var(--space-3);
  flex-wrap: wrap;
}

.dialogCardHeader h3 {
  max-width: 22ch;
}

</style>
