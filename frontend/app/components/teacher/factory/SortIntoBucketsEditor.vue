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

			<label class="field fieldFull">
				<span>Prompt-tekst</span>
				<textarea v-model="props.task.prompt!.text" rows="2"></textarea>
			</label>

			<label class="field fieldFull">
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

					<label class="field fieldFull">
						<span>Beskrivelse</span>
						<textarea v-model="bucket.description" rows="2"></textarea>
					</label>

					<div class="colorField fieldFull">
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

		<div class="sectionRow sectionRowTop">
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

					<label class="field fieldFull">
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

.colorField {
   display: grid;
   gap: var(--space-1);
}

.fieldFull {
   grid-column: 1 / -1;
}

.field span,
.toggleRow span,
.sectionRow h5,
.configCardHeader h5 {
   color: var(--factory-label-color);
   font-family: var(--factory-label-font), monospace;
   font-size: var(--factory-label-size);
   font-weight: var(--font-weight-bold);
   letter-spacing: var(--factory-label-tracking);
   text-transform: uppercase;
}

.sectionRow h5,
.configCardHeader h5 {
   margin: 0;
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

.sectionRow,
.configCardHeader {
   display: flex;
   justify-content: space-between;
   gap: var(--space-3);
   align-items: center;
   margin-top: var(--space-4);
   margin-bottom: var(--space-3);
}

.sectionRow p {
   margin: var(--space-1) 0 0;
   color: var(--color-text-secondary);
   line-height: var(--font-leading-base);
}

.sectionRowTop {
   margin-top: var(--space-4);
}

.stackList {
   display: grid;
   gap: var(--space-3);
}

.configCard {
   padding: var(--space-4);
   border: var(--border-width-hairline) solid var(--color-border-default);
   border-radius: var(--radius-2);
   background: var(--color-bg-canvas);
}

.toggleRow {
   display: inline-flex;
   align-items: center;
   gap: var(--space-2);
}

.colorPickerRow {
   display: flex;
}

.colorPicker {
   width: 3.25rem; /* deliberate: fixed color-picker size */
   height: 3rem; /* deliberate: fixed color-picker size */
   padding: 0;
   border: var(--border-width-hairline) solid var(--color-border-default);
   border-radius: var(--radius-1);
   background: transparent;
   overflow: hidden;
}

.colorPicker::-webkit-color-swatch-wrapper {
  padding: 0;
}

.colorPicker::-webkit-color-swatch {
  border: none;
  border-radius: var(--radius-1);
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

   .sectionRow,
   .configCardHeader {
      flex-direction: column;
      align-items: flex-start;
   }
}
</style>
