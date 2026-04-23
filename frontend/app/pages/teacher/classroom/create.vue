<script setup lang="ts">
import { World } from "~/composables/types/WorldTypes";
import { routePaths } from "~/composables/utils/routePaths";
import { useClassrooms } from "~/composables/useClassrooms";
import type { ClassroomRequest } from "~/composables/types/ClassroomTypes";
import TextInput from "~/components/ui/TextInput.vue";

const route = useRoute();
const router = useRouter();
const { teacher } = useClassrooms();

const draft = reactive({
  name: "",
  description: "",
  worldUid: "",
});

const formError = ref("");
const loading = ref(false);

const availableWorlds = computed(() => World.backendFetchAllWorlds());

const lockedWorldUid = computed(() => {
  const uid = route.query.uid;
  return typeof uid === "string" ? uid.trim() : "";
});

const showWorldSelector = computed(() => lockedWorldUid.value.length === 0);

const effectiveWorldUid = computed(() => {
  return lockedWorldUid.value || draft.worldUid.trim();
});

const canCreate = computed(() =>
  draft.name.trim().length > 0 && effectiveWorldUid.value.length > 0,
);

const goBack = () => {
  if (import.meta.client && window.history.length > 1) {
    router.back();
    return;
  }

  void navigateTo(routePaths.teacherOverview);
};

const createClassroom = async () => {
  formError.value = "";

  if (!canCreate.value) {
    formError.value = "Klasserommet må ha navn, og verden må være satt.";
    return;
  }

  loading.value = true;

  try {
    const payload: ClassroomRequest = {
      name: draft.name.trim(),
      description: draft.description.trim(),
      worldUid: effectiveWorldUid.value,
    };

    await teacher.createClassroom(payload);
    await navigateTo(routePaths.teacherOverview);
  } catch (error: any) {
    formError.value =
      error?.data?.message ||
      error?.data?.error ||
      error?.statusMessage ||
      "Klarte ikke å opprette klasserommet.";
  } finally {
    loading.value = false;
  }
};
</script>

<template>
  <section class="classroomCreatePage">
    <header class="pageHeader">
      <button class="backButton" type="button" @click="goBack">Tilbake</button>
      <h1>Lag klasserom</h1>
      <p class="subtitle">
        Gi klasserommet et navn og velg hvilken verden klasserommet skal bruke.
      </p>
    </header>

    <form class="pageGrid" @submit.prevent="createClassroom">
      <div class="editorPanel">
        <section class="panelSection">
          <div class="sectionHeading">
            <h2>Klasserom</h2>
            <p>Oppretter klasserom mot backend.</p>
          </div>

          <div class="fieldGrid">
            <div class="field field--full">
              <TextInput
                name="name"
                label="Navn"
                placeholder="For eksempel: 6A Vår 2026"
                :required="true"
                @update:modelValue="draft.name = $event"
              />
            </div>

            <div class="field field--full">
              <TextInput
                name="description"
                label="Beskrivelse"
                placeholder="For eksempel: Klasse for vårsemesteret"
                @update:modelValue="draft.description = $event"
              />
            </div>
          </div>
        </section>

        <section class="panelSection">
          <div class="sectionHeading">
            <h2>Verden</h2>
            <p v-if="showWorldSelector">Velg verdenen som skal brukes i klasserommet.</p>
            <p v-else>Denne verdenen er satt på forhånd og kan ikke endres her.</p>
          </div>

          <div v-if="showWorldSelector" class="fieldGrid">
            <label class="field field--full">
              <span>Velg verden</span>
              <select v-model="draft.worldUid">
                <option value="">Velg verden</option>
                <option
                  v-for="world in availableWorlds"
                  :key="world.uid"
                  :value="world.uid"
                >
                  {{ world.uid }}
                </option>
              </select>
            </label>
          </div>

          <div v-else class="lockedWorldCard">
            <p class="cardEyebrow">Verden</p>
            <h3>{{ lockedWorldUid }}</h3>
            <p>Denne verdenen er satt på forhånd og kan ikke endres her.</p>
          </div>
        </section>

        <div class="actionRow actionRow--save">
          <button type="submit" class="primaryButton" :disabled="!canCreate || loading">
            {{ loading ? "Lagrer..." : "Lag klasserom" }}
          </button>
        </div>

        <p v-if="formError" class="formError">{{ formError }}</p>
      </div>
    </form>
  </section>
