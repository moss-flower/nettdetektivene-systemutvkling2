import { getBackendAPI } from "~~/server/utils/backendAPI";

export default defineEventHandler(async (event) => {
  const api = getBackendAPI(event);
  const response = await api.raw("/auth/logout", {
    method: "POST",
  });

  const setCookies =
    typeof response.headers.getSetCookie === "function"
      ? response.headers.getSetCookie()
      : [];

  for (const cookie of setCookies) {
    appendResponseHeader(event, "set-cookie", cookie);
  }

  return null;
});
