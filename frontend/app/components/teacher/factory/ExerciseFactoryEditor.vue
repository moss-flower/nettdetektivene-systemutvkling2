<script setup lang="ts">
import EmailHotspotsEditor from "~/components/teacher/factory/EmailHotspotsEditor.vue";
import MultipleChoiceEditor from "~/components/teacher/factory/MultipleChoiceEditor.vue";
import XOrYEditor from "~/components/teacher/factory/XOrYEditor.vue";
import {
	clearExerciseFactoryOverride,
	createExerciseDraft,
	createTaskDraft,
	exerciseFactoryNewSelectionKey,
	makeExerciseFactoryDraftSelectionKey,
	makeExerciseFactoryExistingSelectionKey,
	parseExerciseFactorySelectionKey,
	readExerciseFactoryStore,
	removeExerciseFactoryDraft,
	saveExerciseFactoryDraft,
	saveExerciseFactoryOverride,
	type ExerciseFactoryPayload,
} from "~/composables/useExerciseFactory";
import { Exercise } from "~/composables/types/ExerciseType";
import type {
	EmailHotspotTaskData,
	ExerciseTask,
	ExerciseTemplateKind,
	MultipleChoiceTaskData,
	XOrYTaskData,
} from "~/composables/types/TaskTypes";

const props = defineProps<{
	selectionKey: string;
}>();

const emit = defineEmits<{
	(event: "selection-updated", selectionKey: string): void;
}>();

const cloneExercisePayload = (payload: ExerciseFactoryPayload): ExerciseFactoryPayload => {
	return JSON.parse(JSON.stringify(payload)) as ExerciseFactoryPayload;
};

const draft = reactive<ExerciseFactoryPayload>(createExerciseDraft());
const loadedSnapshot = ref<ExerciseFactoryPayload>(createExerciseDraft());
const copyState = ref<"idle" | "copied" | "failed">("idle");
const saveState = ref<"idle" | "saved">("idle");
const loadedExerciseUid = ref("");
const loadedLocalId = ref("");
const hasStoredOverride = ref(false);
const lastSavedAt = ref("");
const parsedSelection = computed(() => parseExerciseFactorySelectionKey(props.selectionKey));

const canSave = computed(() => {
	return draft.title.trim().length > 0
		&& draft.entryName.trim().length > 0
		&& draft.description.trim().length > 0
		&& draft.tasks.length > 0;
});

const payloadPreview = computed<ExerciseFactoryPayload>(() => ({
	title: draft.title.trim(),
	entryName: draft.entryName.trim(),
	description: draft.description.trim(),
	tasks: draft.tasks,
}));

const payloadPreviewText = computed(() => JSON.stringify(payloadPreview.value, null, 2));
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

const editorTitle = computed(() => {
	if (parsedSelection.value.kind === "existing") {
		return `Rediger oppgave: ${payloadPreview.value.title || loadedExerciseUid.value}`;
	}

	if (parsedSelection.value.kind === "draft") {
		return payloadPreview.value.title ? `Lokal oppgave: ${payloadPreview.value.title}` : "Ny lokal oppgave";
	}

	return "Ny oppgave";
});

const editorDescription = computed(() => {
	if (parsedSelection.value.kind === "existing") {
		return "Du redigerer en eksisterende oppgave. Lokal lagring fungerer som en override over mock-dataen til backend finnes.";
	}

	if (parsedSelection.value.kind === "draft") {
		return "Dette er en lokal oppgave uten backend-uid ennå. Du kan fortsette å redigere den fra nedtrekkslisten.";
	}

	return "Opprett en ny oppgave lokalt. Etter første lagring dukker den opp i listen og kan redigeres videre.";
	});

const taskLabel = (task: ExerciseTask, index: number) => {
	const baseLabel = `Oppgave ${index + 1}`;

	if (task.templateKind === "multiple-choice") {
		return `${baseLabel}: flervalg`;
	}

	if (task.templateKind === "email-hotspots") {
		return `${baseLabel}: e-post hotspots`;
	}

	return `${baseLabel}: X eller Y`;
};

const multipleChoiceTask = (task: ExerciseTask) => task as MultipleChoiceTaskData;
const xOrYTask = (task: ExerciseTask) => task as XOrYTaskData;
const emailTask = (task: ExerciseTask) => task as EmailHotspotTaskData;

const applyDraft = (payload: ExerciseFactoryPayload) => {
	const clonedPayload = cloneExercisePayload(payload);

	draft.title = clonedPayload.title;
	draft.entryName = clonedPayload.entryName;
	draft.description = clonedPayload.description;
	draft.tasks.splice(0, draft.tasks.length, ...clonedPayload.tasks);
};

