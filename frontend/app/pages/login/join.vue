<script setup lang="ts">
import { routePaths } from "~/composables/utils/routePaths";
import { useClassrooms } from "~/composables/useClassrooms";

const { student } = useClassrooms();

const codeWords = ref(["", "", ""]);

function normalizeCodeWord(value: string) {
  return value.toUpperCase().replace(/[^A-ZÆØÅ]/g, "").trim();
}

function updateCodeWord(index: number, value: string) {
  codeWords.value[index] = normalizeCodeWord(value);
}

const classroomCode = computed(() => codeWords.value.join("-"));

const formError = ref("");
const loading = ref(false);

const formValid = computed(() =>
  codeWords.value.every((word) => word.trim().length > 0),
);

async function handleJoinClassroom() {
  formError.value = "";

  if (!formValid.value) {
    formError.value = "Skriv inn alle tre ordene i klasseromskoden.";
    return;
  }

  loading.value = true;

  try {
    await student.joinClassroom(classroomCode.value);
    await navigateTo(routePaths.studentHome);
  } catch (error: any) {
    formError.value =
      error?.data?.detail ||
      error?.data?.message ||
      error?.data?.error ||
      error?.statusMessage ||
      "Klarte ikke å bli med i klasserommet. Sjekk klasseromskoden.";
  } finally {
    loading.value = false;
  }
}
</script>

<template>
  <section class="joinPage">
    <div class="joinCard">
      <h1 class="title">Bli med i klasserom</h1>
      <p class="subtitle">Skriv inn klasseromskoden for å bli med.</p>

      <form class="joinForm" @submit.prevent="handleJoinClassroom">
        <fieldset class="codeFieldset">
          <legend class="codeLegend">Klasseromskode</legend>

          <div class="codeInputs">
            <input
              :value="codeWords[0]"
              class="codeInput"
              type="text"
              inputmode="text"
              autocomplete="off"
              autocapitalize="characters"
              @input="updateCodeWord(0, ($event.target as HTMLInputElement).value)"
            />
            <span class="codeSeparator">-</span>
            <input
              :value="codeWords[1]"
              class="codeInput"
              type="text"
              inputmode="text"
              autocomplete="off"
              autocapitalize="characters"
              @input="updateCodeWord(1, ($event.target as HTMLInputElement).value)"
            />
            <span class="codeSeparator">-</span>
            <input
              :value="codeWords[2]"
              class="codeInput"
              type="text"
              inputmode="text"
              autocomplete="off"
              autocapitalize="characters"
              @input="updateCodeWord(2, ($event.target as HTMLInputElement).value)"
            />
          </div>

        </fieldset>

        <button type="submit" class="joinButton" :disabled="!formValid || loading">
          {{ loading ? "Blir med..." : "Bli med" }}
        </button>

        <p v-if="formError" class="formError">{{ formError }}</p>
      </form>

      <p class="backText">
        <NuxtLink :to="routePaths.home" class="backLink">Tilbake</NuxtLink>
      </p>
    </div>
  </section>
</template>

<style scoped>
.joinPage {
  height: 100%;
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: var(--space-4);
  background: color-mix(in srgb, var(--color-bg-canvas) 10%, var(--palette-white));
}

.joinCard {
  width: 100%;
  max-width: 28rem;
  background: var(--color-bg-surface);
  border: var(--border-width-hairline) solid var(--color-border-default);
  border-radius: var(--radius-2);
  box-shadow: var(--shadow-2);
  padding: var(--space-8);
}

.title {
  font-size: 2rem;
  font-weight: 800;
  margin-bottom: var(--space-1);
  color: var(--color-text-primary);
}

.subtitle {
  color: var(--color-text-secondary);
  margin-bottom: var(--space-6);
}

.joinForm {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
}

.codeFieldset {
  margin: 0;
  padding: 0;
  border: 0;
}

.codeLegend {
  margin-bottom: var(--space-2);
  font-weight: 700;
  color: var(--color-text-primary);
}

.codeInputs {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto minmax(0, 1fr) auto minmax(0, 1fr);
  gap: var(--space-2);
  align-items: center;
}

.codeInput {
  width: 100%;
  min-width: 0;
  padding: 0.85rem var(--space-3);
  border: var(--border-width-hairline) solid var(--color-border-default);
  border-radius: var(--radius-2);
  background: color-mix(in srgb, var(--color-bg-surface) 96%, var(--color-semantic-info) 4%);
  color: var(--color-text-primary);
  font: inherit;
  font-weight: 700;
  text-align: center;
  text-transform: uppercase;
}

.codeInput:focus {
  outline: none;
  border-color: var(--color-brand-primary);
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.12);
}

.codeSeparator {
  font-size: 1.25rem;
  font-weight: 700;
  color: var(--color-text-secondary);
}

.joinButton {
  margin-top: var(--space-2);
  padding: 0.9rem var(--space-4);
  border: none;
  border-radius: var(--radius-2);
  background: var(--color-brand-primary);
  color: var(--color-text-onDark);
  font-weight: 700;
  cursor: pointer;
}

.joinButton:hover:not(:disabled) {
  background: var(--palette-navy-700);
}

.joinButton:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.formError {
  color: var(--color-semantic-error);
  font-size: 0.9rem;
}

.backText {
  margin-top: 1.25rem;
  text-align: center;
}

.backLink {
  color: var(--color-brand-primary);
  font-weight: 700;
}

</style>
