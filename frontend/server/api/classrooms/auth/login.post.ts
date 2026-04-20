import { getBackendAPI } from "~~/server/utils/backendAPI";
import type { LoginRequest, AuthResponse } from "~/composables/types/AuthTypes";

export default defineEventHandler(async (event) => {
  const api = getBackendAPI(event);
  const body = await readBody<LoginRequest>(event);

  return await api<AuthResponse>("/auth/login", {
    method: "POST",
    body,
  });
});