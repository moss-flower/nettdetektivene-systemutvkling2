<script setup lang="ts">
import type { WeeklyMysteryPrompt, WeeklyMysterySubmissionResponse } from '~/composables/types/WeeklyMysteryTypes';
import type { MultipleChoiceTaskData } from '~/composables/types/TaskTypes';
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
	]);
};

const getStudentName = (studentPublicUid: string) => {
	return classroom.value?.members.find((member) => member.publicUuid === studentPublicUid)?.username ?? 'Ukjent elev';
};

const buildReturnTo = () => {
	const query = new URLSearchParams();
	query.set('classroomId', classroomId.value);

	if (currentMysteryId.value) {
		query.set('linkPromptId', currentMysteryId.value);
	}

	return `${routePaths.teacherClassroomMystery}?${query.toString()}`;
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
		statusMessage.value = 'Mysteriet er aktivt for klasserommet.';
	} catch {
		errorMessage.value = 'Kunne ikke aktivere mysteriet. Prøv igjen.';
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
		statusMessage.value = 'Nytt mysterium er laget og aktivert.';
	} catch {
		errorMessage.value = 'Kunne ikke lage mysteriet. Sjekk feltene og prøv igjen.';
	} finally {
		assigningPromptId.value = '';
		isSavingPrompt.value = false;
	}
};

onMounted(() => {
	void linkReturnedExercise();
});
</script>

<template>
	<section class="pageShell">
		<header class="pageHeader">
			<NuxtLink class="backLink" :to="routePathsWithString(routePaths.teacherClassroomOverview, `?classroomId=${classroomId}`)">Tilbake til klasserommet</NuxtLink>
			<p class="eyebrow">Ukens mysterium</p>
			<h1>{{ classroom?.name ?? 'Ukjent klasserom' }}</h1>
			<p class="subtitle">Lag en sak elevene kan svare på med bilde og forklaring, eller aktiver en sak du allerede har laget.</p>
		</header>

		<div class="pageGrid">
			<section class="card activeCard">
				<div class="mysteryHeader">
					<h2>Aktivt mysterium</h2>
				</div>

				<template v-if="currentMystery">
					<p class="weekLabel">{{ currentMystery.weekLabel || 'Ukens mysterium' }}</p>
					<h3>{{ currentMystery.title }}</h3>
					<p>{{ currentMystery.prompt }}</p>
					<p class="mysteryMeta"><strong>Tips:</strong> {{ currentMystery.clue }}</p>
					<p class="mysteryMeta">
						<strong>Spill:</strong>
						{{ currentMystery.exerciseUid ? 'Klart for elevene' : 'Ikke laget ennå' }}
					</p>
				</template>

				<p v-else>Det er ikke valgt noe aktivt mysterium for dette klasserommet ennå.</p>
			</section>

			<section class="card createCard">
				<div class="mysteryHeader">
					<p class="weekLabel">Ny sak</p>
					<h2>Lag og aktiver mysterium</h2>
					<p class="mysteryMeta">Når du lagrer, blir mysteriet aktivt for dette klasserommet med en gang.</p>
				</div>

				<form class="promptForm" @submit.prevent="createAndActivatePrompt">
					<label class="field">
						<span>Tittel</span>
						<input v-model="promptDraft.title" type="text" maxlength="100" placeholder="F.eks. Det mystiske skjermbildet">
					</label>

					<label class="field fieldFull">
						<span>Oppdrag til elevene</span>
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
						{{ isSavingPrompt ? 'Lagrer...' : 'Lag og aktiver' }}
					</button>
				</form>

				<p v-if="statusMessage" class="successMessage">{{ statusMessage }}</p>
				<p v-if="errorMessage" class="errorMessage">{{ errorMessage }}</p>
			</section>

			<section class="card submissionsCard">
				<div class="mysteryHeader">
					<h2>Elevforslag</h2>
					<p class="mysteryMeta">Forslagene elevene har sendt inn til det aktive mysteriet. Bruk et forslag som startpunkt for det spillbare mysteriet.</p>
				</div>

				<ul v-if="currentSubmissions && currentSubmissions.length > 0" class="submissionList">
					<li v-for="submission in currentSubmissions" :key="submission.publicUid" class="submissionItem">
						<img :src="submission.fileUrl" :alt="`Innsendt bilde fra ${getStudentName(submission.studentPublicUid)}`" class="submissionImage">

						<div class="submissionContent">
							<p class="weekLabel">{{ getStudentName(submission.studentPublicUid) }}</p>
							<h3>{{ submission.fileName }}</h3>
							<p>{{ submission.message }}</p>
						</div>

						<button class="secondaryButton" type="button" @click="createGameFromSubmission(submission)">
							Lag spill av forslag
						</button>
					</li>
				</ul>

				<p v-else>Ingen elever har sendt inn forslag til det aktive mysteriet ennå.</p>
			</section>

			<section class="card">
				<div class="mysteryHeader">
					<h2>Dine mysterier</h2>
					<p class="mysteryMeta">Velg en tidligere laget sak som aktivt mysterium for klassen.</p>
				</div>

				<ul v-if="savedPrompts && savedPrompts.length > 0" class="promptList">
					<li v-for="prompt in savedPrompts" :key="prompt.publicId" class="promptItem">
						<div>
							<p class="weekLabel">{{ prompt.weekLabel || 'Lagret mysterium' }}</p>
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

				<p v-else>Du har ikke laget noen mysterier ennå.</p>
			</section>

			<section class="card">
				<div class="mysteryHeader">
					<h2>Tidligere mysterier</h2>
				</div>

				<ul v-if="mysteryHistory && mysteryHistory.length > 0" class="historyList">
					<li v-for="mystery in mysteryHistory" :key="mystery.publicId" class="historyItem">
						<p class="weekLabel">{{ mystery.weekLabel || 'Tidligere mysterium' }}</p>
						<h3>{{ mystery.title }}</h3>
						<p>{{ mystery.prompt }}</p>
					</li>
				</ul>

				<p v-else>Ingen tidligere mysterier er registrert for dette klasserommet.</p>
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

