import { getBackendAPI } from "~~/server/utils/backendAPI";

export default defineEventHandler(async (event) => {
  const api = getBackendAPI(event);
  const achievementUUID = getRouterParam(event, "achievementUUID");

  if (!achievementUUID) {
    throw createError({
      statusCode: 400,
      statusMessage: "Missing achievementUUID",
    });
  }

  return await api(`/achievements/${achievementUUID}`, {
    method: "GET",
  });
});