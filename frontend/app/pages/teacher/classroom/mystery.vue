<script setup lang="ts">
import type { WeeklyMysteryPrompt, WeeklyMysterySubmissionResponse } from '~/composables/types/WeeklyMysteryTypes';
import type { MultipleChoiceTaskData } from '~/composables/types/TaskTypes';
import { Exercise } from '~/composables/types/ExerciseType';
import { routePaths, routePathsWithString } from '~/composables/utils/routePaths';
import {
	createDraftId,
	exerciseFactoryNewSelectionKey,
	type ExerciseFactoryPayload,
} from '~/composables/useExerciseFactory';
import { useFactorySessionCache } from '~/composables/useFactorySessionCache';

const route = useRoute();
const router = useRouter();
const { teacher } = useClassrooms();
const { $api } = useNuxtApp();
const factorySessionCache = useFactorySessionCache();

const classroomId = computed(() => String(route.query.classroomId ?? ''));
const today = new Date().toISOString().slice(0, 10);
const promptDraft = reactive({
	title: '',
	prompt: '',
	clue: '',
	date: today,
});
const isSavingPrompt = ref(false);
const assigningPromptId = ref('');
const statusMessage = ref('');
const errorMessage = ref('');
const showHistoryDialog = ref(false);

const handleDialogKeydown = (event: KeyboardEvent) => {
	if (event.key === 'Escape') {
		showHistoryDialog.value = false;
	}
};

const { data: classroom } = await useAsyncData(
	'teacher-classroom-mystery-classroom',
	() => (classroomId.value ? teacher.getClassroomById(classroomId.value).catch(() => null) : null),
	{ watch: [classroomId] },
);

const { data: currentMystery, refresh: refreshCurrentMystery } = await useAsyncData<WeeklyMysteryPrompt | null>(
	'teacher-classroom-mystery-current',
	() => (classroomId.value ? $api(`/classrooms/${classroomId.value}/weekly-mystery`, { method: 'GET' }).catch(() => null) : null),
	{ watch: [classroomId] },
);

const { data: mysteryHistory, refresh: refreshMysteryHistory } = await useAsyncData<WeeklyMysteryPrompt[]>(
	'teacher-classroom-mystery-history',
	() => (classroomId.value ? $api('/weekly-mystery/classroom-history', {
		method: 'GET',
		query: {
			classroomUid: classroomId.value,
		},
	}).catch(() => []) : []),
	{ watch: [classroomId] },
);

const { data: savedPrompts, refresh: refreshSavedPrompts } = await useAsyncData<WeeklyMysteryPrompt[]>(
	'teacher-weekly-mystery-prompts',
	() => $api<WeeklyMysteryPrompt[]>('/weekly-mystery/prompts', { method: 'GET' }).catch(() => []),
);

const currentMysteryId = computed(() => currentMystery.value?.publicId ?? '');

const buildReturnTo = () => {
	const query = new URLSearchParams();
	query.set('classroomId', classroomId.value);

	if (currentMysteryId.value) {
		query.set('linkPromptId', currentMysteryId.value);
	}

	return `${routePaths.teacherClassroomMystery}?${query.toString()}`;
};

const linkedExerciseUid = computed(() => {
	const raw = currentMystery.value?.exerciseUid;

	return typeof raw === 'string' && raw.trim().length > 0 ? raw.trim() : '';
});

const { data: linkedExercise, refresh: refreshLinkedExercise } = await useAsyncData(
	'teacher-weekly-mystery-linked-exercise',
	() => (linkedExerciseUid.value ? Exercise.backendFetchExerciseByUid(linkedExerciseUid.value) : null),
	{
		watch: [linkedExerciseUid],
	},
);

const editLinkedExerciseHref = computed(() => {
	if (!linkedExerciseUid.value) {
		return null;
	}

	return {
		path: routePaths.teacherFactoryExercise,
		query: {
			uid: linkedExerciseUid.value,
			returnTo: buildReturnTo(),
		},
	};
});

