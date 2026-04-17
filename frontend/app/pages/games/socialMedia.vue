<script setup lang="ts">
import ExerciseResultDialog from '~/components/gameplay/shared/ExerciseResultDialog.vue';
import SelectedPopup from '~/components/gameplay/shared/SelectedPopup.vue';
import SocialMediaExercise from '~/components/gameplay/socialmedia/SocialMediaExercise.vue';
import type { SocialMediaTask, SocialMediaChoice } from '~/composables/types/TaskTypes'
import { sampleAndShuffle } from '~/composables/utils/gameUtils'


const task: SocialMediaTask = {
  id: "1",
  kind: "socialMedia",
  description: "Hva gjør du med dette innlegget?",
  posts: [
    {
      postId: "1",
      imageUrl: "https://placehold.co/600x400?text=Post+1",
      expectedChoice: "share",
    },
    {
      postId: "2",
      imageUrl: "https://placehold.co/600x400?text=Post+2",
      expectedChoice: "checkSources",
    },
    {
      postId: "3",
      imageUrl: "https://placehold.co/600x400?text=Post+3",
      expectedChoice: "askAdult",
    },
  ]
};

const tasks = sampleAndShuffle(task.posts, 3)
const currentIndex = ref(0)
const showTaskResultDialog = ref(false)
const showExerciseResultDialog = ref(false)
const lastAnswerCorrect = ref(false)
const correctCount = ref(0)

const onAnswered = (correct: boolean) => {
  lastAnswerCorrect.value = correct
  if (correct) correctCount.value++
  showTaskResultDialog.value = true
}

const next = () => {
  showTaskResultDialog.value = false
  if (currentIndex.value < tasks.length - 1) {
    currentIndex.value++
  } else {
    showExerciseResultDialog.value = true
  }
}
</script>


<template>
  <div>
    <h1>Sosiale Medier</h1>

    <SocialMediaExercise
      v-if="tasks[currentIndex]"
      :posts="tasks[currentIndex]!"
      @answered="onAnswered"
    />

    <SelectedPopup
      v-if="showTaskResultDialog"
      :correct="lastAnswerCorrect"
      :is-last="currentIndex >= tasks.length - 1"
      @next="next"
    />

    <ExerciseResultDialog
      v-if="showExerciseResultDialog"
      :correct-count="correctCount"
      :total="tasks.length"
    />
  </div>
</template>

<style scoped>
/* Styles here */
</style>
