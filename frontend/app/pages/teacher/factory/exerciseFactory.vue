<script setup lang="ts">
import ExerciseFactoryEditor from "~/components/teacher/factory/ExerciseFactoryEditor.vue";
import PopupFactorySaved from "~/components/teacher/factory/PopupFactorySaved.vue";
import { routePaths } from "~/composables/utils/routePaths";
import {
	exerciseFactoryNewSelectionKey,
	makeExerciseFactoryExistingSelectionKey,
	parseExerciseFactorySelectionKey,
} from "~/composables/useExerciseFactory";

const route = useRoute();
const showSavedPopup = ref(false);
const pendingSavedNavigationTarget = ref<string | null>(null);
const returnTo = computed(() => typeof route.query.returnTo === "string" ? route.query.returnTo : "");
const lockedExerciseUid = computed(() => {
	const uid = route.query.uid;

	return typeof uid === "string" && uid.trim().length > 0 ? uid.trim() : "";
});
const selectedExerciseKey = computed(() => lockedExerciseUid.value
	? makeExerciseFactoryExistingSelectionKey(lockedExerciseUid.value)
	: exerciseFactoryNewSelectionKey);

const goBack = () => {
	void navigateTo(returnTo.value || routePaths.teacherFactoryStation);
};

const handleSelectionUpdated = async (selectionKey: string) => {
	const selection = parseExerciseFactorySelectionKey(selectionKey);

	if (selection.kind === "existing" && selection.uid !== lockedExerciseUid.value) {
		await navigateTo({
			path: routePaths.teacherFactoryExercise,
			query: {
				uid: selection.uid,
				...(returnTo.value ? { returnTo: returnTo.value } : {}),
			},
		}, { replace: true });
	}
};

const handleSaved = (payload?: { returnTarget?: string | null }) => {
	pendingSavedNavigationTarget.value = payload?.returnTarget ?? null;
	showSavedPopup.value = true;
};

const handleDeleted = async () => {
	await navigateTo({
		path: routePaths.teacherFactoryExercise,
		query: returnTo.value ? { returnTo: returnTo.value } : {},
	}, { replace: true });
};

const closeSavedPopup = () => {
	showSavedPopup.value = false;

	if (pendingSavedNavigationTarget.value) {
		const nextTarget = pendingSavedNavigationTarget.value;
		pendingSavedNavigationTarget.value = null;
		void navigateTo(nextTarget);
	}
};

</script>

<template>
	<section class="factoryPage">
		<header class="factoryHero">
			<button class="backButton" type="button" @click="goBack">Tilbake</button>
		</header>

		<ExerciseFactoryEditor
			:selection-key="selectedExerciseKey"
			@selection-updated="handleSelectionUpdated"
			@saved="handleSaved"
			@deleted="handleDeleted"
		/>

		<PopupFactorySaved
			v-if="showSavedPopup"
			button-label="Lukk"
			message="Endringene er lagret."
			@close="closeSavedPopup"
		/>
	</section>
</template>

<style scoped>
.factoryPage {
   width: 100%;
   max-width: var(--layout-max-width);
   margin: 0 auto;
   padding: var(--layout-page-padding);
   color: var(--color-text-primary);
   background: var(--color-bg-canvas);
}

.factoryHero {
  display: grid;
  gap: var(--space-2);
   margin-bottom: var(--space-4);
  padding: var(--space-5);
  border: var(--border-width-hairline) solid var(--color-border-default);
  border-top: var(--border-width-thick) solid var(--color-accent-reward);
  border-radius: var(--radius-3);
  background: var(--color-bg-surface);
  box-shadow: var(--shadow-folder);
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

h1,
h2,
h3 {
   margin: 0;
}

h1 {
  font-family: var(--font-family-serif), sans-serif;
  font-size: var(--font-size-28);
  line-height: var(--font-leading-28);
  color: var(--color-text-primary);
}

.selectorPanel {
   display: grid;
   gap: var(--space-4);
   margin-bottom: var(--space-5);
   padding: var(--space-5);
   border: var(--border-width-hairline) solid var(--color-border-default);
   border-radius: var(--radius-4);
   background: var(--color-bg-surface);
   box-shadow: var(--shadow-folder);
}
.selectorControls {
   display: flex;
}

.field {
   display: grid;
   gap: var(--space-2);
  /* deliberate: reading-measure cap for form fields */
   max-width: 32rem;
}

.fieldGrow {
   flex: 1;
}

.field select {
   width: 100%;
   padding: var(--space-4);
   border: var(--border-width-hairline) solid var(--color-border-default);
   border-radius: var(--radius-3);
   background: var(--color-bg-surface);
   color: var(--color-text-primary);
   font: inherit;
}

.field select:focus {
   outline: none;
   border-color: var(--color-brand-primary);
}
@media (max-width: 800px) {
   .selectorControls {
      display: grid;
   }

   .field {
      max-width: none;
   }
}
</style>
