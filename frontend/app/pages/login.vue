<script setup lang="ts">
import TextInput from '~/components/ui/TextInput.vue';

//Holds current email and password fields
const email = ref("");
const password = ref("");

//Tracks validity of fields
const emailValid = ref(false);
const passwordValid = ref(false);

const formError = ref("") //Displays user feedback on failed login attempt

//REGEX restrics email form
const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

//Dynamically updates full form validity based on both email and password being currently valid
const formValid = computed(() => emailValid.value && passwordValid.value);

//Temporary placeholder handleLogin 
function handleLogin() {
  formError.value = "";

  if (!formValid.value) {
    formError.value = "Ugyldig input"
    return;
  }

  console.log("Login payload:", {
    email: email.value,
    password: password.value,
  });
}

</script>

<template>
  <section class="loginPage">
    <div class="loginCard">
      <h1 class="title">Logg in</h1>
      <p class="subtitle">Velkommen tilbake til Nettdetektivene!</p>

      <form class="loginForm" @submit.prevent="handleLogin">
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

        <button type="submit" class="loginButton" :disabled="!formValid">
          Logg in
        </button>

        <p v-if="formError" class="formError">{{ formError }}</p>
      </form>

      <p class="registerText">
        Har du ikke en konto?
        <NuxtLink :to="routePaths.register" class="registerLink">Registrer deg nå</NuxtLink>
      </p>
    </div>
  </section>
</template>

<style scoped>
.loginPage {
  height: 100%;
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: var(--space-4);
  background: var(--bg);
}

.loginCard {
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

.loginForm {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.loginButton {
  margin-top: 0.5rem;
  padding: 0.9rem 1rem;
  border: none;
  border-radius: var(--radius);
  background: var(--primary);
  color: white;
  font-weight: 700;
  cursor: pointer;
}

.loginButton:hover:not(:disabled) {
  background: var(--primary-hover);
}

.loginButton:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.registerText {
  margin-top: 1.25rem;
  color: var(--muted);
  text-align: center;
}

.registerLink {
  font-weight: 700;
  color: var(--primary);
}

.formError {
  color: var(--danger);
  font-size: 0.9rem;
}
</style>