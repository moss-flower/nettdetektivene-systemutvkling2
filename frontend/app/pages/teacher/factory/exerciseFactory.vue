<script setup lang="ts">
import ExerciseFactoryEditor from "~/components/teacher/factory/ExerciseFactoryEditor.vue";
import { routePaths } from "~/composables/routePaths";
import {
	exerciseFactoryNewSelectionKey,
	getExerciseFactorySelectionOptions,
	type ExerciseFactorySelectableOption,
} from "~/composables/useExerciseFactory";

const backPath = routePaths.teacherOverview;
const selectedExerciseKey = ref<string>("");
const selectableOptions = ref<ExerciseFactorySelectableOption[]>([]);

const refreshSelectableOptions = () => {
	selectableOptions.value = getExerciseFactorySelectionOptions();
};

const selectedOption = computed(() => {
	return selectableOptions.value.find((option) => option.selectionKey === selectedExerciseKey.value) ?? null;
});

const startNewExercise = () => {
	selectedExerciseKey.value = exerciseFactoryNewSelectionKey;
};

const handleSelectionUpdated = (selectionKey: string) => {
	refreshSelectableOptions();
	selectedExerciseKey.value = selectionKey;
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
				<h1>Oppgavefabrikk</h1>
				<p class="factoryDescription">
					Velg en eksisterende oppgave for å redigere den, eller start en ny lokal oppgave. Nye lokale oppgaver kan velges igjen fra listen etter første lagring.
				</p>
			</div>
		</header>

		<section class="selectorPanel">
			<div class="selectorHeading">
				<h2>Velg oppgave</h2>
				<p>Eksisterende oppgaver kan få en lokal override, og nye oppgaver kan opprettes og redigeres videre senere.</p>
			</div>

			<div class="selectorControls">
				<label class="field field--grow">
					<span>Oppgave</span>
					<select v-model="selectedExerciseKey">
						<option value="">Velg oppgave</option>
						<option v-for="option in selectableOptions" :key="option.selectionKey" :value="option.selectionKey">
							{{ option.label }}
						</option>
					</select>
				</label>

				<button type="button" class="primaryButton" @click="startNewExercise">
					Ny oppgave
				</button>
			</div>

			<article class="selectedResourceCard">
				<p class="cardEyebrow">Valgt ressurs</p>
				<h3>
					{{ selectedExerciseKey === exerciseFactoryNewSelectionKey ? 'Ny oppgave' : selectedOption?.label ?? 'Ingen valgt ennå' }}
				</h3>
				<p>
					{{ selectedExerciseKey === exerciseFactoryNewSelectionKey ? 'Tom editor for en ny lokal oppgave.' : selectedOption?.badge ?? 'Velg en oppgave eller trykk Ny oppgave for å åpne editoren.' }}
				</p>
				<p class="selectedResourceDescription">
					{{ selectedExerciseKey === exerciseFactoryNewSelectionKey ? 'Selve uid-en vil bli satt av backend senere.' : selectedOption?.description ?? '' }}
				</p>
			</article>
		</section>

		<ExerciseFactoryEditor
			v-if="selectedExerciseKey"
			:selection-key="selectedExerciseKey"
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
