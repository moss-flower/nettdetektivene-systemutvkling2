<script setup lang="ts">
import draggable from 'vuedraggable'

import type {
  SortBucket,
  SortIntoBucketsResult,
  SortableItem,
} from '~/composables/types/TaskTypes'

const props = defineProps<{
  prompt?: {
    title?: string
    text?: string
  }
  items: SortableItem[]
  buckets: SortBucket[]
  unplacedLabel?: string
  disabled?: boolean
}>()

const emit = defineEmits<{
  (e: 'answered', payload: SortIntoBucketsResult): void
}>()

const getBucketStyle = (bucket: SortBucket) => ({
  ...(bucket.backgroundColor ? { backgroundColor: bucket.backgroundColor } : {}),
})

const selectedItemId = ref<string | null>(null)
const unplacedItems = ref<SortableItem[]>([])
const bucketItems = ref<Record<string, SortableItem[]>>({})

const resetState = () => {
  unplacedItems.value = [...props.items]
  bucketItems.value = Object.fromEntries(props.buckets.map((bucket) => [bucket.key, []]))
  selectedItemId.value = null
}

const allItemsPlaced = computed(() =>
  unplacedItems.value.length === 0 && props.buckets.every((bucket) => (bucketItems.value[bucket.key] ?? []).length > 0),
)

const locateListByItemId = (itemId: string) => {
  const unplacedMatch = unplacedItems.value.find((item) => item.id === itemId)

  if (unplacedMatch) {
    return unplacedItems.value
  }

  return Object.values(bucketItems.value).find((items) => items.some((item) => item.id === itemId)) ?? null
}

const moveSelectedTo = (bucketKey: string | null) => {
  if (!selectedItemId.value || props.disabled) {
    return
  }

  const currentList = locateListByItemId(selectedItemId.value)

  if (!currentList) {
    selectedItemId.value = null
    return
  }

  const itemIndex = currentList.findIndex((item) => item.id === selectedItemId.value)

  if (itemIndex === -1) {
    selectedItemId.value = null
    return
  }

  const [item] = currentList.splice(itemIndex, 1)

  if (!item) {
    selectedItemId.value = null
    return
  }

  if (bucketKey === null) {
    unplacedItems.value.push(item)
  } else {
    bucketItems.value[bucketKey]?.push(item)
  }

  selectedItemId.value = null
}

const selectItem = (itemId: string) => {
  if (props.disabled) {
    return
  }

  selectedItemId.value = selectedItemId.value === itemId ? null : itemId
}

const submit = () => {
  if (!allItemsPlaced.value || props.disabled) {
    return
  }

  const placements = Object.fromEntries(
    props.buckets.map((bucket) => [bucket.key, (bucketItems.value[bucket.key] ?? []).map((item) => item.id)]),
  )

  const correct = props.items.every((item) => {
    const targetItems = bucketItems.value[item.correctBucketKey] ?? []
    return targetItems.some((bucketItem) => bucketItem.id === item.id)
  })

  emit('answered', {
    correct,
    placements,
  })
}

watch(
  () => [props.items, props.buckets],
  () => resetState(),
  { immediate: true },
)
</script>

