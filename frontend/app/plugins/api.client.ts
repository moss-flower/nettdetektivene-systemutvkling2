export default defineNuxtPlugin(() => { 
  const api = $fetch.create({
    baseURL: "/api/v1",
    credentials: 'include',
    
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