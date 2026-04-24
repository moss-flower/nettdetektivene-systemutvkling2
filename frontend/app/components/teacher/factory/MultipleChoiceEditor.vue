<script setup lang="ts">
import type { MultipleChoiceTaskData } from "~/composables/types/TaskTypes";

const props = defineProps<{
	task: MultipleChoiceTaskData;
}>();

const emit = defineEmits<{
	(e: "add-option"): void;
	(e: "remove-option", optionIndex: number): void;
}>();
</script>

<template>
	<div class="templateSection">
		<h4>Flervalg</h4>
		<div class="fieldGrid">
			<label class="field">
				<span>Prompt-tittel</span>
				<input v-model="props.task.prompt.title" type="text" >
			</label>

			<label class="field fieldFull">
				<span>Prompt-tekst</span>
				<textarea v-model="props.task.prompt.text" rows="2"></textarea>
			</label>

			<label class="field">
				<span>Bilde-URL</span>
				<input v-model="props.task.prompt.imageUrl" type="url" >
			</label>

			<label class="field">
				<span>Bildetekst</span>
				<input v-model="props.task.prompt.imageAlt" type="text" >
			</label>

			<label class="field">
				<span>Riktig svar</span>
				<select v-model="props.task.correctChoice">
					<option v-for="option in props.task.options" :key="option.key" :value="option.key">
						{{ option.key.toUpperCase() }}
					</option>
				</select>
			</label>
		</div>

		<div class="optionList">
			<div
				v-for="(option, optionIndex) in props.task.options"
				:key="`${props.task.id}-${option.key}`"
				class="optionCard"
			>
				<label class="field optionField">
					<span>Alternativ {{ option.key.toUpperCase() }}</span>
					<input v-model="option.title" type="text" >
				</label>
				<button
					type="button"
					class="deleteButton"
					:disabled="props.task.options.length <= 2"
					@click="emit('remove-option', optionIndex)"
				>
					Fjern
				</button>
			</div>
		</div>

		<button type="button" class="ghostButton" @click="emit('add-option')">
			+ Legg til svaralternativ
		</button>
	</div>
</template>

<style scoped>
.templateSection {
	margin-top: var(--space-4);
	padding-top: var(--space-4);
	border-top: var(--border-width-hairline) solid var(--color-border-default);
}

.templateSection h4 {
	margin: 0 0 0.8rem;
	font-weight: 700;
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

.fieldFull {
	grid-column: 1 / -1;
}

.field span {
	font-weight: 700;
}

.field input,
.field select,
.field textarea {
	width: 100%;
	padding: 0.85rem var(--space-4);
	border: var(--border-width-hairline) solid var(--color-border-default);
	border-radius: 0.9rem;
	background: rgba(0, 0, 0, 0.16);
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
	border-color: var(--color-brand-primary);
}

.optionList {
	display: grid;
	gap: var(--space-3);
	margin-top: 0.9rem;
	margin-bottom: 0.9rem;
}

.optionCard {
	display: flex;
	align-items: flex-start;
	justify-content: space-between;
	gap: var(--space-3);
	padding: 0.85rem var(--space-4);
	border: var(--border-width-hairline) solid var(--color-border-default);
	border-radius: var(--radius-2);
	background: rgba(255, 255, 255, 0.02);
}

.optionField {
	flex: 1;
}

.ghostButton,
.deleteButton {
	border: var(--border-width-hairline) solid var(--color-border-default);
	border-radius: var(--radius-pill);
	padding: 0.7rem var(--space-4);
	font-weight: 700;
	transition: transform var(--transition), border-color var(--transition), background var(--transition);
}

.ghostButton {
	background: transparent;
	color: var(--color-text-primary);
}

.deleteButton {
	background: transparent;
	color: #fca5a5;
}

.ghostButton:hover,
.deleteButton:hover {
	transform: translateY(-1px);
}

.ghostButton:disabled,
.deleteButton:disabled {
	opacity: 0.5;
	cursor: not-allowed;
	transform: none;
}

@media (max-width: 800px) {
	.fieldGrid {
		grid-template-columns: 1fr;
	}

	.optionCard {
		flex-direction: column;
		align-items: flex-start;
	}
}
</style>
