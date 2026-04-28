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
