import type { Notification } from "./types/NotificationTypes";

const connected = ref(false)
const notifications = ref<Notification[]>([])
let _source: EventSource | null = null
let _isLoaded = false

export function useNotifications() {
  const { $api } = useNuxtApp();

  function upsertNotification(notification: Notification) {
    const existingIndex = notifications.value.findIndex(
      ({ notificationUuid }) => notificationUuid === notification.notificationUuid,
    )

    if (existingIndex >= 0) {
      notifications.value.splice(existingIndex, 1)
    }

    notifications.value.unshift(notification)
  }

  function connect() {
    if (_source) return

    _source = new EventSource('/api/v1/notifications/sse')

    _source.onopen = () => {
      connected.value = true
    }

    _source.onerror = () => {
      if (_source?.readyState === EventSource.CLOSED) {
        connected.value = false
        _source?.close()
        _source = null
        _isLoaded = false
      } else {
        connected.value = false
      }
    }

    _source.onmessage = (e: MessageEvent) => {
      const data: Notification = JSON.parse(e.data)
      upsertNotification(data)
    }
  }

  const ensureLoaded = async (): Promise<void> => {
    if (_isLoaded) {
      return
    }

    connect()
    _isLoaded = true
  }

  function disconnect() {
    _source?.close()
    _source = null
    connected.value = false
    _isLoaded = false
  }

  const deleteNotification = async (uuid: string): Promise<void> => {
    await $api(`/notifications/${uuid}`, {
      method: "DELETE",
    });

    notifications.value = notifications.value.filter(
      ({ notificationUuid }) => notificationUuid !== uuid,
    )
  };

  const notificationCount = computed(() => notifications.value.length)

  return {
    connected,
    notifications,
    notificationCount,
    connect,
    ensureLoaded,
    disconnect,
    deleteNotification,
  }
}
