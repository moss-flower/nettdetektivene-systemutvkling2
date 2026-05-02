export default defineNuxtRouteMiddleware(async (to) => {
  if (!to.path.startsWith("/teacher")) {
    return;
  }

  const { $api } = useNuxtApp();

  try {
    await $api("/classrooms", {
      method: "GET",
      headers: import.meta.server ? useRequestHeaders(["cookie"]) : undefined,
    });
  } catch {
    return navigateTo("/");
  }
});
