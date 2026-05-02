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
            <h2 id="suggestion-dialog-title">{{ currentMystery?.title ?? 'Ingen oppdrag publisert ennå' }}</h2>
            <p v-if="currentMystery" class="studentMysteryHelper">{{ currentMystery.prompt }}</p>
            <p v-if="currentMystery?.clue" class="studentMysteryMeta"><strong>Tips:</strong> {{ currentMystery.clue }}</p>
            <p v-if="!currentMystery" class="studentMysteryError">Læreren har ikke publisert et oppdrag for klassen ennå. Når oppdraget er klart, kan du sende inn bilde og forklaring her.</p>
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

        <div v-if="currentMystery" class="mysteryDialogBody">
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
          v-if="currentMystery && submissionHistory.length > 0"
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
  /* case-room wall */
  --mystery-wall: var(--palette-wood-900);
  --mystery-wall-seam: var(--palette-overlay-warm-glow);
  --mystery-wall-glow: var(--palette-overlay-warm-glow);

  /* Text on wall */
  --mystery-eyebrow: var(--palette-gold-500);
  --mystery-title: var(--palette-cream-100);
  --mystery-title-depth: var(--palette-overlay-dark-strong);
  --mystery-intro: var(--palette-cream-300);

  /* Week badge, classified stamp */
  --mystery-week-bg: var(--palette-navy-900);
  --mystery-week-text: var(--palette-cream-100);

  /* play card, active case file, gilt frame */
  --mystery-play-bg-top: var(--palette-cream-100);
  --mystery-play-bg-bottom: var(--palette-cream-200);
  --mystery-play-border: var(--palette-gold-600);
  --mystery-play-highlight: var(--palette-gold-400);
  --mystery-play-liner: var(--palette-cork-900);
  --mystery-play-shadow: var(--palette-overlay-dark-deep);
  --mystery-play-text: var(--palette-cork-900);

  /* Primary action */
  --mystery-action-bg: var(--palette-navy-900);
  --mystery-action-text: var(--palette-cream-100);
  --mystery-action-hover: var(--palette-navy-700);

  /* Secondary action */
  --mystery-secondary-bg: var(--palette-cream-100);
  --mystery-secondary-border: var(--palette-cork-900);
  --mystery-secondary-text: var(--palette-cork-900);
  --mystery-secondary-hover: var(--palette-overlay-cork-soft);

  /* card surface (sections, dialog, form area) */
  --mystery-card-bg-top: var(--palette-cream-100);
  --mystery-card-bg-bottom: var(--palette-cream-200);
  --mystery-card-border: var(--palette-overlay-cork-medium);
  --mystery-card-shadow: var(--palette-overlay-dark-deep);
  --mystery-card-text: var(--palette-cork-900);
  --mystery-card-muted: var(--palette-cork-580);

  /* form fields */
  --mystery-field-label: var(--palette-cork-900);
  --mystery-field-bg: var(--palette-cream-100);
  --mystery-field-border: var(--palette-gray-200);
  --mystery-field-text: var(--palette-cork-900);

  /* upload zone */
  --mystery-upload-bg: var(--palette-overlay-cork-soft);
  --mystery-upload-border: var(--palette-cork-500);
  --mystery-upload-text: var(--palette-cork-580);

  /* divider */
  --mystery-divider: var(--palette-overlay-cork-shadow);

  /* feedback */
  --mystery-error: var(--color-semantic-error);
  --mystery-success: var(--color-semantic-success);
  --mystery-meta: var(--palette-cork-580);

  /* Dialog */
  --mystery-dialog-overlay: var(--palette-overlay-dark-strong);

  position: relative;
  min-height: 100%;
  width: 100%;
  display: grid;
  gap: var(--space-6);
  align-content: start;
  padding: var(--layout-page-padding);
  border-radius: var(--radius-2);
  color: var(--mystery-title);
  background:
    radial-gradient(ellipse 60% 45% at 50% 25%, var(--mystery-wall-glow), transparent 70%),
    repeating-linear-gradient(
      90deg,
      transparent 0,
      transparent 180px,
      var(--mystery-wall-seam) 180px,
      var(--mystery-wall-seam) 181px
    ),
    var(--mystery-wall);
  overflow-x: hidden;
}

.studentMysteryContent {
  width: min(820px, 100%);
  margin: 0 auto;
  display: grid;
  gap: var(--space-6);
}

.studentMysteryHeader {
  display: grid;
  gap: var(--space-2);
}

.mysteryTitleRow {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: var(--space-3);
}

.studentMysteryEyebrow {
  margin: 0;
  color: var(--mystery-eyebrow);
  font-family: var(--font-family-body), sans-serif;
  font-size: var(--font-size-14);
  line-height: var(--font-leading-14);
  font-weight: var(--font-weight-bold);
  letter-spacing: var(--font-tracking-wider);
  text-transform: uppercase;
}

