<script setup lang="ts">
import draggable from "vuedraggable";
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
	worldFactoryDefaultImage,
	type WorldFactoryPayload,
	type WorldFactoryStoredRecord,
} from "~/composables/useWorldFactory";

import type { Station } from "~/composables/types/StationTypes";
import { useFactoryImageUpload } from "~/composables/useFactoryImageUpload";
import { useStations } from "~/composables/useStations";
import type { World } from "~/composables/types/WorldTypes";
import { useFactorySessionCache } from "~/composables/useFactorySessionCache";

const props = defineProps<{
	selectionKey: string;
}>();

const { getAllStations } = useStations();
const { uploadImage } = useFactoryImageUpload();

const emit = defineEmits<{
	(event: "selection-updated", selectionKey: string): void;
	(event: "saved", payload: { returnTarget?: string | null }): void;
	(event: "deleted"): void;
}>();

const draft = reactive<WorldFactoryPayload>(createWorldDraft());
const route = useRoute();
const factorySessionCache = useFactorySessionCache();
const loadedSnapshot = ref<WorldFactoryPayload>(createWorldDraft());
const storedDrafts = ref<WorldFactoryStoredRecord[]>([]);
const copyState = ref<"idle" | "copied" | "failed">("idle");
const loadedLocalId = ref("");
const availableStations = ref<Station[]>([]);
const availableWorlds = ref<World[]>([]);
const knownExistingUids = ref<Set<string>>(new Set());
const imageUploadState = ref<"idle" | "uploading">("idle");
const imageUploadError = ref("");

const knownStationMap = computed(() => new Map(availableStations.value.map((station) => [station.uid, station])));
const parsedSelection = computed(() => parseWorldFactorySelectionKey(props.selectionKey));

type WorldStationCard = {
	uid: string;
	order: number;
	title: string;
	image: string;
	backgroundColor: string;
	description: string;
	isKnown: boolean;
	usageTags: string[];
};

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

const stationUsageTags = computed(() => {
	const tagsByStationUid = new Map<string, string[]>();

	for (const world of availableWorlds.value) {
		for (const stationUid of world.stationUids) {
			const tags = tagsByStationUid.get(stationUid) ?? [];
			tags.push(world.title || world.uid);
			tagsByStationUid.set(stationUid, tags);
		}
	}

	return tagsByStationUid;
});

const selectedStations = computed<WorldStationCard[]>(() => payloadPreview.value.stationUids.map((uid, index) => {
	const station = knownStationMap.value.get(uid);

	return {
		uid,
		order: index + 1,
		title: station?.title ?? "Ukjent stasjon",
		image: station?.image ?? worldFactoryDefaultImage,
		backgroundColor: station?.backgroundColor ?? "rgba(255, 255, 255, 0.04)",
		description: station?.description ?? "Denne stasjonen finnes ikke i oversikten akkurat na.",
		isKnown: Boolean(station),
		usageTags: stationUsageTags.value.get(uid) ?? [],
	};
}));

const selectedStationCards = computed<WorldStationCard[]>({
	get: () => selectedStations.value,
	set: (stations) => {
		const nextStationUids = stations
			.map((station) => station.uid)
			.filter((uid, index, uids) => uid && uids.indexOf(uid) === index);

		draft.stationUids.splice(0, draft.stationUids.length, ...nextStationUids);
	},
});

const availableStationCards = computed<WorldStationCard[]>({
	get: () => availableStations.value
		.filter((station) => !draft.stationUids.includes(station.uid))
		.map((station) => ({
			uid: station.uid,
			order: 0,
			title: station.title,
			image: station.image,
			backgroundColor: station.backgroundColor,
			description: station.description,
			isKnown: true,
			usageTags: stationUsageTags.value.get(station.uid) ?? [],
		})),
	set: () => {},
});

const editorTitle = computed(() => {
	if (parsedSelection.value.kind === "existing") {
		return `Rediger stasjonskart: ${payloadPreview.value.title || payloadPreview.value.uid || "Uten tittel"}`;
	}

	if (parsedSelection.value.kind === "draft") {
		return payloadPreview.value.title ? `Utkast til stasjonskart: ${payloadPreview.value.title}` : "Nytt utkast til stasjonskart";
	}

	return "Nytt stasjonskart";
});

