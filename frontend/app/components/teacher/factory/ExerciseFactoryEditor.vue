<script setup lang="ts">
import draggable from "vuedraggable";
import EmailHotspotsEditor from "~/components/teacher/factory/EmailHotspotsEditor.vue";
import MultipleChoiceEditor from "~/components/teacher/factory/MultipleChoiceEditor.vue";
import PasswordStrengthEditor from "~/components/teacher/factory/PasswordStrengthEditor.vue";
import SortIntoBucketsEditor from "~/components/teacher/factory/SortIntoBucketsEditor.vue";
import XOrYEditor from "~/components/teacher/factory/XOrYEditor.vue";
import {
	createExerciseDraft,
	createTaskDraft,
	deleteExerciseFactorySelection,
	loadExerciseFactoryPayload,
	mapExerciseToBackendRequest,
	parseExerciseFactorySelectionKey,
	saveExerciseFactoryPayload,
	type ExerciseFactoryPayload,
} from "~/composables/useExerciseFactory";
import type { BackendNotepadEntryType } from "~/composables/types/NotepadTypes";
import type {
	EmailHotspotTaskData,
	ExerciseTask,
	ExerciseTemplateKind,
	MultipleChoiceTaskData,
	PasswordStrengthTaskData,
	SortIntoBucketsTaskData,
	XOrYTaskData,
} from "~/composables/types/TaskTypes";
import { useFactorySessionCache } from "~/composables/useFactorySessionCache";

const props = defineProps<{
	selectionKey: string;
}>();

const emit = defineEmits<{
	(event: "selection-updated", selectionKey: string): void;
	(event: "saved"): void;
	(event: "deleted"): void;
}>();

const cloneExercisePayload = (payload: ExerciseFactoryPayload): ExerciseFactoryPayload => {
	return JSON.parse(JSON.stringify(payload)) as ExerciseFactoryPayload;
};

const draft = reactive<ExerciseFactoryPayload>(createExerciseDraft());
const route = useRoute();
const factorySessionCache = useFactorySessionCache();
const loadedSnapshot = ref<ExerciseFactoryPayload>(createExerciseDraft());
const copyState = ref<"idle" | "copied" | "failed">("idle");
const expandedTaskIds = ref<string[]>([]);
const removedTasks = ref<ExerciseTask[]>([]);
const loadedLocalId = ref("");
const parsedSelection = computed(() => parseExerciseFactorySelectionKey(props.selectionKey));

const canSave = computed(() => {
	const hasValidNotepadEntry = !draft.notepadEntry || (
		draft.notepadEntry.title.trim().length > 0
		&& draft.notepadEntry.content.trim().length > 0
	);

	return draft.title.trim().length > 0
		&& draft.difficultyName.trim().length > 0
		&& draft.description.trim().length > 0
		&& draft.hint.trim().length > 0
		&& hasValidNotepadEntry
		&& draft.tasks.length > 0;
});

const payloadPreview = computed<ExerciseFactoryPayload>(() => ({
	title: draft.title.trim(),
	difficultyName: draft.difficultyName.trim(),
	description: draft.description.trim(),
	hint: draft.hint.trim(),
	notepadEntry: draft.notepadEntry
		? {
			title: draft.notepadEntry.title.trim(),
			type: draft.notepadEntry.type,
			content: draft.notepadEntry.content.trim(),
			source: draft.notepadEntry.source.trim(),
		}
		: null,
	tasks: draft.tasks,
}));

const payloadPreviewText = computed(() => JSON.stringify(mapExerciseToBackendRequest(payloadPreview.value), null, 2));
const hasChanges = computed(() => JSON.stringify(payloadPreview.value) !== JSON.stringify(loadedSnapshot.value));

