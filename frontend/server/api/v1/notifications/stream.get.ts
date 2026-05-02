export default defineEventHandler(async (event) => {
  const config = useRuntimeConfig(event)
  const backendUrl = `${config.apiBase}/notifications/stream`

  const response = await fetch(backendUrl, {
    headers: {
      cookie: getHeader(event, 'cookie') ?? '',
      authorization: getHeader(event, 'authorization') ?? '',
    },
  })

  // Copy all headers from the backend response directly
  response.headers.forEach((value, key) => {
    setHeader(event, key, value)
  })
  const reader = response.body!.getReader()
  const first = await reader.read()
  console.log('[SSE Proxy] first chunk:', new TextDecoder().decode(first.value))

  return sendStream(event, response.body!)
})