const editorDescription = computed(() => {
	if (parsedSelection.value.kind === "existing") {
		return "Du redigerer et eksisterende stasjonskart.";
	}

	if (parsedSelection.value.kind === "draft") {
		return "Dette er et utkast som du kan fortsette å redigere senere.";
	}

	return "Opprett et nytt stasjonskart og legg til innhold når du er klar.";
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
	const cachedPayload = factorySessionCache.getDraft("world", selectionKey);

	storedDrafts.value = store.drafts;
	loadedLocalId.value = loaded.localId;
	updateLoadedSnapshot(loaded.payload);
	applyDraft(cachedPayload ?? loaded.payload);
	copyState.value = "idle";
};

const cloneStationCard = (station: WorldStationCard): WorldStationCard => ({ ...station });

const resetDraft = () => {
	applyDraft(loadedSnapshot.value);
	copyState.value = "idle";
};

const goToStationFactory = () => {
	factorySessionCache.setDraft("world", props.selectionKey, payloadPreview.value);
	factorySessionCache.pushReturnTarget(route.fullPath);
	void navigateTo(`${routePaths.teacherFactoryStation}?returnTo=${encodeURIComponent(route.fullPath)}`);
};

const editStation = (uid: string) => {
	factorySessionCache.setDraft("world", props.selectionKey, payloadPreview.value);
	factorySessionCache.pushReturnTarget(route.fullPath);
	void navigateTo(`${routePaths.teacherFactoryStation}?uid=${uid}&returnTo=${encodeURIComponent(route.fullPath)}`);
};

const finishSave = (selectionKey: string) => {
	emit("selection-updated", selectionKey);
	emit("saved", { returnTarget: null });
};

const savePayload = async () => {
	if (!canSave.value) {
		return;
	}

	const saved = await saveWorldFactoryPayload(props.selectionKey, payloadPreview.value);

	storedDrafts.value = readWorldFactoryStore().drafts;
	loadedLocalId.value = "";
	factorySessionCache.moveDraft("world", props.selectionKey, saved.selectionKey);
	updateLoadedSnapshot(saved.payload);
	const returnTo = typeof route.query.returnTo === "string" ? route.query.returnTo : "";
	if (returnTo) {
		factorySessionCache.popReturnTarget();
	}
	const returnTarget = returnTo ? { fullPath: returnTo } : factorySessionCache.popReturnTarget();

	if (returnTarget) {
		emit("selection-updated", saved.selectionKey);
		emit("saved", { returnTarget: returnTarget.fullPath });
		return;
	}

	finishSave(saved.selectionKey);
};

const deleteSelection = async () => {
	if (!window.confirm("Er du sikker på at du vil slette dette elementet?")) {
		return;
	}

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

const handleImageSelected = async (event: Event) => {
	const input = event.target as HTMLInputElement | null;
	const file = input?.files?.[0];

	if (!file) {
		return;
	}

	imageUploadState.value = "uploading";
	imageUploadError.value = "";

	try {
		const uploaded = await uploadImage(file, "uploads/factory/worlds");
		draft.image = uploaded.url;
	} catch (error) {
		imageUploadError.value = error instanceof Error ? error.message : "Bildet kunne ikke lastes opp.";
	} finally {
		imageUploadState.value = "idle";
		if (input) {
			input.value = "";
		}
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
		availableStations.value = await getAllStations();
	} catch {
		availableStations.value = [];
	}

	try {
		const worlds = await fetchAllWorldFactoryWorlds();
		availableWorlds.value = worlds;
		knownExistingUids.value = new Set(worlds.map((world) => world.uid));
	} catch {
		availableWorlds.value = [];
		knownExistingUids.value = new Set();
	}

	storedDrafts.value = readWorldFactoryStore().drafts;
});

watch(payloadPreviewText, () => {
	if (copyState.value !== "idle") {
		copyState.value = "idle";
	}

	factorySessionCache.setDraft("world", props.selectionKey, payloadPreview.value);
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
						<span>Bilde</span>
						<input type="file" accept="image/*" :disabled="imageUploadState === 'uploading'" @change="handleImageSelected" >
						<small class="fieldHint">
							{{ imageUploadState === "uploading"
								? "Laster opp..."
								: draft.image
									? "Bilde lastet opp. Forhandsvisning vises under."
									: "Velg et bilde til stasjonskartet." }}
						</small>
						<p v-if="imageUploadError" class="fieldError">{{ imageUploadError }}</p>
					</label>

					<label class="field field--full">
						<span>Beskrivelse</span>
						<textarea v-model="draft.description" rows="3" placeholder="Kort beskrivelse av stasjonskartet." />
					</label>

					<div class="imagePreviewCard">
						<img :src="payloadPreview.image" alt="Forhandsvisning av stasjonskartbilde" class="imagePreview" >
					</div>

				</div>
			</section>

			<section class="panelSection">
				<div class="sectionHeading">
					<h2>Stasjoner</h2>
					<p>Dra stasjoner inn i stasjonskartet. Rekkefølgen her styrer løypa elevene møter.</p>
				</div>

				<div class="stationColumns">
					<div class="stationColumn">
						<div class="sectionHeading sectionHeading--compact">
							<h3>Stasjoner i stasjonskartet</h3>
						</div>

						<draggable
							v-model="selectedStationCards"
							class="selectedList"
							item-key="uid"
							:group="'world-stations'"
						>
							<template #item="{ element: station }">
								<article class="selectedCard">
									<span class="orderBadge" :aria-label="`Plass ${station.order}`">{{ station.order }}</span>
									<span class="dragHandle" aria-hidden="true">Grip</span>
									<img :src="station.image" :alt="station.title" class="stationCardImage" :style="{ backgroundColor: station.backgroundColor }" >
									<div class="selectedCardContent">
										<p class="cardEyebrow">Plass {{ station.order }}</p>
										<h3>
											{{ station.title }}
											<span v-if="knownStationMap.get(station.uid)?.isBoss" class="bossPill" aria-hidden="true">Boss</span>
										</h3>
										<span :class="['statusPill', { 'statusPill--manual': !station.isKnown }]">
											{{ station.isKnown ? "Tilgjengelig" : 'Ikke tilgjengelig' }}
										</span>
										<div v-if="station.usageTags.length > 0" class="usageTags" aria-label="Brukt i stasjonskart">
											<span v-for="tag in station.usageTags" :key="tag" class="usageTag">{{ tag }}</span>
										</div>
									</div>
									<div class="selectedCardActions">
										<button type="button" class="iconButton" :disabled="!station.isKnown" @click="editStation(station.uid)">
										<img :src="'/images/placeholders/action-edit.svg'" alt="Rediger stasjon" class="selectedCardActionIcon" >
										</button>
									</div>
								</article>
							</template>

							<template #footer>
								<p v-if="selectedStations.length === 0" class="emptyState dropHint">
									Dra stasjoner hit for å legge dem til i stasjonskartet.
								</p>
							</template>
						</draggable>
					</div>

					<div class="stationColumn">
						<div class="sectionHeading sectionHeading--compact sectionHeading--inline">
							<h3>Tilgjengelige stasjoner</h3>
							<button type="button" class="ghostButton" @click="goToStationFactory">
								+ Lag Stasjon
							</button>
						</div>

						<draggable
							v-model="availableStationCards"
							class="knownStations knownStations--columns"
							item-key="uid"
							:group="{ name: 'world-stations', pull: 'clone', put: true }"
							:sort="false"
							:clone="cloneStationCard"
						>
							<template #item="{ element: station }">
								<article class="knownStationCard">
									<span class="dragHandle" aria-hidden="true">Grip</span>
									<img :src="station.image" :alt="station.title" class="stationCardImage" :style="{ backgroundColor: station.backgroundColor }" >
									<div class="selectedCardContent">
										<h3>
											{{ station.title }}
											<span v-if="knownStationMap.get(station.uid)?.isBoss" class="bossPill" aria-hidden="true">Skurk</span>
										</h3>
										<div v-if="station.usageTags.length > 0" class="usageTags" aria-label="Brukt i stasjonskart">
					  <p class="small">Brukt i:</p>
											<span v-for="tag in station.usageTags" :key="tag" class="usageTag">{{ tag }}</span>
										</div>
									</div>
									<div class="selectedCardActions">
										<button type="button" class="iconButton" :disabled="!station.isKnown" @click="editStation(station.uid)">
										<img :src="'/images/placeholders/action-edit.svg'" alt="Rediger stasjon" class="selectedCardActionIcon" >
										</button>
									</div>
								</article>
							</template>

							<template #footer>
								<p v-if="availableStationCards.length === 0" class="emptyState dropHint">
									Alle tilgjengelige stasjoner er lagt til. Dra hit for å fjerne fra stasjonskartet.
								</p>
							</template>
						</draggable>
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
					v-if="parsedSelection.kind === 'existing'"
					type="button"
					class="deleteButton"
					@click="deleteSelection"
				>
					Slett
				</button>
				<button
					type="submit"
					class="primaryButton"
					:disabled="!canSave || (parsedSelection.kind !== 'new' && !hasChanges)"
				>
					Lagre
				</button>
			</div>

			<section class="panelSection debugSection">
				<details>
					<summary class="debugSummary">Dataoversikt</summary>
					<div class="debugContent">
						<div class="sectionHeading sectionHeading--inline">
							<div>
								<h2>JSON-data</h2>
								<p>Her kan du se dataene som er knyttet til stasjonskartet.</p>
							</div>

							<button type="button" class="ghostButton" @click="copyPayload">
								{{ copyState === 'copied' ? 'Kopiert' : copyState === 'failed' ? 'Kopiering feilet' : 'Kopier data' }}
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
  --factory-label-font: var(--font-family-mono);
  --factory-label-size: var(--font-size-12);
  --factory-label-tracking: var(--font-tracking-wider);
  --factory-label-color: var(--color-text-primary);
  --factory-field-bg: var(--color-bg-canvas);
  --factory-field-border: var(--color-border-default);
  --factory-field-focus: var(--color-brand-primary);
  --factory-drop-accept: var(--palette-overlay-green-soft);
  --factory-drop-reject: var(--palette-overlay-red-soft);
  --factory-delete-border: var(--color-semantic-error);
  --factory-delete-bg: var(--palette-overlay-red-soft);
  --factory-delete-text: var(--color-semantic-error);
  --factory-tag-blue: var(--palette-ice-200);
  --factory-tag-gold: var(--palette-gold-400);
  --factory-tag-pink: var(--palette-red-200);
  --factory-tag-green: var(--palette-green-100);
  --factory-payload-bg: var(--color-bg-canvas);
  --factory-payload-text: var(--color-text-primary);
  --factory-payload-font: var(--font-family-mono);
  --factory-station-bg: var(--palette-ice-200);

  display: grid;
  grid-template-columns: 1fr;
  gap: var(--space-5);
  align-items: start;
}

.bossPill {
	display: inline-block;
	margin-left: 0.6rem;
	padding: 0.15rem 0.5rem;
	font-size: 0.72rem;
	font-weight: 700;
	color: var(--palette-white);
	background: #b00020;
	border-radius: 999px;
	line-height: 1;
}

.editorPanel,
.previewPanel {
  padding: var(--space-5);
  border: var(--border-width-hairline) solid var(--color-border-default);
  border-radius: var(--radius-3);
  background: var(--color-bg-surface);
  box-shadow: var(--shadow-folder);
}

.previewPanel {
  border-style: dashed;
}

.panelSection + .panelSection {
  margin-top: var(--space-5);
}

.sectionHeading {
  display: grid;
  gap: var(--space-1);
  margin-bottom: var(--space-4);
}

.sectionHeading--compact {
  margin-bottom: var(--space-3);
}

.sectionHeading--inline {
  grid-template-columns: 1fr auto;
  align-items: start;
  gap: var(--space-3);
}

.sectionHeading h2,
.summaryCard h3,
.knownStationCard h3,
.selectedCard h3 {
  margin: 0;
}

.sectionHeading p,
.summaryCard p,
.knownStationCard p,
.selectedCard p,
.emptyState {
  margin: 0;
  color: var(--color-text-secondary);
  line-height: var(--font-leading-base);
}

.statusStack,
.knownStations,
.selectedList {
  display: grid;
  gap: var(--space-3);
}

.stationColumns {
  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(0, 1fr);
  gap: var(--space-4);
  align-items: stretch;
}

.stationColumn {
  display: grid;
  grid-template-rows: auto 1fr;
  gap: var(--space-3);
}

.stationCardImage {
  display: block;
  /* deliberate: fixed thumbnail size */
  width: 4rem;
  height: 4rem;
  border: var(--border-width-hairline) solid var(--color-border-default);
  border-radius: var(--radius-1);
  object-fit: cover;
}

.fieldGrid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: var(--space-3);
}

.fieldHint {
	color: var(--muted);
	font-size: 0.9rem;
}

.fieldError {
	margin: 0;
	color: #fca5a5;
	font-size: 0.9rem;
}

.field {
  display: grid;
  gap: var(--space-1);
}

.field--full {
  grid-column: 1 / -1;
}

.field--grow {
  flex: 1;
}

.field span {
  color: var(--factory-label-color);
  font-family: var(--factory-label-font), sans-serif;
  font-size: var(--factory-label-size);
  font-weight: var(--font-weight-bold);
  letter-spacing: var(--factory-label-tracking);
  text-transform: uppercase;
}

.field input,
.field textarea {
  width: 100%;
  padding: var(--space-3) var(--space-4);
  border: var(--border-width-hairline) solid var(--factory-field-border);
  border-radius: var(--radius-1);
  background: var(--factory-field-bg);
  color: var(--color-text-primary);
  font: inherit;
}

.field textarea {
  resize: vertical;
}

.field input:focus,
.field textarea:focus {
  outline: none;
  border-color: var(--factory-field-focus);
}

.field input:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.summaryCard,
.knownStationCard,
.selectedCard {
  display: flex;
  justify-content: flex-start;
  align-items: flex-start;
  gap: var(--space-3);
  padding: var(--space-4);
  border: var(--border-width-hairline) solid var(--color-border-default);
  border-radius: var(--radius-2);
  background: var(--color-bg-surface);
}

.summaryCard {
  justify-content: space-between;
}

.imagePreviewCard {
  margin-top: var(--space-4);
  border: var(--border-width-hairline) solid var(--color-border-default);
  border-radius: var(--radius-2);
  overflow: hidden;
  background: var(--color-bg-surface);
}

.imagePreview {
  display: block;
  width: 100%;
  aspect-ratio: 16 / 8;
  object-fit: cover;
}

.knownStations,
.selectedList {
  margin-top: 0;
  height: 100%;
  min-height: 12rem; /* deliberate: keep both drop zones easy to target */
  align-content: start;
}

.selectedCardContent {
  flex: 1;
}

.selectedCardActions {
  display: flex;
  gap: var(--space-2);
  margin-left: auto;
}

.iconButton {
  padding: 0;
  border: 0;
  background: transparent;
  cursor: pointer;
}

.iconButton:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.selectedCardActionIcon {
  display: block;
  width: var(--space-8);
  height: var(--space-8);
  border-radius: var(--radius-1);
}

.orderBadge {
  display: inline-grid;
  place-items: center;
  width: var(--space-7);
  height: var(--space-7);
  border: var(--border-width-hairline) solid var(--color-brand-primary);
  border-radius: var(--radius-pill);
  background: var(--factory-drop-accept);
  color: var(--color-brand-primary);
  font-weight: var(--font-weight-bold);
}

.dropHint {
  padding: var(--space-4);
  border: var(--border-width-hairline) dashed var(--color-border-default);
  border-radius: var(--radius-2);
  background: var(--factory-drop-accept);
}

.dragHandle {
  display: inline-grid;
  place-items: center;
  min-width: var(--size-control-lg);
  padding: var(--space-1) var(--space-2);
  border: var(--border-width-hairline) dashed var(--color-border-default);
  border-radius: var(--radius-pill);
  color: var(--color-brand-primary);
  font-size: var(--font-size-12);
  font-weight: var(--font-weight-bold);
  letter-spacing: var(--font-tracking-wider);
  text-transform: uppercase;
  cursor: grab;
  user-select: none;
}

.dragHandle:active {
  cursor: grabbing;
}

.actionRow {
  display: flex;
  gap: var(--space-3);
  align-items: end;
  flex-wrap: wrap;
}

.statusPill {
  display: inline-flex;
  margin-top: var(--space-3);
  padding: var(--space-1) var(--space-3);
  border-radius: var(--radius-1);
  background: var(--factory-drop-accept);
  color: var(--color-brand-primary);
  font-size: var(--font-size-14);
  font-weight: var(--font-weight-bold);
}

.statusPill--manual {
  background: var(--color-bg-surface-hover);
  color: var(--color-text-primary);
}

.usageTags {
  display: flex;
  flex-wrap: wrap;
  gap: var(--space-1) var(--space-3);
  margin-top: var(--space-2);
}

.usageTag {
  display: inline-flex;
  align-items: center;
  gap: var(--space-1);
  max-width: 100%;
  color: var(--color-text-secondary);
  font-size: var(--font-size-12);
  font-weight: var(--font-weight-bold);
  line-height: var(--font-leading-12);
}

.usageTag::before {
  content: "";
  width: var(--space-1);
  height: var(--space-1);
  border-radius: var(--radius-pill);
  background: var(--color-brand-primary);
  box-shadow: 0 0 0 var(--border-width-thick) var(--factory-tag-green); /* deliberate: tokenized solid shadow because opacity cannot be expressed without raw rgba */
  flex: 0 0 auto;
}

.usageTag:nth-child(4n + 2)::before {
  background: var(--factory-tag-blue);
  box-shadow: 0 0 0 var(--border-width-thick) var(--factory-tag-blue); /* deliberate: tokenized solid shadow because opacity cannot be expressed without raw rgba */
}

.usageTag:nth-child(4n + 3)::before {
  background: var(--factory-tag-gold);
  box-shadow: 0 0 0 var(--border-width-thick) var(--factory-tag-gold); /* deliberate: tokenized solid shadow because opacity cannot be expressed without raw rgba */
}

.usageTag:nth-child(4n + 4)::before {
  background: var(--factory-tag-pink);
  box-shadow: 0 0 0 var(--border-width-thick) var(--factory-tag-pink); /* deliberate: tokenized solid shadow because opacity cannot be expressed without raw rgba */
}

.ghostButton,
.deleteButton,
.primaryButton {
  border: var(--border-width-hairline) solid var(--color-border-default);
  border-radius: var(--radius-1);
  padding: var(--space-3) var(--space-4);
  font-weight: var(--font-weight-bold);
  transition: transform var(--transition-base), border-color var(--transition-base), background var(--transition-base);
}

.ghostButton,
.deleteButton {
  background: transparent;
  color: var(--color-text-primary);
}

.ghostButton {
  background: var(--color-bg-surface);
}

.primaryButton {
  border-color: var(--color-brand-primary);
  background: var(--color-brand-primary);
  color: var(--color-text-onDark);
}

.deleteButton {
  border-color: var(--factory-delete-border);
  background: var(--factory-delete-bg);
  color: var(--factory-delete-text);
}

.ghostButton:hover,
.deleteButton:hover,
.primaryButton:hover {
  transform: translateY(-1px);
}

.ghostButton:hover {
  background: var(--color-bg-surface-hover);
}

.deleteButton:hover {
  background: var(--factory-delete-bg);
  border-color: var(--factory-delete-border);
  filter: brightness(0.85);
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
  margin-top: var(--space-5);
}

.actionRow--reset {
  justify-content: flex-start;
}

.actionRow--save {
  margin-top: var(--space-5);
  justify-content: flex-end;
}

.payloadPreview {
  overflow-x: auto;
  padding: var(--space-4);
  border: var(--border-width-hairline) dashed var(--color-border-default);
  border-radius: var(--radius-2);
  background: var(--factory-payload-bg);
  color: var(--factory-payload-text);
  font-family: var(--factory-payload-font), monospace;
  font-size: var(--font-size-14);
  line-height: var(--font-leading-base);
}

.debugSection {
  opacity: 0.9;
}

.debugSummary {
  cursor: pointer;
  font-weight: var(--font-weight-bold);
  color: var(--color-text-secondary);
  list-style: none;
}

.debugSummary::-webkit-details-marker {
  display: none;
}

.debugContent {
  margin-top: var(--space-4);
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
  .knownStationCard,
  .selectedCard,
  .selectedCardActions,
  .actionRow {
    display: grid;
  }

  .actionRow {
    justify-content: stretch;
  }
}
</style>
