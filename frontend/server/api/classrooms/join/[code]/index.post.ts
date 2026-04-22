import { getBackendAPI } from "~~/server/utils/backendAPI";

export default defineEventHandler(async (event) => {
  const api = getBackendAPI(event);
  const code = getRouterParam(event, "code");

  return await api(`/v1/classrooms/join/${code}`, {
    method: "POST",
  });
});