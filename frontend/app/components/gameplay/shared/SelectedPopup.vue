<template>
  <div class="popup-overlay">
    <div class="popup">
      <p>{{ correct ? '✅ Korrekt!' : '❌ Feil!' }}</p>
      <button @click="$emit('next')">
        {{ isLast ? 'Fullfør' : 'Neste' }}
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  correct: boolean
  isLast: boolean
}>()

defineEmits<{
  (e: 'next'): void
}>()
</script>