const { data: currentSubmissions, refresh: refreshCurrentSubmissions } = await useAsyncData<WeeklyMysterySubmissionResponse[]>(
	'teacher-weekly-mystery-submissions',
	() => (currentMysteryId.value ? $api<WeeklyMysterySubmissionResponse[]>(`/weekly-mystery/${currentMysteryId.value}/submissions`, {
		method: 'GET',
	}).catch(() => []) : []),
	{ watch: [currentMysteryId] },
);

const canSavePrompt = computed(() =>
	Boolean(classroomId.value)
	&& promptDraft.title.trim().length > 0
	&& promptDraft.prompt.trim().length > 0
	&& promptDraft.clue.trim().length > 0
	&& promptDraft.date.length > 0,
);

const resetMessages = () => {
	statusMessage.value = '';
	errorMessage.value = '';
};

const refreshMysteryState = async () => {
	await Promise.all([
		refreshCurrentMystery(),
		refreshMysteryHistory(),
		refreshSavedPrompts(),
		refreshCurrentSubmissions(),
		refreshLinkedExercise(),
	]);
};

const getStudentName = (studentPublicUid: string) => {
	return classroom.value?.members.find((member) => member.publicUuid === studentPublicUid)?.username ?? 'Ukjent elev';
};

const buildExercisePayloadFromSubmission = (submission: WeeklyMysterySubmissionResponse): ExerciseFactoryPayload => {
	const studentName = getStudentName(submission.studentPublicUid);
	const mysteryTitle = currentMystery.value?.title ?? 'Ukens mysterium';
	const task: MultipleChoiceTaskData = {
		id: createDraftId('weekly-mystery-task'),
		templateKind: 'multiple-choice',
		introText: 'Se nøye på bildet og forklar hva som virker trygt eller mistenkelig.',
		feedbackCorrect: 'Bra detektivarbeid. Du la merke til et viktig spor i saken.',
		feedbackIncorrect: 'Se en gang til etter avsender, språk, bildebruk og andre spor som kan hjelpe deg.',
		prompt: {
			title: submission.fileName || mysteryTitle,
			text: submission.message,
			imageUrl: submission.fileUrl,
			imageAlt: `Innsendt bilde fra ${studentName}`,
		},
		options: [
			{ key: 'a', title: 'Dette er det viktigste sporet i saken' },
			{ key: 'b', title: 'Bildet alene beviser at saken stemmer' },
			{ key: 'c', title: 'Det er smartest å dele videre med en gang' },
		],
		correctChoice: 'a',
	};

	return {
		title: `Ukens mysterium: ${mysteryTitle}`,
		difficultyName: 'Ukens mysterium',
		description: [
			currentMystery.value?.prompt,
			`Elevforslag fra ${studentName}: ${submission.message}`,
		].filter(Boolean).join('\n\n'),
		hint: currentMystery.value?.clue || 'Se nøye på bildet og forklar hvilke spor du finner.',
		tasks: [task],
	};
};

const createGameFromSubmission = async (submission: WeeklyMysterySubmissionResponse) => {
	if (!currentMysteryId.value) {
		errorMessage.value = 'Velg et aktivt mysterium før du lager spillet.';
		return;
	}

	resetMessages();
	factorySessionCache.setDraft('exercise', exerciseFactoryNewSelectionKey, buildExercisePayloadFromSubmission(submission));
	await navigateTo({
		path: routePaths.teacherFactoryExercise,
		query: {
			returnTo: buildReturnTo(),
		},
	});
};

const clearFactoryReturnQuery = async () => {
	const nextQuery = { ...route.query };
	delete nextQuery.exerciseUid;
	delete nextQuery.linkPromptId;

	await router.replace({
		path: route.path,
		query: nextQuery,
	});
};

