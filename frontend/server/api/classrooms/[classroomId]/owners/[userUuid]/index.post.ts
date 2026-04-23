import { getBackendAPI } from "~~/server/utils/backendAPI";

export default defineEventHandler(async (event) => {
  const api = getBackendAPI(event);
  const classroomId = getRouterParam(event, "classroomId");
  const userUuid = getRouterParam(event, "userUuid");

  return await api(`/classrooms/${classroomId}/owners/${userUuid}`, {
    method: "POST",
  });
});