import type { Notification } from "./types/NotificationTypes"

const connected = ref(false)
const notifications = ref<Notification[]>([])
let _source: EventSource | null = null

export function useNotifications() {
  function connect() {
    if (_source) return

    _source = new EventSource('/api/v1/notifications/stream')

    _source.onopen = () => {
      connected.value = true
    }

    _source.onerror = () => {
      if (_source?.readyState === EventSource.CLOSED) {
        connected.value = false
        _source?.close()
        _source = null
      } else {
        connected.value = false
      }
    }

    _source.onmessage = (e: MessageEvent) => {
        const data: Notification = JSON.parse(e.data)
        notifications.value.unshift(data)
    }
  }

  function disconnect() {
    _source?.close()
    _source = null
    connected.value = false
  }

  return { connected, notifications, connect, disconnect }
}