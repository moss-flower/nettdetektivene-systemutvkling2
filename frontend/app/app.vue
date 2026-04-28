<script setup lang="ts">
import BackgroundMusic from '~/components/audio/BackgroundMusic.vue'
</script>

<template>
  <NuxtLayout>
    <NuxtPage />
  </NuxtLayout>
  <ClientOnly>
    <BackgroundMusic />
  </ClientOnly>
</template>

<style>
#confetti-canvas {
  inset: 0;
  z-index: 30;
}
</style>
