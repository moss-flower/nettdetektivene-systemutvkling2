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
  <section class="sort-template">
    <header v-if="props.prompt?.title || props.prompt?.text" class="sort-template__header">
      <h2 v-if="props.prompt?.title" class="sort-template__title">
        {{ props.prompt.title }}
      </h2>
      <p v-if="props.prompt?.text" class="sort-template__text">
        {{ props.prompt.text }}
      </p>
    </header>

    <section class="sort-template__tray">
      <div class="sort-template__section-head">
        <div>
          <h3>{{ props.unplacedLabel ?? 'Elementer som skal sorteres' }}</h3>
          <p>Dra kortene, eller trykk pa et kort og plasser det i riktig boks.</p>
        </div>

        <button
          v-if="selectedItemId"
          class="sort-template__ghost-button"
          type="button"
          :disabled="props.disabled"
          @click="moveSelectedTo(null)"
        >
          Flytt valgt tilbake
        </button>
      </div>

      <draggable
        v-model="unplacedItems"
        class="sort-template__item-list"
        item-key="id"
        :group="'sort-buckets'"
        :disabled="props.disabled"
      >
        <template #item="{ element }">
          <button
            class="sort-template__item"
            :class="{ 'sort-template__item--selected': selectedItemId === element.id }"
            type="button"
            :disabled="props.disabled"
            @click="selectItem(element.id)"
          >
            {{ element.label }}
          </button>
        </template>
      </draggable>
    </section>

    <div class="sort-template__bucket-grid">
      <article v-for="bucket in props.buckets" :key="bucket.key" class="sort-template__bucket-card">
        <div class="sort-template__section-head sort-template__section-head--bucket">
          <div>
            <h3>{{ bucket.title }}</h3>
            <p>{{ bucket.description }}</p>
          </div>

          <button
            v-if="selectedItemId"
            class="sort-template__ghost-button"
            type="button"
            :disabled="props.disabled"
            @click="moveSelectedTo(bucket.key)"
          >
            Plasser valgt her
          </button>
        </div>

        <draggable
          v-model="bucketItems[bucket.key]"
          class="sort-template__bucket-dropzone"
          item-key="id"
          :group="'sort-buckets'"
          :disabled="props.disabled"
        >
          <template #item="{ element }">
            <button
              class="sort-template__item"
              :class="{ 'sort-template__item--selected': selectedItemId === element.id }"
              type="button"
              :disabled="props.disabled"
              @click="selectItem(element.id)"
            >
              {{ element.label }}
            </button>
          </template>

          <template #footer>
            <p v-if="(bucketItems[bucket.key] ?? []).length === 0" class="sort-template__empty-state">
              Slipp kort her
            </p>
          </template>
        </draggable>
      </article>
    </div>

    <div class="sort-template__actions">
      <button
        class="sort-template__submit"
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
.sort-template {
  display: grid;
  gap: 1rem;
  color: #1f2937;
}

.sort-template__header {
  display: grid;
  gap: 0.45rem;
}

.sort-template__title {
  font-size: clamp(1.35rem, 2.5vw, 2rem);
  line-height: 1.1;
  color: #172033;
}

.sort-template__text {
  max-width: 56rem;
  line-height: 1.5;
  color: #4b5563;
}

.sort-template__tray,
.sort-template__bucket-card {
  padding: 1rem;
  border-radius: 1.5rem;
  border: 2px solid #d7dfeb;
  background: #fbfdff;
  box-shadow: 0 12px 24px rgba(52, 77, 115, 0.08);
}

.sort-template__section-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 1rem;
  margin-bottom: 0.85rem;
}

.sort-template__section-head h3 {
  margin: 0;
  font-size: 1.05rem;
}

.sort-template__section-head p {
  margin: 0.25rem 0 0;
  color: #5b6576;
  line-height: 1.45;
}

.sort-template__section-head--bucket {
  align-items: center;
}

.sort-template__item-list,
.sort-template__bucket-dropzone {
  min-height: 6rem;
  display: grid;
  gap: 0.75rem;
}

.sort-template__bucket-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 1rem;
}

.sort-template__item {
  width: 100%;
  padding: 0.9rem 1rem;
  border: 2px solid #cfd9e6;
  border-radius: 1rem;
  background: #fff;
  color: #1f2937;
  font-weight: 700;
  text-align: left;
  box-shadow: 0 8px 14px rgba(71, 94, 128, 0.08);
  transition: transform 0.2s ease, border-color 0.2s ease, box-shadow 0.2s ease;
}

.sort-template__item:hover:not(:disabled),
.sort-template__item:focus-visible {
  transform: translateY(-1px);
  border-color: #7ea0c9;
  box-shadow: 0 12px 20px rgba(71, 94, 128, 0.14);
  outline: none;
}

.sort-template__item--selected {
  border-color: #3568a8;
  background: #eef5ff;
}

.sort-template__empty-state {
  margin: 0;
  padding: 1rem;
  border: 2px dashed #c7d3e3;
  border-radius: 1rem;
  color: #64748b;
  text-align: center;
}

.sort-template__ghost-button,
.sort-template__submit {
  padding: 0.8rem 1rem;
  border: 0;
  border-radius: 999px;
  font-weight: 700;
}

.sort-template__ghost-button {
  background: #e8f0fb;
  color: #27456f;
}

.sort-template__submit {
  background: #1f3657;
  color: #f9fbff;
}

.sort-template__actions {
  display: flex;
  justify-content: flex-end;
}

.sort-template__ghost-button:disabled,
.sort-template__submit:disabled,
.sort-template__item:disabled {
  cursor: default;
  opacity: 0.72;
}

@media (max-width: 760px) {
  .sort-template__bucket-grid {
    grid-template-columns: 1fr;
  }

  .sort-template__section-head,
  .sort-template__section-head--bucket {
    flex-direction: column;
    align-items: stretch;
  }

  .sort-template__actions {
    justify-content: stretch;
  }

  .sort-template__submit,
  .sort-template__ghost-button {
    width: 100%;
  }
}
</style>
