<script setup lang="ts">
import StationFactoryEditor from "~/components/teacher/factory/StationFactoryEditor.vue";
import PopupFactorySaved from "~/components/teacher/factory/PopupFactorySaved.vue";
import {
	getStationFactorySelectionOptions,
	stationFactoryNewSelectionKey,
	type StationFactorySelectableOption,
} from "~/composables/useStationFactory";

const selectedStationKey = ref<string>("");
const selectableOptions = ref<StationFactorySelectableOption[]>([]);
const showSavedPopup = ref(false);

const refreshSelectableOptions = () => {
	selectableOptions.value = getStationFactorySelectionOptions();
};
const handleSelectionUpdated = (selectionKey: string) => {
	refreshSelectableOptions();
	selectedStationKey.value = selectionKey;
};

const handleSaved = () => {
	refreshSelectableOptions();
	selectedStationKey.value = "";
	showSavedPopup.value = true;
};

const handleDeleted = () => {
	refreshSelectableOptions();
	selectedStationKey.value = "";
};

const closeSavedPopup = () => {
	showSavedPopup.value = false;
};

onMounted(() => {
	refreshSelectableOptions();
});
</script>

<template>
	<section class="factoryPage">
		<header class="factoryHero">
			<h1>Stasjoner</h1>
		</header>

		<section class="selectorPanel">
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
	margin-bottom: 1rem;
}

h1,
h2,
h3 {
	margin: 0;
}

.selectorPanel {
	display: grid;
	gap: 1rem;
	margin-bottom: 1.25rem;
	padding: 1.25rem;
	border: 1px solid var(--border);
	border-radius: 1.2rem;
	background:
		linear-gradient(180deg, rgba(101, 240, 175, 0.08), rgba(101, 240, 175, 0)) top/100% 5rem no-repeat,
		var(--surface);
	box-shadow: var(--shadow);
}
.selectorControls {
	display: flex;
}

.field {
	display: grid;
	gap: 0.45rem;
	max-width: 32rem;
}

.field--grow {
	flex: 1;
}

.field select {
	width: 100%;
	padding: 0.85rem 1rem;
	border: 1px solid var(--border);
	border-radius: 0.9rem;
	background: rgba(0, 0, 0, 0.16);
	color: var(--text);
	font: inherit;
}

.field select:focus {
	outline: none;
	border-color: var(--primary);
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
