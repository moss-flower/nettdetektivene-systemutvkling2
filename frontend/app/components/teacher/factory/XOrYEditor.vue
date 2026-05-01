<script setup lang="ts">
import type { XOrYTaskData } from "~/composables/types/TaskTypes";

const props = defineProps<{
	task: XOrYTaskData;
}>();
</script>

<template>
	<div class="templateSection">
		<h4>X eller Y</h4>
		<div class="fieldGrid">
			<label class="field">
				<span>Prompt-tittel</span>
				<input v-model="props.task.prompt!.title" type="text" >
			</label>

			<label class="field fieldFull">
				<span>Prompt-tekst</span>
				<textarea v-model="props.task.prompt!.text" rows="2"></textarea>
			</label>

			<label class="field">
				<span>Riktig valg</span>
				<select v-model="props.task.correctChoice">
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
					<input v-model="props.task.options.x.eyebrow" type="text" >
				</label>
				<label class="field">
					<span>Tittel</span>
					<input v-model="props.task.options.x.title" type="text" >
				</label>
				<label class="field">
					<span>Tekst</span>
					<textarea v-model="props.task.options.x.text" rows="3"></textarea>
				</label>
				<label class="field">
					<span>Bilde-URL</span>
					<input v-model="props.task.options.x.imageUrl" type="url" >
				</label>
				<label class="field">
					<span>Bildetekst</span>
					<input v-model="props.task.options.x.imageAlt" type="text" >
				</label>
			</article>

			<article class="splitCard">
				<h5>Alternativ Y</h5>
				<label class="field">
					<span>Eyebrow</span>
					<input v-model="props.task.options.y.eyebrow" type="text" >
				</label>
				<label class="field">
					<span>Tittel</span>
					<input v-model="props.task.options.y.title" type="text" >
				</label>
				<label class="field">
					<span>Tekst</span>
					<textarea v-model="props.task.options.y.text" rows="3"></textarea>
				</label>
				<label class="field">
					<span>Bilde-URL</span>
					<input v-model="props.task.options.y.imageUrl" type="url" >
				</label>
				<label class="field">
					<span>Bildetekst</span>
					<input v-model="props.task.options.y.imageAlt" type="text" >
				</label>
			</article>
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
   margin: 0 0 var(--space-3);
}

.fieldGrid {
   display: grid;
   grid-template-columns: repeat(2, minmax(0, 1fr));
   gap: var(--space-3);
}

.splitGrid {
   display: grid;
   grid-template-columns: repeat(2, minmax(0, 1fr));
   gap: var(--space-3);
   margin-top: var(--space-3);
}

.field {
   display: grid;
   gap: var(--space-1);
}

.fieldFull {
   grid-column: 1 / -1;
}

.field span {
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

.splitCard {
   display: grid;
   gap: var(--space-3);
   padding: var(--space-4);
   border: var(--border-width-hairline) solid var(--color-border-default);
   border-radius: var(--radius-2);
   background: var(--color-bg-canvas);
}

.splitCard h5 {
   margin: 0;
   color: var(--factory-label-color);
   font-family: var(--factory-label-font), monospace;
   font-size: var(--factory-label-size);
   font-weight: var(--font-weight-bold);
   letter-spacing: var(--factory-label-tracking);
   text-transform: uppercase;
}

@media (max-width: 800px) { /* deliberate: matches factory editor mobile breakpoint */
   .fieldGrid {
      grid-template-columns: 1fr;
   }

   .splitGrid {
      grid-template-columns: 1fr;
   }
}
</style>