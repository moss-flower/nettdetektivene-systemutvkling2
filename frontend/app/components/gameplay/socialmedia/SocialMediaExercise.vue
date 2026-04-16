<script setup lang="ts">
import type {
  SocialMediaTask,
  SocialMediaChoice,
  SocialMediaPost,
} from "~/composables/types/TaskTypes";

const props = defineProps<{
  posts: SocialMediaPost;
}>();

const emit = defineEmits<{
  (e: "answered", correct: boolean): void;
}>();

function answer(choice: SocialMediaChoice) {
  const correct =
    choice === props.posts.expectedChoice;

  emit("answered", correct);
}
</script>

<template>
  <div class="socialmedia-singleton">
    <img
      :src="props.posts.imageUrl"
      alt="Social media post"
    />

    <div class="actions">
      <button @click="answer('share')">Del</button>
      <button @click="answer('hold')">Vent</button>
      <button @click="answer('checkSources')">Sjekk kilder</button>
      <button @click="answer('askAdult')">Spør en voksen</button>
    </div>
  </div>
</template>