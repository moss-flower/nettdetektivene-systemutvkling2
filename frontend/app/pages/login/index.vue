<script setup lang="ts">
import { routePaths } from "~/composables/utils/routePaths";
import { useAuth } from "~/composables/useAuth";
import { useClassrooms } from "~/composables/useClassrooms";

type ProviderId = "microsoft" | "idporten";

definePageMeta({
  layout: false,
});

const route = useRoute();
const { login } = useAuth();
const { student } = useClassrooms();

const email = ref("");
const password = ref("");
const selectedProvider = ref<ProviderId>("microsoft");
const formError = ref("");
const loading = ref(false);

const providerOptions: Array<{
  id: ProviderId;
  label: string;
  style: "primary" | "secondary";
  badge: string;
}> = [
  {
    id: "microsoft",
    label: "Bruk jobb- eller skolekonto",
    style: "primary",
    badge: "MS",
  },
  {
    id: "idporten",
    label: "Logg inn med ID-porten",
    style: "secondary",
    badge: "ID",
  },
];

const redirectRoute = computed(() => {
  const redirect = route.query.redirect;

  return typeof redirect === "string" && redirect.startsWith("/") ? redirect : "";
});

const studentRegisterTarget = {
  path: routePaths.register,
  query: { role: "elev" },
};

const teacherRegisterTarget = {
  path: routePaths.register,
  query: { role: "lærer" },
};

function selectProvider(providerId: ProviderId) {
  selectedProvider.value = providerId;
  formError.value = "";
}

async function handleLogin() {
  formError.value = "";

  if (!email.value.trim() || !password.value.trim()) {
    formError.value = "Fyll inn e-post og passord.";
    return;
  }

  loading.value = true;

  try {
    const response = await login(email.value.trim(), password.value);

    if (response.roles.includes("ROLE_TEACHER")) {
      await navigateTo(redirectRoute.value || routePaths.teacherOverview);
      return;
    }

    if (response.roles.includes("ROLE_STUDENT")) {
      try {
        const classroom = await student.getMyClassroom();
        await navigateTo(classroom.pendingEntry ? routePaths.pending : routePaths.studentHome);
      } catch {
        await navigateTo(routePaths.join);
      }
      return;
    }

    await navigateTo(routePaths.home);

  } catch (error: any) {
    formError.value =
      error?.data?.detail ||
      error?.data?.message ||
      error?.data?.error ||
      error?.statusMessage ||
      "Innlogging mislyktes. Sjekk e-post og passord.";
  } finally {
    loading.value = false;
  }
}
</script>

<template>
  <section class="mockFeidePage">
    <div class="feideShell">
      <header class="shellHeader">
        <button type="button" class="languageButton">English</button>
      </header>

      <article class="shellContent">
        <p class="mockBadge">Mock Feide</p>
        <h1>Logg inn med Feide</h1>

        <section class="summaryPanels" aria-label="Innloggingsinformasjon">
          <div class="summaryPanel">
            <div class="panelMark serviceMark">ND</div>
            <div class="panelBody">
              <p class="panelLabel">Tjeneste</p>
              <p class="panelText">
                Du må logge inn via Feide for å åpne Nettdetektivene.
              </p>
            </div>
          </div>

          <div class="summaryPanel">
            <div class="panelMark orgMark">N</div>
            <div class="panelBody">
              <p class="panelLabel">Din tilknytning</p>
              <p class="panelValue">Din skole</p>
              <button type="button" class="inlineLink">Bytt tilknytning</button>
            </div>
          </div>
        </section>

        <section class="mainCard" aria-label="Mock Feide-innlogging">
          <div class="providerList">
            <button
              v-for="provider in providerOptions"
              :key="provider.id"
              type="button"
              :class="[
                'providerButton',
                provider.style,
                { active: selectedProvider === provider.id },
              ]"
              @click="selectProvider(provider.id)"
            >
              <span class="providerBadge">{{ provider.badge }}</span>
              <span>{{ provider.label }}</span>
            </button>
          </div>

          <div class="formDivider">
            <span>eller bruk testinnlogging</span>
          </div>

          <form class="mockForm" @submit.prevent="handleLogin">
            <div class="fieldGroup">
              <label class="fieldLabel" for="mock-email">E-post</label>
              <input
                id="mock-email"
                v-model="email"
                class="fieldInput"
                name="email"
                type="email"
                placeholder="f.eks. ola.nordmann@ntnu.no"
                autocomplete="username"
              />
            </div>

            <div class="fieldGroup">
              <label class="fieldLabel" for="mock-password">Passord</label>
              <input
                id="mock-password"
                v-model="password"
                class="fieldInput"
                name="password"
                type="password"
                placeholder="Skriv inn passord"
                autocomplete="current-password"
              />
            </div>

            <button type="submit" class="submitButton" :disabled="loading">
              {{ loading ? "Logger inn..." : "Fortsett med innlogging" }}
            </button>

            <p v-if="formError" class="formError">{{ formError }}</p>
          </form>
        </section>

        <section class="registerPanel" aria-label="Registrering">
          <p class="registerTitle">Har du ikke bruker?</p>
          <div class="registerLinks">
            <NuxtLink :to="studentRegisterTarget" class="registerLink">
              Registrer elev
            </NuxtLink>
            <NuxtLink :to="teacherRegisterTarget" class="registerLink">
              Registrer lærer
            </NuxtLink>
          </div>
        </section>

        <details class="helpPanel">
          <summary>Trenger du hjelp?</summary>
          <div class="helpContent">
            <p>Dette er en prototype av Feide-innloggingen deres.</p>
            <p>
              Logg inn med en backend-bruker. Rollen fra backend bestemmer om du sendes til elev- eller lærerflaten.
            </p>
          </div>
        </details>
      </article>

      <footer class="shellFooter">
        <p>Feide leveres normalt av Sikt. Denne siden er kun en lokal mock.</p>
        <p class="footerMeta">
          <span>Terms of use</span>
          <span>&bull;</span>
          <span>Privacy and cookie information</span>
        </p>
      </footer>
    </div>
  </section>