const editorTitle = computed(() => {
	if (parsedSelection.value.kind === "existing") {
		return `Rediger oppgave: ${payloadPreview.value.title || "Uten tittel"}`;
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

	if (task.templateKind === "sort-into-buckets") {
		return `${baseLabel}: sorter i bokser`;
	}

	if (task.templateKind === "password-strength") {
		return `${baseLabel}: passordstyrke`;
	}

	return `${baseLabel}: X eller Y`;
};

const multipleChoiceTask = (task: ExerciseTask) => task as MultipleChoiceTaskData;
const xOrYTask = (task: ExerciseTask) => task as XOrYTaskData;
const emailTask = (task: ExerciseTask) => task as EmailHotspotTaskData;
const sortTask = (task: ExerciseTask) => task as SortIntoBucketsTaskData;
const passwordTask = (task: ExerciseTask) => task as PasswordStrengthTaskData;

const isTaskExpanded = (taskId: string) => expandedTaskIds.value.includes(taskId);

const setExpandedTasks = (taskIds: string[]) => {
	expandedTaskIds.value = [...taskIds];
};

const toggleTaskExpanded = (taskId: string) => {
	if (isTaskExpanded(taskId)) {
		expandedTaskIds.value = expandedTaskIds.value.filter((id) => id !== taskId);
		return;
	}

	expandedTaskIds.value = [...expandedTaskIds.value, taskId];
};

const applyDraft = (payload: ExerciseFactoryPayload) => {
	const clonedPayload = cloneExercisePayload(payload);

	draft.title = clonedPayload.title;
	draft.difficultyName = clonedPayload.difficultyName;
	draft.description = clonedPayload.description;
	draft.hint = clonedPayload.hint;
	draft.notepadEntry = clonedPayload.notepadEntry ? JSON.parse(JSON.stringify(clonedPayload.notepadEntry)) : null;
	draft.tasks.splice(0, draft.tasks.length, ...clonedPayload.tasks);
	setExpandedTasks([]);
};

	const noteTypeOptions: Array<{ value: BackendNotepadEntryType; label: string }> = [
		{ value: "TIP", label: "Tips" },
		{ value: "INFO", label: "Info" },
	];

const enableNotepadEntry = () => {
	if (draft.notepadEntry) {
		return;
	}

	draft.notepadEntry = {
		title: "",
		type: "TIP",
		content: "",
		source: "",
	};
};

const disableNotepadEntry = () => {
	draft.notepadEntry = null;
};

const updateLoadedSnapshot = (payload: ExerciseFactoryPayload) => {
	loadedSnapshot.value = cloneExercisePayload(payload);
};

const loadSelection = async (selectionKey: string) => {
	const loaded = await loadExerciseFactoryPayload(selectionKey);
	const cachedPayload = factorySessionCache.getDraft("exercise", selectionKey);

	loadedLocalId.value = loaded.localId;
	updateLoadedSnapshot(loaded.payload);
	applyDraft(cachedPayload ?? loaded.payload);
	copyState.value = "idle";
};

const resetDraft = () => {
	applyDraft(loadedSnapshot.value);
	copyState.value = "idle";
};

const addTask = (kind: ExerciseTemplateKind) => {
	const nextTask = createTaskDraft(kind, draft.tasks.length + 1);

	draft.tasks.push(nextTask);
	expandedTaskIds.value = [...expandedTaskIds.value, nextTask.id];
};

const handleRemovedTaskChange = (event: { added?: { element?: ExerciseTask } }) => {
	const removedTask = event.added?.element;
	removedTasks.value = [];

	if (!removedTask) {
		return;
	}

	if (draft.tasks.length === 0) {
		draft.tasks.push(removedTask);
		return;
	}

	expandedTaskIds.value = expandedTaskIds.value.filter((id) => id !== removedTask.id);
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

const finishSave = (selectionKey: string) => {
	emit("selection-updated", selectionKey);
	emit("saved");
};

const withSavedExerciseUid = (fullPath: string, selectionKey: string) => {
	const selection = parseExerciseFactorySelectionKey(selectionKey);

	if (selection.kind !== "existing") {
		return fullPath;
	}

	const url = new URL(fullPath, "http://factory-return.local");
	url.searchParams.set("exerciseUid", selection.uid);

	return `${url.pathname}${url.search}${url.hash}`;
};

const savePayload = async () => {
	if (!canSave.value) {
		return;
	}

	const nextPayload = cloneExercisePayload(payloadPreview.value);
	const saved = await saveExerciseFactoryPayload(props.selectionKey, nextPayload);

	loadedLocalId.value = "";
	factorySessionCache.moveDraft("exercise", props.selectionKey, saved.selectionKey);
	updateLoadedSnapshot(saved.payload);
	const returnTo = typeof route.query.returnTo === "string" ? route.query.returnTo : "";
	if (returnTo) {
		factorySessionCache.popReturnTarget();
	}
	const returnTarget = returnTo ? { fullPath: returnTo } : factorySessionCache.popReturnTarget();

	if (returnTarget) {
		await navigateTo(withSavedExerciseUid(returnTarget.fullPath, saved.selectionKey));
		return;
	}

	finishSave(saved.selectionKey);
};

const deleteSelection = async () => {
	await deleteExerciseFactorySelection(props.selectionKey);
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

watch(payloadPreviewText, () => {
	if (copyState.value !== "idle") {
		copyState.value = "idle";
	}

	factorySessionCache.setDraft("exercise", props.selectionKey, payloadPreview.value);
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
						<input v-model="draft.title" type="text" placeholder="For eksempel: Mistenkelige meldinger" >
					</label>

					<label class="field">
						<span>Difficulty name</span>
						<input v-model="draft.difficultyName" type="text" placeholder="For eksempel: Lett" >
					</label>

					<label class="field fieldFull">
						<span>Beskrivelse</span>
						<textarea v-model="draft.description" rows="3" placeholder="Kort beskrivelse av hva eleven skal øve på." />
					</label>

					<label class="field fieldFull">
						<span>Tips</span>
						<textarea v-model="draft.hint" rows="3" placeholder="Tips som eleven kan åpne fra brevet under oppgaven." />
					</label>

					<div class="field fieldFull noteToggleRow">
						<div>
							<span>Notatoppføring</span>
							<p class="fieldHelp">Legg til et notat som følger oppgaven når den lagres i backend.</p>
						</div>
						<button
							v-if="!draft.notepadEntry"
							type="button"
							class="ghostButton"
							@click="enableNotepadEntry"
						>
							+ Legg til notat
						</button>
						<button
							v-else
							type="button"
							class="ghostButton"
							@click="disableNotepadEntry"
						>
							Fjern notat
						</button>
					</div>

					<template v-if="draft.notepadEntry">
						<label class="field">
							<span>Notattittel</span>
							<input v-model="draft.notepadEntry.title" type="text" placeholder="For eksempel: Huskeregel" >
						</label>

						<label class="field">
							<span>Notattype</span>
							<select v-model="draft.notepadEntry.type">
								<option v-for="option in noteTypeOptions" :key="option.value" :value="option.value">
									{{ option.label }}
								</option>
							</select>
						</label>

						<label class="field fieldFull">
							<span>Notatkilde</span>
							<p class="fieldHelp">Kilde settes automatisk ut fra stasjonen som oppgaven blir brukt i.</p>
						</label>

						<label class="field fieldFull">
							<span>Notatinnhold</span>
							<textarea v-model="draft.notepadEntry.content" rows="3" placeholder="Teksten som skal lagres sammen med oppgaven." />
						</label>
					</template>
				</div>
			</section>

			<section class="panelSection">
				<div class="sectionHeading sectionHeadingInline">
					<div>
						<h2>Oppgaver</h2>
						<p>Hver oppgave blir ett element i oppgavelisten.</p>
					</div>

					<div class="taskAddRow">
						<button type="button" class="typeButton" data-task-type="multiple-choice" @click="addTask('multiple-choice')">
							+ Flervalg
						</button>
						<button type="button" class="typeButton" data-task-type="x-or-y" @click="addTask('x-or-y')">
							+ X eller Y
						</button>
						<button type="button" class="typeButton" data-task-type="email-hotspots" @click="addTask('email-hotspots')">
							+ E-post
						</button>
						<button type="button" class="typeButton" data-task-type="sort-into-buckets" @click="addTask('sort-into-buckets')">
							+ Sortering
						</button>
						<button type="button" class="typeButton" data-task-type="password-strength" @click="addTask('password-strength')">
							+ Passord
						</button>
					</div>
				</div>

				<p v-if="draft.tasks.length === 0" class="emptyTaskState">
					Ingen oppgaver lagt til ennå. Start med å velge en oppgavetype.
				</p>

				<draggable
					v-model="draft.tasks"
					class="taskList"
					item-key="id"
					:group="'exercise-tasks'"
					handle=".dragHandle"
				>
					<template #item="{ element: task, index: taskIndex }">
						<article class="taskCard" :data-task-type="task.templateKind">
							<div class="taskCardHeader">
								<div class="taskCardTitle">
									<span class="orderBadge" :aria-label="`Plass ${taskIndex + 1}`">{{ taskIndex + 1 }}</span>
									<span class="dragHandle" aria-hidden="true">Grip</span>
									<div>
										<p class="taskEyebrow">{{ taskLabel(task, taskIndex) }}</p>
										<h3>{{ task.id }}</h3>
									</div>
								</div>

								<div class="taskCardActions">
									<button
										v-if="!isTaskExpanded(task.id)"
										type="button"
										class="ghostButton"
										@click="toggleTaskExpanded(task.id)"
									>
										Rediger igjen
									</button>
								</div>
							</div>

					<template v-if="isTaskExpanded(task.id)">
						<div class="fieldGrid">
							<label class="field fieldFull">
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

						<template v-else-if="task.templateKind === 'sort-into-buckets'">
							<SortIntoBucketsEditor :task="sortTask(task)" />
						</template>

						<template v-else-if="task.templateKind === 'password-strength'">
							<PasswordStrengthEditor :task="passwordTask(task)" />
						</template>

							<template v-else>
								<EmailHotspotsEditor :task="emailTask(task)" />
							</template>

							<div class="taskFooterActions">
								<button
									type="button"
									class="primaryButton"
									@click="toggleTaskExpanded(task.id)"
								>
									Done
								</button>
							</div>
						</template>
						</article>
					</template>
				</draggable>

				<draggable
					v-if="draft.tasks.length > 1"
					v-model="removedTasks"
					class="removeDropZone"
					item-key="id"
					:group="'exercise-tasks'"
					@change="handleRemovedTaskChange"
				>
					<template #item="{ element }">
						<div class="removeDropZone__item">{{ element.id }}</div>
					</template>

					<template #footer>
						<p>Dra en oppgave hit for å fjerne den fra oppgavesettet.</p>
					</template>
				</draggable>
			</section>

			<div class="actionRow actionRowReset">
				<button type="button" class="ghostButton" :disabled="!hasChanges" @click="resetDraft">
					Tilbakestill endringer
				</button>
			</div>
		</div>

		<aside class="previewPanel">
			<section class="panelSection">
				<div class="sectionHeading">
					<h2>Innhold</h2>
					<p>Her ser du hva som blir med i oppgaven du redigerer akkurat nå.</p>
				</div>

				<div class="statusStack">
					<article class="summaryCard">
						<p class="cardEyebrow">Oppgaver</p>
						<h3>{{ draft.tasks.length }} oppgaver</h3>
						<p>{{ hasChanges ? 'Det finnes ulagrede endringer.' : 'Editoren er synkron med sist lastede versjon.' }}</p>
					</article>

					<article class="summaryCard">
						<p class="cardEyebrow">Info</p>
						<h3>{{ draft.difficultyName || 'Ingen vanskelighetsgrad enda' }}</h3>
						<p>Tittel, vanskelighetsgrad, beskrivelse og tips blir lagret sammen med oppgavene.</p>
					</article>
				</div>
			</section>

			<div class="actionRow actionRowSave">
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
						<div class="sectionHeading sectionHeadingInline">
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
  --factory-drop-reject-border: var(--color-semantic-error);

   --factory-delete-border: var(--color-semantic-error);
   --factory-delete-bg: var(--palette-overlay-red-soft);
   --factory-delete-text: var(--palette-red-700);

   --factory-badge-bg: var(--palette-overlay-green-soft);

   --factory-tag-blue: var(--palette-ice-200);
   --factory-tag-gold: var(--palette-gold-400);
   --factory-tag-pink: var(--palette-red-200);
   --factory-tag-green: var(--palette-green-100);

   --factory-payload-bg: var(--color-bg-canvas);
   --factory-payload-text: var(--color-text-primary);
   --factory-payload-font: var(--font-family-mono);

  --factory-type-multiple-choice: var(--palette-green-100);
  --factory-type-x-or-y: var(--palette-ice-200);
  --factory-type-email-hotspots: var(--palette-red-200);
  --factory-type-sort-into-buckets: var(--palette-gold-400);
  --factory-type-password-strength: var(--palette-navy-200);

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
   margin-top: var(--space-6);
   padding-top: var(--space-6);
   border-top: var(--border-width-hairline) solid var(--color-border-default);
}

.sectionHeading {
   display: grid;
   gap: var(--space-1);
   margin-bottom: var(--space-4);
}

.sectionHeadingInline {
   display: grid;
   grid-template-columns: 1fr auto;
   align-items: start;
   gap: var(--space-3);
}

.sectionHeading h2,
.summaryCard h3,
.taskCard h3 {
   margin: 0;
}

.sectionHeading p,
.summaryCard p {
   margin: 0;
   color: var(--color-text-secondary);
   line-height: var(--font-leading-base);
}

.statusStack,
.taskList {
   display: grid;
   gap: var(--space-4);
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

.fieldLabel {
  color: var(--factory-label-color);
  font-family: var(--factory-label-font), monospace;
  font-size: var(--factory-label-size);
  font-weight: var(--font-weight-bold);
  letter-spacing: var(--factory-label-tracking);
  text-transform: uppercase;
}

.fieldFull {
   grid-column: 1 / -1;
}

.field input,
.field select,
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
.field select:focus,
.field textarea:focus {
   outline: none;
   border-color: var(--factory-field-focus);
}

.taskAddRow,
.actionRow {
   display: flex;
   gap: var(--space-3);
   align-items: end;
   flex-wrap: wrap;
}

.taskAddRow {
   justify-content: flex-end;
}

.summaryCard {
   display: flex;
   justify-content: space-between;
   align-items: flex-start;
   gap: var(--space-3);
   padding: var(--space-4);
   border: var(--border-width-hairline) solid var(--color-border-default);
   border-radius: var(--radius-2);
   background: var(--color-bg-surface);
}


.taskCard {
  position: relative;
   display: grid;
   gap: var(--space-3);
   padding: var(--space-4);
   border: var(--border-width-hairline) solid var(--color-border-default);
   border-radius: var(--radius-2);
   background: var(--color-bg-surface);
  overflow: hidden;
}

.taskCard::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  bottom: 0;
  width: var(--space-2);
  /* fallback if no data-type-task matches */
  background: var(--color-border-default);
}

.taskCard[data-task-type="multiple-choice"]::before { background: var(--factory-type-multiple-choice); }
.taskCard[data-task-type="x-or-y"]::before { background: var(--factory-type-x-or-y); }
.taskCard[data-task-type="email-hotspots"]::before { background: var(--factory-type-email-hotspots); }
.taskCard[data-task-type="sort-into-buckets"]::before { background: var(--factory-type-sort-into-buckets); }
.taskCard[data-task-type="password-strength"]::before { background: var(--factory-type-password-strength); }

.taskCardHeader {
   display: flex;
   align-items: flex-start;
   justify-content: space-between;
   gap: var(--space-3);
   margin-bottom: var(--space-4);
}

.taskCardTitle {
   display: flex;
   align-items: flex-start;
   gap: var(--space-3);
}

.taskCardActions {
   display: flex;
   flex-wrap: wrap;
   justify-content: flex-end;
   gap: var(--space-3);
   margin-left: auto;
}

.taskFooterActions {
   display: flex;
   justify-content: flex-end;
   margin-top: var(--space-4);
}

.orderBadge {
   display: inline-grid;
   place-items: center;
   width: var(--space-7);
   height: var(--space-7);
   border: var(--border-width-hairline) solid var(--color-brand-primary);
   border-radius: var(--radius-pill);
   background: var(--factory-badge-bg);
   color: var(--color-brand-primary);
   font-weight: var(--font-weight-bold);
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

.taskList + .removeDropZone {
   margin-top: var(--space-4);
}

.removeDropZone {
   display: grid;
   place-items: center;
   min-height: 4.5rem; /* deliberate: minimum drop target */
   padding: var(--space-4);
   border: var(--border-width-hairline) dashed var(--factory-drop-reject-border);
   border-radius: var(--radius-2);
   background: var(--factory-drop-reject);
   color: var(--factory-delete-text);
   text-align: center;
}

.removeDropZone p {
   margin: 0;
}

.removeDropZone__item {
   display: none;
}

.taskEyebrow,
.cardEyebrow {
   margin: 0 0 var(--space-1);
   color: var(--factory-label-color);
   font-family: var(--factory-label-font), monospace;
   font-size: var(--factory-label-size);
   font-weight: var(--font-weight-bold);
   letter-spacing: var(--factory-label-tracking);
   text-transform: uppercase;
}

.emptyTaskState {
   padding: var(--space-4);
   border: var(--border-width-hairline) dashed var(--color-border-default);
   border-radius: var(--radius-2);
   background: var(--factory-badge-bg);
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

.typeButton {
  border: var(--border-width-hairline) solid var(--color-border-default);
  border-radius: var(--radius-1);
  padding: var(--space-3) var(--space-4);
  font-weight: var(--font-weight-bold);
  color: var(--color-text-primary);
  background: var(--color-bg-canvas);
  cursor: pointer;
  transition:
      transform var(--transition-base),
      border-color var(--transition-base),
      background var(--transition-base),
      filter var(--transition-base);
}

.typeButton[data-task-type="multiple-choice"] { background: var(--factory-type-multiple-choice); }
.typeButton[data-task-type="x-or-y"] { background: var(--factory-type-x-or-y); }
.typeButton[data-task-type="email-hotspots"] { background: var(--factory-type-email-hotspots); }
.typeButton[data-task-type="sort-into-buckets"] { background: var(--factory-type-sort-into-buckets); }
.typeButton[data-task-type="password-strength"] { background: var(--factory-type-password-strength); }

.typeButton:hover {
  transform: translateY(calc(-1 * var(--border-width-hairline)));
  filter: brightness(0.95);
}

.typeButton:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none;
}

.ghostButton,
.primaryButton,
.deleteButton {
   border: var(--border-width-hairline) solid var(--color-border-default);
   border-radius: var(--radius-1);
   padding: var(--space-3) var(--space-4);
   font-weight: var(--font-weight-bold);
   transition: transform var(--transition-base), border-color var(--transition-base), background var(--transition-base);
}

.ghostButton,
.deleteButton {
  margin-top: var(--space-2);
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
.primaryButton:hover,
.deleteButton:hover {
   transform: translateY(calc(-1 * var(--border-width-hairline)));
}

.ghostButton:hover {
   background: var(--color-bg-surface-hover);
}

.deleteButton:hover {
   filter: brightness(0.85);
}

.ghostButton:disabled,
.primaryButton:disabled,
.deleteButton:disabled {
   opacity: 0.5;
   cursor: not-allowed;
   transform: none;
}

.actionRowReset {
   justify-content: flex-start;
}

.actionRowSave {
   margin-top: var(--space-5);
   justify-content: flex-end;
}

@media (max-width: 800px) { /* deliberate: matches factory editor mobile breakpoint */
   .fieldGrid {
      grid-template-columns: 1fr;
   }

   .sectionHeadingInline,
   .summaryCard,
   .taskCardHeader,
   .taskCardActions,
   .actionRow {
      display: grid;
   }

   .taskAddRow,
   .taskCardActions,
   .actionRow {
      justify-content: stretch;
   }
}
</style>
