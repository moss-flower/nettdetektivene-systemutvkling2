import { getBackendAPI } from "~~/server/utils/backendAPI";
import type { LoginRequest, AuthResponse } from "~/composables/types/AuthTypes";

export default defineEventHandler(async (event) => {
  const api = getBackendAPI(event);
  const body = await readBody<LoginRequest>(event);

  console.log("REACHED ROUTE")

  const response = await api.raw<AuthResponse>("/auth/login", {
    method: "POST",
    body,
  });

  const setCookies =
    typeof response.headers.getSetCookie === "function"
      ? response.headers.getSetCookie()
      : [];

  for (const cookie of setCookies) {
    appendResponseHeader(event, "set-cookie", cookie);
  }

  return response._data;
});