</template>

<style scoped>
.mockFeidePage {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  padding: var(--space-4);
  background: #c5d4e7;
  color: #020202;
}

.feideShell {
  width: min(100%, 32rem);
  align-self: flex-start;
  margin: var(--space-4) auto;
  background: var(--palette-white);
  box-shadow: 0 18px 40px rgba(45, 56, 76, 0.15);
}

.shellHeader {
  display: flex;
  justify-content: flex-end;
  padding: var(--space-2);
  background: #dbe5f1;
}

.languageButton {
  border: 0;
  background: transparent;
  min-height: 2.5rem;
  padding: 0 0.85rem;
  font: inherit;
  color: #2d384c;
  cursor: pointer;
}

.languageButton:hover,
.languageButton:focus-visible {
  background: #606ca5;
  color: var(--palette-white);
  outline: none;
}

.shellContent {
  padding: 1.25rem;
}

.mockBadge {
  display: inline-flex;
  align-items: center;
  min-height: 1.9rem;
  margin: 0 0 0.6rem;
  padding: 0 0.7rem;
  background: #e2e9f3;
  color: #283678;
  font-size: 0.86rem;
  font-weight: 700;
  letter-spacing: 0.04em;
  text-transform: uppercase;
}

h1 {
  margin-bottom: var(--space-4);
  color: #333333;
  font-size: clamp(1.8rem, 3vw, 2.1rem);
  font-weight: 900;
  line-height: 1.15;
}

.summaryPanels {
  display: grid;
  gap: var(--space-3);
  margin-bottom: var(--space-4);
}

.summaryPanel {
  display: flex;
  gap: 0.85rem;
  align-items: flex-start;
  padding: 0.85rem 0;
  border-bottom: var(--border-width-thin) solid #c5d4e7;
}

.panelMark {
  flex: 0 0 3rem;
  width: 3rem;
  height: 3rem;
  display: grid;
  place-items: center;
  border-radius: 0.75rem;
  font-weight: 800;
  color: var(--palette-white);
}

