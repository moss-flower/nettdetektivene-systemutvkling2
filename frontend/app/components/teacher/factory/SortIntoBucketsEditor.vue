<script setup lang="ts">
import { createDraftId } from "~/composables/useExerciseFactory";

import type { SortIntoBucketsTaskData } from "~/composables/types/TaskTypes";

const props = defineProps<{
	task: SortIntoBucketsTaskData;
}>();

const addBucket = () => {
	const nextIndex = props.task.buckets.length + 1;
	const nextKey = `bucket-${nextIndex}`;

	props.task.buckets.push({
		key: nextKey,
		title: `Kategori ${nextIndex}`,
		description: "",
	});

	if (props.task.items.length > 0 && !props.task.items.some((item) => item.correctBucketKey === nextKey)) {
		props.task.items[props.task.items.length - 1]!.correctBucketKey = nextKey;
	}
};

const removeBucket = (bucketIndex: number) => {
	if (props.task.buckets.length <= 2) {
		return;
	}

	const removedBucket = props.task.buckets[bucketIndex];
	props.task.buckets.splice(bucketIndex, 1);

	const fallbackBucketKey = props.task.buckets[0]?.key ?? "";

	props.task.items.forEach((item) => {
		if (item.correctBucketKey === removedBucket?.key) {
			item.correctBucketKey = fallbackBucketKey;
		}
	});
};

const addItem = () => {
	props.task.items.push({
		id: createDraftId("sortable-item"),
		label: `Kort ${props.task.items.length + 1}`,
		correctBucketKey: props.task.buckets[0]?.key ?? "",
		explanation: "",
	});
};

const removeItem = (itemIndex: number) => {
	if (props.task.items.length <= 2) {
		return;
	}

	props.task.items.splice(itemIndex, 1);
};

const hasBucketColor = (bucketIndex: number) => Boolean(props.task.buckets[bucketIndex]?.backgroundColor);

const setBucketColorEnabled = (bucketIndex: number, enabled: boolean) => {
	const bucket = props.task.buckets[bucketIndex];

	if (!bucket) {
		return;
	}

	bucket.backgroundColor = enabled ? bucket.backgroundColor ?? "#edf9f0" : undefined;
};
</script>

<template>
	<div class="templateSection">
		<h4>Sorter i bokser</h4>
		<div class="fieldGrid">
			<label class="field">
				<span>Prompt-tittel</span>
				<input v-model="props.task.prompt!.title" type="text" >
			</label>

			<label class="field field--full">
				<span>Prompt-tekst</span>
				<textarea v-model="props.task.prompt!.text" rows="2"></textarea>
			</label>

			<label class="field field--full">
				<span>Etikett for usorterte kort</span>
				<input v-model="props.task.unplacedLabel" type="text" >
			</label>
		</div>

		<div class="sectionRow">
			<div>
				<h5>Bokser</h5>
				<p>Disse blir kategoriene eleven skal sortere kortene inn i.</p>
			</div>
			<button type="button" class="ghostButton" @click="addBucket">
				+ Legg til boks
			</button>
		</div>

		<div class="stackList">
			<article v-for="(bucket, bucketIndex) in props.task.buckets" :key="bucket.key" class="configCard">
				<div class="configCardHeader">
					<h5>{{ bucket.title || `Boks ${bucketIndex + 1}` }}</h5>
					<button
						type="button"
						class="deleteButton"
						:disabled="props.task.buckets.length <= 2"
						@click="removeBucket(bucketIndex)"
					>
						Fjern
					</button>
				</div>

				<div class="fieldGrid">
					<label class="field">
						<span>Nokkel</span>
						<input v-model="bucket.key" type="text" >
					</label>

					<label class="field">
						<span>Tittel</span>
						<input v-model="bucket.title" type="text" >
					</label>

					<label class="field field--full">
						<span>Beskrivelse</span>
						<textarea v-model="bucket.description" rows="2"></textarea>
					</label>

					<div class="colorField field--full">
						<label class="toggleRow">
							<input
								:checked="hasBucketColor(bucketIndex)"
								type="checkbox"
								@change="setBucketColorEnabled(bucketIndex, ($event.target as HTMLInputElement).checked)"
							>
							<span>Bruk bakgrunnsfarge</span>
						</label>

						<div v-if="hasBucketColor(bucketIndex)" class="colorPickerRow">
							<input v-model="bucket.backgroundColor" class="colorPicker" type="color" >
						</div>
					</div>
				</div>
			</article>
		</div>

		<div class="sectionRow sectionRow--top">
			<div>
				<h5>Kort</h5>
				<p>Hvert kort trenger en riktig boks.</p>
			</div>
			<button type="button" class="ghostButton" @click="addItem">
				+ Legg til kort
			</button>
		</div>

		<div class="stackList">
			<article v-for="(item, itemIndex) in props.task.items" :key="item.id" class="configCard">
				<div class="configCardHeader">
					<h5>{{ item.label || `Kort ${itemIndex + 1}` }}</h5>
					<button
						type="button"
						class="deleteButton"
						:disabled="props.task.items.length <= 2"
						@click="removeItem(itemIndex)"
					>
						Fjern
					</button>
				</div>

				<div class="fieldGrid">
					<label class="field">
						<span>Etikett</span>
						<input v-model="item.label" type="text" >
					</label>

					<label class="field">
						<span>Riktig boks</span>
						<select v-model="item.correctBucketKey">
							<option v-for="bucket in props.task.buckets" :key="bucket.key" :value="bucket.key">
								{{ bucket.title || bucket.key }}
							</option>
						</select>
					</label>

					<label class="field field--full">
						<span>Forklaring</span>
						<textarea v-model="item.explanation" rows="2"></textarea>
					</label>
				</div>
			</article>
		</div>
	</div>
