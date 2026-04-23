import { getBackendAPI } from "~~/server/utils/backendAPI";

export default defineEventHandler(async (event) => {
  const api = getBackendAPI(event);

  console.log("REACHED ROUTE GET ACHIEVMEENTS UNLOCKED")

  return await api("/v1/achievements/unlocked", {
    method: "GET",
  });
});