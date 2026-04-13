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
    return `Maxmimum ${props.maxLength} characters`;
  }

  if (props.pattern && normalized && !props.pattern.test(normalized)) {
    return "Invalid format";
  }

  return null;
}

function updateValidity() {
  const displayError = validate(inputValue.value);
  emit("update:isValid", !displayError);
}

function handleInput(event: Event) {
  const value = (event.target as HTMLInputElement).value;
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
    />

    <p v-if="props.description" class="description">
      {{ props.description }}
    </p>

    <p v-if="displayError" class="displayError">{{ displayError }}</p>
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
  color: var(--text);
}

.description {
  font-size: 0.95rem;
  color: var(--muted);
}

.displayError {
  font-size: 0.9rem;
  color: var(--danger);
}

.input {
  width: 100%;
  padding: 0.85rem 1rem;
  border: 1px solid var(--border);
  border-radius: var(--radius);
  background: var(--surface);
  color: var(--text);
  font: inherit;
  transition:
    border-color var(--transition),
    box-shadow var(--transition);
}

</style>
