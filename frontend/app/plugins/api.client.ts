export default defineNuxtPlugin(() => {
  const api = $fetch.create({
    baseURL: '/api',
    credentials: 'include',
  })

  return {
    provide: {
      api,
    },
  }
})