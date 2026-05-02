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
const startingPassword = computed(() => props.task.startingPassword ?? '')
const requiresStartingPassword = computed(() => props.task.mode === 'improve' && startingPassword.value.length > 0)
const startingPasswordIndex = computed(() => {
  if (!requiresStartingPassword.value) {
    return -1
  }

  return normalizedPassword.value.indexOf(startingPassword.value)
})
const includesStartingPassword = computed(() => !requiresStartingPassword.value || startingPasswordIndex.value >= 0)
const addedCharacters = computed(() => {
  if (!requiresStartingPassword.value) {
    return normalizedPassword.value
  }

  if (startingPasswordIndex.value < 0) {
    return ''
  }

  const start = startingPasswordIndex.value
  const end = start + startingPassword.value.length
  return `${normalizedPassword.value.slice(0, start)}${normalizedPassword.value.slice(end)}`
})
const loweredStrengthSubject = computed(() => addedCharacters.value.toLowerCase())
const hasUppercase = computed(() => /[A-Z]/.test(normalizedPassword.value))
const hasLowercase = computed(() => /[a-z]/.test(normalizedPassword.value))
const hasDigit = computed(() => /\d/.test(normalizedPassword.value))
const hasSymbol = computed(() => /[^A-Za-z0-9]/.test(normalizedPassword.value))
const addedHasUppercase = computed(() => /[A-Z]/.test(addedCharacters.value))
const addedHasLowercase = computed(() => /[a-z]/.test(addedCharacters.value))
const addedHasDigit = computed(() => /\d/.test(addedCharacters.value))
const addedHasSymbol = computed(() => /[^A-Za-z0-9]/.test(addedCharacters.value))

