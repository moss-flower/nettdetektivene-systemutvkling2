import { getBackendAPI } from "~~/server/utils/backendAPI";

export default defineEventHandler(async (event) => {
  const api = getBackendAPI(event);
  const classroomId = getRouterParam(event, "classroomId");

  return await api(`/classrooms/${classroomId}/code`, {
    method: "POST",
  });
});