<script setup lang="ts">
import WorldFactoryEditor from "~/components/teacher/factory/WorldFactoryEditor.vue";
import PopupFactorySaved from "~/components/teacher/factory/PopupFactorySaved.vue";
import { routePaths } from "~/composables/utils/routePaths";
import {
	getWorldFactorySelectionOptions,
	makeWorldFactoryExistingSelectionKey,
	type WorldFactorySelectableOption,
	worldFactoryNewSelectionKey,
} from "~/composables/useWorldFactory";

const route = useRoute();
const router = useRouter();
const selectedWorldKey = ref<string>("");
const selectableOptions = ref<WorldFactorySelectableOption[]>([]);
const showSavedPopup = ref(false);
const lockedWorldUid = computed(() => {
	const uid = route.query.uid;

	return typeof uid === "string" && uid.trim().length > 0 ? uid.trim() : "";
});
const showWorldSelector = computed(() => lockedWorldUid.value.length === 0);

const goBack = () => {
	if (import.meta.client && window.history.length > 1) {
		router.back();
		return;
	}

	void navigateTo(routePaths.teacherOverview);
};

const refreshSelectableOptions = () => {
	selectableOptions.value = getWorldFactorySelectionOptions();
};
const handleSelectionUpdated = (selectionKey: string) => {
	refreshSelectableOptions();
	selectedWorldKey.value = selectionKey;
};

const handleSaved = () => {
	refreshSelectableOptions();
	selectedWorldKey.value = lockedWorldUid.value
		? makeWorldFactoryExistingSelectionKey(lockedWorldUid.value)
		: "";
	showSavedPopup.value = true;
};

const handleDeleted = () => {
	refreshSelectableOptions();
	selectedWorldKey.value = lockedWorldUid.value
		? makeWorldFactoryExistingSelectionKey(lockedWorldUid.value)
		: "";
};

const closeSavedPopup = () => {
	showSavedPopup.value = false;
};

onMounted(() => {
	refreshSelectableOptions();
});

watch(
	lockedWorldUid,
	(uid) => {
		selectedWorldKey.value = uid ? makeWorldFactoryExistingSelectionKey(uid) : "";
	},
	{ immediate: true },
);
</script>

<template>
	<section class="factoryPage">
		<header class="factoryHero">
			<button class="backButton" type="button" @click="goBack">Tilbake</button>
			<h1 v-if="showWorldSelector">Verdener</h1>
		</header>

		<section v-if="showWorldSelector" class="selectorPanel">
			<div class="selectorControls">
				<label class="field field--grow">
					<select v-model="selectedWorldKey">
						<option value="">Velg verden</option>
						<option :value="worldFactoryNewSelectionKey">Ny verden</option>
						<option v-for="option in selectableOptions" :key="option.selectionKey" :value="option.selectionKey">
							{{ option.label }}
						</option>
					</select>
				</label>
			</div>
		</section>

		<WorldFactoryEditor
			v-if="selectedWorldKey"
			:selection-key="selectedWorldKey"
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
