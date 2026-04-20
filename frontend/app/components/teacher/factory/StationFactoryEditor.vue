<script setup lang="ts">
import {
	clearStationFactoryOverride,
	createStationDraft,
	getStationFactorySelectionOptions,
	makeStationFactoryDraftSelectionKey,
	makeStationFactoryExistingSelectionKey,
	normalizeStationExerciseUids,
	parseStationFactorySelectionKey,
	readStationFactoryStore,
	removeStationFactoryDraft,
	saveStationFactoryDraft,
	saveStationFactoryOverride,
	stationFactoryDefaultImage,
	stationFactoryNewSelectionKey,
	type StationFactoryPayload,
} from "~/composables/useStationFactory";
import { Exercise } from "~/composables/types/ExerciseType";
import { Station } from "~/composables/types/StationType";

const props = defineProps<{
	selectionKey: string;
}>();

const emit = defineEmits<{
	(event: "selection-updated", selectionKey: string): void;
}>();

const draft = reactive<StationFactoryPayload>(createStationDraft());
const loadedSnapshot = ref<StationFactoryPayload>(createStationDraft());
const exerciseUidInput = ref("");
const copyState = ref<"idle" | "copied" | "failed">("idle");
const saveState = ref<"idle" | "saved">("idle");
const loadedStationUid = ref("");
const loadedLocalId = ref("");
const hasStoredOverride = ref(false);
const lastSavedAt = ref("");

const availableExercises = computed(() => Exercise.backendFetchAllExercises());
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

const formattedLastSavedAt = computed(() => {
	if (!lastSavedAt.value) {
		return "";
	}

	return new Intl.DateTimeFormat("nb-NO", {
		dateStyle: "medium",
		timeStyle: "short",
	}).format(new Date(lastSavedAt.value));
});

const selectedExercises = computed(() => payloadPreview.value.exerciseUids.map((uid, index) => {
	const exercise = knownExerciseMap.value.get(uid);

	return {
		uid,
		order: index + 1,
		title: exercise?.title ?? "Ukjent øvelse",
		entryName: exercise?.entryName ?? "Manuell uid",
		description: exercise?.description ?? "Denne uid-en finnes ikke i dagens mock-data, men kan fortsatt være gyldig i backend senere.",
		isKnown: Boolean(exercise),
	};
}));

const editorTitle = computed(() => {
	if (parsedSelection.value.kind === "existing") {
		return `Rediger stasjon: ${payloadPreview.value.title || loadedStationUid.value}`;
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
		return "Dette er en lokal stasjon uten backend-uid ennå. Du kan fortsette å redigere den fra nedtrekkslisten.";
	}

	return "Opprett en ny stasjon lokalt. Etter første lagring dukker den opp i listen og kan redigeres videre.";
	});

const applyDraft = (payload: StationFactoryPayload) => {
	draft.title = payload.title;
	draft.description = payload.description;
	draft.image = payload.image;
	draft.exerciseUids.splice(0, draft.exerciseUids.length, ...payload.exerciseUids);
	exerciseUidInput.value = "";
};

const updateLoadedSnapshot = (payload: StationFactoryPayload) => {
	loadedSnapshot.value = {
		title: payload.title,
		description: payload.description,
		image: payload.image,
		exerciseUids: [...payload.exerciseUids],
	};
};

const loadSelection = (selectionKey: string) => {
	const selection = parseStationFactorySelectionKey(selectionKey);
	const store = readStationFactoryStore();

	loadedStationUid.value = "";
	loadedLocalId.value = "";
	hasStoredOverride.value = false;
	lastSavedAt.value = "";

	if (selection.kind === "existing") {
		const backendStation = Station.backendFetchStationByUid(selection.uid);
		const override = store.overrides[selection.uid] ?? null;
		const nextPayload = override?.payload
			?? createStationDraft({
				title: backendStation?.title ?? "",
				description: backendStation?.description ?? "",
				image: backendStation?.image ?? stationFactoryDefaultImage,
				exerciseUids: backendStation?.exerciseUids ?? [],
			});

		loadedStationUid.value = selection.uid;
		hasStoredOverride.value = Boolean(override);
		lastSavedAt.value = override?.updatedAt ?? "";
		applyDraft(nextPayload);
		updateLoadedSnapshot(nextPayload);
	} else if (selection.kind === "draft") {
		const storedDraft = store.drafts.find((draftEntry) => draftEntry.localId === selection.localId) ?? null;
		const nextPayload = storedDraft?.payload ?? createStationDraft();

		loadedLocalId.value = storedDraft?.localId ?? "";
		lastSavedAt.value = storedDraft?.updatedAt ?? "";
		applyDraft(nextPayload);
		updateLoadedSnapshot(nextPayload);
	} else {
		const nextPayload = createStationDraft();

		applyDraft(nextPayload);
		updateLoadedSnapshot(nextPayload);
	}

	copyState.value = "idle";
	saveState.value = "idle";
};

