<script setup lang="ts">
import { useNotifications } from '~/composables/useNotifications';
const { connect, disconnect } = useNotifications();

onMounted(() => connect());
onUnmounted(() => disconnect());
</script>

<template>
    <NotificationTray />
    <slot />
</template>