const linkReturnedExercise = async () => {
	const exerciseUid = typeof route.query.exerciseUid === 'string' ? route.query.exerciseUid : '';
	const promptId = typeof route.query.linkPromptId === 'string' ? route.query.linkPromptId : '';

	if (!exerciseUid || !promptId) {
		return;
	}

	resetMessages();

	try {
		await $api(`/weekly-mystery/${promptId}/exercise`, {
			method: 'PATCH',
			body: { exerciseUid },
		});
		await refreshMysteryState();
		statusMessage.value = 'Spillet er koblet til ukens mysterium.';
	} catch {
		errorMessage.value = 'Spillet ble lagret, men kunne ikke kobles til mysteriet. Prøv igjen.';
	} finally {
		await clearFactoryReturnQuery();
	}
};

const assignPrompt = async (promptId: string) => {
	if (!classroomId.value || assigningPromptId.value) {
		return;
	}

	resetMessages();
	assigningPromptId.value = promptId;

	try {
		await $api(`/classrooms/${classroomId.value}/weekly-mystery`, {
			method: 'PATCH',
			body: { promptId },
		});
		await refreshMysteryState();
		statusMessage.value = 'Oppdraget er aktivt for klasserommet.';
	} catch {
		errorMessage.value = 'Kunne ikke aktivere oppdraget. Prøv igjen.';
	} finally {
		assigningPromptId.value = '';
	}
};

const createAndActivatePrompt = async () => {
	if (!canSavePrompt.value || isSavingPrompt.value) {
		return;
	}

	resetMessages();
	isSavingPrompt.value = true;

	try {
		const createdPrompt = await $api<WeeklyMysteryPrompt>('/weekly-mystery/prompts', {
			method: 'POST',
			body: {
				title: promptDraft.title.trim(),
				prompt: promptDraft.prompt.trim(),
				clue: promptDraft.clue.trim(),
				date: promptDraft.date,
				publicClassroomIds: [classroomId.value],
			},
		});

		assigningPromptId.value = createdPrompt.publicId;
		await $api(`/classrooms/${classroomId.value}/weekly-mystery`, {
			method: 'PATCH',
			body: { promptId: createdPrompt.publicId },
		});
		await refreshMysteryState();
		promptDraft.title = '';
		promptDraft.prompt = '';
		promptDraft.clue = '';
		promptDraft.date = today;
		statusMessage.value = 'Ukens oppdrag er publisert for klassen.';
	} catch {
		errorMessage.value = 'Kunne ikke publisere oppdraget. Sjekk feltene og prøv igjen.';
	} finally {
		assigningPromptId.value = '';
		isSavingPrompt.value = false;
	}
};

onMounted(() => {
	void linkReturnedExercise();
	window.addEventListener('keydown', handleDialogKeydown);
});

onBeforeUnmount(() => {
	if (import.meta.client) {
		window.removeEventListener('keydown', handleDialogKeydown);
		document.body.style.overflow = '';
	}
});

watch(showHistoryDialog, (isOpen) => {
	if (import.meta.client) {
		document.body.style.overflow = isOpen ? 'hidden' : '';
	}
});
</script>