const updateLoadedSnapshot = (payload: ExerciseFactoryPayload) => {
	loadedSnapshot.value = cloneExercisePayload(payload);
};

const loadSelection = (selectionKey: string) => {
	const selection = parseExerciseFactorySelectionKey(selectionKey);
	const store = readExerciseFactoryStore();

	loadedExerciseUid.value = "";
	loadedLocalId.value = "";
	hasStoredOverride.value = false;
	lastSavedAt.value = "";

	if (selection.kind === "existing") {
		const backendExercise = Exercise.backendFetchExerciseByUid(selection.uid);
		const override = store.overrides[selection.uid] ?? null;
		const nextPayload = override?.payload
			?? createExerciseDraft();

		if (!override && backendExercise) {
			nextPayload.title = backendExercise.title;
			nextPayload.entryName = backendExercise.entryName;
			nextPayload.description = backendExercise.description ?? "";
			nextPayload.tasks = cloneExercisePayload({
				title: backendExercise.title,
				entryName: backendExercise.entryName,
				description: backendExercise.description ?? "",
				tasks: backendExercise.tasks,
			}).tasks;
		}

		loadedExerciseUid.value = selection.uid;
		hasStoredOverride.value = Boolean(override);
		lastSavedAt.value = override?.updatedAt ?? "";
		applyDraft(nextPayload);
		updateLoadedSnapshot(nextPayload);
	} else if (selection.kind === "draft") {
		const storedDraft = store.drafts.find((draftEntry) => draftEntry.localId === selection.localId) ?? null;
		const nextPayload = storedDraft?.payload ?? createExerciseDraft();

		loadedLocalId.value = storedDraft?.localId ?? "";
		lastSavedAt.value = storedDraft?.updatedAt ?? "";
		applyDraft(nextPayload);
		updateLoadedSnapshot(nextPayload);
	} else {
		const nextPayload = createExerciseDraft();

		applyDraft(nextPayload);
		updateLoadedSnapshot(nextPayload);
	}

	copyState.value = "idle";
	saveState.value = "idle";
};

const resetDraft = () => {
	applyDraft(loadedSnapshot.value);
	copyState.value = "idle";
	saveState.value = "idle";
};

const addTask = (kind: ExerciseTemplateKind) => {
	draft.tasks.push(createTaskDraft(kind, draft.tasks.length + 1));
};

const removeTask = (taskIndex: number) => {
	if (draft.tasks.length === 1) {
		return;
	}

	draft.tasks.splice(taskIndex, 1);
};

const moveTask = (taskIndex: number, direction: -1 | 1) => {
	const targetIndex = taskIndex + direction;

	if (targetIndex < 0 || targetIndex >= draft.tasks.length) {
		return;
	}

	const nextTasks = [...draft.tasks];
	const [movedTask] = nextTasks.splice(taskIndex, 1);

	if (!movedTask) {
		return;
	}

	nextTasks.splice(targetIndex, 0, movedTask);
	draft.tasks.splice(0, draft.tasks.length, ...nextTasks);
};

const addOption = (taskIndex: number) => {
	const task = draft.tasks[taskIndex];

	if (!task || task.templateKind !== "multiple-choice") {
		return;
	}

	const optionKey = String.fromCharCode(97 + task.options.length);

	task.options.push({
		key: optionKey,
		title: `Svaralternativ ${optionKey.toUpperCase()}`,
	});
};

const removeOption = (taskIndex: number, optionIndex: number) => {
	const task = draft.tasks[taskIndex];

	if (!task || task.templateKind !== "multiple-choice") {
		return;
	}

	if (task.options.length <= 2) {
		return;
	}

	const removedOption = task.options[optionIndex];
	task.options.splice(optionIndex, 1);
	task.options.forEach((option, index) => {
		option.key = String.fromCharCode(97 + index);
	});

	if (task.correctChoice === removedOption?.key) {
		task.correctChoice = task.options[0]?.key ?? "a";
	}
};

const savePayload = () => {
	if (!canSave.value) {
		return;
	}

	const nextPayload = cloneExercisePayload(payloadPreview.value);

	if (parsedSelection.value.kind === "existing") {
		const record = saveExerciseFactoryOverride(parsedSelection.value.uid, nextPayload);

		updateLoadedSnapshot(nextPayload);
		hasStoredOverride.value = true;
		lastSavedAt.value = record.updatedAt;
		saveState.value = "saved";
		emit("selection-updated", makeExerciseFactoryExistingSelectionKey(parsedSelection.value.uid));
		return;
	}

	const record = saveExerciseFactoryDraft(nextPayload, loadedLocalId.value || undefined);

	loadedLocalId.value = record.localId;
	updateLoadedSnapshot(nextPayload);
	lastSavedAt.value = record.updatedAt;
	saveState.value = "saved";
	emit("selection-updated", makeExerciseFactoryDraftSelectionKey(record.localId));
};

