<script setup lang="ts">
import { World } from "~/composables/types/WorldTypes";
import { routePaths } from "~/composables/utils/routePaths";
import { useClassrooms } from "~/composables/useClassrooms";
import type { ClassroomRequest } from "~/composables/types/ClassroomTypes";
import TextInput from "~/components/ui/TextInput.vue";

definePageMeta({ layout: "teacher" });

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

const { data: availableWorlds } = await useAsyncData("available-worlds", () => World.backendFetchAllWorlds());

const lockedWorldUid = computed(() => {
  const uid = route.query.uid;
  return typeof uid === "string" ? uid.trim() : "";
});

const lockedWorld = computed(() => {
  return (availableWorlds.value ?? []).find((world) => world.uid === lockedWorldUid.value) ?? null;
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
    formError.value = "Klasserommet må ha navn, og stasjonskart må være satt.";
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
        Gi klasserommet et navn og velg hvilket stasjonskart klasserommet skal bruke.
      </p>
    </header>

    <form class="pageGrid" @submit.prevent="createClassroom">
      <div class="editorPanel">
        <section class="panelSection">
          <div class="sectionHeading">
            <h2>Klasserom</h2>
            <p>Fyll inn informasjonen for å opprette et nytt klasserom.</p>
          </div>

          <div class="fieldGrid">
            <div class="field fieldFull">
              <TextInput
                name="name"
                label="Navn"
                placeholder="For eksempel: 6A Vår 2026"
                :required="true"
                @update:modelValue="draft.name = $event"
              />
            </div>

            <div class="field fieldFull">
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
            <h2>Stasjonskart</h2>
            <p v-if="showWorldSelector">Velg stasjonskartet som skal brukes i klasserommet.</p>
            <p v-else>Dette stasjonskartet er satt på forhånd og kan ikke endres her.</p>
          </div>

          <div v-if="showWorldSelector" class="fieldGrid">
            <label class="field fieldFull">
              <span>Velg stasjonskart</span>
              <select v-model="draft.worldUid">
                <option value="">Velg stasjonskart</option>
                <option
                  v-for="world in availableWorlds ?? []"

                  :key="world.uid"
                  :value="world.uid"
                >
                  {{ world.title }}
                </option>
              </select>
            </label>
          </div>

          <div v-else class="lockedWorldCard">
            <p class="cardEyebrow">Stasjonskart</p>
            <h3>{{ lockedWorld?.title || lockedWorldUid }}</h3>
            <p>Dette stasjonskartet er satt på forhånd og kan ikke endres her.</p>
          </div>
        </section>

        <div class="actionRow actionRowSave">
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
   max-width: var(--layout-max-width);
   margin: 0 auto;
   padding: var(--layout-page-padding);
   color: var(--color-text-primary);
}

.pageHeader {
   display: grid;
   gap: var(--space-2);
   margin-bottom: var(--space-4);
   padding: var(--space-5);
   border: var(--border-width-hairline) solid var(--color-border-default);
   border-top: var(--border-width-thick) solid var(--color-accent-reward);
   border-radius: var(--radius-3);
   background: var(--color-bg-surface);
   box-shadow: var(--shadow-1);
}

h1 {
   margin: 0;
   font-family: var(--font-family-serif), sans-serif;
   font-size: var(--font-size-28);
   line-height: var(--font-leading-28);
   color: var(--color-text-primary);
}

.backButton {
   display: inline-flex;
   align-items: center;
   margin-bottom: var(--space-3);
   padding: 0;
   border: 0;
   background: transparent;
   color: var(--color-brand-primary);
   font: inherit;
   font-weight: var(--font-weight-semibold);
   cursor: pointer;
   transition: color var(--transition-fast);
}

.backButton:hover {
   text-decoration: underline;
}

.subtitle,
.sectionHeading p,
.lockedWorldCard p {
   margin: 0;
   color: var(--color-text-secondary);
}

.pageGrid {
   display: grid;
   grid-template-columns: minmax(0, 1fr);
   gap: var(--space-5);
   align-items: start;
}

.editorPanel {
   --create-field-bg: var(--color-bg-canvas);
   --create-field-border: var(--color-border-default);
   --create-field-focus: var(--color-brand-primary);
  /* palette direct exception */
   --create-select-bg: var(--palette-ice-200);

   padding: var(--space-5);
   border: var(--border-width-hairline) solid var(--color-border-default);
   border-radius: var(--radius-3);
   background: var(--color-bg-surface);
   box-shadow: var(--shadow-1);
}

.panelSection + .panelSection {
   margin-top: var(--space-6);
}

.sectionHeading {
   display: grid;
   gap: var(--space-1);
   margin-bottom: var(--space-4);
}

.fieldGrid {
   display: grid;
   grid-template-columns: repeat(2, minmax(0, 1fr));
   gap: var(--space-4);
}

.field {
   display: grid;
   gap: var(--space-2);
}

.fieldFull {
   grid-column: 1 / -1;
}

.field span,
.cardEyebrow {
   font-weight: var(--font-weight-bold);
}

.field select {
   width: 100%;
   padding: var(--space-3) var(--space-4);
   border: var(--border-width-hairline) solid var(--create-field-border);
   border-radius: var(--radius-1);
   background: var(--create-select-bg);
   color: var(--color-text-primary);
   font: inherit;
   transition: border-color var(--transition-fast);
}

.field select:focus {
   outline: none;
   border-color: var(--create-field-focus);
}

.field :deep(input) {
   border: var(--border-width-hairline) solid var(--create-field-border);
   border-radius: var(--radius-1);
   background: var(--create-field-bg);
   transition: border-color var(--transition-fast);
}

.field :deep(input:focus) {
   outline: none;
   border-color: var(--create-field-focus);
}

.lockedWorldCard {
   display: grid;
   gap: var(--space-2);
   padding: var(--space-4);
   border: var(--border-width-hairline) solid var(--color-border-default);
   border-radius: var(--radius-2);
   background: var(--color-bg-canvas);
}

.primaryButton {
   display: inline-flex;
   align-items: center;
   justify-content: center;
   min-height: var(--size-control-lg);
   padding: var(--space-3) var(--space-5);
   border: var(--border-width-hairline) solid var(--color-brand-primary);
   border-radius: var(--radius-1);
   background: var(--color-brand-primary);
   color: var(--color-text-onDark);
   font: inherit;
   font-weight: var(--font-weight-bold);
   cursor: pointer;
   transition: transform var(--transition-fast);
}

.primaryButton:hover:not(:disabled) {
   transform: translateY(calc(-1 * var(--border-width-hairline)));
}

.primaryButton:disabled {
   opacity: 0.5;
   cursor: not-allowed;
   transform: none;
}

.actionRow {
   display: flex;
   justify-content: flex-end;
   margin-top: var(--space-6);
}

.formError {
   margin-top: var(--space-3);
   color: var(--color-semantic-error);
   font-weight: var(--font-weight-semibold);
}

.cardEyebrow {
   color: var(--color-text-secondary);
   font-family: var(--font-family-mono), monospace;
   font-weight: var(--font-weight-bold);
   letter-spacing: var(--font-tracking-wider);
   text-transform: uppercase;
   font-size: var(--font-size-12);
}

@media (max-width: 900px) { /* deliberate: preserves existing tablet breakpoint */
   .pageGrid {
      grid-template-columns: 1fr;
   }
}

@media (max-width: 800px) { /* deliberate: preserves existing mobile breakpoint */
   .fieldGrid {
      grid-template-columns: 1fr;
   }

   .sectionHeadingInline,
   .actionRow {
      display: grid;
   }

   .actionRow {
      justify-content: stretch;
   }
}
</style>
