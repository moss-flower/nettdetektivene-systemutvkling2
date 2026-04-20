export default defineNuxtPlugin(() => {
  const config = useRuntimeConfig()
  const apiBase = String(config.public.apiBase)

  const api = $fetch.create({
    baseURL: apiBase,

    onRequest({ options }) {
      const token = sessionStorage.getItem('token')

      if (token) {
        const headers = new Headers(options.headers || {})
        headers.set('Authorization', `Bearer ${token}`)
        options.headers = headers
      }
    },
  })

  return {
    provide: {
      api,
    },
  }
})