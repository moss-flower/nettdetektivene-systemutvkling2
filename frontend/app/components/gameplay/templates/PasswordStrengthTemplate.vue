<script setup lang="ts">
import type { PasswordStrengthResult, PasswordStrengthTaskData } from '~/composables/types/TaskTypes'

const props = defineProps<{
  task: PasswordStrengthTaskData
  disabled?: boolean
}>()

const emit = defineEmits<{
  (e: 'answered', payload: PasswordStrengthResult): void
}>()

const password = ref(props.task.startingPassword ?? '')

const minimumLength = computed(() => props.task.minLength ?? 10)
const commonPatterns = computed(() => (props.task.commonPatterns ?? []).map((pattern) => pattern.toLowerCase()))
const normalizedPassword = computed(() => password.value.trim())
const loweredPassword = computed(() => normalizedPassword.value.toLowerCase())
const hasUppercase = computed(() => /[A-Z]/.test(normalizedPassword.value))
const hasLowercase = computed(() => /[a-z]/.test(normalizedPassword.value))
const hasDigit = computed(() => /\d/.test(normalizedPassword.value))
const hasSymbol = computed(() => /[^A-Za-z0-9]/.test(normalizedPassword.value))

const checks = computed(() => {
  const containsCommonPattern = commonPatterns.value.some((pattern) => pattern.length > 0 && loweredPassword.value.includes(pattern))
  const containsBirthYear = /(?:19\d{2}|20(?:0\d|1\d|2\d))/g.test(normalizedPassword.value)
  const containsRepeatingPattern = /(.{2,})\1+/.test(loweredPassword.value) || /(.)\1{2,}/.test(loweredPassword.value)
  const requiresChangedPassword = props.task.mode === 'improve' && Boolean(props.task.startingPassword)
  const changedFromStart = !requiresChangedPassword || normalizedPassword.value !== props.task.startingPassword
  const hasMixedCharacters = [hasUppercase.value, hasLowercase.value, hasDigit.value, hasSymbol.value].filter(Boolean).length >= 3

  return [
    {
      id: 'length',
      label: `Minst ${minimumLength.value} tegn`,
      passed: normalizedPassword.value.length >= minimumLength.value,
    },
    {
      id: 'mix',
      label: 'Bland bokstaver, tall eller symbol',
      passed: hasMixedCharacters,
    },
    {
      id: 'birth-year',
      label: 'Unnga fodselsar og andre lette arstall',
      passed: !containsBirthYear,
    },
    {
      id: 'common',
      label: 'Unnga vanlige monster som 123 eller qwerty',
      passed: !containsCommonPattern,
    },
    {
      id: 'repeating',
      label: 'Unnga gjentakende monster som abab eller 1111',
      passed: !containsRepeatingPattern,
    },
    {
      id: 'changed',
      label: 'Gjor passordet annerledes enn det svake du startet med',
      passed: changedFromStart,
    },
  ]
})

const score = computed(() => checks.value.filter((check) => check.passed && check.id !== 'changed').length)
const canPass = computed(() => {
  const changedCheck = checks.value.find((check) => check.id === 'changed')
  return normalizedPassword.value.length > 0
    && score.value >= props.task.minScoreToPass
    && Boolean(changedCheck?.passed)
})

const submit = () => {
  if (props.disabled || normalizedPassword.value.length === 0) {
    return
  }

  emit('answered', {
    correct: canPass.value,
    password: normalizedPassword.value,
    score: score.value,
  })
}
</script>

<template>
  <section class="password-strength-template">
    <header v-if="props.task.prompt?.title || props.task.prompt?.text" class="password-strength-template__header">
      <h2 v-if="props.task.prompt?.title" class="password-strength-template__title">
        {{ props.task.prompt.title }}
      </h2>
      <p v-if="props.task.prompt?.text" class="password-strength-template__text">
        {{ props.task.prompt.text }}
      </p>
    </header>

    <div class="password-strength-template__panel">
      <p v-if="props.task.mode === 'improve' && props.task.startingPassword" class="password-strength-template__starting-password">
        Startpassord: <strong>{{ props.task.startingPassword }}</strong>
      </p>

      <label class="password-strength-template__field">
        <span>{{ props.task.mode === 'improve' ? 'Skriv et tryggere passord' : 'Skriv et nytt passord' }}</span>
        <input
          v-model="password"
          class="password-strength-template__input"
          type="text"
          :placeholder="props.task.placeholder ?? 'Skriv passordet ditt her'"
          :disabled="props.disabled"
        >
      </label>

      <p v-if="props.task.prompt?.helperText" class="password-strength-template__helper">
        {{ props.task.prompt.helperText }}
      </p>

      <div class="password-strength-template__actions">
        <button
          class="password-strength-template__submit"
          type="button"
          :disabled="props.disabled || normalizedPassword.length === 0"
          @click="submit"
        >
          Sjekk passordet
        </button>
      </div>
    </div>
  </section>
</template>

<style scoped>
.password-strength-template {
  display: grid;
  gap: 1rem;
  color: #1f2937;
}

.password-strength-template__header {
  display: grid;
  gap: 0.45rem;
}

.password-strength-template__title {
  font-size: clamp(1.35rem, 2.4vw, 2rem);
  line-height: 1.1;
  color: #172033;
}

.password-strength-template__text {
  max-width: 58rem;
  line-height: 1.5;
  color: #4b5563;
}

.password-strength-template__panel {
  display: grid;
  gap: 1rem;
  padding: clamp(1rem, 2vw, 1.5rem);
  border-radius: 1.6rem;
  border: 2px solid #d7dfeb;
  background: linear-gradient(180deg, #fffdfa 0%, #f8fbff 100%);
  box-shadow: 0 14px 26px rgba(52, 77, 115, 0.08);
}

.password-strength-template__starting-password,
.password-strength-template__helper {
  margin: 0;
}

.password-strength-template__starting-password {
  padding: 0.85rem 1rem;
  border-radius: 1rem;
  background: #fff5df;
  color: #744210;
}

.password-strength-template__field {
  display: grid;
  gap: 0.45rem;
}

.password-strength-template__field span {
  font-weight: 700;
}

.password-strength-template__input {
  width: 100%;
  padding: 1rem 1.1rem;
  border: 2px solid #cfd9e6;
  border-radius: 1rem;
  background: #fff;
  color: #172033;
  font: inherit;
}

.password-strength-template__input:focus {
  outline: none;
  border-color: #4c7bb0;
}

.password-strength-template__helper {
  color: #576273;
}

.password-strength-template__actions {
  display: flex;
  justify-content: flex-end;
}

.password-strength-template__submit {
  padding: 0.9rem 1.15rem;
  border: 0;
  border-radius: 999px;
  background: #1f3657;
  color: #f9fbff;
  font-weight: 700;
}

.password-strength-template__submit:disabled,
.password-strength-template__input:disabled {
  cursor: default;
  opacity: 0.72;
}

@media (max-width: 760px) {
  .password-strength-template__actions {
    flex-direction: column;
  }

  .password-strength-template__submit {
    width: 100%;
  }
}
</style>
