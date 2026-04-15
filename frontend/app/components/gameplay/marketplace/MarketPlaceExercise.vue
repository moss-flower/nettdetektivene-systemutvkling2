<script setup lang="ts">
import type { MarketplaceTask, MarketplaceChoice, MarketplaceListing } from '~/composables/types/TaskTypes'

const props = defineProps<{
  listing: MarketplaceListing
}>()

const emit = defineEmits<{
  (e: 'answered', correct: boolean): void
}>()

function answer(choice: MarketplaceChoice) {
  const correct = choice === props.listing.expectedChoice
  emit('answered', correct)
}
</script>

<template>
  <div class="marketplace-singleton">
    <img
      :src="props.listing.imageUrl"
      alt="Product"
    />
    <div class="actions">
      <button @click="answer('buy')">Kjøp</button>
      <button @click="answer('reviewMore')">Undersøk mer</button>
      <button @click="answer('dontBuy')">Ikke kjøp</button>
    </div>
  </div>
</template>