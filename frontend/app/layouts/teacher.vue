<script setup lang="ts">
const { ensureLoaded, disconnect } = useNotifications();

onMounted(() => {
  void ensureLoaded();
});

onUnmounted(() => disconnect());
</script>

<template>
  <slot />
</template>
