import { getBackendAPI } from "~~/server/utils/backendAPI";
import type { RegisterRequest, AuthResponse } from "~/composables/types/AuthTypes";

export default defineEventHandler(async (event) => {
  const api = getBackendAPI(event);
  const body = await readBody<RegisterRequest>(event);

  return await api<AuthResponse>("/auth/register/teacher", {
    method: "POST",
    body,
  });
});