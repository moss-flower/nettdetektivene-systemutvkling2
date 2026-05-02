<script setup lang="ts">
import type { XOrYTaskData } from "~/composables/types/TaskTypes";
import { useFactoryImageUpload } from "~/composables/useFactoryImageUpload";

const props = defineProps<{
	task: XOrYTaskData;
}>();

const { uploadImage } = useFactoryImageUpload();
const xUploadState = ref<"idle" | "uploading">("idle");
const yUploadState = ref<"idle" | "uploading">("idle");
const xUploadError = ref("");
const yUploadError = ref("");

const uploadOptionImage = async (choice: "x" | "y", event: Event) => {
	const input = event.target as HTMLInputElement | null;
	const file = input?.files?.[0];

	if (!file) {
		return;
	}

	if (choice === "x") {
		xUploadState.value = "uploading";
		xUploadError.value = "";
	} else {
		yUploadState.value = "uploading";
		yUploadError.value = "";
	}

	try {
		const uploaded = await uploadImage(file, "uploads/factory/tasks");
		props.task.options[choice].imageUrl = uploaded.url;
	} catch (error) {
		const message = error instanceof Error ? error.message : "Bildet kunne ikke lastes opp.";
		if (choice === "x") {
			xUploadError.value = message;
		} else {
			yUploadError.value = message;
		}
	} finally {
		if (choice === "x") {
			xUploadState.value = "idle";
		} else {
			yUploadState.value = "idle";
		}
		if (input) {
			input.value = "";
		}
	}
};
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
					<span>Bilde</span>
					<input type="file" accept="image/*" :disabled="xUploadState === 'uploading'" @change="uploadOptionImage('x', $event)" >
					<small class="fieldHint">{{ xUploadState === "uploading" ? "Laster opp..." : "Velg et bilde for alternativ X." }}</small>
					<p v-if="xUploadError" class="fieldError">{{ xUploadError }}</p>
				</label>
				<label class="field">
					<span>Bildetekst</span>
					<input v-model="props.task.options.x.imageAlt" type="text" >
				</label>
				<div class="imagePreviewCard">
					<img :src="props.task.options.x.imageUrl" :alt="props.task.options.x.imageAlt || 'Forhandsvisning av alternativ X'" class="imagePreview" >
				</div>
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
					<span>Bilde</span>
					<input type="file" accept="image/*" :disabled="yUploadState === 'uploading'" @change="uploadOptionImage('y', $event)" >
					<small class="fieldHint">{{ yUploadState === "uploading" ? "Laster opp..." : "Velg et bilde for alternativ Y." }}</small>
					<p v-if="yUploadError" class="fieldError">{{ yUploadError }}</p>
				</label>
				<label class="field">
					<span>Bildetekst</span>
					<input v-model="props.task.options.y.imageAlt" type="text" >
				</label>
				<div class="imagePreviewCard">
					<img :src="props.task.options.y.imageUrl" :alt="props.task.options.y.imageAlt || 'Forhandsvisning av alternativ Y'" class="imagePreview" >
				</div>
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

.fieldHint {
	color: var(--muted);
	font-size: 0.9rem;
}

.fieldError {
	margin: 0;
	color: #fca5a5;
	font-size: 0.9rem;
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

.imagePreviewCard {
	padding: 0.75rem;
	border: var(--border-width-hairline) solid var(--color-border-default);
	border-radius: var(--radius-2);
	background: rgba(255, 255, 255, 0.02);
}

.imagePreview {
	display: block;
	width: 100%;
	max-height: 14rem;
	object-fit: cover;
	border-radius: 0.75rem;
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