.studentMysteryHeader h1 {
  margin: 0;
  max-width: 14ch;
  font-family: var(--font-family-serif), sans-serif;
  /* fluid hero scale, deliberate exception */
  font-size: clamp(1.7rem, 3vw, 2.4rem);
  line-height: 1.05;
  font-weight: var(--font-weight-regular);
  color: var(--mystery-title);
  text-shadow: 0 1px 2px var(--mystery-title-depth);
}

.studentMysteryIntro {
  margin: 0;
  max-width: 58ch;
  color: var(--mystery-intro);
  font-family: var(--font-family-body), sans-serif;
  font-size: var(--font-size-16);
  line-height: var(--font-leading-16);
}

.mysterySecondaryAction {
  width: fit-content;
  min-height: var(--size-control-sm);
  padding: var(--space-3) var(--space-4);
  border: var(--border-width-hairline) solid var(--mystery-secondary-border);
  border-radius: var(--radius-pill);
  background: var(--mystery-secondary-bg);
  color: var(--mystery-secondary-text);
  font: inherit;
  font-weight: var(--font-weight-semibold);
  cursor: pointer;
  transition: background var(--transition-fast);
}

.mysterySecondaryAction:hover {
  background: var(--mystery-secondary-hover);
}

/* --- Play card: active case file with gilt frame --- */
.mysteryPlayCard {
  display: grid;
  gap: var(--space-3);
  padding: var(--space-5);
  border: var(--border-width-chunky) solid var(--mystery-play-border);
  border-radius: var(--radius-2);
  background: linear-gradient(
    180deg,
    var(--mystery-play-bg-top) 0%,
    var(--mystery-play-bg-bottom) 100%
  );
  box-shadow:
    inset 0 0 0 var(--border-width-thin) var(--mystery-play-liner),
    inset 0 var(--border-width-thin) 0 var(--mystery-play-highlight),
    var(--space-2) var(--space-4) var(--space-6) var(--mystery-play-shadow);
  color: var(--mystery-play-text);
}

.mysteryPlayTitle {
  margin: 0;
  font-family: var(--font-family-serif), sans-serif;
  font-size: var(--font-size-28);
  line-height: var(--font-leading-28);
  font-weight: var(--font-weight-semibold);
  color: var(--mystery-play-text);
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
  padding: var(--space-3) var(--space-4);
  border: none;
  border-radius: var(--radius-2);
  background: var(--mystery-action-bg);
  color: var(--mystery-action-text);
  font-family: var(--font-family-body), sans-serif;
  font-weight: var(--font-weight-semibold);
  text-decoration: none;
  cursor: pointer;
  transition: background var(--transition-fast);
}

.mysteryPlayActionDisabled {
  pointer-events: none;
  opacity: 0.55;
}

.mysteryPlayAction:hover {
  background: var(--mystery-action-hover);
}

/* --- Sections: cream case-report cards on the wall --- */
.studentMysterySection {
  display: grid;
  gap: var(--space-4);
  padding: var(--space-5);
  border: var(--border-width-hairline) solid var(--mystery-card-border);
  border-radius: var(--radius-2);
  background: linear-gradient(
    180deg,
    var(--mystery-card-bg-top) 0%,
    var(--mystery-card-bg-bottom) 100%
  );
  box-shadow: var(--space-2) var(--space-4) var(--space-6) var(--mystery-card-shadow);
  color: var(--mystery-card-text);
}

.mysterySectionFeature {
  padding: 0;
  border: none;
  border-radius: 0;
  background: transparent;
  box-shadow: none;
}

.mysterySectionHeader {
  display: grid;
  gap: var(--space-1);
}

.studentMysterySection h2,
.studentMysterySection h3 {
  margin: 0;
  font-family: var(--font-family-display), sans-serif;
}

.studentMysterySection h2 {
  font-size: var(--font-size-28);
  line-height: var(--font-leading-28);
  font-weight: var(--font-weight-bold);
  color: var(--mystery-card-text);
}

.mysterySectionCompact {
  gap: var(--space-2);
}

.studentMysteryWeek {
  width: fit-content;
  padding: var(--space-1) var(--space-3);
  border-radius: var(--radius-pill);
  background: var(--mystery-week-bg);
  color: var(--mystery-week-text);
  font-size: var(--font-size-12);
  line-height: var(--font-leading-12);
  font-weight: var(--font-weight-bold);
}

.mysteryWeekHero {
  margin-top: var(--space-1);
  flex: 0 0 auto;
}

.studentMysteryHelper {
  color: var(--mystery-card-muted);
  font-family: var(--font-family-body), sans-serif;
  font-size: var(--font-size-16);
  line-height: var(--font-leading-16);
}

/* --- form fields --- */
.studentMysteryField {
  display: grid;
  gap: var(--space-2);
}