<template>
	<section class="pageShell">
		<header class="pageHeader">
			<NuxtLink class="backLink" :to="routePathsWithString(routePaths.teacherClassroomOverview, `?classroomId=${classroomId}`)">Tilbake til klasserommet</NuxtLink>
			<p class="eyebrow">Ukens mysterium</p>
			<h1>{{ classroom?.name ?? 'Ukjent klasserom' }}</h1>
			<p class="subtitle">Publiser ukens oppdrag, samle elevforslag og gjør ett av forslagene om til et spillbart mysterium.</p>
		</header>

		<section class="activeStrip">
			<div class="activeStripBody">
				<div class="activeStripPanel activeStripOppdrag">
					<p class="activeStripEyebrow">Nå aktivt · Oppdrag</p>
					<h2 class="activeStripTitle">{{ currentMystery?.title ?? 'Ingen aktivt oppdrag ennå' }}</h2>
					<p class="activeStripPrompt">
						{{ currentMystery?.prompt ?? 'Publiser et ukesoppdrag først, så kan elevene sende inn forslag.' }}
					</p>
					<div v-if="currentMystery?.clue" class="activeStripTipCard">
						<span class="activeStripTipLabel">Tips til elevene</span>
						<p class="activeStripTipBody">{{ currentMystery.clue }}</p>
					</div>
				</div>

				<div class="activeStripPanel activeStripSpillCard" aria-label="Aktivt spill for elevene">
					<p class="activeStripEyebrow activeStripEyebrowMuted">Aktivt spill</p>

					<template v-if="currentMystery?.exerciseUid && linkedExercise">
						<p class="activeGameTitle">{{ linkedExercise.title }}</p>
						<p v-if="linkedExercise.description" class="activeGameDescription">
							{{ linkedExercise.description }}
						</p>
						<p class="activeStripGameFoot">
							{{ linkedExercise.tasks.length }}
							{{ linkedExercise.tasks.length === 1 ? 'oppgave' : 'oppgaver' }} koblet til elevopplevelsen
						</p>
					</template>
					<p v-else-if="currentMystery?.exerciseUid && !linkedExercise" class="activeStripPlaceholder">
						Spillet er merket som koblet, men innholdet kunne ikke lastes. Prøv å oppdatere siden.
					</p>
					<p v-else-if="currentMystery" class="activeStripPlaceholder">
						Ingen spill koblet ennå. Når du er klar, velg et elevforslag og lag det spillbare mysteriet.
					</p>
					<p v-else class="activeStripPlaceholder">
						Når et oppdrag er aktivt og du har laget spill fra et forslag, vises det her for oversikt.
					</p>
				</div>
			</div>

			<div v-if="editLinkedExerciseHref" class="activeStripActions">
				<NuxtLink class="inlineFactoryLink" :to="editLinkedExerciseHref">
					Rediger aktivt spill
				</NuxtLink>
			</div>
		</section>

		<div class="pageGrid">
			<section class="card createCard">
				<header class="createCardHeader">
					<div class="createCardHeaderTop">
						<div class="mysteryHeader createCardTitles">
							<h2>Publiser oppdrag</h2>
						</div>
						<button class="secondaryButton createCardPastButton" type="button" @click="showHistoryDialog = true">
							Se tidligere oppdrag
						</button>
					</div>
					<p class="mysteryMeta createCardLead">
						Dette er spørsmålet elevene svarer på med bilde og forklaring. Selve spillet lages etterpå fra et elevforslag.
					</p>
				</header>

				<form class="promptForm" @submit.prevent="createAndActivatePrompt">
					<label class="field">
						<span>Oppdragstittel</span>
						<input v-model="promptDraft.title" type="text" maxlength="100" placeholder="F.eks. Finn det mistenkelige skjermbildet">
					</label>

					<label class="field fieldFull">
						<span>Hva skal elevene sende inn?</span>
						<textarea v-model="promptDraft.prompt" rows="4" placeholder="Hva skal elevene undersøke, forklare eller sende inn?"></textarea>
					</label>

					<label class="field fieldFull">
						<span>Tips</span>
						<textarea v-model="promptDraft.clue" rows="2" placeholder="Gi et lite spor som hjelper elevene i gang."></textarea>
					</label>

					<label class="field">
						<span>Dato</span>
						<input v-model="promptDraft.date" type="date">
					</label>

					<button class="primaryButton" type="submit" :disabled="!canSavePrompt || isSavingPrompt || Boolean(assigningPromptId)">
						{{ isSavingPrompt ? 'Publiserer...' : 'Publiser oppdrag' }}
					</button>
				</form>

				<p v-if="statusMessage" class="successMessage">{{ statusMessage }}</p>
				<p v-if="errorMessage" class="errorMessage">{{ errorMessage }}</p>
			</section>

			<section class="card submissionsCard">
				<div class="mysteryHeader">
					<h2>Elevforslag til spill</h2>
				</div>

				<ul v-if="currentSubmissions && currentSubmissions.length > 0" class="submissionList">
					<li v-for="submission in currentSubmissions" :key="submission.publicUid" class="submissionItem">
						<img :src="submission.fileUrl" :alt="`Innsendt bilde fra ${getStudentName(submission.studentPublicUid)}`" class="submissionImage">

						<div class="submissionContent">
							<p class="weekLabel">{{ getStudentName(submission.studentPublicUid) }}</p>
							<h3>{{ submission.fileName }}</h3>
							<p>{{ submission.message }}</p>
						</div>

						<button class="secondaryButton submissionActionButton" type="button" @click="createGameFromSubmission(submission)">
							Lag spill av forslag
						</button>
					</li>
				</ul>

				<p v-else>Ingen elever har sendt inn forslag til det aktive mysteriet ennå.</p>
			</section>
		</div>

		<div v-if="showHistoryDialog" class="dialogBackdrop" @click.self="showHistoryDialog = false">
			<section class="dialogPanel" role="dialog" aria-modal="true" aria-labelledby="history-dialog-title">
				<header class="dialogHeader">
					<div>
						<p class="weekLabel">Arkiv</p>
						<h2 id="history-dialog-title">Tidligere oppdrag</h2>
						<p class="mysteryMeta">Her kan du hente frem tidligere oppdrag og aktivere dem for klassen igjen.</p>
					</div>

					<button class="dialogClose" type="button" aria-label="Lukk tidligere oppdrag" @click="showHistoryDialog = false">
						×
					</button>
				</header>

				<ul v-if="savedPrompts && savedPrompts.length > 0" class="promptList">
					<li v-for="prompt in savedPrompts" :key="prompt.publicId" class="promptItem">
						<div>
							<p class="weekLabel">{{ prompt.weekLabel || 'Lagret oppdrag' }}</p>
							<h3>{{ prompt.title }}</h3>
							<p>{{ prompt.prompt }}</p>
							<p class="mysteryMeta"><strong>Tips:</strong> {{ prompt.clue }}</p>
						</div>

						<button
							class="secondaryButton"
							type="button"
							:disabled="assigningPromptId === prompt.publicId || currentMystery?.publicId === prompt.publicId"
							@click="assignPrompt(prompt.publicId)"
						>
							{{ currentMystery?.publicId === prompt.publicId ? 'Aktiv nå' : assigningPromptId === prompt.publicId ? 'Aktiverer...' : 'Gjør aktiv' }}
						</button>
					</li>
				</ul>

				<p v-else>Du har ikke publisert noen tidligere oppdrag ennå.</p>

				<div v-if="mysteryHistory && mysteryHistory.length > 0" class="historyBlock">
					<h3>Brukt i klassen</h3>
					<ul class="historyList">
						<li v-for="mystery in mysteryHistory" :key="mystery.publicId" class="historyItem">
							<p class="weekLabel">{{ mystery.weekLabel || 'Tidligere mysterium' }}</p>
							<h4>{{ mystery.title }}</h4>
							<p>{{ mystery.prompt }}</p>
						</li>
					</ul>
				</div>
			</section>
		</div>
	</section>
