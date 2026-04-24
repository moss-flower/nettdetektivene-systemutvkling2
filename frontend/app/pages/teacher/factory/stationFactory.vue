<script setup lang="ts">
import StationFactoryEditor from "~/components/teacher/factory/StationFactoryEditor.vue";
import PopupFactorySaved from "~/components/teacher/factory/PopupFactorySaved.vue";
import { routePaths } from "~/composables/utils/routePaths";
import {
	getStationFactorySelectionOptions,
	makeStationFactoryExistingSelectionKey,
	stationFactoryNewSelectionKey,
	type StationFactorySelectableOption,
} from "~/composables/useStationFactory";

const route = useRoute();
const router = useRouter();
const selectedStationKey = ref<string>("");
const selectableOptions = ref<StationFactorySelectableOption[]>([]);
const showSavedPopup = ref(false);
const lockedStationUid = computed(() => {
	const uid = route.query.uid;

	return typeof uid === "string" && uid.trim().length > 0 ? uid.trim() : "";
});
const showStationSelector = computed(() => lockedStationUid.value.length === 0);

const goBack = () => {
	if (import.meta.client && window.history.length > 1) {
		router.back();
		return;
	}

	void navigateTo(routePaths.teacherOverview);
};

const refreshSelectableOptions = () => {
	selectableOptions.value = getStationFactorySelectionOptions();
};
const handleSelectionUpdated = (selectionKey: string) => {
	refreshSelectableOptions();
	selectedStationKey.value = selectionKey;
};

const handleSaved = () => {
	refreshSelectableOptions();
	selectedStationKey.value = lockedStationUid.value
		? makeStationFactoryExistingSelectionKey(lockedStationUid.value)
		: "";
	showSavedPopup.value = true;
};

const handleDeleted = () => {
	refreshSelectableOptions();
	selectedStationKey.value = lockedStationUid.value
		? makeStationFactoryExistingSelectionKey(lockedStationUid.value)
		: "";
};

const closeSavedPopup = () => {
	showSavedPopup.value = false;
};

onMounted(() => {
	refreshSelectableOptions();
});

watch(
	lockedStationUid,
	(uid) => {
		selectedStationKey.value = uid ? makeStationFactoryExistingSelectionKey(uid) : "";
	},
	{ immediate: true },
);
</script>

<template>
	<section class="factoryPage">
		<header class="factoryHero">
			<button class="backButton" type="button" @click="goBack">Tilbake</button>
			<h1 v-if="showStationSelector">Stasjoner</h1>
		</header>

		<section v-if="showStationSelector" class="selectorPanel">
			<div class="selectorControls">
				<label class="field field--grow">
					<select v-model="selectedStationKey">
						<option value="">Velg stasjon</option>
						<option :value="stationFactoryNewSelectionKey">Ny stasjon</option>
						<option v-for="option in selectableOptions" :key="option.selectionKey" :value="option.selectionKey">
							{{ option.label }}
						</option>
					</select>
				</label>
			</div>
		</section>

		<StationFactoryEditor
			v-if="selectedStationKey"
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
