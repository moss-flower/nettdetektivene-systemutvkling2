<script setup lang="ts">
import { useNotifications } from '~/composables/useNotifications'

const { connected, notifications } = useNotifications()

function onDismiss(index: number) {
  notifications.value.splice(index, 1)
}
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
        v-for="(n, i) in notifications"
        :key="n.notificationUuid"
        :notification="n"
        :index="i"
        @dismiss="onDismiss"
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
  font-size: 0.8rem;
  font-weight: 700;
  letter-spacing: 0.06em;
  text-transform: uppercase;
  color: var(--color-text-secondary);
}

.tray__status--connected {
  color: #22c55e;
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
  font-size: 0.9rem;
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