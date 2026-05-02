import { getBackendAPI } from "~~/server/utils/backendAPI";

export default defineEventHandler(async (event) => {
  const api = getBackendAPI(event);
  const uid = getRouterParam(event, "uid");

  return await api(`/exercises/${uid}`, {
    method: "DELETE",
  });
});
