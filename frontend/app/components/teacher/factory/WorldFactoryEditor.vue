<script setup lang="ts">
import { routePaths } from "~/composables/utils/routePaths";
import {
	createWorldDraft,
	deleteWorldFactorySelection,
	fetchAllWorldFactoryWorlds,
	loadWorldFactoryPayload,
	normalizeWorldStationUids,
	parseWorldFactorySelectionKey,
	readWorldFactoryStore,
	saveWorldFactoryPayload,
	saveWorldFactoryPayloadAsNew,
	worldFactoryDefaultImage,
	type WorldFactoryPayload,
	type WorldFactoryStoredRecord,
} from "~/composables/useWorldFactory";
import { Station } from "~/composables/types/StationType";

const props = defineProps<{
	selectionKey: string;
}>();

const emit = defineEmits<{
	(event: "selection-updated", selectionKey: string): void;
	(event: "saved"): void;
	(event: "deleted"): void;
}>();

const draft = reactive<WorldFactoryPayload>(createWorldDraft());
const loadedSnapshot = ref<WorldFactoryPayload>(createWorldDraft());
const storedDrafts = ref<WorldFactoryStoredRecord[]>([]);
const copyState = ref<"idle" | "copied" | "failed">("idle");
const loadedLocalId = ref("");
const showStationPicker = ref(false);
const availableStations = ref<Station[]>([]);
const knownExistingUids = ref<Set<string>>(new Set());

const knownStationMap = computed(() => new Map(availableStations.value.map((station) => [station.uid, station])));
const unselectedStations = computed(() => availableStations.value.filter((station) => !draft.stationUids.includes(station.uid)));
const parsedSelection = computed(() => parseWorldFactorySelectionKey(props.selectionKey));

const payloadPreview = computed<WorldFactoryPayload>(() => ({
	title: draft.title.trim(),
	uid: draft.uid.trim(),
	description: draft.description.trim(),
	image: draft.image.trim() || worldFactoryDefaultImage,
	stationUids: normalizeWorldStationUids(draft.stationUids),
}));
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
const canSave = computed(() => payloadPreview.value.title.length > 0
	&& payloadPreview.value.description.length > 0
	&& payloadPreview.value.image.length > 0);
const hasChanges = computed(() => JSON.stringify(payloadPreview.value) !== JSON.stringify(loadedSnapshot.value));

const selectedStations = computed(() => payloadPreview.value.stationUids.map((uid, index) => {
	const station = knownStationMap.value.get(uid);

	return {
		uid,
		order: index + 1,
		title: station?.title ?? "Ukjent stasjon",
		image: station?.image ?? worldFactoryDefaultImage,
		backgroundColor: station?.backgroundColor ?? "rgba(255, 255, 255, 0.04)",
		description: station?.description ?? "Denne stasjonen finnes ikke i oversikten akkurat na.",
		isKnown: Boolean(station),
	};
}));

const editorTitle = computed(() => {
	if (parsedSelection.value.kind === "existing") {
		return `Rediger stasjonskart: ${payloadPreview.value.title || payloadPreview.value.uid || "Uten tittel"}`;
	}

	if (parsedSelection.value.kind === "draft") {
		return payloadPreview.value.title ? `Lokalt stasjonskart: ${payloadPreview.value.title}` : "Nytt lokalt stasjonskart";
	}

	return "Nytt stasjonskart";
});

const editorDescription = computed(() => {
	if (parsedSelection.value.kind === "existing") {
		return "Du redigerer et eksisterende stasjonskart. Lokal lagring fungerer som en override over mock-dataen til backend finnes.";
	}

	if (parsedSelection.value.kind === "draft") {
		return "Dette er et lokalt stasjonskart uten backend-kobling. Det kan senere velges når klasserom-oppretting får valg av stasjonskart.";
	}

	return "Opprett et nytt stasjonskart lokalt. Det kan lagres uten stasjoner, så lenge det har en unik id.";
});

const applyDraft = (payload: WorldFactoryPayload) => {
	draft.title = payload.title;
	draft.uid = payload.uid;
	draft.description = payload.description;
	draft.image = payload.image;
	draft.stationUids.splice(0, draft.stationUids.length, ...payload.stationUids);
};

