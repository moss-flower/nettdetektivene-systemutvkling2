<script setup lang="ts">
import XOrYExercise from '~/components/gameplay/shared/XOrYExercise.vue'
import type { NewsStandPair, XOrYChoice, XOrYOptionContent, XOrYPair, XOrYResult } from '~/composables/types/TaskTypes'

const props = defineProps<{
  data: NewsStandPair
  disabled?: boolean
  selectedChoice?: 'correct' | 'fake' | null
}>()

const emit = defineEmits<{
  (e: 'answered', payload: { correct: boolean, choice: 'correct' | 'fake' }): void
}>()

const mappedPair = computed<XOrYPair<XOrYOptionContent>>(() => ({
  x: {
    imageUrl: props.data.correct.imageUrl,
    imageAlt: `Bilde til artikkelen ${props.data.correct.title}`,
    eyebrow: props.data.correct.author,
    title: props.data.correct.title,
    text: props.data.correct.text,
  },
  y: {
    imageUrl: props.data.fake.imageUrl,
    imageAlt: `Bilde til artikkelen ${props.data.fake.title}`,
    eyebrow: props.data.fake.author,
    title: props.data.fake.title,
    text: props.data.fake.text,
  },
}))

const mappedSelectedChoice = computed<XOrYChoice | null>(() => {
  if (props.selectedChoice === 'correct') {
    return 'x'
  }

  if (props.selectedChoice === 'fake') {
    return 'y'
  }

  return null
})

const onAnswered = (result: XOrYResult) => {
  emit('answered', {
    correct: result.correct,
    choice: result.choice === 'x' ? 'correct' : 'fake',
  })
}
</script>

<template>
  <XOrYExercise
    :options="mappedPair"
    :correct-choice="'x'"
    :disabled="props.disabled"
    :selected-choice="mappedSelectedChoice"
    @answered="onAnswered"
  />
</template>