export default defineNuxtConfig({
  ssr: false,
  css: ['~/assets/css/main.css'],
  devtools: { enabled: true },

  app: {
    head: {
      title: "Nettdetektivene",
      meta: [
        { name: "viewport", content: "width=device-width, initial-scale=1" }
      ]
    }
  },

  runtimeConfig: {
    apiBase: process.env.NUXT_API_BASE || 'http://localhost:8080/api/v1',
  },
})
