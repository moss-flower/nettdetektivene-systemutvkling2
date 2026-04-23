<script setup lang="ts">
import { World } from "~/composables/types/WorldTypes";
import { routePaths } from "~/composables/utils/routePaths";

const route = useRoute();
const router = useRouter();

const draft = reactive({
	name: "",
	worldUid: "",
});

const submittedPayload = ref<null | { name: string; worldUid: string }>(null);

const { data: availableWorlds } = await useAsyncData("available-worlds", () => World.backendFetchAllWorlds());
const lockedWorldUid = computed(() => {
	const uid = route.query.uid;

	return typeof uid === "string" && uid.trim().length > 0 ? uid.trim() : "";
});
const showWorldSelector = computed(() => lockedWorldUid.value.length === 0);
const selectedWorldUid = computed(() => lockedWorldUid.value || draft.worldUid);
const selectedWorld = computed(() => {
	const nextUid = selectedWorldUid.value.trim().toLowerCase();

	return (availableWorlds.value ?? []).find((world) => world.uid.trim().toLowerCase() === nextUid) ?? null;
});
const canCreate = computed(() => draft.name.trim().length > 0 && selectedWorld.value !== null);

const goBack = () => {
	if (import.meta.client && window.history.length > 1) {
		router.back();
		return;
	}

	void navigateTo(routePaths.teacherOverview);
};

const goToWorldFactory = () => {
	void navigateTo(routePaths.teacherFactoryWorld);
};

const createClassroom = () => {
	if (!canCreate.value || !selectedWorld.value) {
		return;
	}

	submittedPayload.value = {
		name: draft.name.trim(),
		worldUid: selectedWorld.value.uid,
	};
	console.log("Create classroom payload:", submittedPayload.value);
};

watch(
	lockedWorldUid,
	(uid) => {
		if (uid) {
			draft.worldUid = "";
		}
	},
	{ immediate: true },
);
</script>

<template>
	<section class="classroomCreatePage">
		<header class="pageHeader">
			<button class="backButton" type="button" @click="goBack">Tilbake</button>
			<h1>Lag klasserom</h1>
			<p class="subtitle">
				Velg hvilken verden klasserommet skal bruke, og klargjor navnet som vises til elevene.
			</p>
		</header>

		<form class="pageGrid" @submit.prevent="createClassroom">
			<div class="editorPanel">
				<section class="panelSection">
					<div class="sectionHeading">
						<h2>Klasserom</h2>
						<p>Dette er en lokal oppretting inntil backend for klasserom er pa plass.</p>
					</div>

					<div class="fieldGrid">
						<label class="field">
							<span>Navn</span>
							<input v-model="draft.name" type="text" placeholder="For eksempel: 6A Vaar 2026" >
						</label>
					</div>
				</section>

				<section class="panelSection">
					<div v-if="showWorldSelector" class="sectionHeading sectionHeading--inline">
						<div>
							<h2>Verden</h2>
							<p>Velg verdenen som skal brukes i klasserommet.</p>
						</div>
						<button type="button" class="ghostButton" @click="goToWorldFactory">
							+ Lag verden
						</button>
					</div>

					<div v-if="showWorldSelector" class="fieldGrid">
						<label class="field field--full">
							<span>Velg verden</span>
							<select v-model="draft.worldUid">
								<option value="">Velg verden</option>
								<option v-for="world in availableWorlds ?? []" :key="world.uid" :value="world.uid">
									{{ world.uid }}
								</option>
							</select>
						</label>
					</div>

					<div v-else class="lockedWorldCard">
						<p class="cardEyebrow">Verden</p>
						<h3>{{ selectedWorld?.uid ?? lockedWorldUid }}</h3>
						<p>
							Klasserommet er bundet til verden fra URL-en og kan ikke bytte verden her.
						</p>
					</div>
				</section>

				<div class="actionRow actionRow--save">
					<button type="submit" class="primaryButton" :disabled="!canCreate">
						Lag klasserom
					</button>
				</div>

				<section v-if="submittedPayload" class="panelSection successSection">
					<div class="sectionHeading">
						<h2>Klasserom lagret lokalt</h2>
						<p>Payloaden er klar for backend nar klasserom-oppretting blir koblet opp.</p>
					</div>

					<pre class="payloadPreview">{{ JSON.stringify(submittedPayload, null, 2) }}</pre>
				</section>
			</div>
		</form>
	</section>
</template>

<style scoped>
.classroomCreatePage {
	width: 100%;
	max-width: 1400px;
	margin: 0 auto;
	padding: var(--space-4);
}

.pageHeader {
	margin-bottom: 1rem;
}

.backButton {
	display: inline-flex;
	align-items: center;
	margin-bottom: 0.75rem;
	padding: 0;
	border: 0;
	background: transparent;
	color: var(--primary);
	font: inherit;
	font-weight: 600;
	cursor: pointer;
}

.backButton:hover {
	text-decoration: underline;
}

.subtitle,
.sectionHeading p,
.summaryCard p,
.lockedWorldCard p {
	margin: 0;
	color: var(--muted);
	line-height: 1.5;
}

h1,
h2,
h3 {
	margin: 0;
}

.pageGrid {
	display: grid;
	grid-template-columns: minmax(0, 1fr);
	gap: 1.25rem;
	align-items: start;
}

.editorPanel {
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
}

.sectionHeading {
	display: grid;
	gap: 0.35rem;
	margin-bottom: 1rem;
}

.sectionHeading--inline {
	grid-template-columns: 1fr auto;
	align-items: start;
	gap: 0.75rem;
}

.fieldGrid {
	display: grid;
	grid-template-columns: repeat(2, minmax(0, 1fr));
	gap: 0.9rem;
}

.field {
	display: grid;
	gap: 0.45rem;
}

.field--full {
	grid-column: 1 / -1;
}

.field span,
.cardEyebrow {
	font-weight: 700;
}

.field input,
.field select {
	width: 100%;
	padding: 0.85rem 1rem;
	border: 1px solid var(--border);
	border-radius: 0.9rem;
	background: rgba(0, 0, 0, 0.16);
	color: var(--text);
	font: inherit;
}

.field input:focus,
.field select:focus {
	outline: none;
	border-color: var(--primary);
}

.lockedWorldCard {
	display: grid;
	gap: 0.5rem;
	padding: 1rem;
	border: 1px solid var(--border);
	border-radius: 1rem;
	background: rgba(255, 255, 255, 0.02);
}

.ghostButton,
.primaryButton {
	border: 1px solid var(--border);
	border-radius: 999px;
	padding: 0.75rem 1rem;
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

.ghostButton:hover,
.primaryButton:hover {
	transform: translateY(-1px);
}

.primaryButton:disabled {
	opacity: 0.5;
	cursor: not-allowed;
	transform: none;
}

.actionRow {
	display: flex;
	justify-content: flex-end;
	margin-top: 1.5rem;
}

.actionRow--save {
	margin-top: 0;
}

.successSection {
	opacity: 0.95;
}

.payloadPreview {
	overflow-x: auto;
	padding: 1rem;
	border-radius: 1rem;
	background: rgba(0, 0, 0, 0.24);
	color: #d7f9ea;
	font-size: 0.9rem;
	line-height: 1.5;
	margin: 0;
}

@media (max-width: 900px) {
	.pageGrid {
		grid-template-columns: 1fr;
	}
}

@media (max-width: 800px) {
	.fieldGrid {
		grid-template-columns: 1fr;
	}

	.sectionHeading--inline,
	.actionRow {
		display: grid;
	}

	.actionRow {
		justify-content: stretch;
	}
}
</style>