.studentMysteryField > span {
  font-family: var(--font-family-body), sans-serif;
  font-weight: var(--font-weight-semibold);
  color: var(--mystery-field-label);
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
  padding: var(--space-4);
  border: var(--border-width-hairline) dashed var(--mystery-upload-border);
  border-radius: var(--radius-2);
  background: var(--mystery-upload-bg);
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
  padding: var(--space-3) var(--space-4);
  border-radius: var(--radius-pill);
  background: var(--mystery-action-bg);
  color: var(--mystery-action-text);
  font-family: var(--font-family-body), sans-serif;
  font-size: var(--font-size-14);
  line-height: var(--font-leading-14);
  font-weight: var(--font-weight-semibold);
}

.mysteryUploadText {
  color: var(--mystery-upload-text);
  font-family: var(--font-family-body), sans-serif;
  font-size: var(--font-size-14);
  line-height: var(--font-leading-14);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.mysteryUploadPreview {
  width: auto;
  height: auto;
  /* responsive image cap, deliberate fluid exception */
  max-width: clamp(7rem, 22vw, 10rem);
  max-height: clamp(7rem, 22vw, 10rem);
  flex: 1 1 9rem;
  object-fit: contain;
  border-radius: var(--radius-2);
  border: var(--border-width-hairline) solid var(--mystery-field-border);
  background: var(--mystery-field-bg);
}

.studentMysteryField input,
.studentMysteryField textarea {
  width: 100%;
  padding: var(--space-4);
  border: var(--border-width-hairline) solid var(--mystery-field-border);
  border-radius: var(--radius-2);
  background: var(--mystery-field-bg);
  color: var(--mystery-field-text);
  font: inherit;
}

.studentMysteryField textarea {
  resize: vertical;
  min-height: var(--space-8);
  line-height: var(--font-leading-16);
}

/* --- form footer --- */
.mysteryFormFooter {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--space-3);
  flex-wrap: wrap;
}

.studentMysteryMeta {
  color: var(--mystery-meta);
  font-family: var(--font-family-body), sans-serif;
  font-size: var(--font-size-14);
  line-height: var(--font-leading-14);
}

.studentMysteryError {
  color: var(--mystery-error);
  font-family: var(--font-family-body), sans-serif;
  font-weight: var(--font-weight-semibold);
}

.studentMysterySuccess {
  color: var(--mystery-success);
  font-family: var(--font-family-body), sans-serif;
  font-weight: var(--font-weight-semibold);
}

.studentMysterySubmit {
  min-height: var(--size-control-md);
  padding: var(--space-3) var(--space-4);
  border: none;
  border-radius: var(--radius-2);
  background: var(--mystery-action-bg);
  color: var(--mystery-action-text);
  font: inherit;
  font-weight: var(--font-weight-semibold);
  cursor: pointer;
  transition: background-color var(--transition-fast);
}

.studentMysterySubmit:disabled {
  cursor: not-allowed;
  background: var(--color-disabled-bg);
  color: var(--color-disabled-text);
  box-shadow: inset 0 0 0 var(--border-width-hairline) var(--mystery-divider);
}

.studentMysterySubmit:not(:disabled):hover {
  background: var(--mystery-action-hover);
}

/* --- Suggestion list --- */
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
  border-bottom: var(--border-width-hairline) solid var(--mystery-divider);
}

.mysteryListItem:last-child {
  padding-bottom: 0;
  border-bottom: none;
}

.mysteryListHeader,
.mysteryStatusRow {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: var(--space-3);
  flex-wrap: wrap;
}
/* --- Dialog: evidence review panel --- */
.studentMysteryDialog {
  position: fixed;
  inset: 0;
  z-index: 50;
  display: grid;
  place-items: center;
  padding: var(--space-4);
  background: var(--mystery-dialog-overlay);
}

.mysteryDialogPanel {
  width: min(820px, 100%);
  max-height: 80vh;
  overflow: auto;
  display: grid;
  gap: var(--space-4);
  padding: var(--space-5);
  border-radius: var(--radius-3);
  background: linear-gradient(
    180deg,
    var(--mystery-card-bg-top) 0%,
    var(--mystery-card-bg-bottom) 100%
  );
  box-shadow: var(--space-2) var(--space-4) var(--space-6) var(--mystery-card-shadow);
  color: var(--mystery-card-text);
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
  padding: var(--space-3) var(--space-4);
  border: var(--border-width-hairline) solid var(--mystery-secondary-border);
  border-radius: var(--radius-pill);
  background: var(--mystery-secondary-bg);
  color: var(--mystery-secondary-text);
  font: inherit;
  font-weight: var(--font-weight-semibold);
  cursor: pointer;
  transition: background var(--transition-fast);
}

.mysteryDialogClose:hover {
  background: var(--mystery-secondary-hover);
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
  padding: var(--space-4);
  border: var(--border-width-hairline) solid var(--mystery-card-border);
  border-radius: var(--radius-2);
  background: var(--mystery-card-bg-top);
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
