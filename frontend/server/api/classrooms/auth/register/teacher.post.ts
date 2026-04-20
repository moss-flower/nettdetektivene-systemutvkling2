import { getBackendAPI } from "~~/server/utils/backendAPI";
import type { StudentRegisterRequest, StudentRegisterResponse } from "~/composables/types/AuthTypes";

export default defineEventHandler(async (event) => {
  const api = getBackendAPI(event);
  const body = await readBody<StudentRegisterRequest>(event);

  return await api<StudentRegisterResponse>("/auth/register/teacher", {
    method: "POST",
    body,
  });
});