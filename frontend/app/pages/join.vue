<script setup lang="ts">
import TextInput from "~/components/ui/TextInput.vue";

//Holds input variables
const classroomCode = ref("");
const username = ref("");

//Holds validity of fields
const classroomCodeValid = ref(false);
const usernameValid = ref(false);

//Error display
const formError = ref("");

//REGEX restricts input
const classroomCodePattern = /^\d{6}$/;

//Dynamically checks if credentials are valid
const formValid = computed(() =>
  classroomCodeValid.value && usernameValid.value
);

//Temporary placeholder
function handleJoinClassroom() {
  formError.value = "";

  if (!formValid.value) {
    formError.value = "Please enter a valid 6-digit classroom code and username.";
    return;
  }

  console.log("Join classroom payload:", {
    classroomCode: classroomCode.value,
    username: username.value,
  });
}
</script>

<template>
  <section class="joinPage">
    <div class="joinCard">
      <h1 class="title">Bli med i klasserom</h1>
      <p class="subtitle">
        Skriv inn klasseromskoden og velg et brukernavn for å bli med.
      </p>

      <form class="joinForm" @submit.prevent="handleJoinClassroom">
        <TextInput
          name="classroomCode"
          label="Klasseromskode"
          placeholder="123456"
          :required="true"
          :pattern="classroomCodePattern"
          :minLength="6"
          :maxLength="6"
          @update:modelValue="classroomCode = $event"
          @update:isValid="classroomCodeValid = $event"
        />

        <TextInput
          name="username"
          label="Brukernavn"
          placeholder="Detektiv Kari"
          :required="true"
          :minLength="3"
          :maxLength="20"
          @update:modelValue="username = $event"
          @update:isValid="usernameValid = $event"
        />

        <button type="submit" class="joinButton" :disabled="!formValid">
          Bli med
        </button>

        <p v-if="formError" class="formError">{{ formError }}</p>
      </form>

      <p class="backText">
        <NuxtLink to="/" class="backLink">Tilbake</NuxtLink>
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