export default defineNuxtConfig({
  ssr: false,
  css: ['~/assets/css/main.css'],
  devtools: { enabled: true },

  app: {
    head: {
      title: "Nettdetektivene",
      meta: [
        { name: "viewport", content: "width=device-width, initial-scale=1" }
      ],
      link: [
        {
          rel: 'stylesheet',
          href: 'https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@400;600&family=Nunito:wght@400;600;700&display=swap',
        },
      ],
    }

  },

  runtimeConfig: {
    apiBase: import.meta.env.NUXT_API_BASE || 'http://localhost:8080/api/v1',
  },
})
