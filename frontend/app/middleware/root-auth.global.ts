import { routePaths } from "~/composables/utils/routePaths";

const authHeaders = () => import.meta.server ? useRequestHeaders(["cookie"]) : undefined;

export default defineNuxtRouteMiddleware(async (to) => {
  if (to.path !== routePaths.home) {
    return;
  }

  try {
    await $fetch("/api/v1/classrooms", {
      method: "GET",
      credentials: "include",
      headers: authHeaders(),
    });

    return navigateTo(routePaths.teacherOverview);
  } catch {
    // Not a teacher session. Try student session next.
  }

  try {
    await $fetch("/api/v1/classrooms/me", {
      method: "GET",
      credentials: "include",
      headers: authHeaders(),
    });

    return navigateTo(routePaths.studentHome);
  } catch {
    return;
  }
});
