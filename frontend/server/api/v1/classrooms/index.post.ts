import { getBackendAPI } from "~~/server/utils/backendAPI";
import type { ClassroomRequest } from "~/composables/types/ClassroomTypes";

export default defineEventHandler(async (event) => {
  const api = getBackendAPI(event);
  const body = await readBody<ClassroomRequest>(event);

  console.log("Reached route");

  return await api("classrooms", {
    method: "POST",
    body,
  });
});