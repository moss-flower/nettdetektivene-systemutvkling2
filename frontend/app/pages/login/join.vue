<script setup lang="ts">
import TextInput from "~/components/ui/TextInput.vue";
import { routePaths } from "~/composables/utils/routePaths";
import { useClassrooms } from "~/composables/useClassrooms";

const { student } = useClassrooms();

// Holds input variables
const classroomCode = ref("");

// Holds validity of fields
const classroomCodeValid = ref(false);

// Error display
const formError = ref("");
const loading = ref(false);

// Regex restricts input
const classroomCodePattern = /^\d{6}$/;

// Dynamically checks if credentials are valid
const formValid = computed(() => classroomCodeValid.value);

// Join classroom and navigate to student home
async function handleJoinClassroom() {
  formError.value = "";

  if (!formValid.value) {
    formError.value = "Vennligst skriv inn en gyldig 6-sifret klasseromskode.";
    return;
  }

  loading.value = true;

  try {
    await student.joinClassroom(classroomCode.value.trim());
    await navigateTo(routePaths.studentHome);
  } catch (error: any) {
    formError.value =
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
        <TextInput
          name="classroomCode"
          label="Klasseromskode"
          placeholder="123456"
          :required="true"
          :pattern="classroomCodePattern"
          :minLength="6"
          :maxLength="30"
          @update:modelValue="classroomCode = $event"
          @update:isValid="classroomCodeValid = $event"
        />

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
  background: var(--bg);
}

.joinCard {
  width: 100%;
  max-width: 28rem;
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: 1rem;
  box-shadow: var(--shadow);
  padding: 2rem;
}

.title {
  font-size: 2rem;
  font-weight: 800;
  margin-bottom: 0.25rem;
  color: var(--text);
}

.subtitle {
  color: var(--muted);
  margin-bottom: 1.5rem;
}

.joinForm {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.joinButton {
  margin-top: 0.5rem;
  padding: 0.9rem 1rem;
  border: none;
  border-radius: var(--radius);
  background: var(--primary);
  color: white;
  font-weight: 700;
  cursor: pointer;
}

.joinButton:hover:not(:disabled) {
  background: var(--primary-hover);
}

.joinButton:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.formError {
  color: var(--danger);
  font-size: 0.9rem;
}

.backText {
  margin-top: 1.25rem;
  text-align: center;
}

.backLink {
  color: var(--primary);
  font-weight: 700;
}
</style>