.pageGrid {
	display: grid;
	gap: 1rem;
}

.card {
	padding: 1rem;
	border: 1px solid var(--color-border-default);
	border-radius: var(--radius-3);
	background: var(--color-bg-surface);
	box-shadow: var(--shadow-1);
}

.activeCard {
	border-color: color-mix(in srgb, var(--color-semantic-info) 60%, var(--color-border-default));
	background: color-mix(in srgb, var(--color-bg-surface) 90%, var(--color-semantic-info) 10%);
}

.createCard {
	background:
		radial-gradient(circle at top right, rgba(245, 202, 46, 0.2), transparent 30%),
		var(--color-bg-surface);
}

.submissionsCard {
	background:
		linear-gradient(90deg, color-mix(in srgb, var(--color-bg-surface) 94%, var(--color-semantic-success) 6%), var(--color-bg-surface));
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
}

.mysteryMeta {
	margin-top: 0.75rem;
	color: var(--color-text-secondary);
}

.promptForm {
	display: grid;
	grid-template-columns: repeat(2, minmax(0, 1fr));
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
	grid-template-columns: 8rem minmax(0, 1fr) auto;
	gap: 1rem;
	align-items: start;
	padding-top: 1rem;
	border-top: 1px solid var(--color-border-subtle);
}

.submissionItem:first-child {
	padding-top: 0;
	border-top: none;
}

.submissionImage {
	width: 8rem;
	aspect-ratio: 4 / 3;
	border: 1px solid var(--color-border-default);
	border-radius: var(--radius-2);
	background: color-mix(in srgb, var(--color-bg-surface) 90%, var(--color-brand-primary) 10%);
	object-fit: cover;
}

.submissionContent {
	min-width: 0;
}

.submissionContent h3 {
	margin: 0 0 0.45rem;
}

.submissionContent p {
	overflow-wrap: anywhere;
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

@media (max-width: 720px) {
	.promptForm {
		grid-template-columns: 1fr;
	}

	.promptItem {
		display: grid;
	}

	.submissionItem {
		grid-template-columns: 1fr;
	}

	.submissionImage {
		width: 100%;
		max-height: 16rem;
	}
}
</style>