</template>

<style scoped>
.pageShell {
	width: 100%;
	max-width: 1000px;
	margin: 0 auto;
	padding: var(--space-4);
	color: var(--color-text-primary);
	background: linear-gradient(180deg, rgba(255, 255, 255, 0.78), rgba(255, 255, 255, 0.88)), linear-gradient(180deg, var(--palette-gray-50) 0%, var(--palette-cream-100) 100%);
	border-radius: var(--radius-4);
}

.pageHeader {
	margin-bottom: var(--space-4);
}

.backLink {
	display: inline-block;
	margin-bottom: 0.75rem;
	color: var(--color-brand-primary);
	font-weight: 600;
}

.eyebrow {
	color: var(--color-brand-primary);
	font-weight: 700;
	letter-spacing: 0.08em;
	text-transform: uppercase;
	margin-bottom: 0.5rem;
}

.subtitle {
	max-width: 42rem;
	margin: 0.5rem 0 0;
	color: var(--color-text-secondary);
}

.activeStrip {
	display: grid;
	gap: clamp(0.85rem, 1.5vw, 1.05rem);
	margin-bottom: var(--space-4);
	padding: clamp(1rem, 2vw, 1.35rem);
	border: 1px solid color-mix(in srgb, var(--color-semantic-info) 50%, var(--color-border-default));
	border-radius: var(--radius-3);
	background:
		linear-gradient(90deg, color-mix(in srgb, var(--color-bg-surface) 86%, var(--color-semantic-info) 14%), var(--color-bg-surface));
	box-shadow: var(--shadow-1);
}

