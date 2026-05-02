export default defineEventHandler(async (event) => {
  const config = useRuntimeConfig(event)
  const backendUrl = `${config.apiBase}/notifications/sse`

  const response = await fetch(backendUrl, {
    headers: {
      cookie: getHeader(event, 'cookie') ?? '',
      authorization: getHeader(event, 'authorization') ?? '',
      'Accept': 'text/event-stream',
    },
  })

  if (!response.body) {
    throw createError({ statusCode: 502, message: 'No response body from backend' })
  }

  setHeader(event, 'Content-Type', 'text/event-stream')
  setHeader(event, 'Cache-Control', 'no-cache')
  setHeader(event, 'Connection', 'keep-alive')
  setHeader(event, 'X-Accel-Buffering', 'no')

  return sendStream(event, response.body)
})