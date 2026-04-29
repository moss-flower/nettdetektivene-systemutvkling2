import { getBackendAPI } from "~~/server/utils/backendAPI";

export default defineEventHandler(async (event) => {
  const api = getBackendAPI(event);
  const studentUuid = getRouterParam(event, "studentUuid");

  return await api(`/notepad/students/${studentUuid}/notes/`, {
    method: "GET",
    query: getQuery(event),
  });
});
