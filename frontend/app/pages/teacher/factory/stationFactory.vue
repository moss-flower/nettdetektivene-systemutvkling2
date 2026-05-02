<script setup lang="ts">
import StationFactoryEditor from "~/components/teacher/factory/StationFactoryEditor.vue";
import PopupFactorySaved from "~/components/teacher/factory/PopupFactorySaved.vue";
import { routePaths } from "~/composables/utils/routePaths";
import {
	makeStationFactoryExistingSelectionKey,
	parseStationFactorySelectionKey,
	stationFactoryNewSelectionKey,
} from "~/composables/useStationFactory";

const route = useRoute();
const showSavedPopup = ref(false);
const returnTo = computed(() => typeof route.query.returnTo === "string" ? route.query.returnTo : "");
const lockedStationUid = computed(() => {
	const uid = route.query.uid;

	return typeof uid === "string" && uid.trim().length > 0 ? uid.trim() : "";
});
const selectedStationKey = computed(() => lockedStationUid.value
	? makeStationFactoryExistingSelectionKey(lockedStationUid.value)
	: stationFactoryNewSelectionKey);

const goBack = () => {
	void navigateTo(returnTo.value || routePaths.teacherFactoryWorld);
};

const handleSelectionUpdated = async (selectionKey: string) => {
	const selection = parseStationFactorySelectionKey(selectionKey);

	if (selection.kind === "existing" && selection.uid !== lockedStationUid.value) {
		await navigateTo({
			path: routePaths.teacherFactoryStation,
			query: {
				uid: selection.uid,
				...(returnTo.value ? { returnTo: returnTo.value } : {}),
			},
		}, { replace: true });
	}
};

const handleSaved = () => {
	showSavedPopup.value = true;
};

const handleDeleted = async () => {
	await navigateTo({
		path: routePaths.teacherFactoryStation,
		query: returnTo.value ? { returnTo: returnTo.value } : {},
	}, { replace: true });
};

const closeSavedPopup = () => {
	showSavedPopup.value = false;
};

</script>

<template>
	<section class="factoryPage">
		<header class="factoryHero">
			<button class="backButton" type="button" @click="goBack">Tilbake</button>
		</header>

		<StationFactoryEditor
			:selection-key="selectedStationKey"
			@selection-updated="handleSelectionUpdated"
			@saved="handleSaved"
			@deleted="handleDeleted"
		/>

		<PopupFactorySaved v-if="showSavedPopup" @close="closeSavedPopup" />
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
