<template>
  <div class="marketplace-singleton">
    <img :src="data.imageurl" alt="Product" />
    <div class="actions">
      <button @click="answer(MarketplaceChoices.Buy)">Kjøp</button>
      <button @click="answer(MarketplaceChoices.ReviewMore)">Undersøk mer</button>
      <button @click="answer(MarketplaceChoices.DontBuy)">Ikke kjøp</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { TaskMarketplaceSingleton, MarketplaceChoices } from '~/composables/types/TaskTypes'

const props = defineProps<{
  data: TaskMarketplaceSingleton
}>()

const emit = defineEmits<{
  (e: 'answered', correct: boolean): void
}>()

function answer(choice: MarketplaceChoices) {
  const correct = choice === props.data.type
  emit('answered', correct)
}
</script>