.activeStripBody {
	display: grid;
	gap: 1rem;
	min-width: 0;
	grid-template-columns: 1fr;
}

@media (min-width: 700px) {
	.activeStripBody {
		grid-template-columns: minmax(0, 1fr) minmax(0, 1fr);
		align-items: start;
		column-gap: clamp(1rem, 2.5vw, 1.5rem);
	}
}

.activeStripPanel {
	min-width: 0;
	display: grid;
	align-content: start;
	gap: 0.5rem;
}

.activeStripEyebrow {
	margin: 0;
	font-size: 0.75rem;
	font-weight: 700;
	letter-spacing: 0.06em;
	text-transform: uppercase;
	color: var(--color-brand-primary);
	overflow-wrap: normal;
	word-break: normal;
}

.activeStripEyebrowMuted {
	color: var(--color-text-secondary);
}

.activeStripTitle {
	margin: 0;
	font-size: clamp(1.15rem, 2.2vw, 1.35rem);
	line-height: 1.25;
}

.activeStripPrompt {
	margin: 0;
	color: var(--color-text-secondary);
	line-height: 1.45;
}

.activeStripTipCard {
	margin-top: 0.35rem;
	padding: 0.65rem 0.75rem;
	border-radius: var(--radius-2);
	border: 1px solid var(--color-border-default);
	background: transparent;
}

.activeStripTipLabel {
	display: block;
	font-size: 0.7rem;
	font-weight: 800;
	text-transform: uppercase;
	letter-spacing: 0.05em;
	color: var(--color-text-secondary);
	margin-bottom: 0.35rem;
}

.activeStripTipBody {
	margin: 0;
	color: var(--color-text-primary);
	line-height: 1.45;
	font-size: 0.95rem;
}

.activeStripSpillCard {
	padding: clamp(0.75rem, 1.5vw, 1rem);
	border-radius: var(--radius-3);
	border: 1px solid color-mix(in srgb, var(--color-brand-primary) 18%, var(--color-border-default));
	background: color-mix(in srgb, var(--color-bg-surface) 88%, var(--color-semantic-info) 12%);
	box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.65);
	gap: 0.55rem;
}

.activeStripGameFoot {
	margin: 0.15rem 0 0;
	font-size: 0.9rem;
	color: var(--color-text-secondary);
}

.activeStripPlaceholder {
	margin: 0;
	font-size: 0.92rem;
	color: var(--color-text-secondary);
	line-height: 1.45;
}

.activeStripActions {
	display: flex;
	flex-wrap: wrap;
	gap: 0.75rem;
	align-items: center;
	justify-content: flex-end;
	padding-top: 0.85rem;
	margin-top: 0.35rem;
	border-top: 1px solid color-mix(in srgb, var(--color-border-default) 85%, transparent);
}

.activeStrip .activeGameTitle {
	margin: 0;
	font-weight: 800;
	font-size: 1.05rem;
	line-height: 1.3;
	color: var(--color-text-primary);
}

.activeStrip .activeGameDescription {
	margin: 0;
	font-size: 0.92rem;
	color: var(--color-text-secondary);
	line-height: 1.45;
	white-space: pre-wrap;
	overflow-wrap: break-word;
	word-break: normal;
}

.activeStrip h2,
.activeStrip > p {
	margin-top: 0;
}

.activeGameTitle {
	font-weight: 800;
}

.activeGameDescription {
	white-space: pre-wrap;
	overflow-wrap: break-word;
	word-break: normal;
}

