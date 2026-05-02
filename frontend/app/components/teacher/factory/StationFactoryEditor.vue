<script setup lang="ts">
import draggable from "vuedraggable";
import { routePaths } from "~/composables/utils/routePaths";
import {
	createStationDraft,
	deleteStationFactorySelection,
	fetchAllStationFactoryStations,
	loadStationFactoryPayload,
	normalizeStationExerciseUids,
	parseStationFactorySelectionKey,
	saveStationFactoryPayload,
	stationFactoryDefaultImage,
	type StationFactoryPayload,
} from "~/composables/useStationFactory";
import { Exercise } from "~/composables/types/ExerciseType";
import { useFactorySessionCache } from "~/composables/useFactorySessionCache";

const props = defineProps<{
	selectionKey: string;
}>();

const emit = defineEmits<{
	(event: "selection-updated", selectionKey: string): void;
	(event: "saved"): void;
	(event: "deleted"): void;
}>();

const draft = reactive<StationFactoryPayload>(createStationDraft());
const route = useRoute();
const factorySessionCache = useFactorySessionCache();
const loadedSnapshot = ref<StationFactoryPayload>(createStationDraft());
const copyState = ref<"idle" | "copied" | "failed">("idle");
const loadedLocalId = ref("");
const availableExercises = ref<Exercise[]>([]);
const availableStations = ref<Station[]>([]);

const knownExerciseMap = computed(() => new Map(availableExercises.value.map((exercise) => [exercise.uid, exercise])));
const parsedSelection = computed(() => parseStationFactorySelectionKey(props.selectionKey));

type StationExerciseCard = {
	uid: string;
	order: number;
	title: string;
	entryName: string;
	description: string;
	isKnown: boolean;
	usageTags: string[];
};

const payloadPreview = computed<StationFactoryPayload>(() => ({
	title: draft.title.trim(),
	description: draft.description.trim(),
	hint: draft.hint.trim(),
	image: draft.image.trim() || stationFactoryDefaultImage,
	exerciseUids: normalizeStationExerciseUids(draft.exerciseUids),
  isBoss : draft.isBoss,
}));

const payloadPreviewText = computed(() => JSON.stringify(payloadPreview.value, null, 2));
const canSave = computed(() => {
	return payloadPreview.value.title.length > 0
		&& payloadPreview.value.description.length > 0
		&& payloadPreview.value.hint.length > 0
		&& payloadPreview.value.image.length > 0
		&& payloadPreview.value.exerciseUids.length > 0;
});

const hasChanges = computed(() => JSON.stringify(payloadPreview.value) !== JSON.stringify(loadedSnapshot.value));

const exerciseUsageTags = computed(() => {
	const tagsByExerciseUid = new Map<string, string[]>();

	for (const station of availableStations.value) {
		for (const exerciseUid of station.exerciseUids) {
			const tags = tagsByExerciseUid.get(exerciseUid) ?? [];
			tags.push(station.title || station.uid);
			tagsByExerciseUid.set(exerciseUid, tags);
		}
	}

	return tagsByExerciseUid;
});

const selectedExercises = computed(() => payloadPreview.value.exerciseUids.map((uid, index) => {
	const exercise = knownExerciseMap.value.get(uid);

	return {
		uid,
		order: index + 1,
		title: exercise?.title ?? "Ukjent øvelse",
		entryName: exercise?.entryName ?? "Ikke tilgjengelig",
		description: exercise?.description ?? "Denne oppgaven finnes ikke i oversikten akkurat na.",
		isKnown: Boolean(exercise),
		usageTags: exerciseUsageTags.value.get(uid) ?? [],
	};
}));

const selectedExerciseCards = computed<StationExerciseCard[]>({
	get: () => selectedExercises.value,
	set: (exercises) => {
		const nextExerciseUids = exercises
			.map((exercise) => exercise.uid)
			.filter((uid, index, uids) => uid && uids.indexOf(uid) === index);

		draft.exerciseUids.splice(0, draft.exerciseUids.length, ...nextExerciseUids);
	},
});

