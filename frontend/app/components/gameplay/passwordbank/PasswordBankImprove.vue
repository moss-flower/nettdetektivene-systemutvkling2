<template>
  <div class="password-improve">
    <p>{{ data.description }}</p>
    <p class="original">Opprinnelig passord: <strong>{{ data.passwordStart }}</strong></p>
    <input v-model="improved" type="text" placeholder="Skriv et bedre passord..." />
    <button @click="submit">Send inn</button>
  </div>
</template>

<script setup lang="ts">
import { TaskPasswordBankImproveSingleton } from '~/composables/types/TaskTypes'

const props = defineProps<{
  data: TaskPasswordBankImproveSingleton
}>()

const emit = defineEmits<{
  (e: 'answered', correct: boolean): void
}>()

const improved = ref('')

function submit() {
  // TODO: add real validation logic
  emit('answered', true)
}
</script>
