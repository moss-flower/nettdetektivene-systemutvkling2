<script setup lang="ts">
import { useNotifications } from '~/composables/useNotifications';
const { connected, notifications } = useNotifications()

function dismiss(index: number) {
  notifications.value.splice(index, 1)
}
</script>

<template>
  <div class="notification-tray">
    <span>{{ connected ? 'Connected' : 'Disconnected' }}</span>
    <TransitionGroup name="slide" tag="ul">
      <li v-for="(n, i) in notifications" :key="n.id">
        {{ n.message }}
        <button @click="dismiss(i)">✕</button>
      </li>
    </TransitionGroup>
  </div>
</template>