</template>

<style scoped>
.templateSection {
	margin-top: 1rem;
	padding-top: 1rem;
	border-top: 1px solid var(--border);
}

.templateSection h4,
.sectionRow h5,
.configCardHeader h5 {
	margin: 0;
	font-weight: 700;
}

.fieldGrid {
	display: grid;
	grid-template-columns: repeat(2, minmax(0, 1fr));
	gap: 0.9rem;
}

.field,
.colorField {
	display: grid;
	gap: 0.45rem;
}

.field--full {
	grid-column: 1 / -1;
}

.field span,
.toggleRow span {
	font-weight: 700;
}

.field input,
.field select,
.field textarea {
	width: 100%;
	padding: 0.85rem 1rem;
	border: 1px solid var(--border);
	border-radius: 0.9rem;
	background: rgba(0, 0, 0, 0.16);
	color: var(--text);
	font: inherit;
}

.field textarea {
	resize: vertical;
}

.field input:focus,
.field select:focus,
.field textarea:focus {
	outline: none;
	border-color: var(--primary);
}

.sectionRow,
.configCardHeader {
	display: flex;
	justify-content: space-between;
	gap: 0.75rem;
	align-items: center;
	margin-top: 1rem;
	margin-bottom: 0.9rem;
}

.sectionRow p {
	margin: 0.3rem 0 0;
	color: var(--muted);
	line-height: 1.5;
}

.sectionRow--top {
	margin-top: 1.25rem;
}

.stackList {
	display: grid;
	gap: 0.75rem;
}

.configCard {
	padding: 1rem;
	border: 1px solid var(--border);
	border-radius: 1rem;
	background: rgba(255, 255, 255, 0.02);
}

.toggleRow {
	display: inline-flex;
	align-items: center;
	gap: 0.6rem;
}

.colorPickerRow {
	display: flex;
}

.colorPicker {
	width: 3.25rem;
	height: 3rem;
	padding: 0;
	border: 1px solid var(--border);
	border-radius: 0.75rem;
	background: transparent;
	overflow: hidden;
}

.ghostButton,
.deleteButton {
	border: 1px solid var(--border);
	border-radius: 999px;
	padding: 0.7rem 1rem;
	font-weight: 700;
	transition: transform var(--transition), border-color var(--transition), background var(--transition);
}

.ghostButton {
	background: transparent;
	color: var(--text);
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

	.sectionRow,
	.configCardHeader {
		flex-direction: column;
		align-items: flex-start;
	}
}
</style>
