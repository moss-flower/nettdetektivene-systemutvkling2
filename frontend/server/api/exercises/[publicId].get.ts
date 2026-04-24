import { getBackendAPI } from "~~/server/utils/backendAPI";

export default defineEventHandler(async (event) => {
  const api = getBackendAPI(event);
  const publicId = getRouterParam(event, "publicId");

  return await api(`/v1/exercises/${publicId}`, {
    method: "GET",
  });
});