const clearLocalState = () => {
	if (parsedSelection.value.kind === "existing") {
		clearExerciseFactoryOverride(parsedSelection.value.uid);
		loadSelection(makeExerciseFactoryExistingSelectionKey(parsedSelection.value.uid));
		emit("selection-updated", makeExerciseFactoryExistingSelectionKey(parsedSelection.value.uid));
		return;
	}

	if (parsedSelection.value.kind === "draft" && loadedLocalId.value) {
		removeExerciseFactoryDraft(loadedLocalId.value);
		emit("selection-updated", exerciseFactoryNewSelectionKey);
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
						<h3>{{ parsedSelection.kind === 'existing' ? 'Eksisterende oppgave' : parsedSelection.kind === 'draft' ? 'Lokal oppgave' : 'Ny oppgave' }}</h3>
						<p>{{ loadedExerciseUid || loadedLocalId || 'Ingen uid ennå' }}</p>
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
						<input v-model="draft.title" type="text" placeholder="For eksempel: Mistenkelige meldinger" >
					</label>

					<label class="field">
						<span>Entry name</span>
						<input v-model="draft.entryName" type="text" placeholder="For eksempel: Oppgave 1" >
					</label>

					<label class="field field--full">
						<span>Beskrivelse</span>
						<textarea v-model="draft.description" rows="3" placeholder="Kort beskrivelse av hva eleven skal øve på." />
					</label>
				</div>
			</section>

			<section class="panelSection">
				<div class="sectionHeading sectionHeading--inline">
					<div>
						<h2>Oppgaver</h2>
						<p>Hver oppgave blir ett element i tasks-arrayet.</p>
					</div>

					<div class="taskAddRow">
						<button type="button" class="ghostButton" @click="addTask('multiple-choice')">
							+ Flervalg
						</button>
						<button type="button" class="ghostButton" @click="addTask('x-or-y')">
							+ X eller Y
						</button>
						<button type="button" class="ghostButton" @click="addTask('email-hotspots')">
							+ E-post
						</button>
					</div>
				</div>

				<p v-if="draft.tasks.length === 0" class="emptyTaskState">
					Ingen oppgaver lagt til ennå. Start med å velge en oppgavetype.
				</p>

				<article v-for="(task, taskIndex) in draft.tasks" :key="task.id" class="taskCard">
					<div class="taskCardHeader">
						<div>
							<p class="taskEyebrow">{{ taskLabel(task, taskIndex) }}</p>
							<h3>{{ task.id }}</h3>
						</div>

						<div class="taskCardActions">
							<button
								type="button"
								class="ghostButton"
								:disabled="taskIndex === 0"
								@click="moveTask(taskIndex, -1)"
							>
								Opp
							</button>
							<button
								type="button"
								class="ghostButton"
								:disabled="taskIndex === draft.tasks.length - 1"
								@click="moveTask(taskIndex, 1)"
							>
								Ned
							</button>
							<button
								type="button"
								class="deleteButton"
								:disabled="draft.tasks.length === 1"
								@click="removeTask(taskIndex)"
							>
								Fjern
							</button>
						</div>
					</div>

					<div class="fieldGrid">
						<label class="field field--full">
							<span>Introtekst</span>
							<textarea v-model="task.introText" rows="2"></textarea>
						</label>

						<label class="field">
							<span>Riktig tilbakemelding</span>
							<textarea v-model="task.feedbackCorrect" rows="2"></textarea>
						</label>

						<label class="field">
							<span>Feil tilbakemelding</span>
							<textarea v-model="task.feedbackIncorrect" rows="2"></textarea>
						</label>
					</div>

					<template v-if="task.templateKind === 'multiple-choice'">
						<MultipleChoiceEditor
							:task="multipleChoiceTask(task)"
							@add-option="addOption(taskIndex)"
							@remove-option="removeOption(taskIndex, $event)"
						/>
					</template>

					<template v-else-if="task.templateKind === 'x-or-y'">
						<XOrYEditor :task="xOrYTask(task)" />
					</template>

					<template v-else>
						<EmailHotspotsEditor :task="emailTask(task)" />
					</template>
				</article>
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
					{{ parsedSelection.kind === 'existing' ? 'Fjern lokal override' : 'Slett lokal oppgave' }}
				</button>
				<button type="submit" class="primaryButton" :disabled="!canSave || !hasChanges">
					Lagre oppgave lokalt
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
					<p>Her ser du hva som blir med i oppgaven du redigerer akkurat nå.</p>
				</div>

				<div class="statusStack">
					<article class="summaryCard">
						<p class="cardEyebrow">Tasks</p>
						<h3>{{ draft.tasks.length }} oppgaver</h3>
						<p>{{ hasChanges ? 'Det finnes ulagrede endringer.' : 'Editoren er synkron med sist lastede versjon.' }}</p>
					</article>

					<article class="summaryCard">
						<p class="cardEyebrow">Metadata</p>
						<h3>{{ draft.entryName || 'Ingen entry name ennå' }}</h3>
						<p>Tittel, entry name og beskrivelse blir lagret sammen med oppgavens tasks.</p>
					</article>
				</div>
			</section>
		</aside>
	</div>
</template>

<style scoped>
.factoryGrid {
	display: grid;
	grid-template-columns: minmax(0, 1.3fr) minmax(22rem, 0.9fr);
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
	padding-top: 1.5rem;
	border-top: 1px solid var(--border);
}

.sectionHeading {
	display: grid;
	gap: 0.35rem;
	margin-bottom: 1rem;
}

.sectionHeading--inline {
	display: flex;
	align-items: flex-start;
	justify-content: space-between;
	gap: 1rem;
}

.sectionHeading h2,
.summaryCard h3,
.taskCard h3 {
	margin: 0;
}

.sectionHeading p,
.summaryCard p {
	margin: 0;
	color: var(--muted);
	line-height: 1.5;
}

.summaryGrid,
.statusStack {
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

.field,
.toggleField {
	display: grid;
	gap: 0.45rem;
}

.field--full {
	grid-column: 1 / -1;
}

.field span,
.toggleField span {
	font-weight: 700;
}

.field input,
.field select,
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
.field select:focus,
.field textarea:focus {
	outline: none;
	border-color: var(--primary);
}

.taskAddRow,
.actionRow {
	display: flex;
	flex-wrap: wrap;
	gap: 0.75rem;
}

.taskCardActions {
	display: flex;
	flex-wrap: wrap;
	justify-content: flex-end;
	gap: 0.75rem;
}

.summaryCard,
.taskCard {
	border: 1px solid var(--border);
	border-radius: 1rem;
	background: rgba(255, 255, 255, 0.02);
}

.summaryCard {
	display: flex;
	justify-content: space-between;
	align-items: flex-start;
	gap: 0.75rem;
	padding: 1rem;
}

.taskCard {
	padding: 1rem;
}

.taskCard + .taskCard {
	margin-top: 1rem;
}

.taskCardHeader {
	display: flex;
	align-items: flex-start;
	justify-content: space-between;
	gap: 0.75rem;
	margin-bottom: 1rem;
}

.taskEyebrow,
.cardEyebrow {
	margin-bottom: 0.45rem;
	color: var(--primary);
	font-size: 0.82rem;
	font-weight: 700;
	letter-spacing: 0.08em;
	text-transform: uppercase;
}

.payloadPreview {
	overflow-x: auto;
	padding: 1rem;
	border: 1px solid var(--border);
	border-radius: 1rem;
	background: #111827;
	color: #d1fae5;
	font-size: 0.88rem;
	line-height: 1.55;
}

.ghostButton,
.primaryButton,
.deleteButton {
	border: 1px solid var(--border);
	border-radius: 999px;
	padding: 0.7rem 1rem;
	font-weight: 700;
	transition: transform var(--transition), border-color var(--transition), background var(--transition);
}

.ghostButton {
	background: transparent;
	color: var(--text);
}

.primaryButton {
	border-color: var(--primary);
	background: var(--primary);
	color: var(--bg);
}

.deleteButton {
	background: transparent;
	color: #fca5a5;
}

.ghostButton:hover,
.primaryButton:hover,
.deleteButton:hover {
	transform: translateY(-1px);
}

.ghostButton:disabled,
.primaryButton:disabled,
.deleteButton:disabled {
	opacity: 0.5;
	cursor: not-allowed;
	transform: none;
}

.emptyTaskState {
	padding: 1rem;
	border: 1px dashed var(--border);
	border-radius: 1rem;
	color: var(--muted);
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
	.taskCardHeader,
	.taskCardActions,
	.actionRow,
	.summaryCard {
		flex-direction: column;
		align-items: flex-start;
	}
	}
</style>