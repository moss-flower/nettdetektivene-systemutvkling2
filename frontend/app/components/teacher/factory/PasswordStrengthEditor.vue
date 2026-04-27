<script setup lang="ts">
import type { PasswordStrengthTaskData } from "~/composables/types/TaskTypes";

const props = defineProps<{
	task: PasswordStrengthTaskData;
}>();

const commonPatternsText = computed({
	get: () => (props.task.commonPatterns ?? []).join(", "),
	set: (value: string) => {
		props.task.commonPatterns = value
			.split(",")
			.map((pattern) => pattern.trim())
			.filter(Boolean);
	},
});
</script>

<template>
	<div class="templateSection">
		<h4>Passordstyrke</h4>
		<div class="fieldGrid">
			<label class="field">
				<span>Prompt-tittel</span>
				<input v-model="props.task.prompt!.title" type="text" >
			</label>

			<label class="field">
				<span>Modus</span>
				<select v-model="props.task.mode">
					<option value="create">Lag nytt passord</option>
					<option value="improve">Forbedre passord</option>
				</select>
			</label>

			<label class="field fieldFull">
				<span>Prompt-tekst</span>
				<textarea v-model="props.task.prompt!.text" rows="2"></textarea>
			</label>

			<label class="field fieldFull">
				<span>Hjelpetekst</span>
				<textarea v-model="props.task.prompt!.helperText" rows="2"></textarea>
			</label>

			<label class="field">
				<span>Placeholder</span>
				<input v-model="props.task.placeholder" type="text" >
			</label>

			<label class="field">
				<span>Minste lengde</span>
				<input v-model.number="props.task.minLength" type="number" min="1" >
			</label>

			<label class="field fieldFull">
				<span>Startpassord</span>
				<input v-model="props.task.startingPassword" type="text" placeholder="La sta tomt hvis oppgaven ikke trenger startpassord" >
			</label>

			<label class="field fieldFull">
				<span>Vanlige monstre</span>
				<input v-model="commonPatternsText" type="text" placeholder="123, qwerty, passord" >
			</label>
		</div>
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

@media (max-width: 800px) {
	.fieldGrid {
		grid-template-columns: 1fr;
	}
}
</style>
