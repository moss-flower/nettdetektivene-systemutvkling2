<template>
  <div class="password-guess">
    <p>{{ data.description }}</p>
    <div class="passwords">
      <div v-for="(pw, index) in shuffledPasswords" :key="index" class="password-item">
        <span>{{ pw.password }}</span>
        <div class="actions">
          <button @click="answer(pw.password, 'weak')">Svakt</button>
          <button @click="answer(pw.password, 'strong')">Sterkt</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { TaskPasswordBankGuessSingleton } from '~/composables/types/TaskTypes'

const props = defineProps<{
  data: TaskPasswordBankGuessSingleton
}>()

const emit = defineEmits<{
  (e: 'answered', correct: boolean): void
}>()

const shuffledPasswords = computed(() => {
  const all = [
    ...props.data.passwordWeak.map(p => ({ password: p, isWeak: true })),
    ...props.data.passwordStrong.map(p => ({ password: p, isWeak: false })),
  ]
  for (let i = all.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [all[i]!, all[j]!] = [all[j]!, all[i]!]
  }
  return all
})

function answer(password: string, guess: 'weak' | 'strong') {
  const item = shuffledPasswords.value.find(p => p.password === password)
  if (!item) return
  const correct = (guess === 'weak') === item.isWeak
  emit('answered', correct)
}
</script>
