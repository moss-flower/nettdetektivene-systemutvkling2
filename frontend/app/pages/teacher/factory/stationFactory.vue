<script setup lang="ts">
import StationFactoryEditor from "~/components/teacher/factory/StationFactoryEditor.vue";
import { routePaths } from "~/composables/utils/routePaths";
import {
	getStationFactorySelectionOptions,
	stationFactoryNewSelectionKey,
	type StationFactorySelectableOption,
} from "~/composables/useStationFactory";

const backPath = routePaths.teacherOverview;
const selectedStationKey = ref<string>("");
const selectableOptions = ref<StationFactorySelectableOption[]>([]);

const refreshSelectableOptions = () => {
	selectableOptions.value = getStationFactorySelectionOptions();
};

const selectedOption = computed(() => {
	return selectableOptions.value.find((option) => option.selectionKey === selectedStationKey.value) ?? null;
});

const startNewStation = () => {
	selectedStationKey.value = stationFactoryNewSelectionKey;
};

const handleSelectionUpdated = (selectionKey: string) => {
	refreshSelectableOptions();
	selectedStationKey.value = selectionKey;
};

onMounted(() => {
	refreshSelectableOptions();
});
</script>

<template>
	<section class="factoryPage">
		<header class="factoryHero">
			<div>
				<NuxtLink class="factoryBackLink" :to="backPath">
					Tilbake til læreroversikt
				</NuxtLink>
				<p class="factoryEyebrow">Lærerverktøy</p>
				<h1>Stasjonsfabrikk</h1>
				<p class="factoryDescription">
					Velg en eksisterende stasjon for å redigere den, eller start en ny lokal stasjon. Nye lokale stasjoner kan velges igjen fra listen etter første lagring.
				</p>
			</div>
		</header>

		<section class="selectorPanel">
			<div class="selectorHeading">
				<h2>Velg stasjon</h2>
				<p>Eksisterende stasjoner kan få en lokal override, og nye stasjoner kan opprettes og redigeres videre senere.</p>
			</div>

			<div class="selectorControls">
				<label class="field field--grow">
					<span>Stasjon</span>
					<select v-model="selectedStationKey">
						<option value="">Velg stasjon</option>
						<option v-for="option in selectableOptions" :key="option.selectionKey" :value="option.selectionKey">
							{{ option.label }}
						</option>
					</select>
				</label>

				<button type="button" class="primaryButton" @click="startNewStation">
					Ny stasjon
				</button>
			</div>

			<article class="selectedResourceCard">
				<p class="cardEyebrow">Valgt ressurs</p>
				<h3>
					{{ selectedStationKey === stationFactoryNewSelectionKey ? 'Ny stasjon' : selectedOption?.label ?? 'Ingen valgt ennå' }}
				</h3>
				<p>
					{{ selectedStationKey === stationFactoryNewSelectionKey ? 'Tom editor for en ny lokal stasjon.' : selectedOption?.badge ?? 'Velg en stasjon eller trykk Ny stasjon for å åpne editoren.' }}
				</p>
				<p class="selectedResourceDescription">
					{{ selectedStationKey === stationFactoryNewSelectionKey ? 'Selve uid-en vil bli satt av backend senere.' : selectedOption?.description ?? '' }}
				</p>
			</article>
		</section>

		<StationFactoryEditor
			v-if="selectedStationKey"
			:selection-key="selectedStationKey"
			@selection-updated="handleSelectionUpdated"
		/>
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
	margin-bottom: 1.5rem;
}

.factoryBackLink {
	display: inline-flex;
	margin-bottom: 0.75rem;
	color: var(--primary);
	font-weight: 700;
}

.factoryEyebrow,
.cardEyebrow {
	margin: 0 0 0.5rem;
	color: var(--primary);
	font-weight: 700;
	letter-spacing: 0.08em;
	text-transform: uppercase;
	font-size: 0.82rem;
}

h1,
h2,
h3 {
	margin: 0;
}

.factoryDescription,
.selectorHeading p,
.selectedResourceCard p {
	margin: 0;
	color: var(--muted);
	line-height: 1.5;
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

.selectorHeading {
	display: grid;
	gap: 0.35rem;
}

.selectorControls {
	display: flex;
	gap: 0.75rem;
	align-items: end;
	flex-wrap: wrap;
}

.field {
	display: grid;
	gap: 0.45rem;
	max-width: 32rem;
}

.field--grow {
	flex: 1;
}

.field span {
	font-weight: 700;
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

.selectedResourceCard {
	display: grid;
	gap: 0.25rem;
	max-width: 32rem;
	padding: 1rem;
	border: 1px solid var(--border);
	border-radius: 1rem;
	background: rgba(255, 255, 255, 0.02);
}

.selectedResourceDescription {
	font-size: 0.95rem;
}

.primaryButton {
	border: 1px solid var(--primary);
	border-radius: 999px;
	padding: 0.75rem 1rem;
	font-weight: 700;
	background: var(--primary);
	color: var(--bg);
	transition: transform var(--transition), border-color var(--transition), background var(--transition);
}

.primaryButton:hover {
	transform: translateY(-1px);
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