<template>
  <section class="sortTemplate">
    <header v-if="props.prompt?.title || props.prompt?.text" class="sortTemplateHeader">
      <h2 v-if="props.prompt?.title" class="sortTemplateTitle">
        {{ props.prompt.title }}
      </h2>
      <p v-if="props.prompt?.text" class="sortTemplateText">
        {{ props.prompt.text }}
      </p>
    </header>

    <div class="sortTemplateWorkspace">
      <section class="sortTemplateTray">
        <div class="templateSectionHead">
          <div>
            <h3>{{ props.unplacedLabel ?? 'Elementer som skal sorteres' }}</h3>
            <p>Dra kortene, eller trykk pa et kort og plasser det i riktig boks.</p>
          </div>

          <button
            v-if="selectedItemId"
            class="templateGhostButton"
            type="button"
            :disabled="props.disabled"
            @click="moveSelectedTo(null)"
          >
            Flytt valgt tilbake
          </button>
        </div>

        <draggable
          v-model="unplacedItems"
          class="templateItemList"
          item-key="id"
          :group="'sortBuckets'"
          :disabled="props.disabled"
        >
          <template #item="{ element }">
            <button
              class="sortTemplateItem"
              :class="{ 'templateItemSelected': selectedItemId === element.id }"
              type="button"
              :disabled="props.disabled"
              @click="selectItem(element.id)"
            >
              {{ element.label }}
            </button>
          </template>
        </draggable>
      </section>

      <div class="bucketGrid">
        <article
          v-for="bucket in props.buckets"
          :key="bucket.key"
          class="bucketCard"
          :style="getBucketStyle(bucket)"
        >
          <div class="templateSectionHead bucketHead">
            <div>
              <h3>{{ bucket.title }}</h3>
              <p>{{ bucket.description }}</p>
            </div>

            <button
              v-if="selectedItemId"
              class="ghostButton"
              type="button"
              :disabled="props.disabled"
              @click="moveSelectedTo(bucket.key)"
            >
              Plasser valgt her
            </button>
          </div>

          <draggable
            v-model="bucketItems[bucket.key]"
            class="bucketDropzone"
            item-key="id"
            :group="'sortBuckets'"
            :disabled="props.disabled"
          >
            <template #item="{ element }">
              <button
                class="sortTemplateItem"
                :class="{ itemSelected: selectedItemId === element.id }"
                type="button"
                :disabled="props.disabled"
                @click="selectItem(element.id)"
              >
                {{ element.label }}
              </button>
            </template>

            <template #footer>
              <p v-if="(bucketItems[bucket.key] ?? []).length === 0" class="emptyState">
                Slipp kort her
              </p>
            </template>
          </draggable>
        </article>
      </div>
    </div>

    <div class="sortTemplateActions">
      <button
        class="sortTemplateSubmit"
        type="button"
        :disabled="props.disabled || !allItemsPlaced"
        @click="submit"
      >
        Sjekk svar
      </button>
    </div>
  </section>
</template>

<style scoped>
.sortTemplate {
  /* header */
  --sb-title: var(--palette-cork-900);
  --sb-muted: var(--palette-cork-580);

  /* tray & bucket card surfaces */
  --sb-card-bg: var(--palette-cream-100);
  --sb-card-border: var(--palette-overlay-cork-medium);
  --sb-card-shadow: var(--palette-overlay-dark-soft);
  --sb-card-text: var(--palette-cork-900);

  /* section head text */
  --sb-section-title: var(--palette-cork-900);
  --sb-section-muted: var(--palette-cork-580);

  /* draggable item pill, idle */
  --sb-item-bg: var(--palette-white);
  --sb-item-border: var(--palette-overlay-cork-medium);
  --sb-item-text: var(--palette-cork-900);
  --sb-item-shadow: var(--palette-overlay-dark-soft);

  /* item pill, hover */
  --sb-item-border-hover: var(--palette-navy-200);
  --sb-item-shadow-hover: var(--palette-overlay-dark-medium);

  /* item pill, selected */
  --sb-item-bg-selected: var(--palette-cream-200);
  --sb-item-border-selected: var(--palette-navy-900);

  /* empty dropzone */
  --sb-empty-border: var(--palette-overlay-cork-medium);
  --sb-empty-text: var(--palette-cork-580);

  /* ghost / move button */
  --sb-ghost-bg: var(--palette-cream-200);
  --sb-ghost-text: var(--palette-navy-700);

  /* submit button */
  --sb-submit-bg: var(--palette-navy-900);
  --sb-submit-text: var(--palette-cream-100);

  /* disabled */
  --sb-disabled-opacity: 0.6;

  display: grid;
  gap: var(--space-4);
  color: var(--sb-title);
}

