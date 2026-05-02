<script setup lang="ts">
import type { Notification } from '~/composables/types/NotificationTypes';

const props = defineProps<{ notification: Notification }>()

const { deleteNotification } = useNotifications()

async function dismiss() {
  await deleteNotification(props.notification.notificationUuid)
}

function formatDate(date: string) {
  return new Date(date).toLocaleString('nb-NO', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  })
}

function priorityLabel(priority: number) {
  if (priority >= 3) return 'Høy'
  if (priority === 2) return 'Middels'
  return 'Lav'
}

function priorityClass(priority: number) {
  if (priority >= 3) return 'priority--high'
  if (priority === 2) return 'priority--medium'
  return 'priority--low'
}
</script>

<template>
  <li class="tray__item">
    <div class="tray__item-header">
      <span class="tray__priority" :class="priorityClass(notification.priority)">
        {{ priorityLabel(notification.priority) }}
      </span>
      <span class="tray__date">{{ formatDate(notification.timestamp) }}</span>
    </div>
    <span class="tray__username">{{ notification.senderName }}</span>
    <span class="tray__message">{{ notification.message }}</span>
    <button class="tray__dismiss" @click="dismiss">✕</button>
  </li>
</template>

<style scoped>
.tray__item {
  display: grid;
  gap: var(--space-2);
  padding: var(--space-3) var(--space-4);
  border: var(--border-width-hairline) solid var(--color-border-default);
  border-radius: var(--radius-2);
  background: var(--color-bg-surface);
  position: relative;
}

.tray__item-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--space-3);
}

.tray__priority {
  font-size: var(--font-size-12);
  font-weight: var(--font-weight-bold);
  letter-spacing: var(--font-tracking-wider);
  text-transform: uppercase;
  padding: var(--space-1) var(--space-3);
  border-radius: var(--radius-pill);
}

/* deliberate: palette-direct, priority chip */
.priority--high {
  background: var(--palette-overlay-red-soft);
  color: var(--color-semantic-error);
}

.priority--medium {
  background: var(--palette-overlay-gold-soft);
  color: var(--palette-gold-700);
}

.priority--low {
  background: var(--palette-overlay-green-soft);
  color: var(--palette-green-700);
}

.tray__date {
  font-size: var(--font-size-12);
  color: var(--color-text-secondary);
}

.tray__username {
  font-size: var(--font-size-16);
  color: var(--color-text-primary);
}

.tray__message {
  font-size: var(--font-size-14);
  color: var(--color-text-primary);
  line-height: 1.4; /* deliberate: reading-optimised line-height */
}

.tray__dismiss {
  position: absolute;
  top: var(--space-3);
  right: var(--space-3);
  background: none;
  border: none;
  cursor: pointer;
  color: var(--color-text-secondary);
  font-size: var(--font-size-14);
  padding: 0;
  line-height: 1;
  transition: color var(--transition-fast);
}

.tray__dismiss:hover {
  color: var(--color-semantic-error);
}
</style>
