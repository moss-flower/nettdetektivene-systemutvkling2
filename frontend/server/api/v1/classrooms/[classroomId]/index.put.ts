import { getBackendAPI } from "~~/server/utils/backendAPI";
import type { ClassroomRequest } from "~/composables/types/ClassroomTypes";

export default defineEventHandler(async (event) => {
  const api = getBackendAPI(event);
  const classroomId = getRouterParam(event, "classroomId");
  const body = await readBody<ClassroomRequest>(event);

  return await api(`/classrooms/${classroomId}`, {
    method: "PUT",
    body,
  });
});
