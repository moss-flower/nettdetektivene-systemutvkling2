export default defineNuxtRouteMiddleware(async (to) => {
  if (!to.path.startsWith("/teacher")) {
    return;
  }

  try {
    await $fetch("/api/v1/classrooms", {
      method: "GET",
      credentials: "include",
      headers: import.meta.server ? useRequestHeaders(["cookie"]) : undefined,
    });
  } catch {
    return navigateTo("/");
  }
});
