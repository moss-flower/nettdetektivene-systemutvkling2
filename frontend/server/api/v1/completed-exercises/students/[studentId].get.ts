import { getBackendAPI } from "~~/server/utils/backendAPI";

export default defineEventHandler(async (event) => {
  const api = getBackendAPI(event);
  const studentId = getRouterParam(event, "studentId");

  return await api(`/completed-exercises/students/${studentId}`, {
    method: "GET",
  });
});
