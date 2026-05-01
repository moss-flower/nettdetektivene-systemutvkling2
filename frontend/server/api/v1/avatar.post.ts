import { getBackendAPI } from "~~/server/utils/backendAPI";
import type { AvatarConfig } from "~/composables/types/AvatarTypes";

export default defineEventHandler(async (event) => {
  const api = getBackendAPI(event);
  const body = await readBody<AvatarConfig>(event);

  return await api("/avatar", {
    method: "POST",
    body,
  });
});
