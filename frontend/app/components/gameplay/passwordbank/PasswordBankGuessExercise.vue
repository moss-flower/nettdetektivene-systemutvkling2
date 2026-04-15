<script setup lang="ts">
import type { PasswordBankGuessItem } from "~/composables/types/TaskTypes";

const props = defineProps<{
  taskInfo: PasswordBankGuessItem;
}>();

const emit = defineEmits<{
  (e: "answered", correct: boolean): void;
}>();

const shuffledPasswords = computed(() => {
  const all = [
    ...props.taskInfo.weakPasswords,
    ...props.taskInfo.strongPasswords,
  ];

  for (let i = all.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [all[i]!, all[j]!] = [all[j]!, all[i]!];
  }

  return all;
});

function answer(password: string, guess: "weak" | "strong") {
  const isWeak = props.taskInfo.weakPasswords.includes(password);

  const correct =
    (guess === "weak" && isWeak) ||
    (guess === "strong" && !isWeak);

  emit("answered", correct);
}
</script>

<template>
  <div class="password-guess">
    <p>{{ props.taskInfo.guide }}</p>

    <div
      v-for="(pw, index) in shuffledPasswords"
      :key="`${pw}-${index}`"
      class="password-item"
    >
      <span>{{ pw }}</span>

      <div class="actions">
        <button @click="answer(pw, 'weak')">Svakt</button>
        <button @click="answer(pw, 'strong')">Sterkt</button>
      </div>
    </div>
  </div>
</template>