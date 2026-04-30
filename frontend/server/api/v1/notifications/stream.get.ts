import { getBackendAPI } from "#imports"

export default defineEventHandler(async (event) => {
  const config = useRuntimeConfig(event)
  const backendUrl = `${config.apiBase}/notifications/stream`

  // Forward the request to Spring Boot as a raw stream
  const response = await fetch(backendUrl, {
    headers: {
      cookie: getHeader(event, 'cookie') ?? '',
      authorization: getHeader(event, 'authorization') ?? '',
    },
  })

  // Pass SSE headers through
  setHeader(event, 'Content-Type', 'text/event-stream')
  setHeader(event, 'Cache-Control', 'no-cache')
  setHeader(event, 'Connection', 'keep-alive')

  return sendStream(event, response.body!)
})