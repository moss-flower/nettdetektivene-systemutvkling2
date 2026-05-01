import { getBackendAPI } from "~~/server/utils/backendAPI";

export default defineEventHandler(async (event) => {
  const api = getBackendAPI(event);

  return await api("/notepad/notes", {
    method: "GET",
    query: getQuery(event),
  });
});
