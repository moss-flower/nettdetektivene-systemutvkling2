import { getBackendAPI } from "~~/server/utils/backendAPI";
import type { AchievementRequest } from "~/composables/types/AchievementTypes";

export default defineEventHandler(async (event) => {
  const api = getBackendAPI(event);
  const body = await readBody<AchievementRequest>(event);

  return await api("/achievements", {
    method: "POST",
    body,
  });
});