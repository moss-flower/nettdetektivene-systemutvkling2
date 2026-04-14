<template>
  <div class="image-recognition-singleton">
    <img :src="data.imageurl" alt="Image" />
    <div class="actions">
      <button @click="answer(ImageRecognitionType.Real)">Ekte</button>
      <button @click="answer(ImageRecognitionType.AI)">AI-generert</button>
      <button @click="answer(ImageRecognitionType.Manipulated)">Manipulert</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { TaskImageRecognitionSingleton, ImageRecognitionType } from '~/composables/types/TaskTypes'

const props = defineProps<{
  data: TaskImageRecognitionSingleton
}>()

const emit = defineEmits<{
  (e: 'answered', correct: boolean): void
}>()

function answer(choice: ImageRecognitionType) {
  const correct = choice === props.data.type
  emit('answered', correct)
}
</script>