const checks = computed(() => {
  const containsCommonPattern = commonPatterns.value.some((pattern) => pattern.length > 0 && loweredStrengthSubject.value.includes(pattern))
  const containsBirthYear = /(?:^|\D)(?:19\d{2}|20(?:0\d|1\d|2\d))(?!\d)/.test(addedCharacters.value)
  const containsRepeatingPattern = /(.{2,})\1+/.test(loweredStrengthSubject.value) || /(.)\1{2,}/.test(loweredStrengthSubject.value)
  const changedFromStart = !requiresStartingPassword.value || normalizedPassword.value !== startingPassword.value
  const hasMixedCharacters = [hasUppercase.value, hasLowercase.value, hasDigit.value, hasSymbol.value].filter(Boolean).length >= 3
  const improveModeChecks = requiresStartingPassword.value
    ? [
        {
          id: 'added-uppercase',
          label: 'Legg til minst en stor bokstav',
          passed: addedHasUppercase.value,
        },
        {
          id: 'added-lowercase',
          label: 'Legg til minst en liten bokstav',
          passed: addedHasLowercase.value,
        },
        {
          id: 'added-digit',
          label: 'Legg til minst ett tall',
          passed: addedHasDigit.value,
        },
        {
          id: 'added-symbol',
          label: 'Legg til minst ett symbol',
          passed: addedHasSymbol.value,
        },
      ]
    : [
        {
          id: 'mix',
          label: 'Bland bokstaver, tall eller symbol',
          passed: hasMixedCharacters,
        },
      ]

  return [
    {
      id: 'length',
      label: `Minst ${minimumLength.value} tegn`,
      passed: normalizedPassword.value.length >= minimumLength.value,
    },
    ...improveModeChecks,
    {
      id: 'birth-year',
      label: requiresStartingPassword.value
        ? 'Ikke legg til fodselsar eller andre lette arstall'
        : 'Unnga fodselsar og andre lette arstall',
      passed: !containsBirthYear,
    },
    {
      id: 'common',
      label: requiresStartingPassword.value
        ? 'Ikke legg til vanlige monster som 123 eller qwerty'
        : 'Unnga vanlige monster som 123 eller qwerty',
      passed: !containsCommonPattern,
    },
    {
      id: 'repeating',
      label: requiresStartingPassword.value
        ? 'Ikke legg til gjentakende monster som abab eller 1111'
        : 'Unnga gjentakende monster som abab eller 1111',
      passed: !containsRepeatingPattern,
    },
    {
      id: 'includes-start',
      label: 'Behold startpassordet som en del av det nye passordet',
      passed: includesStartingPassword.value,
      required: requiresStartingPassword.value,
    },
    {
      id: 'changed',
      label: 'Gjor passordet annerledes enn det svake du startet med',
      passed: changedFromStart,
      required: requiresStartingPassword.value,
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
  /* header */
  --ps-title: var(--palette-cork-900);
  --ps-muted: var(--palette-cork-580);

  /* panel card */
  --ps-panel-bg-top: var(--palette-cream-100);
  --ps-panel-border: var(--palette-overlay-cork-medium);
  --ps-panel-shadow: var(--palette-overlay-dark-soft);
  --ps-panel-text: var(--palette-cork-900);

  /* starting password callout */
  --ps-callout-bg: var(--palette-cream-200);
  --ps-callout-text: var(--palette-cork-700);

  /* input field */
  --ps-input-bg: var(--palette-white);
  --ps-input-border: var(--palette-gray-200);
  --ps-input-border-focus: var(--palette-navy-700);
  --ps-input-text: var(--palette-cork-900);

  /* label */
  --ps-label: var(--palette-cork-900);

  /* helper text */
  --ps-helper: var(--palette-cork-580);

  /* submit button */
  --ps-submit-bg: var(--palette-navy-900);
  --ps-submit-text: var(--palette-cream-100);

  /* disabled */
  --ps-disabled-opacity: 0.6;

  display: grid;
  gap: var(--space-4);
  color: var(--ps-panel-text);
}

.strengthTemplateHeader {
  display: grid;
  gap: var(--space-1);
}

.strengthTemplateTitle {
  /* fluid title scale, deliberate exception */
  font-size: clamp(1.35rem, 2.4vw, 2rem);
  line-height: 1.1;
  color: var(--ps-title);
}

.strengthTemplateText {
  max-width: 58ch;
  font-size: var(--font-size-16);
  line-height: var(--font-leading-16);
  color: var(--ps-muted);
}

.strengthTemplatePanel {
  display: grid;
  gap: var(--space-4);
  padding: var(--space-5);
  border-radius: var(--radius-3);
  border: var(--border-width-thin) solid var(--ps-panel-border);
  background: var(--ps-panel-bg-top);
  box-shadow: 0 var(--space-3) var(--space-5) var(--ps-panel-shadow);
}

.templateStartingPassword,
.strengthTemplateHelper {
  margin: 0;
}

.templateStartingPassword {
  padding: var(--space-2) var(--space-4);
  border-radius: var(--radius-2);
  background: var(--ps-callout-bg);
  color: var(--ps-callout-text);
}

.strengthTemplateField {
  display: grid;
  gap: var(--space-1);
}

.strengthTemplateField span {
  font-weight: var(--font-weight-bold);
  color: var(--ps-label);
}

.strengthTemplateInput {
  width: 100%;
  padding: var(--space-4) var(--space-3);
  border: var(--border-width-thin) solid var(--ps-input-border);
  border-radius: var(--radius-2);
  background: var(--ps-input-bg);
  color: var(--ps-input-text);
  font: inherit;
}

.strengthTemplateInput:focus {
  outline: none;
  border-color: var(--ps-input-border-focus);
}

.strengthTemplateHelper {
  color: var(--ps-helper);
}

.strengthTemplateActions {
  display: flex;
  justify-content: flex-end;
}

.strengthTemplateSubmit {
  padding: var(--space-3) var(--space-3);
  border: 0;
  border-radius: var(--radius-pill);
  background: var(--ps-submit-bg);
  color: var(--ps-submit-text);
  font-weight: var(--font-weight-bold);
}

.strengthTemplateSubmit:disabled,
.strengthTemplateInput:disabled {
  cursor: default;
  opacity: var(--ps-disabled-opacity);
}

</style>
