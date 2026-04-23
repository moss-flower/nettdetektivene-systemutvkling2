import { getBackendAPI } from "~~/server/utils/backendAPI";

export default defineEventHandler(async (event) => {
  const api = getBackendAPI(event);
  const code = getRouterParam(event, "code");

  return await api(`/classrooms/join/${code}`, {
    method: "POST",
  });
});
