<script setup lang="ts">
import {
	createWorldDraft,
	makeWorldFactoryDraftSelectionKey,
	makeWorldFactoryExistingSelectionKey,
	normalizeWorldStationUids,
	parseWorldFactorySelectionKey,
	readWorldFactoryStore,
	saveWorldFactoryDraft,
	saveWorldFactoryOverride,
	worldFactoryDefaultImage,
	type WorldFactoryPayload,
	type WorldFactoryStoredRecord,
} from "~/composables/useWorldFactory";
import { Station } from "~/composables/types/StationType";
import { World } from "~/composables/types/WorldTypes";

const props = defineProps<{
	selectionKey: string;
}>();

const emit = defineEmits<{
	(event: "selection-updated", selectionKey: string): void;
	(event: "saved"): void;
}>();

const draft = reactive<WorldFactoryPayload>(createWorldDraft());
const loadedSnapshot = ref<WorldFactoryPayload>(createWorldDraft());
const storedDrafts = ref<WorldFactoryStoredRecord[]>([]);
const copyState = ref<"idle" | "copied" | "failed">("idle");
const loadedLocalId = ref("");

const availableStations = computed(() => Station.backendFetchAllStations());
const knownStationMap = computed(() => new Map(availableStations.value.map((station) => [station.uid, station])));
const parsedSelection = computed(() => parseWorldFactorySelectionKey(props.selectionKey));

const payloadPreview = computed<WorldFactoryPayload>(() => ({
	title: draft.title.trim(),
	uid: draft.uid.trim(),
	description: "",
	image: draft.image.trim() || worldFactoryDefaultImage,
	stationUids: normalizeWorldStationUids(draft.stationUids),
}));

const knownExistingUids = computed(() => new Set(World.backendFetchAllWorlds().map((world) => world.uid)));
const uidConflict = computed(() => {
	const nextUid = payloadPreview.value.uid;

	if (!nextUid || parsedSelection.value.kind === "existing") {
		return false;
	}

	if (knownExistingUids.value.has(nextUid)) {
		return true;
	}

	return storedDrafts.value.some((draftRecord) => {
		if (draftRecord.localId === loadedLocalId.value) {
			return false;
		}

		return draftRecord.payload.uid === nextUid;
	});
});

const payloadPreviewText = computed(() => JSON.stringify(payloadPreview.value, null, 2));
const canSave = computed(() => payloadPreview.value.title.length > 0);
const hasChanges = computed(() => JSON.stringify(payloadPreview.value) !== JSON.stringify(loadedSnapshot.value));

const selectedStations = computed(() => payloadPreview.value.stationUids.map((uid, index) => {
	const station = knownStationMap.value.get(uid);

	return {
		uid,
		order: index + 1,
		title: station?.title ?? "Ukjent stasjon",
		description: station?.description ?? "Denne stasjonen finnes ikke i oversikten akkurat na.",
		isKnown: Boolean(station),
	};
}));

const editorTitle = computed(() => {
	if (parsedSelection.value.kind === "existing") {
		return `Rediger verden: ${payloadPreview.value.title || payloadPreview.value.uid || "Uten tittel"}`;
	}

	if (parsedSelection.value.kind === "draft") {
		return payloadPreview.value.title ? `Lokal verden: ${payloadPreview.value.title}` : "Ny lokal verden";
	}

	return "Ny verden";
});

const editorDescription = computed(() => {
	if (parsedSelection.value.kind === "existing") {
		return "Du redigerer en eksisterende verden. Lokal lagring fungerer som en override over mock-dataen til backend finnes.";
	}

	if (parsedSelection.value.kind === "draft") {
		return "Dette er en lokal verden uten backend-kobling. Den kan senere velges når klasserom-oppretting får valg av verden.";
	}

	return "Opprett en ny verden lokalt. Den kan lagres uten stasjoner, så lenge den har en unik id.";
});

const applyDraft = (payload: WorldFactoryPayload) => {
	draft.title = payload.title;
	draft.uid = payload.uid;
	draft.description = "";
	draft.image = payload.image;
	draft.stationUids.splice(0, draft.stationUids.length, ...payload.stationUids);
};

