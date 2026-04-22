import { getBackendAPI } from "~~/server/utils/backendAPI";
import type { RegisterRequest, AuthResponse } from "~/composables/types/AuthTypes.ts";

export default defineEventHandler(async (event) => {
  const api = getBackendAPI(event);
  const body = await readBody<RegisterRequest>(event);

  return await api<AuthResponse>("/register/student", {
    method: "POST",
    body,
  });
});