const availableExerciseCards = computed<StationExerciseCard[]>({
	get: () => availableExercises.value
		.filter((exercise) => !draft.exerciseUids.includes(exercise.uid))
		.map((exercise) => ({
			uid: exercise.uid,
			order: 0,
			title: exercise.title,
			entryName: exercise.entryName,
			description: exercise.description,
			isKnown: true,
			usageTags: exerciseUsageTags.value.get(exercise.uid) ?? [],
		})),
	set: () => {},
});

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
	draft.hint = payload.hint;
	draft.image = payload.image;
	draft.exerciseUids.splice(0, draft.exerciseUids.length, ...payload.exerciseUids);
  draft.isBoss = Boolean(payload.isBoss);
};

const updateLoadedSnapshot = (payload: StationFactoryPayload) => {
	loadedSnapshot.value = {
		title: payload.title,
		description: payload.description,
		hint: payload.hint,
		image: payload.image,
		exerciseUids: [...payload.exerciseUids],
    isBoss: Boolean(payload.isBoss),
	};
};

const loadSelection = async (selectionKey: string) => {
	const loaded = await loadStationFactoryPayload(selectionKey);
	const cachedPayload = factorySessionCache.getDraft("station", selectionKey);

	loadedLocalId.value = loaded.localId;
	updateLoadedSnapshot(loaded.payload);
	applyDraft(cachedPayload ?? loaded.payload);
	copyState.value = "idle";
};

const cloneExerciseCard = (exercise: StationExerciseCard): StationExerciseCard => ({ ...exercise });

const resetDraft = () => {
	applyDraft(loadedSnapshot.value);
	copyState.value = "idle";
};

const goToExerciseFactory = () => {
	factorySessionCache.setDraft("station", props.selectionKey, payloadPreview.value);
	factorySessionCache.pushReturnTarget(route.fullPath);
	void navigateTo(`${routePaths.teacherFactoryExercise}?returnTo=${encodeURIComponent(route.fullPath)}`);
};

const editExercise = (uid: string) => {
	factorySessionCache.setDraft("station", props.selectionKey, payloadPreview.value);
	factorySessionCache.pushReturnTarget(route.fullPath);
	void navigateTo(`${routePaths.teacherFactoryExercise}?uid=${uid}&returnTo=${encodeURIComponent(route.fullPath)}`);
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
	factorySessionCache.moveDraft("station", props.selectionKey, saved.selectionKey);
	updateLoadedSnapshot(saved.payload);
	const returnTo = typeof route.query.returnTo === "string" ? route.query.returnTo : "";
	if (returnTo) {
		factorySessionCache.popReturnTarget();
	}
	const returnTarget = returnTo ? { fullPath: returnTo } : factorySessionCache.popReturnTarget();

	if (returnTarget) {
		await navigateTo(returnTarget.fullPath);
		return;
	}

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

	try {
		availableStations.value = await fetchAllStationFactoryStations();
	} catch {
		availableStations.value = [];
	}
});

