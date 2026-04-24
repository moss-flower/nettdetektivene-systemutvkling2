import { getBackendAPI } from "~~/server/utils/backendAPI";

export default defineEventHandler(async (event) => {
  const api = getBackendAPI(event);
  const uid = getRouterParam(event, "uid");
  const body = await readBody(event);

  return await api(`/worlds/${uid}`, {
    method: "PUT",
    body,
  });
});
