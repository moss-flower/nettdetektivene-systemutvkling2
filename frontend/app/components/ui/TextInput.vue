<script setup lang="ts">
const props = withDefaults(
  defineProps<{
    name?: string;
    label?: string;
    description?: string;
    placeholder?: string;
    type?: "text" | "email" | "password";
    required?: boolean;
    minLength?: number;
    maxLength?: number;
    pattern?: RegExp;
    showError?: boolean;
  }>(),
  {
    name: "",
    label: "",
    description: "",
    placeholder: "",
    type: "text",
    required: false,
    minLength: 0,
    maxLength: 255,
    pattern: undefined,
    showError: true,
  },
);

const emit = defineEmits<{
  (e: "update:modelValue", value: string): void;
  (e: "update:isValid", value: boolean): void;
}>();

const displayError = ref<string>("");
const inputValue = ref<string>("");

function normalize(value: string): string {
  return value.trim();
}

function validate(value: string): string | null {
  const normalized = normalize(value);

  if (props.required && !normalized) {
    return "This field is required";
  }

  if (props.minLength !== undefined && normalized.length < props.minLength) {
    return `Minimum ${props.minLength} characters`;
  }

  if (props.maxLength !== undefined && normalized.length > props.maxLength) {
    return `Maximum ${props.maxLength} characters`;
  }

  if (props.pattern && normalized && !props.pattern.test(normalized)) {
    return "Invalid format";
  }

  return null;
}

function updateValidity() {
  displayError.value = validate(inputValue.value) ?? "";
  emit("update:isValid", !displayError.value);
}

function handleInput(event: Event) {
  const value = (event.target as HTMLInputElement).value;
  inputValue.value = value;
  emit("update:modelValue", value);
  updateValidity();
}
</script>

<template>
  <div class="field">
    <label v-if="props.label" class="label">
      {{ props.label }}
    </label>

    <input
      :name="props.name"
      :type="props.type"
      :placeholder="props.placeholder"
      :value="inputValue"
      @input="handleInput"
      class="input"
      :class="{ inputError: displayError }"
    />

    <p v-if="props.description" class="description">
      {{ props.description }}
    </p>

    <p v-if="props.showError && displayError" class="displayError">
      {{ displayError }}
    </p>
  </div>
</template>

<style scoped>
.field {
  display: flex;
  flex-direction: column;
  gap: 0.35rem;
}

.label {
  font-weight: 700;
  color: var(--color-text-primary);
}

.description {
  font-size: 0.95rem;
  color: var(--color-text-secondary);
}

.displayError {
  font-size: 0.9rem;
  color: var(--color-semantic-error);
}

.input {
  width: 100%;
  padding: 0.85rem var(--space-4);
  border: var(--border-width-hairline) solid var(--color-border-default);
  border-radius: var(--radius-2);
  background: var(--color-bg-surface);
  color: var(--color-text-primary);
  font: inherit;
  transition:
    border-color var(--transition-base),
    box-shadow var(--transition-base);
}

.input:focus {
  outline: none;
  border-color: var(--color-brand-primary);
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.12);
}

.inputError {
  border-color: var(--color-semantic-error);
}
</style>
