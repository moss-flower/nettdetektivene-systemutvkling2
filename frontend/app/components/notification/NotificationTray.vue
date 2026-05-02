<script setup lang="ts">
import { useNotifications } from '~/composables/useNotifications'

const { connected, notifications } = useNotifications()
</script>

<template>
  <div class="notification-tray panel">
    <div class="tray__header">
      <h2>Varslinger</h2>
      <span class="tray__status" :class="{ 'tray__status--connected': connected }">
        {{ connected ? 'Tilkoblet' : 'Frakoblet' }}
      </span>
    </div>

    <TransitionGroup name="slide" tag="ul" class="tray__list">
      <NotificationItem
        v-for="n in notifications"
        :key="n.notificationUuid"
        :notification="n"
      />
    </TransitionGroup>

    <p v-if="notifications.length === 0" class="panelMessage">
      Ingen varslinger ennå.
    </p>
  </div>
</template>

<style scoped>
.notification-tray {
  display: grid;
  gap: var(--space-4);
}

.tray__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--space-3);
}

.tray__header h2 {
  margin: 0;
}

.tray__status {
  font-size: var(--font-size-12);
  font-weight: var(--font-weight-bold);
  letter-spacing: var(--font-tracking-wider);
  text-transform: uppercase;
  color: var(--color-text-secondary);
}

.tray__status--connected {
  color: var(--palette-green-700); /* deliberate: palette-direct, connection status */
}

.tray__list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: grid;
  gap: var(--space-3);
}

.panelMessage {
  margin: 0;
  color: var(--color-text-secondary);
  font-size: var(--font-size-14);
}

/* transitions stay here because they apply to the list rendering */
.slide-enter-active,
.slide-leave-active {
  transition: all 0.2s ease;
}

.slide-enter-from {
  opacity: 0;
  transform: translateY(-6px);
}

.slide-leave-to {
  opacity: 0;
  transform: translateY(6px);
}
</style>