const updateLoadedSnapshot = (payload: WorldFactoryPayload) => {
	loadedSnapshot.value = {
		title: payload.title,
		uid: payload.uid,
		description: "",
		image: payload.image,
		stationUids: [...payload.stationUids],
	};
};

const loadSelection = (selectionKey: string) => {
	const selection = parseWorldFactorySelectionKey(selectionKey);
	const store = readWorldFactoryStore();

	storedDrafts.value = store.drafts;
	loadedLocalId.value = "";

	if (selection.kind === "existing") {
		const backendWorld = World.backendFetchWorldByUid(selection.uid);
		const override = store.overrides[selection.uid] ?? null;
		const nextPayload = override?.payload
			?? createWorldDraft({
				title: backendWorld?.uid ?? selection.uid,
				uid: backendWorld?.uid ?? selection.uid,
				description: backendWorld?.description ?? "",
				image: backendWorld?.image ?? worldFactoryDefaultImage,
				stationUids: backendWorld?.stationUids ?? [],
			});

		applyDraft(nextPayload);
		updateLoadedSnapshot(nextPayload);
	} else if (selection.kind === "draft") {
		const storedDraft = store.drafts.find((draftRecord) => draftRecord.localId === selection.localId) ?? null;
		const nextPayload = storedDraft?.payload ?? createWorldDraft();

		loadedLocalId.value = storedDraft?.localId ?? "";
		applyDraft(nextPayload);
		updateLoadedSnapshot(nextPayload);
	} else {
		const nextPayload = createWorldDraft();

		applyDraft(nextPayload);
		updateLoadedSnapshot(nextPayload);
	}

	copyState.value = "idle";
};

const addStationUid = (uidValue: string) => {
	const nextUid = uidValue.trim();

	if (!nextUid || draft.stationUids.includes(nextUid)) {
		return;
	}

	draft.stationUids.push(nextUid);
};

const removeStationUid = (uidToRemove: string) => {
	const nextStationUids = draft.stationUids.filter((uid) => uid !== uidToRemove);
	draft.stationUids.splice(0, draft.stationUids.length, ...nextStationUids);
};

const moveStation = (index: number, direction: -1 | 1) => {
	const targetIndex = index + direction;

	if (targetIndex < 0 || targetIndex >= draft.stationUids.length) {
		return;
	}

	const nextStationUids = [...draft.stationUids];
	const [movedUid] = nextStationUids.splice(index, 1);

	if (movedUid === undefined) {
		return;
	}

	nextStationUids.splice(targetIndex, 0, movedUid);
	draft.stationUids.splice(0, draft.stationUids.length, ...nextStationUids);
};

const resetDraft = () => {
	applyDraft(loadedSnapshot.value);
	copyState.value = "idle";
};

const finishSave = (selectionKey: string) => {
	emit("selection-updated", selectionKey);
	emit("saved");
};

const savePayload = () => {
	if (!canSave.value) {
		return;
	}

	if (parsedSelection.value.kind === "existing") {
		saveWorldFactoryOverride(parsedSelection.value.uid, payloadPreview.value);

		storedDrafts.value = readWorldFactoryStore().drafts;
		updateLoadedSnapshot(payloadPreview.value);
		finishSave(makeWorldFactoryExistingSelectionKey(parsedSelection.value.uid));
		return;
	}

	const record = saveWorldFactoryDraft(payloadPreview.value, loadedLocalId.value || undefined);

	storedDrafts.value = readWorldFactoryStore().drafts;
	loadedLocalId.value = record.localId;
	updateLoadedSnapshot(payloadPreview.value);
	finishSave(makeWorldFactoryDraftSelectionKey(record.localId));
};

