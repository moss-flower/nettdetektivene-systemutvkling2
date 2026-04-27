<script setup lang="ts">
import { routePaths } from "~/composables/utils/routePaths";
import {
	createStationDraft,
	deleteStationFactorySelection,
	loadStationFactoryPayload,
	normalizeStationExerciseUids,
	parseStationFactorySelectionKey,
	saveStationFactoryPayload,
	saveStationFactoryPayloadAsNew,
	stationFactoryDefaultImage,
	type StationFactoryPayload,
} from "~/composables/useStationFactory";
import { Exercise } from "~/composables/types/ExerciseType";

const props = defineProps<{
	selectionKey: string;
}>();

const emit = defineEmits<{
	(event: "selection-updated", selectionKey: string): void;
	(event: "saved"): void;
	(event: "deleted"): void;
}>();

const draft = reactive<StationFactoryPayload>(createStationDraft());
const loadedSnapshot = ref<StationFactoryPayload>(createStationDraft());
const copyState = ref<"idle" | "copied" | "failed">("idle");
const loadedLocalId = ref("");
const availableExercises = ref<Exercise[]>([]);

const knownExerciseMap = computed(() => new Map(availableExercises.value.map((exercise) => [exercise.uid, exercise])));
const parsedSelection = computed(() => parseStationFactorySelectionKey(props.selectionKey));

const payloadPreview = computed<StationFactoryPayload>(() => ({
	title: draft.title.trim(),
	description: draft.description.trim(),
	image: draft.image.trim() || stationFactoryDefaultImage,
	exerciseUids: normalizeStationExerciseUids(draft.exerciseUids),
}));

const payloadPreviewText = computed(() => JSON.stringify(payloadPreview.value, null, 2));
const canSave = computed(() => {
	return payloadPreview.value.title.length > 0
		&& payloadPreview.value.description.length > 0
		&& payloadPreview.value.image.length > 0
		&& payloadPreview.value.exerciseUids.length > 0;
});

const hasChanges = computed(() => JSON.stringify(payloadPreview.value) !== JSON.stringify(loadedSnapshot.value));

const selectedExercises = computed(() => payloadPreview.value.exerciseUids.map((uid, index) => {
	const exercise = knownExerciseMap.value.get(uid);

	return {
		uid,
		order: index + 1,
		title: exercise?.title ?? "Ukjent øvelse",
		entryName: exercise?.entryName ?? "Ikke tilgjengelig",
		description: exercise?.description ?? "Denne oppgaven finnes ikke i oversikten akkurat na.",
		isKnown: Boolean(exercise),
	};
}));

const editorTitle = computed(() => {
	if (parsedSelection.value.kind === "existing") {
		return `Rediger stasjon: ${payloadPreview.value.title || "Uten tittel"}`;
	}

	if (parsedSelection.value.kind === "draft") {
		return payloadPreview.value.title ? `Lokal stasjon: ${payloadPreview.value.title}` : "Ny lokal stasjon";
	}

	return "Ny stasjon";
});

const editorDescription = computed(() => {
	if (parsedSelection.value.kind === "existing") {
		return "Du redigerer en eksisterende stasjon. Lokal lagring fungerer som en override over mock-dataen til backend finnes.";
	}

	if (parsedSelection.value.kind === "draft") {
		return "Dette er en lokal stasjon som du kan fortsette å redigere senere fra listen.";
	}

	return "Opprett en ny stasjon lokalt. Etter første lagring dukker den opp i listen og kan redigeres videre.";
	});

const applyDraft = (payload: StationFactoryPayload) => {
	draft.title = payload.title;
	draft.description = payload.description;
	draft.image = payload.image;
	draft.exerciseUids.splice(0, draft.exerciseUids.length, ...payload.exerciseUids);
};

const updateLoadedSnapshot = (payload: StationFactoryPayload) => {
	loadedSnapshot.value = {
		title: payload.title,
		description: payload.description,
		image: payload.image,
		exerciseUids: [...payload.exerciseUids],
	};
};

const loadSelection = async (selectionKey: string) => {
	const loaded = await loadStationFactoryPayload(selectionKey);

	loadedLocalId.value = loaded.localId;
	applyDraft(loaded.payload);
	updateLoadedSnapshot(loaded.payload);
	copyState.value = "idle";
};

const addExerciseUid = (uidValue: string) => {
	const nextUid = uidValue.trim();

	if (!nextUid || draft.exerciseUids.includes(nextUid)) {
		return;
	}

	draft.exerciseUids.push(nextUid);
};

