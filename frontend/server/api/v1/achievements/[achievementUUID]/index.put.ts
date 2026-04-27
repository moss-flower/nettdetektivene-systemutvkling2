import { getBackendAPI } from "~~/server/utils/backendAPI";
import type { AchievementRequest } from "~/composables/types/AchievementTypes";

export default defineEventHandler(async (event) => {
  const api = getBackendAPI(event);
  const achievementUUID = getRouterParam(event, "achievementUUID");
  const body = await readBody<AchievementRequest>(event);

  if (!achievementUUID) {
    throw createError({
      statusCode: 400,
      statusMessage: "Missing achievementUUID",
    });
  }

  return await api(`/achievements/${achievementUUID}`, {
    method: "PUT",
    body,
  });
});