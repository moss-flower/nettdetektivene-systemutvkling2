export default defineEventHandler(async (event) => {
  const config = useRuntimeConfig(event)
  const backendUrl = `${config.apiBase}/notifications/stream`

  const response = await fetch(backendUrl, {
    headers: {
      cookie: getHeader(event, 'cookie') ?? '',
      authorization: getHeader(event, 'authorization') ?? '',
    },
  })

  setHeader(event, 'Content-Type', 'text/event-stream')
  setHeader(event, 'Cache-Control', 'no-cache')
  setHeader(event, 'Connection', 'keep-alive')

  return sendStream(event, response.body!)
})