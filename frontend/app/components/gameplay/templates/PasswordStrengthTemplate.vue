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
const blockedTerms = computed(() => (props.task.blockedTerms ?? []).map((term) => term.toLowerCase()))
const commonPatterns = computed(() => (props.task.commonPatterns ?? []).map((pattern) => pattern.toLowerCase()))
const normalizedPassword = computed(() => password.value.trim())
const loweredPassword = computed(() => normalizedPassword.value.toLowerCase())
const wordParts = computed(() => normalizedPassword.value.split(/[^A-Za-z0-9]+/).filter(Boolean))
const hasUppercase = computed(() => /[A-Z]/.test(normalizedPassword.value))
const hasLowercase = computed(() => /[a-z]/.test(normalizedPassword.value))
const hasDigit = computed(() => /\d/.test(normalizedPassword.value))
const hasSymbol = computed(() => /[^A-Za-z0-9]/.test(normalizedPassword.value))

const checks = computed(() => {
  const containsBlockedTerm = blockedTerms.value.some((term) => term.length > 0 && loweredPassword.value.includes(term))
  const containsCommonPattern = commonPatterns.value.some((pattern) => pattern.length > 0 && loweredPassword.value.includes(pattern))
  const changedFromStart = props.task.mode !== 'improve'
    || normalizedPassword.value !== (props.task.startingPassword ?? '')
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
      id: 'phrase',
      label: 'Bruk gjerne flere ord eller en liten frase',
      passed: wordParts.value.length >= 2 || normalizedPassword.value.length >= minimumLength.value + 3,
    },
    {
      id: 'personal',
      label: 'Unnga navn, kjaledyr og fodselsar',
      passed: !containsBlockedTerm,
    },
    {
      id: 'common',
      label: 'Unnga vanlige monster som 123 eller qwerty',
      passed: !containsCommonPattern,
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

const strengthLevel = computed(() => {
  if (score.value <= 1) {
    return {
      label: 'Utrygt',
      className: 'password-strength-template__meter-fill--low',
      width: '25%',
    }
  }

  if (score.value === 2) {
    return {
      label: 'Pa vei',
      className: 'password-strength-template__meter-fill--medium',
      width: '50%',
    }
  }

  if (score.value === 3) {
    return {
      label: 'Bra',
      className: 'password-strength-template__meter-fill--good',
      width: '75%',
    }
  }

  return {
    label: 'Sterkt',
    className: 'password-strength-template__meter-fill--strong',
    width: '100%',
  }
})

const tips = computed(() => checks.value.filter((check) => !check.passed).map((check) => check.label))

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

      <div class="password-strength-template__meter-card">
        <div class="password-strength-template__meter-header">
          <span>Styrkemaler</span>
          <strong>{{ strengthLevel.label }}</strong>
        </div>

        <div class="password-strength-template__meter-track" aria-hidden="true">
          <div
            class="password-strength-template__meter-fill"
            :class="strengthLevel.className"
            :style="{ width: strengthLevel.width }"
          />
        </div>

        <p class="password-strength-template__score">
          {{ score }} av 5 sjekkpunkter oppfylt
        </p>
      </div>

      <div class="password-strength-template__checks">
        <div
          v-for="check in checks"
          :key="check.id"
          class="password-strength-template__check"
          :class="{
            'password-strength-template__check--passed': check.passed,
            'password-strength-template__check--failed': !check.passed,
          }"
        >
          <span>{{ check.passed ? 'OK' : 'Tips' }}</span>
          <p>{{ check.label }}</p>
        </div>
      </div>

      <div v-if="tips.length > 0" class="password-strength-template__tips-box">
        <h3>Dette kan du forbedre</h3>
        <ul>
          <li v-for="tip in tips" :key="tip">{{ tip }}</li>
        </ul>
      </div>

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
.password-strength-template__helper,
.password-strength-template__score {
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

.password-strength-template__field span,
.password-strength-template__meter-header span,
.password-strength-template__meter-header strong,
.password-strength-template__tips-box h3 {
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

.password-strength-template__meter-card {
  display: grid;
  gap: 0.7rem;
  padding: 1rem;
  border-radius: 1.25rem;
  background: #f4f8fd;
}

.password-strength-template__meter-header {
  display: flex;
  justify-content: space-between;
  gap: 1rem;
}

.password-strength-template__meter-track {
  height: 0.85rem;
  border-radius: 999px;
  background: #dce6f2;
  overflow: hidden;
}

.password-strength-template__meter-fill {
  height: 100%;
  border-radius: inherit;
  transition: width 0.2s ease;
}

.password-strength-template__meter-fill--low {
  background: #ef4444;
}

.password-strength-template__meter-fill--medium {
  background: #f59e0b;
}

.password-strength-template__meter-fill--good {
  background: #3b82f6;
}

.password-strength-template__meter-fill--strong {
  background: #16a34a;
}

.password-strength-template__checks {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0.75rem;
}

.password-strength-template__check {
  display: grid;
  gap: 0.35rem;
  padding: 0.95rem 1rem;
  border-radius: 1rem;
}

.password-strength-template__check span {
  font-size: 0.8rem;
  font-weight: 800;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.password-strength-template__check p {
  margin: 0;
  line-height: 1.4;
}

.password-strength-template__check--passed {
  background: #e8f8ed;
  color: #166534;
}

.password-strength-template__check--failed {
  background: #fff1ef;
  color: #9a3412;
}

.password-strength-template__tips-box {
  padding: 1rem;
  border-radius: 1rem;
  background: #fffbeb;
  color: #7c4a03;
}

.password-strength-template__tips-box h3,
.password-strength-template__tips-box ul {
  margin: 0;
}

.password-strength-template__tips-box ul {
  padding-left: 1.1rem;
  margin-top: 0.5rem;
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
  .password-strength-template__checks {
    grid-template-columns: 1fr;
  }

  .password-strength-template__meter-header,
  .password-strength-template__actions {
    flex-direction: column;
  }

  .password-strength-template__submit {
    width: 100%;
  }
}
</style>
