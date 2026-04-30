<script setup lang="ts">
import type { WeeklyMysteryPrompt } from '~/composables/types/WeeklyMysteryTypes';
import { routePaths, routePathsWithString } from '~/composables/utils/routePaths';

const route = useRoute();
const { teacher } = useClassrooms();
const { $api } = useNuxtApp();

const classroomId = computed(() => String(route.query.classroomId ?? ''));

const { data: classroom } = await useAsyncData(
	'teacher-classroom-mystery-classroom',
	() => (classroomId.value ? teacher.getClassroomById(classroomId.value).catch(() => null) : null),
	{ watch: [classroomId] },
);

const { data: currentMystery } = await useAsyncData<WeeklyMysteryPrompt | null>(
	'teacher-classroom-mystery-current',
	() => (classroomId.value ? $api(`/classrooms/${classroomId.value}/weekly-mystery`, { method: 'GET' }).catch(() => null) : null),
	{ watch: [classroomId] },
);

const { data: mysteryHistory } = await useAsyncData<WeeklyMysteryPrompt[]>(
	'teacher-classroom-mystery-history',
	() => (classroomId.value ? $api(`/classrooms/${classroomId.value}/weekly-mystery/history`, { method: 'GET' }).catch(() => []) : []),
	{ watch: [classroomId] },
);
</script>

<template>
	<section class="pageShell">
		<header class="pageHeader">
			<NuxtLink class="backLink" :to="routePathsWithString(routePaths.teacherClassroomOverview, `?classroomId=${classroomId}`)">Tilbake til klasserommet</NuxtLink>
			<p class="eyebrow">Ukens mysterium</p>
			<h1>{{ classroom?.name ?? 'Ukjent klasserom' }}</h1>
		</header>

		<div class="pageGrid">
			<section class="card">
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
</style>
