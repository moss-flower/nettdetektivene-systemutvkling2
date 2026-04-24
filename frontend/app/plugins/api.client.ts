export default defineNuxtPlugin(() => { 
  const api = $fetch.create({
    baseURL: "/api/v1",
    credentials: 'include',
    
  })

  return {
    provide: {
      api,
    },
  }
})