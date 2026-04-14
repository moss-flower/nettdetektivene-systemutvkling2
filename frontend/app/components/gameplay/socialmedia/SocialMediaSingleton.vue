<template>
  <div class="socialmedia-singleton">
    <img :src="data.imageurl" alt="Social media post" />
    <div class="actions">
      <button @click="answer(SocialMediaChoices.Share)">Del</button>
      <button @click="answer(SocialMediaChoices.Hold)">Vent</button>
      <button @click="answer(SocialMediaChoices.CheckSources)">Sjekk kilder</button>
      <button @click="answer(SocialMediaChoices.AskAdult)">Spør en voksen</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { TaskSocialMediaDTO, SocialMediaChoices } from '~/composables/types/TaskTypes'

const props = defineProps<{
  data: TaskSocialMediaDTO
}>()

const emit = defineEmits<{
  (e: 'answered', correct: boolean): void
}>()

function answer(choice: SocialMediaChoices) {
  const correct = choice === props.data.choice
  emit('answered', correct)
}
</script>
