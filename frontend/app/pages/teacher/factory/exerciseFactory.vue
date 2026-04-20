<script setup lang="ts">
import {
	createExerciseDraft,
	createTaskDraft,
	readStoredExercisePayloads,
	removeStoredExercisePayload,
	storeExercisePayload,
	type ExerciseFactoryPayload,
	type StoredExerciseFactoryPayload,
} from "~/composables/useExerciseFactory";

import type {
	ExerciseTask,
	ExerciseTemplateKind,
	MultipleChoiceTaskData,
	EmailHotspotTaskData,
	XOrYTaskData,
} from "~/composables/types/TaskTypes";

const backPath = routePaths.teacherOverview;

const draft = reactive<ExerciseFactoryPayload>(createExerciseDraft());
const savedPayloads = ref<StoredExerciseFactoryPayload[]>([]);
const lastSavedId = ref("");
const copyState = ref<"idle" | "copied" | "failed">("idle");

const canSave = computed(() => {
	return draft.title.trim().length > 0
		&& draft.entryName.trim().length > 0
		&& draft.description.trim().length > 0
		&& draft.tasks.length > 0;
});

const payloadPreview = computed(() => ({
	title: draft.title.trim(),
	entryName: draft.entryName.trim(),
	description: draft.description.trim(),
	tasks: draft.tasks,
}));

const payloadPreviewText = computed(() => JSON.stringify(payloadPreview.value, null, 2));

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

const refreshSavedPayloads = () => {
	savedPayloads.value = readStoredExercisePayloads();
};

const resetDraft = () => {
	const nextDraft = createExerciseDraft();
	draft.title = nextDraft.title;
	draft.entryName = nextDraft.entryName;
	draft.description = nextDraft.description;
	draft.tasks.splice(0, draft.tasks.length, ...nextDraft.tasks);
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

	const payload = JSON.parse(payloadPreviewText.value) as ExerciseFactoryPayload;
	const storedPayload = storeExercisePayload(payload);
	refreshSavedPayloads();
	lastSavedId.value = storedPayload.localId;
	resetDraft();
};

