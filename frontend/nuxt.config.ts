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
    apiBase: 'http://localhost:8080/api',
    public: {
    },
  },
})
