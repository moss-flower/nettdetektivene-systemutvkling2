import { routePaths } from "~/composables/utils/routePaths";

const authHeaders = () => import.meta.server ? useRequestHeaders(["cookie"]) : undefined;

export default defineNuxtRouteMiddleware(async (to) => {
  if (to.path !== routePaths.home) {
    return;
  }

  const { $api } = useNuxtApp();

  try {
    await $api("/classrooms", {
      method: "GET",
      headers: authHeaders(),
    });

    return navigateTo(routePaths.teacherOverview);
  } catch {
    // Not a teacher session. Try student session next.
  }

  try {
    const classroom = await $api<{ pendingEntry?: boolean }>("/classrooms/me", {
      method: "GET",
      headers: authHeaders(),
    });

    return navigateTo(classroom.pendingEntry ? routePaths.pending : routePaths.studentHome);
  } catch {
    return;
  }
});
