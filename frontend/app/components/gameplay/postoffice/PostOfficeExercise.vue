<script setup lang="ts">
import type { PostOfficeMail, PostOfficeTask } from "~/composables/types/TaskTypes";

const props = defineProps<{
  mails: PostOfficeMail;
}>();

const emit = defineEmits<{
  (e: "answered", correct: boolean): void;
}>();

function answer(userSaidGood: boolean) {
  const correct = userSaidGood === props.mails.isValid;
  emit("answered", correct);
}
</script>

<template>
  <div class="post-singleton">
    <img
      :src="props.mails.avatarImageUrl"
      alt="Sender avatar"
    />
    <p class="sender">
      {{ props.mails.sender }}
    </p>
    <h2>{{ props.mails.subject }}</h2>
    <p>{{ props.mails.content }}</p>

    <div class="actions">
      <button @click="answer(true)">Troverdig</button>
      <button @click="answer(false)">Svindel</button>
    </div>
  </div>
</template>

<style scoped></style>