const removeExerciseUid = (uidToRemove: string) => {
	const nextExerciseUids = draft.exerciseUids.filter((uid) => uid !== uidToRemove);
	draft.exerciseUids.splice(0, draft.exerciseUids.length, ...nextExerciseUids);
};

const moveExercise = (index: number, direction: -1 | 1) => {
	const targetIndex = index + direction;

	if (targetIndex < 0 || targetIndex >= draft.exerciseUids.length) {
		return;
	}

	const nextExerciseUids = [...draft.exerciseUids];
	const [movedUid] = nextExerciseUids.splice(index, 1);

	if (movedUid === undefined) {
		return;
	}

	nextExerciseUids.splice(targetIndex, 0, movedUid);
	draft.exerciseUids.splice(0, draft.exerciseUids.length, ...nextExerciseUids);
};

const resetDraft = () => {
	applyDraft(loadedSnapshot.value);
	copyState.value = "idle";
};

const goToExerciseFactory = () => {
	void navigateTo(routePaths.teacherFactoryExercise);
};

const finishSave = (selectionKey: string) => {
	emit("selection-updated", selectionKey);
	emit("saved");
};

const savePayload = async () => {
	if (!canSave.value) {
		return;
	}

	const saved = await saveStationFactoryPayload(props.selectionKey, payloadPreview.value);

	loadedLocalId.value = "";
	updateLoadedSnapshot(saved.payload);
	finishSave(saved.selectionKey);
};

const saveAsNew = async () => {
	if (!canSave.value) {
		return;
	}

	const saved = await saveStationFactoryPayloadAsNew(payloadPreview.value);

	loadedLocalId.value = "";
	updateLoadedSnapshot(saved.payload);
	finishSave(saved.selectionKey);
};

const deleteSelection = async () => {
	await deleteStationFactorySelection(props.selectionKey);
	emit("deleted");
};

const copyPayload = async () => {
	try {
		await navigator.clipboard.writeText(payloadPreviewText.value);
		copyState.value = "copied";
	} catch {
		copyState.value = "failed";
	}
};

watch(
	() => props.selectionKey,
	async (selectionKey) => {
		await loadSelection(selectionKey);
	},
	{ immediate: true },
);

onMounted(async () => {
	availableExercises.value = await Exercise.backendFetchAllExercises();
});

watch(payloadPreviewText, () => {
	if (copyState.value !== "idle") {
		copyState.value = "idle";
	}
});
</script>