const addExerciseUid = (uidValue = exerciseUidInput.value) => {
	const nextUid = uidValue.trim();

	if (!nextUid || draft.exerciseUids.includes(nextUid)) {
		return;
	}

	draft.exerciseUids.push(nextUid);
	exerciseUidInput.value = "";
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
	saveState.value = "idle";
};

const savePayload = () => {
	if (!canSave.value) {
		return;
	}

	if (parsedSelection.value.kind === "existing") {
		const record = saveStationFactoryOverride(parsedSelection.value.uid, payloadPreview.value);

		updateLoadedSnapshot(payloadPreview.value);
		hasStoredOverride.value = true;
		lastSavedAt.value = record.updatedAt;
		saveState.value = "saved";
		emit("selection-updated", makeStationFactoryExistingSelectionKey(parsedSelection.value.uid));
		return;
	}

	const record = saveStationFactoryDraft(payloadPreview.value, loadedLocalId.value || undefined);

	loadedLocalId.value = record.localId;
	updateLoadedSnapshot(payloadPreview.value);
	lastSavedAt.value = record.updatedAt;
	saveState.value = "saved";
	emit("selection-updated", makeStationFactoryDraftSelectionKey(record.localId));
};

const clearLocalState = () => {
	if (parsedSelection.value.kind === "existing") {
		clearStationFactoryOverride(parsedSelection.value.uid);
		loadSelection(makeStationFactoryExistingSelectionKey(parsedSelection.value.uid));
		emit("selection-updated", makeStationFactoryExistingSelectionKey(parsedSelection.value.uid));
		return;
	}

	if (parsedSelection.value.kind === "draft" && loadedLocalId.value) {
		removeStationFactoryDraft(loadedLocalId.value);
		emit("selection-updated", stationFactoryNewSelectionKey);
	}
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
	(selectionKey) => {
		loadSelection(selectionKey);
	},
	{ immediate: true },
);

watch(payloadPreviewText, () => {
	if (copyState.value !== "idle") {
		copyState.value = "idle";
	}

	if (saveState.value === "saved" && hasChanges.value) {
		saveState.value = "idle";
	}
});
</script>

