<script setup lang="ts">
import type { PasswordBankImprovePasswordItem } from "~/composables/types/TaskTypes";

const props = defineProps<{
  taskInfo: PasswordBankImprovePasswordItem;
}>();

const emit = defineEmits<{
  (e: "answered", correct: boolean): void;
}>();

const improved = ref("");

function submit() {
  const trimmed = improved.value.trim();

  const correct = trimmed.length >= 8;
  emit("answered", correct);
  // TODO: PROPER VALIDATION (BACKEND?)
}
</script>

<template>
  <div class="password-improve">
    <p>{{ props.taskInfo.guide }}</p>
    <p class="original">
      Opprinnelig passord:
      <strong>{{ props.taskInfo.passwordStart }}</strong>
    </p>

    <input
      v-model="improved"
      type="text"
      placeholder="Skriv et bedre passord..."
    />

    <button @click="submit">Send inn</button>
  </div>
</template>