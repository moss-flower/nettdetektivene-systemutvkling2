<script setup lang="ts">
import type { WeeklyMysteryPrompt } from '~/composables/types/WeeklyMysteryTypes';
import { routePaths, routePathsWithString } from '~/composables/utils/routePaths';

const route = useRoute();
const { teacher } = useClassrooms();
const { $api } = useNuxtApp();

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
	]);
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
}
</style>