<template>
	<div class="factoryGrid">
		<form class="editorPanel" @submit.prevent="savePayload">
			<section class="panelSection">
				<div class="sectionHeading">
					<h2>{{ editorTitle }}</h2>
					<p>{{ editorDescription }}</p>
				</div>

				<div class="summaryGrid">
					<article class="summaryCard">
						<p class="cardEyebrow">Kilde</p>
						<h3>{{ parsedSelection.kind === 'existing' ? 'Eksisterende stasjon' : parsedSelection.kind === 'draft' ? 'Lokal stasjon' : 'Ny stasjon' }}</h3>
						<p>{{ loadedStationUid || loadedLocalId || 'Ingen uid ennå' }}</p>
					</article>

					<article class="summaryCard">
						<p class="cardEyebrow">Status</p>
						<h3>{{ saveState === 'saved' ? 'Lokalt lagret' : hasStoredOverride ? 'Lokal override aktiv' : 'Redigerer grunnlag' }}</h3>
						<p>{{ formattedLastSavedAt || 'Ikke lagret i denne nettleseren ennå.' }}</p>
					</article>
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
					<h2>Exercise uid-er</h2>
					<p>Rekkefølgen her styrer hvilken oppgave som låses opp først.</p>
				</div>

				<div class="uidInputRow">
					<label class="field field--grow">
						<span>Legg til uid manuelt</span>
						<input
							v-model="exerciseUidInput"
							type="text"
							placeholder="For eksempel: newsstand-exercise-1"
							@keydown.enter.prevent="addExerciseUid()"
						>
					</label>

					<button type="button" class="ghostButton" @click="addExerciseUid()">
						+ Legg til uid
					</button>
				</div>

				<div class="knownExercises">
					<article v-for="exercise in availableExercises" :key="exercise.uid" class="knownExerciseCard">
						<div>
							<p class="cardEyebrow">{{ exercise.entryName }}</p>
							<h3>{{ exercise.title }}</h3>
							<p>{{ exercise.uid }}</p>
						</div>

						<button
							type="button"
							class="ghostButton"
							:disabled="draft.exerciseUids.includes(exercise.uid)"
							@click="addExerciseUid(exercise.uid)"
						>
							{{ draft.exerciseUids.includes(exercise.uid) ? 'Lagt til' : 'Bruk uid' }}
						</button>
					</article>
				</div>

				<div v-if="selectedExercises.length > 0" class="selectedList">
					<article v-for="(exercise, index) in selectedExercises" :key="exercise.uid" class="selectedCard">
						<div>
							<p class="cardEyebrow">Plass {{ exercise.order }}</p>
							<h3>{{ exercise.title }}</h3>
							<p>{{ exercise.uid }}</p>
							<span :class="['statusPill', { 'statusPill--manual': !exercise.isKnown }]">
								{{ exercise.isKnown ? exercise.entryName : 'Manuell uid' }}
							</span>
						</div>

						<div class="selectedActions">
							<button type="button" class="ghostButton" :disabled="index === 0" @click="moveExercise(index, -1)">
								Opp
							</button>
							<button type="button" class="ghostButton" :disabled="index === selectedExercises.length - 1" @click="moveExercise(index, 1)">
								Ned
							</button>
							<button type="button" class="deleteButton" @click="removeExerciseUid(exercise.uid)">
								Fjern
							</button>
						</div>
					</article>
				</div>

				<p v-else class="emptyState">
					Ingen exercise uid-er lagt til ennå.
				</p>
			</section>

			<div class="actionRow">
				<button type="button" class="ghostButton" :disabled="!hasChanges" @click="resetDraft">
					Tilbakestill endringer
				</button>
				<button
					v-if="parsedSelection.kind !== 'new'"
					type="button"
					class="ghostButton"
					:disabled="parsedSelection.kind === 'existing' ? !hasStoredOverride : !loadedLocalId"
					@click="clearLocalState"
				>
					{{ parsedSelection.kind === 'existing' ? 'Fjern lokal override' : 'Slett lokal stasjon' }}
				</button>
				<button type="submit" class="primaryButton" :disabled="!canSave || !hasChanges">
					Lagre stasjon lokalt
				</button>
			</div>
		</form>

		<aside class="previewPanel">
			<section class="panelSection">
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
			</section>

			<section class="panelSection">
				<div class="sectionHeading">
					<h2>Innhold</h2>
					<p>Her ser du hva som blir med i stasjonen du redigerer akkurat nå.</p>
				</div>

				<div class="statusStack">
					<article class="summaryCard">
						<p class="cardEyebrow">Øvelser</p>
						<h3>{{ selectedExercises.length }} valgt</h3>
						<p>{{ hasChanges ? 'Det finnes ulagrede endringer.' : 'Editoren er synkron med sist lastede versjon.' }}</p>
					</article>

					<article class="summaryCard">
						<p class="cardEyebrow">Kjente uid-er</p>
						<h3>{{ selectedExercises.filter((exercise) => exercise.isKnown).length }} kjent</h3>
						<p>Manuelle uid-er kan fortsatt være gyldige senere når backend finnes.</p>
					</article>
				</div>
			</section>
		</aside>
	</div>
</template>

<style scoped>
.factoryGrid {
	display: grid;
	grid-template-columns: minmax(0, 1.2fr) minmax(22rem, 0.8fr);
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

.summaryGrid,
.statusStack,
.knownExercises,
.selectedList {
	display: grid;
	gap: 0.75rem;
}

.summaryGrid {
	grid-template-columns: repeat(2, minmax(0, 1fr));
	margin-bottom: 1rem;
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

.uidInputRow,
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

.payloadPreview {
	overflow-x: auto;
	padding: 1rem;
	border-radius: 1rem;
	background: rgba(0, 0, 0, 0.24);
	color: #d7f9ea;
	font-size: 0.9rem;
	line-height: 1.5;
}

@media (max-width: 1100px) {
	.factoryGrid {
		grid-template-columns: 1fr;
	}
}

@media (max-width: 800px) {
	.summaryGrid,
	.fieldGrid {
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