const updateLoadedSnapshot = (payload: WorldFactoryPayload) => {
	loadedSnapshot.value = {
		title: payload.title,
		uid: payload.uid,
		description: payload.description,
		image: payload.image,
		stationUids: [...payload.stationUids],
	};
};

const loadSelection = async (selectionKey: string) => {
	const loaded = await loadWorldFactoryPayload(selectionKey);
	const store = readWorldFactoryStore();

	storedDrafts.value = store.drafts;
	loadedLocalId.value = loaded.localId;
	applyDraft(loaded.payload);
	updateLoadedSnapshot(loaded.payload);
	copyState.value = "idle";
};

const addStationUid = (uidValue: string) => {
	const nextUid = uidValue.trim();

	if (!nextUid || draft.stationUids.includes(nextUid)) {
		return;
	}

	draft.stationUids.push(nextUid);
	showStationPicker.value = false;
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
	showStationPicker.value = false;
};

const goToStationFactory = () => {
	void navigateTo(routePaths.teacherFactoryStation);
};

const editStation = (uid: string) => {
	void navigateTo(`${routePaths.teacherFactoryStation}?uid=${uid}`);
};

const openStationPicker = () => {
	showStationPicker.value = true;
};

const closeStationPicker = () => {
	showStationPicker.value = false;
};

const finishSave = (selectionKey: string) => {
	emit("selection-updated", selectionKey);
	emit("saved");
};

const savePayload = async () => {
	if (!canSave.value) {
		return;
	}

	const saved = await saveWorldFactoryPayload(props.selectionKey, payloadPreview.value);

	storedDrafts.value = readWorldFactoryStore().drafts;
	loadedLocalId.value = "";
	updateLoadedSnapshot(saved.payload);
	finishSave(saved.selectionKey);
};

const saveAsNew = async () => {
	if (!canSave.value) {
		return;
	}

	const saved = await saveWorldFactoryPayloadAsNew(payloadPreview.value);

	storedDrafts.value = readWorldFactoryStore().drafts;
	loadedLocalId.value = "";
	updateLoadedSnapshot(saved.payload);
	finishSave(saved.selectionKey);
};