</template>

<style scoped>
.classroomCreatePage {
	width: 100%;
	max-width: 1400px;
	margin: 0 auto;
	padding: var(--space-4);
}

.pageHeader {
	margin-bottom: 1rem;
}

.backButton {
	display: inline-flex;
	align-items: center;
	margin-bottom: 0.75rem;
	padding: 0;
	border: 0;
	background: transparent;
	color: var(--primary);
	font: inherit;
	font-weight: 600;
	cursor: pointer;
}

.backButton:hover {
	text-decoration: underline;
}

.subtitle,
.sectionHeading p,
.summaryCard p,
.lockedWorldCard p {
	margin: 0;
	color: var(--muted);
	line-height: 1.5;
}

h1,
h2,
h3 {
	margin: 0;
}

.pageGrid {
	display: grid;
	grid-template-columns: minmax(0, 1fr);
	gap: 1.25rem;
	align-items: start;
}

.editorPanel {
	padding: 1.25rem;
	border: 1px solid var(--border);
	border-radius: 1.2rem;
	background:
		linear-gradient(180deg, rgba(101, 240, 175, 0.08), rgba(101, 240, 175, 0)) top/100% 5rem no-repeat,
		var(--surface);
	box-shadow: var(--shadow);
}

.panelSection + .panelSection {
	margin-top: 1.5rem;
}

.sectionHeading {
	display: grid;
	gap: 0.35rem;
	margin-bottom: 1rem;
}

.sectionHeading--inline {
	grid-template-columns: 1fr auto;
	align-items: start;
	gap: 0.75rem;
}

.fieldGrid {
	display: grid;
	grid-template-columns: repeat(2, minmax(0, 1fr));
	gap: 0.9rem;
}

.field {
	display: grid;
	gap: 0.45rem;
}

.field--full {
	grid-column: 1 / -1;
}

.field span,
.cardEyebrow {
	font-weight: 700;
}

.field input,
.field select {
	width: 100%;
	padding: 0.85rem 1rem;
	border: 1px solid var(--border);
	border-radius: 0.9rem;
	background: rgba(0, 0, 0, 0.16);
	color: var(--text);
	font: inherit;
}

.field input:focus,
.field select:focus {
	outline: none;
	border-color: var(--primary);
}

.lockedWorldCard {
	display: grid;
	gap: 0.5rem;
	padding: 1rem;
	border: 1px solid var(--border);
	border-radius: 1rem;
	background: rgba(255, 255, 255, 0.02);
}

.ghostButton,
.primaryButton {
	border: 1px solid var(--border);
	border-radius: 999px;
	padding: 0.75rem 1rem;
	font-weight: 700;
	transition: transform var(--transition), border-color var(--transition), background var(--transition);
}

.ghostButton {
	background: transparent;
	color: var(--text);
}

.primaryButton {
	border-color: var(--primary);
	background: var(--primary);
	color: var(--bg);
}

.ghostButton:hover,
.primaryButton:hover {
	transform: translateY(-1px);
}

.primaryButton:disabled {
	opacity: 0.5;
	cursor: not-allowed;
	transform: none;
}

.actionRow {
	display: flex;
	justify-content: flex-end;
	margin-top: 1.5rem;
}

.actionRow--save {
	margin-top: 0;
}

.successSection {
	opacity: 0.95;
}

.payloadPreview {
	overflow-x: auto;
	padding: 1rem;
	border-radius: 1rem;
	background: rgba(0, 0, 0, 0.24);
	color: #d7f9ea;
	font-size: 0.9rem;
	line-height: 1.5;
	margin: 0;
}

@media (max-width: 900px) {
	.pageGrid {
		grid-template-columns: 1fr;
	}
}

@media (max-width: 800px) {
	.fieldGrid {
		grid-template-columns: 1fr;
	}

	.sectionHeading--inline,
	.actionRow {
		display: grid;
	}

	.actionRow {
		justify-content: stretch;
	}
}
</style>