.inlineFactoryLink {
	color: var(--color-brand-primary);
	font-weight: 700;
	text-decoration: none;
	white-space: nowrap;
}

.inlineFactoryLink:hover {
	text-decoration: underline;
}

.pageGrid {
	display: grid;
	grid-template-columns: minmax(0, 1.15fr) minmax(18rem, 0.85fr);
	gap: clamp(1rem, 2vw, 1.25rem);
	align-items: start;
}

.card {
	padding: 1rem;
	border: 1px solid var(--color-border-default);
	border-radius: var(--radius-3);
	background: var(--color-bg-surface);
	box-shadow: var(--shadow-1);
}

.createCard {
	background: var(--color-bg-surface);
}

.createCardHeader {
	margin-bottom: 0.75rem;
}

.createCardHeaderTop {
	display: flex;
	flex-wrap: wrap;
	align-items: flex-start;
	justify-content: space-between;
	gap: 0.65rem 1rem;
}

.createCardTitles {
	flex: 1 1 min(14rem, 100%);
	min-width: 0;
	margin-bottom: 0;
}

.createCardTitles h2 {
	margin: 0;
}

.createCardPastButton {
	flex-shrink: 0;
	align-self: flex-start;
}

.createCardLead {
	margin-top: 0.75rem;
	margin-bottom: 0;
}

.submissionsCard {
	background: color-mix(in srgb, var(--palette-gray-50) 72%, var(--color-bg-surface));
}

.mysteryHeader {
	margin-bottom: 0.75rem;
}

.weekLabel {
	margin: 0 0 0.5rem;
	font-size: 0.9rem;
	font-weight: 700;
	color: var(--color-brand-primary);
	text-transform: uppercase;
	overflow-wrap: normal;
	word-break: normal;
}

.mysteryMeta {
	margin-top: 0.75rem;
	color: var(--color-text-secondary);
}

.promptForm {
	display: grid;
	grid-template-columns: minmax(0, 1fr) minmax(10rem, 0.8fr);
	gap: 0.9rem;
}

.field {
	display: grid;
	gap: 0.4rem;
	font-weight: 700;
}

.fieldFull {
	grid-column: 1 / -1;
}

.field input,
.field textarea {
	width: 100%;
	border: 1px solid var(--color-border-default);
	border-radius: var(--radius-2);
	padding: 0.75rem 0.85rem;
	background: color-mix(in srgb, var(--color-bg-surface) 96%, var(--color-brand-primary) 4%);
	color: var(--color-text-primary);
	font: inherit;
}

.field textarea {
	resize: vertical;
}

.field input:focus,
.field textarea:focus {
	outline: 3px solid color-mix(in srgb, var(--color-semantic-info) 40%, transparent);
	border-color: var(--color-brand-primary);
}

.primaryButton,
.secondaryButton {
	display: inline-flex;
	align-items: center;
	justify-content: center;
	border: 1px solid var(--color-brand-primary);
	border-radius: var(--radius-2);
	padding: 0.75rem 1rem;
	font-weight: 800;
	cursor: pointer;
}

.primaryButton {
	align-self: end;
	background: var(--color-brand-primary);
	color: var(--color-text-onDark);
}

.secondaryButton {
	background: color-mix(in srgb, var(--color-bg-surface) 88%, var(--color-semantic-info) 12%);
	color: var(--color-brand-primary);
	white-space: nowrap;
}

.primaryButton:disabled,
.secondaryButton:disabled {
	cursor: not-allowed;
	opacity: 0.58;
}

.successMessage,
.errorMessage {
	margin: 1rem 0 0;
	font-weight: 700;
}

.successMessage {
	color: var(--color-semantic-success);
}

.errorMessage {
	color: var(--color-semantic-danger);
}

.promptList {
	display: grid;
	gap: 0.9rem;
	padding: 0;
	margin: 0;
	list-style: none;
}

.promptItem {
	display: flex;
	gap: 1rem;
	align-items: flex-start;
	justify-content: space-between;
	padding-top: 0.9rem;
	border-top: 1px solid var(--color-border-subtle);
}

