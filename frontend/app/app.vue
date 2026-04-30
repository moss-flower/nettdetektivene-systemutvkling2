<script setup lang="ts">
import BackgroundMusic from '~/components/audio/BackgroundMusic.vue'

const route = useRoute()
const isInGame = computed(() =>
  route.path.startsWith('/student') || route.path.startsWith('/games')
)
</script>

<template>
  <NuxtLayout>
    <NuxtPage />
  </NuxtLayout>
  <ClientOnly>
    <BackgroundMusic v-if="isInGame" />
  </ClientOnly>
</template>

<style>
#confetti-canvas {
  inset: 0;
  z-index: 30;
}
</style>
