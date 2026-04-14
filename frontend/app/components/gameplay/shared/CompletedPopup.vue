<template>
  <div class="popup-overlay">
    <div class="popup">
      <h2>{{ isComplete ? msgCompleted : msgFailed }}</h2>
      <p>Du fikk {{ correctCount }} / {{ total }} riktig.</p>
      <button @click="emit('next')">Gå Tilbake</button>
    </div>
  </div>
</template>

<script setup lang="ts">
const props = withDefaults(defineProps<{
  correctCount: number
  total: number
  msgCompleted?: string
  msgFailed?: string
}>(), {
  msgCompleted: 'Ferdig, du klarte det!',
  msgFailed: 'Ikke helt, bedre lykke neste gang!',
})

const isComplete = computed(() => props.correctCount === props.total)

const emit = defineEmits<{
  (e: 'next'): void
}>()
</script>