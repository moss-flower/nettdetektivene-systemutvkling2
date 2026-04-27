<script setup lang="ts">
import TextInput from "~/components/ui/TextInput.vue";
import { routePaths } from "~/composables/utils/routePaths";
import { useAuth } from "~/composables/useAuth";
import { useThemeAttributes } from "~/composables/useThemeAttributes";

useThemeAttributes("teacher");

// Holds current form fields
const username = ref("");
const email = ref("");
const password = ref("");
const repeatPassword = ref("");

// Tracks validity of fields
const emailValid = ref(false);
const passwordValid = ref(false);
const repeatPasswordValid = ref(false);

const formError = ref("");
const loading = ref(false);

const route = useRoute();
const { registerStudent, registerTeacher } = useAuth();

// Regex restricts email form
const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

// Username check
const usernameValid = computed(() => username.value.trim().length > 0);

// Dynamically checks if passwords match
const passwordsMatch = computed(() => password.value === repeatPassword.value);

// Dynamically updates full form validity
const formValid = computed(() =>
  usernameValid.value &&
  emailValid.value &&
  passwordValid.value &&
  repeatPasswordValid.value &&
  passwordsMatch.value,
);

const selectedRole = computed<"elev" | "lærer">(() => {
  const role = String(route.query.role ?? "").toLowerCase();
  return role === "lærer" ? "lærer" : "elev";
});

const registerTitle = computed(() =>
  selectedRole.value === "lærer" ? "Lærerregistrering" : "Registrering",
);

const registerSubtitle = computed(() =>
  selectedRole.value === "lærer"
    ? "Registrer deg for å bruke lærerflaten i Nettdetektivene!"
    : "Registrer deg for å spille Nettdetektivene!",
);

const loginTarget = computed(() => ({
  path: routePaths.login,
  query: { role: selectedRole.value },
}));

async function handleRegister() {
  formError.value = "";

  if (!formValid.value) {
    if (!usernameValid.value) {
      formError.value = "Brukernavn må fylles ut";
    } else if (!passwordsMatch.value) {
      formError.value = "Passordene matcher ikke";
    } else {
      formError.value = "Fyll ut skjemaet riktig";
    }
    return;
  }

  loading.value = true;

  try {
    if (selectedRole.value === "lærer") {
      await registerTeacher(
        username.value.trim(),
        email.value.trim(),
        password.value,
      );
    } else {
      await registerStudent(
        username.value.trim(),
        email.value.trim(),
        password.value,
      );
    }

    await navigateTo(loginTarget.value);
  } catch (error: any) {
    formError.value =
      error?.data?.message ||
      error?.data?.error ||
      error?.statusMessage ||
      "Registrering mislyktes";
  } finally {
    loading.value = false;
  }
}
</script>

<template>
  <section class="registerPage">
    <div class="registerCard">
      <h1 class="title">{{ registerTitle }}</h1>
      <p class="subtitle">{{ registerSubtitle }}</p>

      <form class="registerForm" @submit.prevent="handleRegister">
        <TextInput
          name="username"
          label="Brukernavn"
          placeholder="brukernavn"
          :required="true"
          @update:modelValue="username = $event"
        />

        <TextInput
          name="email"
          label="E-post"
          placeholder="e-post"
          :required="true"
          :pattern="emailPattern"
          @update:modelValue="email = $event"
          @update:isValid="emailValid = $event"
        />

        <TextInput
          name="password"
          label="Passord"
          type="password"
          placeholder="passord"
          :required="true"
          :minLength="8"
          @update:modelValue="password = $event"
          @update:isValid="passwordValid = $event"
        />

        <TextInput
          name="repeatPassword"
          label="Gjenta passord"
          type="password"
          placeholder="gjenta passord"
          :required="true"
          :minLength="8"
          @update:modelValue="repeatPassword = $event"
          @update:isValid="repeatPasswordValid = $event"
        />

        <button type="submit" class="registerButton" :disabled="!formValid || loading">
          {{ loading ? "Registrerer..." : "Registrer" }}
        </button>

        <p v-if="formError" class="formError">{{ formError }}</p>
      </form>

      <p class="registerText">
        Har du allerede en konto?
        <NuxtLink :to="loginTarget" class="loginLink">Logg inn</NuxtLink>
      </p>
    </div>
  </section>
</template>

<style scoped>
.registerPage {
  height: 100%;
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: var(--space-4);
  color: var(--color-text-primary);
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.78), rgba(255, 255, 255, 0.9)),
    linear-gradient(180deg, var(--palette-gray-50) 0%, var(--palette-cream-100) 100%);
}

.registerCard {
  width: 100%;
  max-width: 28rem;
  background: var(--color-bg-surface);
  border: var(--border-width-hairline) solid var(--color-border-default);
  border-radius: var(--radius-3);
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

.registerForm {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
}

.registerButton {
  margin-top: var(--space-2);
  padding: 0.9rem var(--space-4);
  border: var(--border-width-hairline) solid var(--color-brand-primary);
  border-radius: var(--radius-pill);
  background: var(--color-brand-primary);
  color: var(--color-text-onDark);
  font-weight: 700;
  cursor: pointer;
  transition: transform var(--transition-base), background var(--transition-base), border-color var(--transition-base);
}

.registerButton:hover:not(:disabled) {
  transform: translateY(-1px);
  background: var(--palette-navy-700);
}

.registerButton:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.registerText {
  margin-top: 1.25rem;
  color: var(--color-text-secondary);
  text-align: center;
}

.loginLink {
  font-weight: 700;
  color: var(--color-brand-primary);
}

.formError {
  color: var(--color-semantic-error);
  font-size: 0.9rem;
}

@media (max-width: 640px) {
  .registerCard {
    padding: var(--space-6);
  }

  .title {
    font-size: 1.75rem;
  }
}
</style>