.serviceMark {
  background: linear-gradient(135deg, #283678 0%, #4c76b5 100%);
}

.orgMark {
  background: linear-gradient(135deg, #00843d 0%, #19a765 100%);
}

.panelBody {
  flex: 1;
}

.panelLabel {
  margin: 0 0 0.2rem;
  font-size: 0.9rem;
  font-weight: 700;
}

.panelText,
.panelValue {
  margin: 0;
  color: #2d384c;
  line-height: 1.45;
}

.panelValue {
  font-weight: 700;
}

.inlineLink {
  margin-top: 0.2rem;
  border: 0;
  padding: 0;
  background: transparent;
  color: #283678;
  font: inherit;
  font-size: 0.9rem;
  text-decoration: underline;
  cursor: pointer;
}

.mainCard {
  margin-top: var(--space-4);
}

.providerList {
  display: grid;
  gap: var(--space-3);
}

.providerButton,
.submitButton {
  width: 100%;
  min-height: 4.125rem;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.9rem;
  padding: 0 var(--space-4);
  border: 0;
  font: inherit;
  font-size: var(--font-size-16);
  cursor: pointer;
  transition:
    background-color 160ms ease,
    color 160ms ease,
    transform 160ms ease;
}

.providerButton:hover,
.providerButton:focus-visible,
.submitButton:hover,
.submitButton:focus-visible {
  background: #283678;
  color: var(--palette-white);
  outline: none;
}

.providerButton.active {
  outline: var(--border-width-thin) solid #606ca5;
  outline-offset: -var(--border-width-thin);
}

.providerButton.primary,
.submitButton {
  background: #92b9ed;
  color: #020202;
}

.providerButton.secondary {
  background: #d3d3d3;
  color: #020202;
}

.providerBadge {
  flex: 0 0 auto;
  min-width: 2rem;
  height: 2rem;
  display: grid;
  place-items: center;
  border-radius: var(--radius-pill);
  background: rgba(255, 255, 255, 0.65);
  font-size: var(--font-size-12);
  font-weight: 800;
}

.formDivider {
  display: flex;
  align-items: center;
  margin: 1.1rem 0;
  color: #5a6170;
  font-size: 0.92rem;
}

.formDivider::before,
.formDivider::after {
  content: '';
  flex: 1;
  height: 1px;
  background: #c5d4e7;
}

.formDivider span {
  padding: 0 var(--space-3);
}

.mockForm {
  display: grid;
  gap: var(--space-4);
}

.fieldGroup {
  display: grid;
  gap: 0.45rem;
}

.fieldLabel {
  font-weight: 700;
}

.fieldInput {
  width: 100%;
  min-height: 4.125rem;
  padding: 0 var(--space-4);
  border: var(--border-width-thin) solid #606ca5;
  background: var(--palette-white);
  color: #020202;
  font: inherit;
  font-size: var(--font-size-16);
}

.fieldInput:focus {
  outline: var(--border-width-thin) solid #606ca5;
  outline-offset: 0;
}

.formError {
  margin: 0;
  color: #b00020;
  font-weight: 700;
}

.registerPanel {
  margin-top: 1.25rem;
  padding: 1rem;
  background: #eef3f9;
}

.registerTitle {
  margin: 0 0 0.75rem;
  font-weight: 700;
}

.registerLinks {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0.75rem;
}

.registerLink {
  min-height: 3rem;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 0 0.75rem;
  background: #ffffff;
  border: 2px solid #606ca5;
  color: #283678;
  font-weight: 700;
  text-align: center;
  text-decoration: none;
}

.registerLink:hover,
.registerLink:focus-visible {
  background: #283678;
  color: #ffffff;
  outline: none;
}

.helpPanel {
  margin-top: var(--space-6);
  border-top: var(--border-width-thin) solid #c5d4e7;
  border-bottom: var(--border-width-thin) solid #c5d4e7;
  padding: 0.8rem 0;
}

.helpPanel summary {
  cursor: pointer;
  font-weight: 700;
  list-style: none;
}

.helpPanel summary::-webkit-details-marker {
  display: none;
}

.helpPanel summary::after {
  content: '+';
  float: right;
  font-size: 1.2rem;
}

.helpPanel[open] summary::after {
  content: '-';
}

.helpContent {
  padding-top: var(--space-4);
}

.helpContent p {
  margin: 0 0 var(--space-3);
  line-height: 1.45;
}

.shellFooter {
  padding: var(--space-4);
  background: #dbe5f1;
  text-align: center;
  color: #2d384c;
}

.shellFooter p {
  margin: 0;
}

.footerMeta {
  display: flex;
  justify-content: center;
  gap: 0.45rem;
  margin-top: 0.45rem;
  font-size: 0.82rem;
  flex-wrap: wrap;
}

@media (max-width: 640px) {
  .mockFeidePage {
    padding: 0;
  }

  .feideShell {
    width: 100%;
    margin: 0;
    min-height: 100vh;
  }

  .shellContent {
    padding: 1rem;
  }

  .providerButton,
  .submitButton,
  .fieldInput {
    min-height: 3.75rem;
  }

  .registerLinks {
    grid-template-columns: 1fr;
  }
}
</style>
