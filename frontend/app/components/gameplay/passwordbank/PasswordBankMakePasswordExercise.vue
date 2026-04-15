<script setup lang="ts">
import type { PasswordBankMakePasswordItem } from "~/composables/types/TaskTypes";

const props = defineProps<{
  taskInfo: PasswordBankMakePasswordItem;
}>();

const emit = defineEmits<{
  (e: "answered", correct: boolean): void;
}>();

const password = ref("");

function submit() {
  const trimmed = password.value.trim();

  const correct = trimmed.length >= 8;
  emit("answered", correct);
}
</script>

<template>
  <div class="password-make">
    <p>{{ props.taskInfo.guide }}</p>

    <input
      v-model="password"
      type="text"
      placeholder="Lag et sterkt passord..."
    />

    <button @click="submit">Send inn</button>
  </div>
</template>