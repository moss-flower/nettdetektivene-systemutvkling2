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
  display: grid;
  gap: var(--space-4);
  color: var(--palette-gray-900);
}

.sortTemplateHeader {
  display: grid;
  gap: 0.45rem;
}

.sortTemplateWorkspace {
  display: grid;
  grid-template-columns: minmax(0, 1.1fr) minmax(18rem, 0.9fr);
  align-items: stretch;
  gap: var(--space-4);
}

.sortTemplateTitle {
  font-size: clamp(1.35rem, 2.5vw, 2rem);
  line-height: 1.1;
  color: var(--color-text-primary);
}

.sortTemplateText {
  max-width: 56rem;
  line-height: 1.5;
  color: var(--color-text-secondary);
}

.sortTemplateTray,
.bucketCard {
  padding: var(--space-4);
  border-radius: 1.5rem;
  border: var(--border-width-thin) solid var(--color-border-default);
  background: var(--color-bg-surface);
  box-shadow: 0 12px 24px rgba(52, 77, 115, 0.08);
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
  margin-bottom: 0.85rem;
}

.templateSectionHead h3 {
  margin: 0;
  font-size: 1.05rem;
}

.templateSectionHead p {
  margin: var(--space-1) 0 0;
  color: var(--color-text-secondary);
  line-height: 1.45;
}

.bucketHead {
  align-items: center;
}

.templateItemList,
.bucketDropzone {
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
  padding: 0.9rem var(--space-4);
  border: var(--border-width-thin) solid var(--color-border-default);
  border-radius: var(--radius-2);
  background: var(--palette-white);
  color: var(--palette-gray-900);
  font-weight: 700;
  text-align: left;
  box-shadow: 0 8px 14px rgba(71, 94, 128, 0.08);
  transition: transform var(--transition-base) ease, border-color var(--transition-base) ease, box-shadow var(--transition-base) ease;
}

.sortTemplateItem:hover:not(:disabled),
.sortTemplateItem:focus-visible {
  transform: translateY(-1px);
  border-color: #7ea0c9;
  box-shadow: 0 12px 20px rgba(71, 94, 128, 0.14);
  outline: none;
}

.itemSelected {
  border-color: #3568a8;
  background: var(--color-semantic-info);
}

.emptyState {
  margin: 0;
  padding: var(--space-4);
  border: var(--border-width-thin) dashed var(--color-border-default);
  border-radius: var(--radius-2);
  color: var(--color-text-secondary);
  text-align: center;
}

.ghostButton,
.sortTemplateSubmit {
  padding: 0.8rem var(--space-4);
  border: 0;
  border-radius: var(--radius-pill);
  font-weight: 700;
}

.ghostButton {
  background: #e8f0fb;
  color: var(--palette-navy-700);
}

.sortTemplateSubmit {
  background: var(--color-brand-primary);
  color: #f9fbff;
}

.sortTemplateActions {
  display: flex;
  justify-content: flex-end;
}

.ghostButton:disabled,
.sortTemplateSubmit:disabled,
.sortTemplateItem:disabled {
  cursor: default;
  opacity: 0.72;
}

</style>
