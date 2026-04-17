<script setup lang="ts">
import {
	createExerciseDraft,
	createTaskDraft,
	emailHotspotTargetIds,
	readStoredExercisePayloads,
	removeStoredExercisePayload,
	storeExercisePayload,
	type ExerciseFactoryPayload,
	type StoredExerciseFactoryPayload,
} from "~/composables/useExerciseFactory";

import type {
	EmailHotspotBodyLinkBlock,
	EmailHotspotBodyTextBlock,
	EmailHotspotTaskData,
	ExerciseTask,
	ExerciseTemplateKind,
	MultipleChoiceTaskData,
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
const emailTextBlock = (task: ExerciseTask) => emailTask(task).email.bodyBlocks[0] as EmailHotspotBodyTextBlock;
const emailLinkBlock = (task: ExerciseTask) => emailTask(task).email.bodyBlocks[1] as EmailHotspotBodyLinkBlock;
const emailActionButton = (task: ExerciseTask) => emailTask(task).email.actionButton;
const emailAttachment = (task: ExerciseTask) => emailTask(task).email.attachments?.[0];

const getEmailTarget = (task: ExerciseTask, targetId: string) => {
	return emailTask(task).targets.find((target) => target.id === targetId);
};

const ensureEmailTarget = (task: ExerciseTask, targetId: string) => {
	const existingTarget = getEmailTarget(task, targetId);

	if (existingTarget) {
		return existingTarget;
	}

	const nextTarget = {
		id: targetId,
		isSuspicious: false,
	};

	emailTask(task).targets.push(nextTarget);
	return nextTarget;
};

const isEmailHotspotSuspicious = (task: ExerciseTask, targetId: string) => {
	return getEmailTarget(task, targetId)?.isSuspicious ?? false;
};

const setEmailHotspotSuspicious = (task: ExerciseTask, targetId: string, isSuspicious: boolean) => {
	ensureEmailTarget(task, targetId).isSuspicious = isSuspicious;
};

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
							<div class="templateSection">
								<h4>Flervalg</h4>
								<div class="fieldGrid">
									<label class="field">
										<span>Prompt-tittel</span>
										<input v-model="multipleChoiceTask(task).prompt.title" type="text" >
									</label>

									<label class="field field--full">
										<span>Prompt-tekst</span>
										<textarea v-model="multipleChoiceTask(task).prompt.text" rows="2"></textarea>
									</label>

									<label class="field">
										<span>Bilde-URL</span>
										<input v-model="multipleChoiceTask(task).prompt.imageUrl" type="url" >
									</label>

									<label class="field">
										<span>Bildetekst</span>
										<input v-model="multipleChoiceTask(task).prompt.imageAlt" type="text" >
									</label>

									<label class="field">
										<span>Riktig svar</span>
										<select v-model="multipleChoiceTask(task).correctChoice">
											<option v-for="option in multipleChoiceTask(task).options" :key="option.key" :value="option.key">
												{{ option.key.toUpperCase() }}
											</option>
										</select>
									</label>
								</div>

								<div class="optionList">
									<div
										v-for="(option, optionIndex) in multipleChoiceTask(task).options"
										:key="`${task.id}-${option.key}`"
										class="optionCard"
									>
										<label class="field optionField">
											<span>Alternativ {{ option.key.toUpperCase() }}</span>
											<input v-model="option.title" type="text" >
										</label>
										<button
											type="button"
											class="deleteButton"
											:disabled="multipleChoiceTask(task).options.length <= 2"
											@click="removeOption(taskIndex, optionIndex)"
										>
											Fjern
										</button>
									</div>
								</div>

								<button type="button" class="ghostButton" @click="addOption(taskIndex)">
									+ Legg til svaralternativ
								</button>
							</div>
						</template>

						<template v-else-if="task.templateKind === 'x-or-y'">
							<div class="templateSection">
								<h4>X eller Y</h4>
								<div class="fieldGrid">
									<label class="field">
										<span>Prompt-tittel</span>
										<input v-model="xOrYTask(task).prompt!.title" type="text" >
									</label>

									<label class="field field--full">
										<span>Prompt-tekst</span>
										<textarea v-model="xOrYTask(task).prompt!.text" rows="2"></textarea>
									</label>

									<label class="field">
										<span>Riktig valg</span>
										<select v-model="xOrYTask(task).correctChoice">
											<option value="x">X</option>
											<option value="y">Y</option>
										</select>
									</label>
								</div>

								<div class="splitGrid">
									<article class="splitCard">
										<h5>Alternativ X</h5>
										<label class="field">
											<span>Eyebrow</span>
											<input v-model="xOrYTask(task).options.x.eyebrow" type="text" >
										</label>
										<label class="field">
											<span>Tittel</span>
											<input v-model="xOrYTask(task).options.x.title" type="text" >
										</label>
										<label class="field">
											<span>Tekst</span>
											<textarea v-model="xOrYTask(task).options.x.text" rows="3"></textarea>
										</label>
										<label class="field">
											<span>Bilde-URL</span>
											<input v-model="xOrYTask(task).options.x.imageUrl" type="url" >
										</label>
										<label class="field">
											<span>Bildetekst</span>
											<input v-model="xOrYTask(task).options.x.imageAlt" type="text" >
										</label>
									</article>

									<article class="splitCard">
										<h5>Alternativ Y</h5>
										<label class="field">
											<span>Eyebrow</span>
											<input v-model="xOrYTask(task).options.y.eyebrow" type="text" >
										</label>
										<label class="field">
											<span>Tittel</span>
											<input v-model="xOrYTask(task).options.y.title" type="text" >
										</label>
										<label class="field">
											<span>Tekst</span>
											<textarea v-model="xOrYTask(task).options.y.text" rows="3"></textarea>
										</label>
										<label class="field">
											<span>Bilde-URL</span>
											<input v-model="xOrYTask(task).options.y.imageUrl" type="url" >
										</label>
										<label class="field">
											<span>Bildetekst</span>
											<input v-model="xOrYTask(task).options.y.imageAlt" type="text" >
										</label>
									</article>
								</div>
							</div>
						</template>

						<template v-else>
							<div class="templateSection">
								<h4>E-post hotspots</h4>
								<div class="fieldGrid">
									<label class="field">
										<span>Prompt-tittel</span>
										<input v-model="emailTask(task).prompt!.title" type="text" >
									</label>

									<label class="field field--full">
										<span>Prompt-tekst</span>
										<textarea v-model="emailTask(task).prompt!.text" rows="2"></textarea>
									</label>

									<label class="field">
										<span>Avsendernavn</span>
										<input v-model="emailTask(task).email.senderName" type="text" >
									</label>

									<div class="hotspotCard">
										<div class="hotspotCardHeader">
											<span class="hotspotCardTitle">Avsender-hotspot</span>
											<div class="hotspotToggleRow">
												<label class="hotspotToggle">
													<input
														:checked="isEmailHotspotSuspicious(task, emailHotspotTargetIds.senderAddress)"
														type="checkbox"
														@change="setEmailHotspotSuspicious(task, emailHotspotTargetIds.senderAddress, ($event.target as HTMLInputElement).checked)"
													>
													<span>Mistenkelig</span>
												</label>
											</div>
										</div>

										<label class="field">
											<span>Avsenderadresse</span>
											<input v-model="emailTask(task).email.senderAddress" type="email" >
										</label>
									</div>

									<div class="hotspotCard hotspotCard--full">
										<div class="hotspotCardHeader">
											<span class="hotspotCardTitle">Emne-hotspot</span>
											<div class="hotspotToggleRow">
												<label class="hotspotToggle">
													<input
														:checked="isEmailHotspotSuspicious(task, emailHotspotTargetIds.subject)"
														type="checkbox"
														@change="setEmailHotspotSuspicious(task, emailHotspotTargetIds.subject, ($event.target as HTMLInputElement).checked)"
													>
													<span>Mistenkelig</span>
												</label>
											</div>
										</div>

										<label class="field field--full">
											<span>Emne</span>
											<input v-model="emailTask(task).email.subject" type="text" >
										</label>
									</div>

									<div class="hotspotCard hotspotCard--full">
										<div class="hotspotCardHeader">
											<span class="hotspotCardTitle">Tekst-hotspot</span>
											<div class="hotspotToggleRow">
												<label class="hotspotToggle">
													<input
														:checked="isEmailHotspotSuspicious(task, emailHotspotTargetIds.bodyText)"
														type="checkbox"
														@change="setEmailHotspotSuspicious(task, emailHotspotTargetIds.bodyText, ($event.target as HTMLInputElement).checked)"
													>
													<span>Mistenkelig</span>
												</label>
											</div>
										</div>

										<label class="field field--full">
											<span>Brødtekst</span>
											<textarea v-model="emailTextBlock(task).text" rows="3"></textarea>
										</label>
									</div>

									<div class="hotspotCard hotspotCard--full">
										<div class="hotspotCardHeader">
											<span class="hotspotCardTitle">Lenke-hotspot</span>
											<div class="hotspotToggleRow">
												<label class="hotspotToggle">
													<input
														:checked="isEmailHotspotSuspicious(task, emailHotspotTargetIds.bodyLink)"
														type="checkbox"
														@change="setEmailHotspotSuspicious(task, emailHotspotTargetIds.bodyLink, ($event.target as HTMLInputElement).checked)"
													>
													<span>Mistenkelig</span>
												</label>
											</div>
										</div>

										<div class="hotspotCardFields">
											<label class="field">
												<span>Lenketekst</span>
												<input v-model="emailLinkBlock(task).label" type="text" >
											</label>

											<label class="field">
												<span>Lenke-URL</span>
												<input v-model="emailLinkBlock(task).urlText" type="text" >
											</label>
										</div>
									</div>

									<div class="hotspotCard hotspotCard--full">
										<div class="hotspotCardHeader">
											<span class="hotspotCardTitle">Knapp-hotspot</span>
											<div class="hotspotToggleRow">
												<label class="hotspotToggle">
													<input
														:checked="isEmailHotspotSuspicious(task, emailHotspotTargetIds.actionButton)"
														type="checkbox"
														@change="setEmailHotspotSuspicious(task, emailHotspotTargetIds.actionButton, ($event.target as HTMLInputElement).checked)"
													>
													<span>Mistenkelig</span>
												</label>
											</div>
										</div>

										<div class="hotspotCardFields">
											<label class="field">
												<span>Knappetekst</span>
												<input v-model="emailActionButton(task)!.label" type="text" >
											</label>

											<label class="field">
												<span>Knapp-hjelpetekst</span>
												<input v-model="emailActionButton(task)!.helperText" type="text" >
											</label>
										</div>
									</div>

									<div class="hotspotCard hotspotCard--full">
										<div class="hotspotCardHeader">
											<span class="hotspotCardTitle">Vedlegg-hotspot</span>
											<div class="hotspotToggleRow">
												<label class="hotspotToggle">
													<input
														:checked="isEmailHotspotSuspicious(task, emailHotspotTargetIds.attachment)"
														type="checkbox"
														@change="setEmailHotspotSuspicious(task, emailHotspotTargetIds.attachment, ($event.target as HTMLInputElement).checked)"
													>
													<span>Mistenkelig</span>
												</label>
											</div>
										</div>

										<div class="hotspotCardFields">
											<label class="field">
												<span>Vedleggsnavn</span>
												<input v-model="emailAttachment(task)!.name" type="text" >
											</label>

											<label class="field">
												<span>Vedleggsdetaljer</span>
												<input v-model="emailAttachment(task)!.details" type="text" >
											</label>
										</div>
									</div>

								</div>
							</div>
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

.hotspotCard--full {
	grid-column: 1 / -1;
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
.toggleField span,
.splitCard h5,
.templateSection h4 {
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

.hotspotCard {
	display: grid;
	gap: 0.85rem;
	padding: 0.85rem;
	border: 1px solid var(--border);
	border-radius: 0.9rem;
	background: rgba(255, 255, 255, 0.03);
}

.hotspotCardHeader {
	display: flex;
	align-items: center;
	justify-content: space-between;
	gap: 0.75rem;
}

.hotspotToggleRow {
	display: flex;
	flex-wrap: wrap;
	justify-content: flex-end;
	gap: 0.5rem;
}

.hotspotCardTitle {
	font-weight: 700;
	color: var(--text);
}

.hotspotCardFields {
	display: grid;
	grid-template-columns: repeat(2, minmax(0, 1fr));
	gap: 0.9rem;
}

.hotspotToggle {
	display: inline-flex;
	align-items: center;
	gap: 0.5rem;
	padding: 0.45rem 0.75rem;
	border: 1px solid var(--border);
	border-radius: 999px;
	background: rgba(255, 255, 255, 0.03);
	font-weight: 700;
	color: var(--text);
}

.hotspotToggle input {
	margin: 0;
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
.splitCard,
.optionCard,
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
.optionCard,
.savedCard {
	display: flex;
	align-items: flex-start;
	justify-content: space-between;
	gap: 0.75rem;
}

.templateSection {
	margin-top: 1rem;
	padding-top: 1rem;
	border-top: 1px solid var(--border);
}

.templateSection h4 {
	margin: 0 0 0.8rem;
}

.optionList,
.savedList {
	display: grid;
	gap: 0.75rem;
}

.optionCard,
.savedCard {
	padding: 0.85rem 1rem;
}

.optionField {
	flex: 1;
}

.splitGrid {
	display: grid;
	grid-template-columns: repeat(2, minmax(0, 1fr));
	gap: 0.9rem;
}

.splitCard {
	padding: 1rem;
	display: grid;
	gap: 0.75rem;
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
	.fieldGrid,
	.splitGrid,
	.hotspotCardFields {
		grid-template-columns: 1fr;
	}

	.sectionHeading--inline,
	.taskCardHeader,
	.savedCard,
	.hotspotCardHeader {
		flex-direction: column;
		align-items: flex-start;
	}

	.hotspotToggleRow {
		justify-content: flex-start;
	}
}
</style>