const deleteSelection = async () => {
	await deleteWorldFactorySelection(props.selectionKey);
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
	try {
		availableStations.value = await Station.backendFetchAllStations();
	} catch {
		availableStations.value = [];
	}

	try {
		knownExistingUids.value = new Set((await fetchAllWorldFactoryWorlds()).map((world) => world.uid));
	} catch {
		knownExistingUids.value = new Set();
	}

	storedDrafts.value = readWorldFactoryStore().drafts;
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
						<input v-model="draft.title" type="text" placeholder="For eksempel: Skolegarden" >
					</label>

					<label class="field">
						<span>Bilde-URL</span>
						<input v-model="draft.image" type="url" placeholder="https://..." >
					</label>

					<label class="field field--full">
						<span>Beskrivelse</span>
						<textarea v-model="draft.description" rows="3" placeholder="Kort beskrivelse av stasjonskartet." />
					</label>

				</div>
			</section>

			<section class="panelSection">
				<div class="sectionHeading">
					<h2>Stasjoner</h2>
					<p>Bygg stasjonskartet som en enkel loype med stasjoner i rekkefolge.</p>
				</div>

				<div class="worldPath">
					<article
						v-for="(station, index) in selectedStations"
						:key="station.uid"
						:class="['stationNode', `stationNode--${index % 2 === 0 ? 'left' : 'right'}`]"
					>
						<div class="stationNode__imageShell" :style="{ backgroundColor: station.backgroundColor }">
							<img :src="station.image" :alt="station.title" class="stationNode__image" >
						</div>
						<h3>{{ station.title }}</h3>
						<p class="cardEyebrow">Stasjon {{ station.order }}</p>
						<div class="stationNode__actions">
							<button type="button" class="ghostButton stationNode__moveLeft" :disabled="index === 0" @click="moveStation(index, -1)">
								<img src="https://placehold.co/80x80/1f2937/e5f3ef?text=%3C" alt="Flytt til venstre" class="stationNode__actionIcon" >
							</button>
							<button type="button" class="ghostButton" :disabled="!station.isKnown" @click="editStation(station.uid)">
								<img src="https://placehold.co/80x80/0f766e/ecfeff?text=E" alt="Rediger stasjon" class="stationNode__actionIcon" >
							</button>
							<button type="button" class="deleteButton" @click="removeStationUid(station.uid)">
								<img src="https://placehold.co/80x80/991b1b/fee2e2?text=X" alt="Slett stasjon" class="stationNode__actionIcon" >
							</button>
							<button type="button" class="ghostButton stationNode__moveRight" :disabled="index === selectedStations.length - 1" @click="moveStation(index, 1)">
								<img src="https://placehold.co/80x80/1f2937/e5f3ef?text=%3E" alt="Flytt til hoyre" class="stationNode__actionIcon" >
							</button>
						</div>
					</article>

					<button
						type="button"
						:class="['addStationNode', `addStationNode--${selectedStations.length % 2 === 0 ? 'left' : 'right'}`]"
						@click="openStationPicker"
					>
						<span class="addStationNode__plus">+</span>
						<span>Legg til stasjon</span>
					</button>
				</div>

				<p v-if="selectedStations.length === 0" class="emptyState emptyState--path">
					Ingen stasjoner er lagt til ennå. Start med pluss-noden.
				</p>

				<div v-if="showStationPicker" class="stationPickerOverlay" @click.self="closeStationPicker">
					<section class="stationPicker" role="dialog" aria-modal="true" aria-labelledby="station-picker-title">
						<div class="sectionHeading sectionHeading--inline sectionHeading--compact">
							<div>
								<h3 id="station-picker-title">Velg stasjon</h3>
								<p>Legg til en eksisterende stasjon i loypa, eller lag en ny.</p>
							</div>
							<button type="button" class="ghostButton" @click="closeStationPicker">
								Lukk
							</button>
						</div>

						<div class="stationPicker__actions">
							<button type="button" class="ghostButton" @click="goToStationFactory">
								+ Lag Stasjon
							</button>
						</div>

						<div v-if="unselectedStations.length > 0" class="stationPickerList">
							<button
								v-for="station in unselectedStations"
								:key="station.uid"
								type="button"
								class="stationPickerCard"
								@click="addStationUid(station.uid)"
							>
								<img :src="station.image" :alt="station.title" class="stationPickerCard__image" >
								<div>
									<p class="cardEyebrow">{{ station.uid }}</p>
									<h3>{{ station.title }}</h3>
								</div>
							</button>
						</div>

						<p v-else class="emptyState">
							Alle tilgjengelige stasjoner er allerede lagt til i stasjonskartet.
						</p>
					</section>
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
					<p>Her ser du det viktigste i stasjonskartet akkurat nå.</p>
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
						<p>{{ payloadPreview.description || 'Legg til en beskrivelse for stasjonskartet.' }}</p>
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
		var(--color-bg-surface);
	box-shadow: var(--shadow-1);
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
.stationNode h3,
.stationPickerCard h3 {
	margin: 0;
}

.sectionHeading p,
.summaryCard p,
.stationPickerCard p,
.emptyState {
	margin: 0;
	color: var(--muted);
	line-height: 1.5;
}

.statusStack,
.stationPickerList {
	display: grid;
	gap: 0.75rem;
}


.worldPath {
	display: grid;
	gap: 1rem;
	justify-items: start;
}

.stationNode,
.addStationNode {
	width: min(100%, 18rem);
	display: grid;
	gap: 0.75rem;
	padding: 1rem;
	border: 1px solid var(--border);
	border-radius: 1.2rem;
	background: rgba(255, 255, 255, 0.03);
}

.stationNode--right,
.addStationNode--right {
	justify-self: end;
	margin-right: max(0rem, 12%);
}

.stationNode--left,
.addStationNode--left {
	margin-left: max(0rem, 12%);
}

.stationNode__imageShell {
	overflow: hidden;
	border-radius: 1rem;
	aspect-ratio: 1 / 1;
	border: 1px solid var(--border);
}

.stationNode__image {
	display: block;
	width: 100%;
	height: 100%;
	object-fit: cover;
}

.stationNode__actions {
	display: flex;
	gap: 0.75rem;
	align-items: center;
	flex-wrap: nowrap;
}

.stationNode__actionIcon {
	display: block;
	width: 3rem;
	height: 3rem;
	border-radius: 0.45rem;
}

.stationNode__moveLeft {
	margin-right: auto;
}

.stationNode__moveRight {
	margin-left: auto;
}

.addStationNode {
	place-items: center;
	min-height: 13rem;
	background: rgba(101, 240, 175, 0.06);
	color: var(--text);
	text-align: center;
}

.addStationNode__plus {
	display: grid;
	place-items: center;
	width: 4.5rem;
	height: 4.5rem;
	border-radius: 999px;
	border: 1px dashed var(--primary);
	color: var(--primary);
	font-size: 2rem;
	font-weight: 700;
}

.emptyState--path {
	margin-top: 1rem;
}

.stationPickerOverlay {
	position: fixed;
	inset: 0;
	display: grid;
	place-items: center;
	padding: 1.5rem;
	background: rgba(0, 0, 0, 0.6);
	z-index: 1000;
}

.stationPicker {
	width: min(100%, 52rem);
	max-height: min(80vh, 46rem);
	overflow: auto;
	display: grid;
	gap: 1rem;
	padding: 1.5rem;
	border: 1px solid var(--border);
	border-radius: 1.2rem;
	background: var(--color-bg-surface);
	box-shadow: var(--shadow-1);
}

.stationPicker__actions {
	display: flex;
	justify-content: flex-start;
}

.stationPickerList {
	grid-template-columns: repeat(auto-fit, minmax(16rem, 1fr));
}

.stationPickerCard {
	display: grid;
	gap: 0.75rem;
	padding: 0.9rem;
	border: 1px solid var(--border);
	border-radius: 1rem;
	background: rgba(255, 255, 255, 0.03);
	color: var(--text);
	text-align: left;
}

.stationPickerCard__image {
	display: block;
	width: 100%;
	aspect-ratio: 16 / 10;
	border-radius: 0.9rem;
	object-fit: cover;
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

.summaryCard {
	display: flex;
	justify-content: space-between;
	align-items: flex-start;
	gap: 0.75rem;
	padding: 1rem;
	border: 1px solid var(--border);
	border-radius: 1rem;
	background: rgba(255, 255, 255, 0.02);
}

.actionRow {
	display: flex;
	gap: 0.75rem;
	align-items: end;
	flex-wrap: wrap;
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
	transition: transform var(--transition-base), border-color var(--transition-base), background var(--transition-base);
}

.ghostButton,
.deleteButton {
	background: transparent;
	color: var(--text);
}

.ghostButton {
	background: rgba(255, 255, 255, 0.03);
}

.primaryButton {
	border-color: var(--primary);
	background: var(--primary);
	color: var(--color-text-onDark);
}

.deleteButton {
	border-color: rgba(248, 113, 113, 0.55);
	background: rgba(127, 29, 29, 0.18);
	color: #fecaca;
}

.ghostButton:hover,
.deleteButton:hover,
.primaryButton:hover {
	transform: translateY(-1px);
}

.ghostButton:hover {
	background: rgba(255, 255, 255, 0.08);
}

.deleteButton:hover {
	background: rgba(153, 27, 27, 0.32);
	border-color: rgba(248, 113, 113, 0.75);
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

	.stationNode,
	.addStationNode {
		width: 100%;
	}

	.stationNode--right,
	.addStationNode--right,
	.stationNode--left,
	.addStationNode--left {
		justify-self: stretch;
		margin-left: 0;
		margin-right: 0;
	}

	.sectionHeading--inline,
	.summaryCard,
	.stationPickerCard,
	.actionRow {
		display: grid;
	}

	.actionRow {
		justify-content: stretch;
	}
}
</style>