.sortTemplateHeader {
  display: grid;
  gap: var(--space-1);
}

.sortTemplateWorkspace {
  display: grid;
  /* bucket column min, deliberate exception */
  grid-template-columns: minmax(0, 1.1fr) minmax(18rem, 0.9fr);
  align-items: stretch;
  gap: var(--space-4);
}

.sortTemplateTitle {
  /* fluid title scale, deliberate exception */
  font-size: clamp(1.35rem, 2.5vw, 2rem);
  line-height: 1.1;
  color: var(--sb-title);
}

.sortTemplateText {
  max-width: 56ch;
  font-size: var(--font-size-16);
  line-height: var(--font-leading-16);
  color: var(--sb-muted);
}

.sortTemplateTray,
.bucketCard {
  padding: var(--space-4);
  border-radius: var(--radius-3);
  border: var(--border-width-thin) solid var(--sb-card-border);
  background: var(--sb-card-bg);
  box-shadow: 0 var(--space-3) var(--space-5) var(--sb-card-shadow);
}

.sortTemplateTray {
  display: flex;
  flex-direction: column;
  min-height: 100%;
}

.templateSectionHead {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: var(--space-4);
  margin-bottom: var(--space-2);
}

.templateSectionHead h3 {
  margin: 0;
  font-size: var(--font-size-16);
  color: var(--sb-section-title);
}

.templateSectionHead p {
  margin: var(--space-1) 0 0;
  color: var(--sb-section-muted);
  line-height: var(--font-leading-14);
}

.bucketHead {
  align-items: center;
}

.templateItemList,
.bucketDropzone {
  /* min drop target height, deliberate exception */
  min-height: 6rem;
  display: grid;
  gap: var(--space-3);
}

.templateItemList {
  flex: 1;
  align-content: start;
}

.bucketGrid {
  display: grid;
  gap: var(--space-4);
}

.sortTemplateItem {
  width: 100%;
  padding: var(--space-2) var(--space-4);
  border: var(--border-width-thin) solid var(--sb-item-border);
  border-radius: var(--radius-2);
  background: var(--sb-item-bg);
  color: var(--sb-item-text);
  font-weight: var(--font-weight-bold);
  text-align: left;
  box-shadow: 0 var(--space-2) var(--space-3) var(--sb-item-shadow);
  transition: var(--transition-base);
}

.sortTemplateItem:hover:not(:disabled),
.sortTemplateItem:focus-visible {
  transform: translateY(calc(var(--border-width-hairline) * -1));
  border-color: var(--sb-item-border-hover);
  box-shadow: 0 var(--space-3) var(--space-4) var(--sb-item-shadow-hover);
  outline: none;
}

.templateItemSelected,
.itemSelected {
  border-color: var(--sb-item-border-selected);
  background: var(--sb-item-bg-selected);
}

.emptyState {
  margin: 0;
  padding: var(--space-4);
  border: var(--border-width-thin) dashed var(--sb-empty-border);
  border-radius: var(--radius-2);
  color: var(--sb-empty-text);
  text-align: center;
}

.templateGhostButton,
.ghostButton,
.sortTemplateSubmit {
  padding: var(--space-2) var(--space-4);
  border: 0;
  border-radius: var(--radius-pill);
  font-weight: var(--font-weight-bold);
}

.templateGhostButton,
.ghostButton {
  background: var(--sb-ghost-bg);
  color: var(--sb-ghost-text);
}

.sortTemplateSubmit {
  background: var(--sb-submit-bg);
  color: var(--sb-submit-text);
}

.sortTemplateActions {
  display: flex;
  justify-content: flex-end;
}

.templateGhostButton:disabled,
.ghostButton:disabled,
.sortTemplateSubmit:disabled,
.sortTemplateItem:disabled {
  cursor: default;
  opacity: var(--sb-disabled-opacity);
}

</style>
