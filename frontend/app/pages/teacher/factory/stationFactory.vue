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
const router = useRouter();
const showSavedPopup = ref(false);
const lockedStationUid = computed(() => {
	const uid = route.query.uid;

	return typeof uid === "string" && uid.trim().length > 0 ? uid.trim() : "";
});
const selectedStationKey = computed(() => lockedStationUid.value
	? makeStationFactoryExistingSelectionKey(lockedStationUid.value)
	: stationFactoryNewSelectionKey);

const goBack = () => {
	if (import.meta.client && window.history.length > 1) {
		router.back();
		return;
	}

	void navigateTo(routePaths.teacherOverview);
};

const handleSelectionUpdated = async (selectionKey: string) => {
	const selection = parseStationFactorySelectionKey(selectionKey);

	if (selection.kind === "existing" && selection.uid !== lockedStationUid.value) {
		await navigateTo(`${routePaths.teacherFactoryStation}?uid=${selection.uid}`, { replace: true });
	}
};

const handleSaved = () => {
	showSavedPopup.value = true;
};

const handleDeleted = async () => {
	await navigateTo(routePaths.teacherFactoryStation, { replace: true });
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
	max-width: 1400px;
	margin: 0 auto;
	padding: var(--space-4);
	color: var(--color-text-primary);
	background:
		linear-gradient(180deg, rgba(255, 255, 255, 0.78), rgba(255, 255, 255, 0.88)),
		linear-gradient(180deg, var(--palette-gray-50) 0%, var(--palette-cream-100) 100%);
	border-radius: var(--radius-4);
}

.factoryHero {
	margin-bottom: var(--space-4);
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
	font-weight: 600;
	cursor: pointer;
}

.backButton:hover {
	text-decoration: underline;
}

h1,
h2,
h3 {
	margin: 0;
}

.selectorPanel {
	display: grid;
	gap: var(--space-4);
	margin-bottom: 1.25rem;
	padding: 1.25rem;
	border: var(--border-width-hairline) solid var(--color-border-default);
	border-radius: 1.2rem;
	background:
		linear-gradient(180deg, rgba(101, 240, 175, 0.08), rgba(101, 240, 175, 0)) top/100% 5rem no-repeat,
		var(--color-bg-surface);
	box-shadow: var(--shadow-1);
}
.selectorControls {
	display: flex;
}

.field {
	display: grid;
	gap: 0.45rem;
	max-width: 32rem;
}

.fieldGrow {
	flex: 1;
}

.field select {
	width: 100%;
	padding: 0.85rem var(--space-4);
	border: var(--border-width-hairline) solid var(--color-border-default);
	border-radius: 0.9rem;
	background: color-mix(in srgb, var(--color-bg-surface) 92%, var(--color-bg-canvas) 8%);
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