<template>
	<form class="factoryGrid" @submit.prevent="savePayload">
		<div class="editorPanel">
			<section class="panelSection">
				<div class="sectionHeading">
					<h2>{{ editorTitle }}</h2>
					<p>{{ editorDescription }}</p>
				</div>

				<div class="fieldGrid">
					<label class="field">
						<span>Tittel</span>
						<input v-model="draft.title" type="text" placeholder="For eksempel: Postkontoret" >
					</label>

					<label class="field">
						<span>Bilde-URL</span>
						<input v-model="draft.image" type="url" placeholder="https://..." >
					</label>

					<label class="field field--full">
						<span>Beskrivelse</span>
						<textarea v-model="draft.description" rows="3" placeholder="Kort beskrivelse av hva eleven skal øve på i denne stasjonen." />
					</label>
				</div>

				<div class="imagePreviewCard">
					<img :src="payloadPreview.image" alt="Forhåndsvisning av stasjonsbilde" class="imagePreview" >
				</div>
			</section>

			<section class="panelSection">
				<div class="sectionHeading">
					<h2>Oppgavesett</h2>
					<p>Rekkefolgen her styrer hvilken oppgave som kommer forst.</p>
				</div>

				<div class="exerciseColumns">
					<div class="exerciseColumn">
						<div class="sectionHeading sectionHeading--compact">
							<h3>Oppgavesett i stasjonen</h3>
						</div>

						<div v-if="selectedExercises.length > 0" class="selectedList">
							<article v-for="(exercise, index) in selectedExercises" :key="exercise.uid" class="selectedCard">
								<div>
									<p class="cardEyebrow">Plass {{ exercise.order }}</p>
									<h3>{{ exercise.title }}</h3>
									<span :class="['statusPill', { 'statusPill--manual': !exercise.isKnown }]">
										{{ exercise.isKnown ? exercise.entryName : 'Ikke tilgjengelig' }}
									</span>
								</div>

								<div class="selectedActions">
									<button type="button" class="ghostButton" :disabled="index === 0" @click="moveExercise(index, -1)">
										&uarr;
									</button>
									<button type="button" class="ghostButton" :disabled="index === selectedExercises.length - 1" @click="moveExercise(index, 1)">
										&darr;
									</button>
									<button type="button" class="deleteButton" @click="removeExerciseUid(exercise.uid)">
										Fjern
									</button>
								</div>
							</article>
						</div>

						<p v-else class="emptyState">
							Ingen oppgavesett er lagt til i stasjonen ennå.
						</p>
					</div>

					<div class="exerciseColumn">
						<div class="sectionHeading sectionHeading--compact sectionHeading--inline">
							<h3>Tilgjengelige oppgavesett</h3>
							<button type="button" class="ghostButton" @click="goToExerciseFactory">
								+ Lag Oppgavesett
							</button>
						</div>

						<div class="knownExercises knownExercises--columns">
							<article v-for="exercise in availableExercises" :key="exercise.uid" class="knownExerciseCard">
								<div>
									<p class="cardEyebrow">{{ exercise.entryName }}</p>
									<h3>{{ exercise.title }}</h3>
								</div>

								<button
									type="button"
									class="ghostButton"
									:disabled="draft.exerciseUids.includes(exercise.uid)"
									@click="addExerciseUid(exercise.uid)"
								>
									{{ draft.exerciseUids.includes(exercise.uid) ? 'Lagt til' : '+ Bruk' }}
								</button>
							</article>
						</div>
					</div>
				</div>
			</section>

			<div class="actionRow actionRow--reset">
				<button type="button" class="ghostButton" :disabled="!hasChanges" @click="resetDraft">
					Tilbakestill endringer
				</button>
			</div>
		</div>

		<aside class="previewPanel">
			<section class="panelSection">
				<div class="sectionHeading">
					<h2>Innhold</h2>
					<p>Her ser du det viktigste i stasjonen akkurat na.</p>
				</div>

				<div class="statusStack">
					<article class="summaryCard">
						<p class="cardEyebrow">Oppgavesett</p>
						<h3>{{ selectedExercises.length }} valgt</h3>
						<p>{{ hasChanges ? 'Det finnes ulagrede endringer.' : 'Editoren er synkron med sist lastede versjon.' }}</p>
					</article>

					<article class="summaryCard">
						<p class="cardEyebrow">Oppgavesett lagt til</p>
						<h3>{{ selectedExercises.filter((exercise) => exercise.isKnown).length }} fra listen</h3>
						<p>Oppgavesettene i listen kan brukes direkte i stasjonen.</p>
					</article>
				</div>
			</section>

			<div class="actionRow actionRow--save">
				<button
					v-if="parsedSelection.kind !== 'new'"
					type="button"
					class="deleteButton"
					@click="deleteSelection"
				>
					Slett
				</button>
				<button type="button" class="ghostButton" :disabled="!canSave" @click="saveAsNew">
					Lagre som ny
				</button>
				<button
					v-if="parsedSelection.kind !== 'new'"
					type="submit"
					class="primaryButton"
					:disabled="!canSave || !hasChanges"
				>
					Lagre
				</button>
			</div>

			<section class="panelSection debugSection">
				<details>
					<summary class="debugSummary">Debug: payload</summary>
					<div class="debugContent">
						<div class="sectionHeading sectionHeading--inline">
							<div>
								<h2>Payload</h2>
								<p>Dette er objektet du senere kan sende med POST til backend.</p>
							</div>

							<button type="button" class="ghostButton" @click="copyPayload">
								{{ copyState === 'copied' ? 'Kopiert' : copyState === 'failed' ? 'Kopiering feilet' : 'Kopier JSON' }}
							</button>
						</div>

						<pre class="payloadPreview">{{ payloadPreviewText }}</pre>
					</div>
				</details>
			</section>
		</aside>
	</form>
</template>

<style scoped>
.factoryGrid {
	display: grid;
	grid-template-columns: 1fr;
	gap: 1.25rem;
	align-items: start;
}

.editorPanel,
.previewPanel {
	padding: 1.25rem;
	border: 1px solid var(--border);
	border-radius: 1.2rem;
	background:
		linear-gradient(180deg, rgba(101, 240, 175, 0.08), rgba(101, 240, 175, 0)) top/100% 5rem no-repeat,
		var(--surface);
	box-shadow: var(--shadow);
}

.panelSection + .panelSection {
	margin-top: 1.5rem;
}

.sectionHeading {
	display: grid;
	gap: 0.35rem;
	margin-bottom: 1rem;
}

.sectionHeading--compact {
	margin-bottom: 0.75rem;
}

.sectionHeading--inline {
	grid-template-columns: 1fr auto;
	align-items: start;
	gap: 0.75rem;
}

.sectionHeading h2,
.summaryCard h3,
.knownExerciseCard h3,
.selectedCard h3 {
	margin: 0;
}

