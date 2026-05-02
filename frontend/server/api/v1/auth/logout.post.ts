import { getBackendAPI } from "~~/server/utils/backendAPI";

export default defineEventHandler(async (event) => {
  const api = getBackendAPI(event);

  await api("/auth/logout", {
    method: "POST",
  });

  return null;
});
