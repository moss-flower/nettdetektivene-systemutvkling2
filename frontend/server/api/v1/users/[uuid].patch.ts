import { getBackendAPI } from "~~/server/utils/backendAPI";

export default defineEventHandler(async (event) => {
  const uuid = getRouterParam(event, "uuid");
  const api = getBackendAPI(event);
  const body = await readBody(event);

  return await api(`/users/${uuid}/username`, {
    method: "PATCH",
    body,
  });
});