.promptItem:first-child {
	padding-top: 0;
	border-top: none;
}

.submissionList {
	display: grid;
	gap: 1rem;
	padding: 0;
	margin: 0;
	list-style: none;
}

.submissionItem {
	display: grid;
	grid-template-columns: 8rem minmax(0, 1fr);
	grid-template-areas:
		"submissionImage submissionContent"
		"submissionAction submissionAction";
	column-gap: 1rem;
	row-gap: 0.75rem;
	align-items: start;
	padding-top: 1rem;
	border-top: 1px solid var(--color-border-subtle);
}

.submissionItem:first-child {
	padding-top: 0;
	border-top: none;
}

.submissionImage {
	grid-area: submissionImage;
	width: 8rem;
	aspect-ratio: 4 / 3;
	border: 1px solid var(--color-border-default);
	border-radius: var(--radius-2);
	background: color-mix(in srgb, var(--color-bg-surface) 90%, var(--color-brand-primary) 10%);
	object-fit: cover;
}

.submissionContent {
	grid-area: submissionContent;
	min-width: 0;
	overflow-wrap: break-word;
}

.submissionContent h3 {
	margin: 0 0 0.45rem;
	overflow-wrap: break-word;
}

.submissionContent p {
	word-break: normal;
	overflow-wrap: break-word;
	line-height: 1.45;
}

.submissionActionButton {
	grid-area: submissionAction;
	justify-self: end;
	width: fit-content;
	max-width: 100%;
}

.historyList {
	display: grid;
	gap: 0.9rem;
	padding: 0;
	margin: 0;
	list-style: none;
}

.historyItem {
	padding-top: 0.9rem;
	border-top: 1px solid var(--color-border-subtle);
}

.historyItem:first-child {
	padding-top: 0;
	border-top: none;
}

.dialogBackdrop {
	position: fixed;
	inset: 0;
	z-index: 40;
	display: grid;
	place-items: center;
	padding: clamp(1rem, 4vw, 2rem);
	background: rgba(15, 23, 42, 0.48);
}

.dialogPanel {
	width: min(44rem, 100%);
	max-height: min(42rem, 90vh);
	overflow: auto;
	padding: clamp(1rem, 2.5vw, 1.5rem);
	border: 1px solid var(--color-border-default);
	border-radius: var(--radius-4);
	background: var(--color-bg-surface);
	box-shadow: var(--shadow-2);
}

.dialogHeader {
	display: flex;
	justify-content: space-between;
	gap: var(--space-4);
	margin-bottom: var(--space-4);
}

.dialogHeader h2,
.dialogHeader p {
	margin-top: 0;
}

.dialogClose {
	display: inline-flex;
	align-items: center;
	justify-content: center;
	width: 2.5rem;
	height: 2.5rem;
	flex: 0 0 auto;
	border: 1px solid var(--color-border-default);
	border-radius: 999px;
	background: color-mix(in srgb, var(--color-bg-surface) 90%, var(--color-brand-primary) 10%);
	color: var(--color-brand-primary);
	font: inherit;
	font-size: 1.5rem;
	font-weight: 800;
	line-height: 1;
	cursor: pointer;
}

.historyBlock {
	margin-top: var(--space-5);
	padding-top: var(--space-4);
	border-top: 1px solid var(--color-border-default);
}

.historyBlock h3 {
	margin-top: 0;
}

@media (max-width: 1100px) {
	.pageGrid {
		grid-template-columns: 1fr;
	}
}

@media (max-width: 720px) {
	.pageGrid {
		grid-template-columns: 1fr;
	}

	.activeStripActions {
		justify-content: flex-start;
	}

	.promptForm {
		grid-template-columns: 1fr;
	}

	.promptItem {
		display: grid;
	}

	.submissionItem {
		grid-template-columns: 1fr;
		grid-template-areas:
			"submissionImage"
			"submissionContent"
			"submissionAction";
	}

	.submissionImage {
		width: 100%;
		max-height: 16rem;
	}
}
</style>
