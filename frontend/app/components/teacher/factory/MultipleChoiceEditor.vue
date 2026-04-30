<script setup lang="ts">
import type { MultipleChoiceTaskData } from "~/composables/types/TaskTypes";
import { useFactoryImageUpload } from "~/composables/useFactoryImageUpload";

const props = defineProps<{
	task: MultipleChoiceTaskData;
}>();

const emit = defineEmits<{
	(e: "add-option"): void;
	(e: "remove-option", optionIndex: number): void;
}>();

const { uploadImage } = useFactoryImageUpload();
const imageUploadState = ref<"idle" | "uploading">("idle");
const imageUploadError = ref("");

const handleImageSelected = async (event: Event) => {
	const input = event.target as HTMLInputElement | null;
	const file = input?.files?.[0];

	if (!file) {
		return;
	}

	imageUploadState.value = "uploading";
	imageUploadError.value = "";

	try {
		const uploaded = await uploadImage(file, "uploads/factory/tasks");
		props.task.prompt.imageUrl = uploaded.url;
	} catch (error) {
		imageUploadError.value = error instanceof Error ? error.message : "Bildet kunne ikke lastes opp.";
	} finally {
		imageUploadState.value = "idle";
		if (input) {
			input.value = "";
		}
	}
};
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
				<span>Bilde</span>
				<input type="file" accept="image/*" :disabled="imageUploadState === 'uploading'" @change="handleImageSelected" >
				<small class="fieldHint">{{ imageUploadState === "uploading" ? "Laster opp..." : "Velg et bilde som lastes opp til prosjektets MinIO-lager." }}</small>
				<p v-if="imageUploadError" class="fieldError">{{ imageUploadError }}</p>
			</label>

			<label class="field">
				<span>Bildetekst</span>
				<input v-model="props.task.prompt.imageAlt" type="text" >
			</label>

			<div class="imagePreviewCard fieldFull">
				<img :src="props.task.prompt.imageUrl" :alt="props.task.prompt.imageAlt || 'Forhandsvisning av oppgavebilde'" class="imagePreview" >
			</div>

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

.imagePreviewCard {
	padding: 0.85rem;
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
	transition: transform var(--transition-base), border-color var(--transition-base), background var(--transition-base);
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