watch(payloadPreviewText, () => {
	if (copyState.value !== "idle") {
		copyState.value = "idle";
	}

	factorySessionCache.setDraft("station", props.selectionKey, payloadPreview.value);
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

					<label class="field field--full">
						<span>Tipsbrev</span>
						<textarea v-model="draft.hint" rows="3" placeholder="Tips som eleven kan åpne fra brevet på stasjonen." />
					</label>

					<div class="bossToggleWrap">
					<label
						class="field field--checkbox field--checkbox-prominent"
						title="Skurk-stasjon betyr at elevene moter en skurk de skal sla for ekstra mestringsfølelse. Legg gjerne inn et variert oppgavesett med lignende oppgaver fra det elevene har jobbet med fram til denne stasjonen."
					>
						<input v-model="draft.isBoss" type="checkbox" aria-label="Marker som skurk-stasjon" >
						<span>Skurk-stasjon</span>
					</label>
				</div>
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

						<draggable
							v-model="selectedExerciseCards"
							class="selectedList"
							item-key="uid"
							:group="'station-exercises'"
						>
							<template #item="{ element: exercise }">
								<article class="selectedCard">
									<span class="orderBadge" :aria-label="`Plass ${exercise.order}`">{{ exercise.order }}</span>
									<span class="dragHandle" aria-hidden="true">Grip</span>
									<div class="selectedCardContent">
										<p class="cardEyebrow">Plass {{ exercise.order }}</p>
										<h3>{{ exercise.title }}</h3>
										<span :class="['statusPill', { 'statusPill--manual': !exercise.isKnown }]">
											{{ exercise.isKnown ? exercise.entryName : 'Ikke tilgjengelig' }}
										</span>
										<div v-if="exercise.usageTags.length > 0" class="usageTags" aria-label="Brukt i stasjoner">
											<span v-for="tag in exercise.usageTags" :key="tag" class="usageTag">{{ tag }}</span>
										</div>
									</div>
									<div class="selectedCardActions">
										<button type="button" class="iconButton" :disabled="!exercise.isKnown" @click="editExercise(exercise.uid)">
											<img src="https://placehold.co/80x80?text=E&color=6b7280" alt="Rediger oppgavesett" class="selectedCardActionIcon" >
										</button>
									</div>
								</article>
							</template>

							<template #footer>
								<p v-if="selectedExercises.length === 0" class="emptyState dropHint">
									Dra oppgavesett hit for å legge dem til i stasjonen.
								</p>
							</template>
						</draggable>
					</div>

					<div class="exerciseColumn">
						<div class="sectionHeading sectionHeading--compact sectionHeading--inline">
							<h3>Tilgjengelige oppgavesett</h3>
							<button type="button" class="ghostButton" @click="goToExerciseFactory">
								+ Lag Oppgavesett
							</button>
						</div>

						<draggable
							v-model="availableExerciseCards"
							class="knownExercises knownExercises--columns"
							item-key="uid"
							:group="{ name: 'station-exercises', pull: 'clone', put: true }"
							:sort="false"
							:clone="cloneExerciseCard"
						>
							<template #item="{ element: exercise }">
								<article class="knownExerciseCard">
									<span class="dragHandle" aria-hidden="true">Grip</span>
									<div class="selectedCardContent">
										<p class="cardEyebrow">{{ exercise.entryName }}</p>
										<h3>{{ exercise.title }}</h3>
										<div v-if="exercise.usageTags.length > 0" class="usageTags" aria-label="Brukt i stasjoner">
											<span v-for="tag in exercise.usageTags" :key="tag" class="usageTag">{{ tag }}</span>
										</div>
									</div>
									<div class="selectedCardActions">
										<button type="button" class="iconButton" :disabled="!exercise.isKnown" @click="editExercise(exercise.uid)">
											<img src="https://placehold.co/80x80?text=E&color=6b7280" alt="Rediger oppgavesett" class="selectedCardActionIcon" >
										</button>
									</div>
								</article>
							</template>

							<template #footer>
								<p v-if="availableExerciseCards.length === 0" class="emptyState dropHint">
									Alle tilgjengelige oppgavesett er lagt til. Dra hit for å fjerne fra stasjonen.
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
   display: grid;
   grid-template-columns: 1fr;
   gap: var(--space-5);
   align-items: start;
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
   color: var(--color-text-secondary);
   line-height: var(--font-leading-base);
}
.statusStack,
.knownExercises,
.selectedList {
   display: grid;
   gap: var(--space-3);
}
.exerciseColumns {
   display: grid;
   grid-template-columns: minmax(0, 1fr) minmax(0, 1fr);
   gap: var(--space-4);
   align-items: stretch;
}
.exerciseColumn {
   display: grid;
   grid-template-rows: auto 1fr;
   gap: var(--space-3);
}
.fieldGrid {
   display: grid;
   grid-template-columns: repeat(2, minmax(0, 1fr));
   gap: var(--space-3);
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
.field--checkbox {
   grid-column: 1 / -1;
   display: flex;
   flex-direction: row;
   align-items: center;
   gap: var(--space-3);
}
.field--checkbox input {
   width: var(--size-icon-xs);
   height: var(--size-icon-xs);
   accent-color: var(--color-brand-primary);
   cursor: pointer;
   flex-shrink: 0;
}
.summaryCard,
.knownExerciseCard,
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
.knownExercises,
.selectedList {
    margin-top: 0;
    height: 100%;
    min-height: 12rem; /* deliberate: keep both drop zones easy to target */
    align-content: start;
}
.knownExercises--columns {
   margin-top: 0;
}
.selectedActions,
.actionRow {
   display: flex;
   gap: var(--space-3);
   align-items: end;
   flex-wrap: wrap;
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
   .exerciseColumns {
      grid-template-columns: 1fr;
   }
   .sectionHeading--inline,
   .summaryCard,
   .knownExerciseCard,
   .selectedCard,
   .selectedCardActions,
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
