import { getBackendAPI } from "~~/server/utils/backendAPI";

export default defineEventHandler(async (event) => {
  const api = getBackendAPI(event);
  const uid = getRouterParam(event, "uid");

  return await api(`v1/worlds/${uid}`, {
    method: "GET",
  });
});
