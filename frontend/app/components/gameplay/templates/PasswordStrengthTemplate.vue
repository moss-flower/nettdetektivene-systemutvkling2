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
  const containsBirthYear = /(?:^|\D)(?:19\d{2}|20(?:0\d|1\d|2\d))(?!\d)/.test(normalizedPassword.value)
  const containsRepeatingPattern = /(.{2,})\1+/.test(loweredPassword.value) || /(.)\1{2,}/.test(loweredPassword.value)
  const startingPassword = props.task.startingPassword ?? ''
  const requiresStartingPassword = startingPassword.length > 0
  const includesStartingPassword = !requiresStartingPassword || normalizedPassword.value.includes(startingPassword)
  const changedFromStart = !requiresStartingPassword || normalizedPassword.value !== startingPassword
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
      id: 'includes-start',
      label: 'Behold startpassordet som en del av det nye passordet',
      passed: includesStartingPassword,
      required: requiresStartingPassword,
    },
    {
      id: 'changed',
      label: 'Gjor passordet annerledes enn det svake du startet med',
      passed: changedFromStart,
      required: requiresStartingPassword,
    },
  ].filter((check) => check.required !== false)
})

const score = computed(() => checks.value.filter((check) => check.passed && check.id !== 'changed').length)
const canPass = computed(() => normalizedPassword.value.length > 0 && checks.value.every((check) => check.passed))
const missingStartingPassword = computed(() => {
  const includesStartCheck = checks.value.find((check) => check.id === 'includes-start')
  return includesStartCheck ? !includesStartCheck.passed : false
})

const submit = () => {
  if (props.disabled || normalizedPassword.value.length === 0) {
    return
  }

  emit('answered', {
    correct: canPass.value,
    password: normalizedPassword.value,
    score: score.value,
    missingStartingPassword: missingStartingPassword.value,
  })
}
</script>

<template>
  <section class="passwordStrengthTemplate">
    <header v-if="props.task.prompt?.title || props.task.prompt?.text" class="strengthTemplateHeader">
      <h2 v-if="props.task.prompt?.title" class="strengthTemplateTitle">
        {{ props.task.prompt.title }}
      </h2>
      <p v-if="props.task.prompt?.text" class="strengthTemplateText">
        {{ props.task.prompt.text }}
      </p>
    </header>

    <div class="strengthTemplatePanel">
      <p v-if="props.task.mode === 'improve' && props.task.startingPassword" class="templateStartingPassword">
        Startpassord: <strong>{{ props.task.startingPassword }}</strong>
      </p>

      <label class="strengthTemplateField">
        <span>{{ props.task.mode === 'improve' ? 'Skriv et tryggere passord' : 'Skriv et nytt passord' }}</span>
        <input
          v-model="password"
          class="strengthTemplateInput"
          type="text"
          :placeholder="props.task.placeholder ?? 'Skriv passordet ditt her'"
          :disabled="props.disabled"
        >
      </label>

      <p v-if="props.task.prompt?.helperText" class="strengthTemplateHelper">
        {{ props.task.prompt.helperText }}
      </p>

      <div class="strengthTemplateActions">
        <button
          class="strengthTemplateSubmit"
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
.passwordStrengthTemplate {
  display: grid;
  gap: var(--space-4);
  color: var(--palette-gray-900);
}

.strengthTemplateHeader {
  display: grid;
  gap: 0.45rem;
}

.strengthTemplateTitle {
  font-size: clamp(1.35rem, 2.4vw, 2rem);
  line-height: 1.1;
  color: var(--color-text-primary);
}

.strengthTemplateText {
  max-width: 58rem;
  line-height: 1.5;
  color: var(--color-text-secondary);
}

.strengthTemplatePanel {
  display: grid;
  gap: var(--space-4);
  padding: clamp(var(--space-4), 2vw, var(--space-6));
  border-radius: 1.6rem;
  border: var(--border-width-thin) solid var(--color-border-default);
  background: linear-gradient(180deg, var(--color-bg-surface) 0%, var(--palette-white) 100%);
  box-shadow: 0 14px 26px rgba(52, 77, 115, 0.08);
}

.templateStartingPassword,
.strengthTemplateHelper {
  margin: 0;
}

.templateStartingPassword {
  padding: 0.85rem var(--space-4);
  border-radius: var(--radius-2);
  background: var(--palette-cream-200);
  color: var(--palette-cork-700);
}

.strengthTemplateField {
  display: grid;
  gap: 0.45rem;
}

.strengthTemplateField span {
  font-weight: 700;
}

.strengthTemplateInput {
  width: 100%;
  padding: var(--space-4) 1.1rem;
  border: var(--border-width-thin) solid var(--color-border-default);
  border-radius: var(--radius-2);
  background: var(--palette-white);
  color: var(--color-text-primary);
  font: inherit;
}

.strengthTemplateInput:focus {
  outline: none;
  border-color: #4c7bb0;
}

.strengthTemplateHelper {
  color: var(--color-text-secondary);
}

.strengthTemplateActions {
  display: flex;
  justify-content: flex-end;
}

.strengthTemplateSubmit {
  padding: 0.9rem 1.15rem;
  border: 0;
  border-radius: var(--radius-pill);
  background: var(--color-brand-primary);
  color: #f9fbff;
  font-weight: 700;
}

.strengthTemplateSubmit:disabled,
.strengthTemplateInput:disabled {
  cursor: default;
  opacity: 0.72;
}

</style>
