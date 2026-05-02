import type { Notification } from "./types/NotificationTypes";

const connected = ref(false)
const notifications = ref<Notification[]>([])
let _source: EventSource | null = null

export function useNotifications() {
    const {$api} = useNuxtApp();
  function connect() {
    if (_source) return

    _source = new EventSource('/api/v1/notifications/stream')

    _source.onopen = () => {
      console.log("JARJAR says ONOPEN")
      connected.value = true
    }

    _source.onerror = (e) => {
      console.log("JARJAR SAYS ON ERROR")
      console.log('[SSE] onerror fired')
      console.log('[SSE] readyState:', _source?.readyState)
      console.log('[SSE] event type:', e.type)
      console.log('[SSE] full event:', JSON.stringify(e))
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
        console.log("JARJAR says ON MESSAGE")
    }
  }

  function disconnect() {
    _source?.close()
    _source = null
    connected.value = false
  }

  const deleteNotification = async (uuid: string): Promise<void> => {
    await $api(`/notifications/${uuid}`, {
      method: "DELETE",
    });
  };

  return { connected, notifications, connect, disconnect, deleteNotification}
}