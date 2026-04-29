import { getBackendAPI } from "~~/server/utils/backendAPI";
import type { NotepadEntryUpdateRequest } from "~/composables/types/NotepadTypes";

export default defineEventHandler(async (event) => {
  const api = getBackendAPI(event);
  const body = await readBody<NotepadEntryUpdateRequest>(event);
  const uuid = getRouterParam(event, "uuid");

  return await api(`/notepad/update/${uuid}`, {
    method: "PUT",
    body,
  });
});
