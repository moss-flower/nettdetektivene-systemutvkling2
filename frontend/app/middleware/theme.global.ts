export default defineNuxtRouteMiddleware((to) => {
    if (import.meta.server) return
    const theme = to.path.startsWith('/teacher') ? 'teacher' : 'game'
    document.documentElement.dataset.theme = theme
})