const saveAsNew = () => {
	if (!canSave.value) {
		return;
	}

	const record = saveWorldFactoryDraft(payloadPreview.value);

	storedDrafts.value = readWorldFactoryStore().drafts;
	loadedLocalId.value = record.localId;
	updateLoadedSnapshot(payloadPreview.value);
	finishSave(makeWorldFactoryDraftSelectionKey(record.localId));
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
						<input v-model="draft.title" type="text" placeholder="For eksempel: Skolegarden" >
					</label>

				</div>
			</section>

			<section class="panelSection">
				<div class="sectionHeading">
					<h2>Stasjoner</h2>
					<p>Verdener kan lagres uten stasjoner, eller fylles opp og sorteres allerede nå.</p>
				</div>

				<div class="stationColumns">
					<div class="stationColumn">
						<div class="sectionHeading sectionHeading--compact">
							<h3>Stasjoner i verdenen</h3>
						</div>

						<div v-if="selectedStations.length > 0" class="selectedList">
							<article v-for="(station, index) in selectedStations" :key="station.uid" class="selectedCard">
								<div>
									<p class="cardEyebrow">Plass {{ station.order }}</p>
									<h3>{{ station.title }}</h3>
									<span :class="['statusPill', { 'statusPill--manual': !station.isKnown }]">
										{{ station.isKnown ? 'Fra listen' : 'Ikke tilgjengelig' }}
									</span>
								</div>

								<div class="selectedActions">
									<button type="button" class="ghostButton" :disabled="index === 0" @click="moveStation(index, -1)">
										&uarr;
									</button>
									<button type="button" class="ghostButton" :disabled="index === selectedStations.length - 1" @click="moveStation(index, 1)">
										&darr;
									</button>
									<button type="button" class="deleteButton" @click="removeStationUid(station.uid)">
										Fjern
									</button>
								</div>
							</article>
						</div>

						<p v-else class="emptyState">
							Ingen stasjoner er lagt til i verdenen ennå. Denne verdenen kan fortsatt lagres.
						</p>
					</div>

					<div class="stationColumn">
						<div class="sectionHeading sectionHeading--compact">
							<h3>Tilgjengelige stasjoner</h3>
						</div>

						<div class="knownItems knownItems--columns">
							<article v-for="station in availableStations" :key="station.uid" class="knownCard">
								<div>
									<p class="cardEyebrow">Stasjon</p>
									<h3>{{ station.title }}</h3>
								</div>

								<button
									type="button"
									class="ghostButton"
									:disabled="draft.stationUids.includes(station.uid)"
									@click="addStationUid(station.uid)"
								>
									{{ draft.stationUids.includes(station.uid) ? 'Lagt til' : '+ Bruk' }}
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
					<p>Her ser du det viktigste i verdenen akkurat nå.</p>
				</div>

				<div class="statusStack">
					<article class="summaryCard">
						<p class="cardEyebrow">Stasjoner</p>
						<h3>{{ selectedStations.length }} valgt</h3>
						<p>{{ hasChanges ? 'Det finnes ulagrede endringer.' : 'Editoren er synkron med sist lastede versjon.' }}</p>
					</article>

					<article class="summaryCard">
						<p class="cardEyebrow">Tittel</p>
						<h3>{{ payloadPreview.title || 'Ingen tittel ennå' }}</h3>
						<p>Backend skal senere lage uid for verdenen automatisk.</p>
					</article>
				</div>
			</section>

			<div class="actionRow actionRow--save">
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
.knownCard h3,
.selectedCard h3 {
	margin: 0;
}

.sectionHeading p,
.summaryCard p,
.knownCard p,
.selectedCard p,
.emptyState {
	margin: 0;
	color: var(--muted);
	line-height: 1.5;
}

.statusStack,
.knownItems,
.selectedList {
	display: grid;
	gap: 0.75rem;
}

.stationColumns {
	display: grid;
	grid-template-columns: minmax(0, 1fr) minmax(0, 1fr);
	gap: 1rem;
	align-items: start;
}

.stationColumn {
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

.field input:disabled {
	opacity: 0.7;
	cursor: not-allowed;
}

.summaryCard,
.knownCard,
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

.warningText {
	margin-top: 1rem;
	color: #fca5a5;
	font-weight: 600;
}

.selectedActions,
.actionRow {
	display: flex;
	gap: 0.75rem;
	align-items: end;
	flex-wrap: wrap;
}

.knownItems,
.selectedList {
	margin-top: 1rem;
}

.knownItems--columns,
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
	margin-top: 1.5rem;
	justify-content: space-between;
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

	.stationColumns {
		grid-template-columns: 1fr;
	}

	.sectionHeading--inline,
	.summaryCard,
	.knownCard,
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
