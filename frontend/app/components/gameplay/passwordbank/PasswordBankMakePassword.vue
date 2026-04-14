<template>
  <div class="password-make">
    <p>{{ data.description }}</p>
    <input v-model="password" type="text" placeholder="Lag et sterkt passord..." />
    <button @click="submit">Send inn</button>
  </div>
</template>

<script setup lang="ts">
import { TaskPasswordBankMakePasswordSingleton } from '~/composables/types/TaskTypes'

const props = defineProps<{
  data: TaskPasswordBankMakePasswordSingleton
}>()

const emit = defineEmits<{
  (e: 'answered', correct: boolean): void
}>()

const password = ref('')

function submit() {
  // TODO: add real validation logic
  emit('answered', true)
}
</script>