.sectionHeading p,
.summaryCard p,
.knownExerciseCard p,
.selectedCard p,
.emptyState {
	margin: 0;
	color: var(--muted);
	line-height: 1.5;
}

.statusStack,
.knownExercises,
.selectedList {
	display: grid;
	gap: 0.75rem;
}

.exerciseColumns {
	display: grid;
	grid-template-columns: minmax(0, 1fr) minmax(0, 1fr);
	gap: 1rem;
	align-items: start;
}

.exerciseColumn {
	display: grid;
	gap: 0.75rem;
}
.fieldGrid {
	display: grid;
	grid-template-columns: repeat(2, minmax(0, 1fr));
	gap: 0.9rem;
}

.field {
	display: grid;
	gap: 0.45rem;
}

.field--full {
	grid-column: 1 / -1;
}

.field--grow {
	flex: 1;
}

.field span {
	font-weight: 700;
}

.field input,
.field textarea {
	width: 100%;
	padding: 0.85rem 1rem;
	border: 1px solid var(--border);
	border-radius: 0.9rem;
	background: rgba(0, 0, 0, 0.16);
	color: var(--text);
	font: inherit;
}

.field textarea {
	resize: vertical;
}

.field input:focus,
.field textarea:focus {
	outline: none;
	border-color: var(--primary);
}

.summaryCard,
.knownExerciseCard,
.selectedCard {
	display: flex;
	justify-content: space-between;
	align-items: flex-start;
	gap: 0.75rem;
	padding: 1rem;
	border: 1px solid var(--border);
	border-radius: 1rem;
	background: rgba(255, 255, 255, 0.02);
}

.imagePreviewCard {
	margin-top: 1rem;
	border: 1px solid var(--border);
	border-radius: 1rem;
	overflow: hidden;
	background: rgba(255, 255, 255, 0.02);
}

.imagePreview {
	display: block;
	width: 100%;
	aspect-ratio: 16 / 8;
	object-fit: cover;
}

.selectedActions,
.actionRow {
	display: flex;
	gap: 0.75rem;
	align-items: end;
	flex-wrap: wrap;
}

.knownExercises,
.selectedList {
	margin-top: 1rem;
}

.knownExercises--columns,
.selectedList {
	margin-top: 0;
}

.statusPill {
	display: inline-flex;
	margin-top: 0.75rem;
	padding: 0.3rem 0.65rem;
	border-radius: 999px;
	background: rgba(101, 240, 175, 0.12);
	color: var(--primary);
	font-size: 0.85rem;
	font-weight: 700;
}

.statusPill--manual {
	background: rgba(255, 255, 255, 0.08);
	color: var(--text);
}

.ghostButton,
.deleteButton,
.primaryButton {
	border: 1px solid var(--border);
	border-radius: 999px;
	padding: 0.75rem 1rem;
	font-weight: 700;
	transition: transform var(--transition), border-color var(--transition), background var(--transition);
}

.ghostButton,
.deleteButton {
	background: transparent;
	color: var(--text);
}

.primaryButton {
	border-color: var(--primary);
	background: var(--primary);
	color: var(--bg);
}

.deleteButton {
	color: #fca5a5;
}

.ghostButton:hover,
.deleteButton:hover,
.primaryButton:hover {
	transform: translateY(-1px);
}

.ghostButton:disabled,
.deleteButton:disabled,
.primaryButton:disabled {
	opacity: 0.5;
	cursor: not-allowed;
	transform: none;
}

.actionRow {
	justify-content: space-between;
	margin-top: 1.5rem;
}

.actionRow--reset {
	justify-content: flex-start;
}

.actionRow--save {
	margin-top: 0;
	justify-content: flex-end;
}

.payloadPreview {
	overflow-x: auto;
	padding: 1rem;
	border-radius: 1rem;
	background: rgba(0, 0, 0, 0.24);
	color: #d7f9ea;
	font-size: 0.9rem;
	line-height: 1.5;
}

.debugSection {
	opacity: 0.9;
}

.debugSummary {
	cursor: pointer;
	font-weight: 700;
	color: var(--muted);
	list-style: none;
}

.debugSummary::-webkit-details-marker {
	display: none;
}

.debugContent {
	margin-top: 1rem;
}

@media (max-width: 800px) {
	.fieldGrid {
		grid-template-columns: 1fr;
	}

	.exerciseColumns {
		grid-template-columns: 1fr;
	}

	.sectionHeading--inline,
	.summaryCard,
	.knownExerciseCard,
	.selectedCard,
	.actionRow {
		display: grid;
	}

	.selectedActions {
		align-items: stretch;
	}

	.actionRow {
		justify-content: stretch;
	}
}
</style>
