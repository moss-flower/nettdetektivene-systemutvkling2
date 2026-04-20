<script setup lang="ts">
import TextInput from "~/components/ui/TextInput.vue";
import type { RegisterRequest } from "~/composables/types/AuthTypes";
import { useAuth } from "~/composables/useAuth";

//Holds current email and password fields
const username = ref('')
const email = ref("");
const password = ref("");
const repeatPassword = ref("")

//Tracks validity of fields
const emailValid = ref(false);
const passwordValid = ref(false);
const repeatPasswordValid = ref(false); //Does not reflect if passwords match, only if it complies with the minimum length limit

const formError = ref("") //Displays user feedback on failed login attemp

//REGEX restrics email form
const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

//Dynamically checks if passwords match
const passwordsMatch = computed(() => password.value === repeatPassword.value);

//Dynamically updates full form validity based on both email and password being currently valid
const formValid = computed(() =>
  emailValid.value &&
  passwordValid.value &&
  repeatPasswordValid.value &&
  passwordsMatch.value
);

const { registerStudent } = useAuth();

//Temporary placeholder handleLogin
function handleRegister() {
  formError.value = "";

  if (!formValid.value) {
    if (!passwordsMatch.value) {
      formError.value = "Passwords do not match";
    } else {
      formError.value = "Invalid credentials";
    }
    return;
  }

  // console.log("Register payload:", {
  //   email: email.value,
  //   password: password.value,
  // });
}

</script>

<template>
  <section class="registerPage">
    <div class="registerCard">
      <h1 class="title">Registrering</h1>
      <p class="subtitle">Registrer deg for å spille Nettdetektivene!</p>

      <form class="registerForm" @submit.prevent="handleRegister">
        <TextInput
            name="username"
            label="username"
            placeholder="username"
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

        <button type="submit" class="registerButton" :disabled="!formValid">
          Registrer
        </button>

        <p v-if="formError" class="formError">{{ formError }}</p>
      </form>

      <p class="registerText">
        Har du allerede en konto?
        <NuxtLink :to="routePaths.login" class="loginLink">Logg in</NuxtLink>
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
  background: var(--bg);
}

.registerCard {
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

.registerForm {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.registerButton {
  margin-top: 0.5rem;
  padding: 0.9rem 1rem;
  border: none;
  border-radius: var(--radius);
  background: var(--primary);
  color: white;
  font-weight: 700;
  cursor: pointer;
}

.registerButton:hover:not(:disabled) {
  background: var(--primary-hover);
}

.registerButton:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.registerText {
  margin-top: 1.25rem;
  color: var(--muted);
  text-align: center;
}

.loginLink {
  font-weight: 700;
  color: var(--primary);
}

.formError {
  color: var(--danger);
  font-size: 0.9rem;
}
</style>
