import { getBackendAPI } from "~~/server/utils/backendAPI";

export default defineEventHandler(async (event) => {
  const api = getBackendAPI(event);
  const body = await readBody(event);

  return await api("/exercises", {
    method: "POST",
    body,
  });
});
