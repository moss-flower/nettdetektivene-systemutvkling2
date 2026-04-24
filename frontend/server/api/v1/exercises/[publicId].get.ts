import { getBackendAPI } from "~~/server/utils/backendAPI";

export default defineEventHandler(async (event) => {
  const api = getBackendAPI(event);
  const publicId = getRouterParam(event, "publicId");

  return await api(`/exercises/${publicId}`, {
    method: "GET",
  });
});