const deleteSavedPayload = (localId: string) => {
	savedPayloads.value = removeStoredExercisePayload(localId);
	if (lastSavedId.value === localId) {
		lastSavedId.value = "";
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

onMounted(() => {
	refreshSavedPayloads();
});

watch(payloadPreviewText, () => {
	if (copyState.value !== "idle") {
		copyState.value = "idle";
	}
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
					Bygg oppgaven som en selvstendig ressurs. Rekkefolge og opplasing styres senere av stasjonen, ikke av selve exercise-dataen.
				</p>
			</div>
		</header>

		<div class="factoryGrid">
			<form class="editorPanel" @submit.prevent="savePayload">
				<section class="panelSection">
					<div class="sectionHeading">
						<h2>Grunninfo</h2>
						<p>Dette blir toppnivået i payloaden.</p>
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

							<button
								type="button"
								class="deleteButton"
								:disabled="draft.tasks.length === 1"
								@click="removeTask(taskIndex)"
							>
								Fjern
							</button>
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
							<TeacherFactoryMultipleChoiceEditor
								:task="multipleChoiceTask(task)"
								@add-option="addOption(taskIndex)"
								@remove-option="removeOption(taskIndex, $event)"
							/>
						</template>

						<template v-else-if="task.templateKind === 'x-or-y'">
							<TeacherFactoryXOrYEditor :task="xOrYTask(task)" />
						</template>

						<template v-else>
							<TeacherFactoryEmailHotspotsEditor :task="emailTask(task)" />
						</template>
					</article>
				</section>

				<div class="actionRow">
					<button type="button" class="ghostButton" @click="resetDraft">
						Nullstill utkast
					</button>
					<button type="submit" class="primaryButton" :disabled="!canSave">
						Lagre payload lokalt
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
						<h2>Lokale utkast</h2>
						<p>Disse er lagret i nettleseren som stand-in for backend-lagring.</p>
					</div>

					<div v-if="savedPayloads.length > 0" class="savedList">
						<article
							v-for="payload in savedPayloads"
							:key="payload.localId"
							class="savedCard"
							:class="{ 'savedCard--fresh': payload.localId === lastSavedId }"
						>
							<div>
								<p class="savedCardEyebrow">{{ payload.entryName }}</p>
								<h3>{{ payload.title }}</h3>
								<p>{{ payload.description }}</p>
								<span>{{ payload.tasks.length }} oppgaver</span>
							</div>

							<button type="button" class="deleteButton" @click="deleteSavedPayload(payload.localId)">
								Slett
							</button>
						</article>
					</div>

					<p v-else class="emptyState">
						Ingen lagrede payloads ennå.
					</p>
				</section>
			</aside>
		</div>
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

.factoryEyebrow {
	margin: 0 0 0.5rem;
	color: var(--primary);
	font-weight: 700;
	letter-spacing: 0.08em;
	text-transform: uppercase;
}

h1 {
	margin: 0 0 0.75rem;
}

.factoryDescription {
	max-width: 52rem;
	color: var(--muted);
	line-height: 1.5;
}

.editorPanel,
.previewPanel {
	border: 1px solid var(--border);
	border-radius: 1.2rem;
	background:
		linear-gradient(180deg, rgba(101, 240, 175, 0.08), rgba(101, 240, 175, 0)) top/100% 5rem no-repeat,
		var(--surface);
	box-shadow: var(--shadow);
}
.taskEyebrow,
.savedCardEyebrow {
	margin-bottom: 0.45rem;
	color: var(--primary);
	font-size: 0.82rem;
	font-weight: 700;
	letter-spacing: 0.08em;
	text-transform: uppercase;
}

.panelSection h2,
.taskCard h3,
.savedCard h3 {
	margin: 0 0 0.45rem;
}

.sectionHeading p,
.savedCard p {
	color: var(--muted);
	line-height: 1.5;
}

.factoryGrid {
	display: grid;
	grid-template-columns: minmax(0, 1.3fr) minmax(22rem, 0.9fr);
	gap: 1.25rem;
	align-items: start;
}

.editorPanel,
.previewPanel {
	padding: 1.25rem;
}

.panelSection + .panelSection {
	margin-top: 1.5rem;
	padding-top: 1.5rem;
	border-top: 1px solid var(--border);
}

.sectionHeading {
	margin-bottom: 1rem;
}

.sectionHeading--inline {
	display: flex;
	align-items: flex-start;
	justify-content: space-between;
	gap: 1rem;
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

.taskCard,
.savedCard {
	border: 1px solid var(--border);
	border-radius: 1rem;
	background: rgba(255, 255, 255, 0.02);
}

.taskCard {
	padding: 1rem;
}

.taskCard + .taskCard {
	margin-top: 1rem;
}

.taskCardHeader,
.savedCard {
	display: flex;
	align-items: flex-start;
	justify-content: space-between;
	gap: 0.75rem;
}

.savedList {
	display: grid;
	gap: 0.75rem;
}

.savedCard {
	padding: 0.85rem 1rem;
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

.toggleField {
	grid-template-columns: auto 1fr;
	align-items: center;
	padding: 0.85rem 1rem;
	border: 1px solid var(--border);
	border-radius: 0.9rem;
	background: rgba(255, 255, 255, 0.02);
}

.savedCard span {
	display: inline-block;
	margin-top: 0.5rem;
	color: var(--primary);
	font-weight: 700;
}

.emptyTaskState {
	padding: 1rem;
	border: 1px dashed var(--border);
	border-radius: 1rem;
	color: var(--muted);
	line-height: 1.5;
}

.savedCard--fresh {
	border-color: var(--primary);
}

.emptyState {
	color: var(--muted);
}

@media (max-width: 1100px) {
	.factoryGrid {
		grid-template-columns: 1fr;
	}
}

@media (max-width: 800px) {
	.fieldGrid {
		grid-template-columns: 1fr;
	}

	.sectionHeading--inline,
	.taskCardHeader,
	.savedCard {
		flex-direction: column;
		align-items: flex-start;
	}
}
</style>
