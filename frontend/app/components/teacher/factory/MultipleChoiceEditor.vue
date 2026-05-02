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
   margin: 0 0 var(--space-3);
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

.fieldFull {
   grid-column: 1 / -1;
}

.fieldLabel {
   color: var(--factory-label-color);
   font-family: var(--factory-label-font), monospace;
   font-size: var(--factory-label-size);
   font-weight: var(--font-weight-bold);
   letter-spacing: var(--factory-label-tracking);
   text-transform: uppercase;
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

.optionList {
  margin: var(--space-2);
   display: grid;
   gap: var(--space-3);
}

.optionCard {
   display: flex;
   align-items: flex-end;
   justify-content: space-between;
   gap: var(--space-3);
   padding: var(--space-3) var(--space-4);
   border: var(--border-width-hairline) solid var(--color-border-default);
   border-radius: var(--radius-2);
   background: var(--color-bg-canvas);
}

.optionField {
   flex: 1;
}

.ghostButton,
.deleteButton {
   border: var(--border-width-hairline) solid var(--color-border-default);
   border-radius: var(--radius-1);
   padding: var(--space-3) var(--space-4);
   font-weight: var(--font-weight-bold);
   transition: transform var(--transition-base), border-color var(--transition-base), background var(--transition-base);
}

.ghostButton,
.deleteButton {
   background: transparent;
   color: var(--color-text-primary);
}

.ghostButton {
   background: var(--color-bg-surface);
}

.deleteButton {
   border-color: var(--factory-delete-border);
   background: var(--factory-delete-bg);
   color: var(--factory-delete-text);
}

.ghostButton:hover,
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
.deleteButton:disabled {
   opacity: 0.5;
   cursor: not-allowed;
   transform: none;
}

@media (max-width: 800px) { /* deliberate: matches factory editor mobile breakpoint */
   .fieldGrid {
      grid-template-columns: 1fr;
   }

   .optionCard {
      flex-direction: column;
      align-items: flex-start;
   }
}
</style>
