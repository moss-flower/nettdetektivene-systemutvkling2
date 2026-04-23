import { getBackendAPI } from "~~/server/utils/backendAPI";

export default defineEventHandler(async (event) => {
  const api = getBackendAPI(event);
  const body = await readBody(event);

  return await api("/completed-exercises", {
    method: "POST",
    body,
  });
});
