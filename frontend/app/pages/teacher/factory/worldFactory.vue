<script setup lang="ts">
import WorldFactoryEditor from "~/components/teacher/factory/WorldFactoryEditor.vue";
import PopupFactorySaved from "~/components/teacher/factory/PopupFactorySaved.vue";
import { routePaths } from "~/composables/utils/routePaths";
import {
	makeWorldFactoryExistingSelectionKey,
	worldFactoryNewSelectionKey,
	parseWorldFactorySelectionKey,
} from "~/composables/useWorldFactory";

const route = useRoute();
const showSavedPopup = ref(false);
const returnTo = computed(() => typeof route.query.returnTo === "string" ? route.query.returnTo : "");
const lockedWorldUid = computed(() => {
	const uid = route.query.uid;

	return typeof uid === "string" && uid.trim().length > 0 ? uid.trim() : "";
});
const selectedWorldKey = computed(() => lockedWorldUid.value
	? makeWorldFactoryExistingSelectionKey(lockedWorldUid.value)
	: worldFactoryNewSelectionKey);

const goBack = () => {
	void navigateTo(returnTo.value || routePaths.teacherOverview);
};

const handleSelectionUpdated = async (selectionKey: string) => {
	const selection = parseWorldFactorySelectionKey(selectionKey);

	if (selection.kind === "existing" && selection.uid !== lockedWorldUid.value) {
		await navigateTo({
			path: routePaths.teacherFactoryWorld,
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
		path: routePaths.teacherFactoryWorld,
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

		<WorldFactoryEditor
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
