<script setup lang="ts">
import type { Notification } from '~/composables/types/NotificationTypes';

const props = defineProps<{ notification: Notification; index: number }>()
const emit = defineEmits<{ dismiss: [index: number] }>()

const { deleteNotification } = useNotifications()

async function dismiss() {
  await deleteNotification(props.notification.notificationUuid)
  emit('dismiss', props.index)
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
  background: rgba(101, 240, 175, 0.06);
  position: relative;
}

.tray__item-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--space-3);
}

.tray__priority {
  font-size: 0.75rem;
  font-weight: 700;
  letter-spacing: 0.06em;
  text-transform: uppercase;
  padding: 0.2rem 0.5rem;
  border-radius: var(--radius-pill);
}

.priority--high {
  background: rgba(239, 68, 68, 0.12);
  color: #ef4444;
}

.priority--medium {
  background: rgba(245, 158, 11, 0.12);
  color: #f59e0b;
}

.priority--low {
  background: rgba(34, 197, 94, 0.12);
  color: #22c55e;
}

.tray__date {
  font-size: 0.8rem;
  color: var(--color-text-secondary);
}

.tray__username {
  font-size: 1rem;
  color: var(--color-text-primary);
}

.tray__message {
  font-size: 0.95rem;
  color: var(--color-text-primary);
  line-height: 1.4;
}

.tray__dismiss {
  position: absolute;
  top: var(--space-3);
  right: var(--space-3);
  background: none;
  border: none;
  cursor: pointer;
  color: var(--color-text-secondary);
  font-size: 0.85rem;
  padding: 0;
  line-height: 1;
  transition: color var(--transition-fast) ease;
}

.tray__dismiss:hover {
  color: #ef4444;
}
</style>