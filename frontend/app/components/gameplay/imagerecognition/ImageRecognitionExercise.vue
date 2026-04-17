<script setup lang="ts">
import type { ImageRecognitionItem, ImageRecognitionType } from '~/composables/types/TaskTypes'

const props = defineProps<{
  images: ImageRecognitionItem
}>()

const emit = defineEmits<{
  (e: 'answered', correct: boolean): void
}>()

function answer(choice: ImageRecognitionType) {
  const correct = choice === props.images.type
  emit('answered', correct)
}
</script>

<template>
  <div class="image-recognition">
    <img :src="'placehold.co/600x400?text=Product+2'" alt="Image" />
    <div class="actions">
      <button @click="answer('real')">Ekte</button>
      <button @click="answer('AI')">AI-generert</button>
      <button @click="answer('manipulated')">Manipulert</button>
    </div>
  </div>
</template>


