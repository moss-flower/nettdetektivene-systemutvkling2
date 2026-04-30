import { getBackendAPI } from "~~/server/utils/backendAPI";
import type { NotepadEntryCreateRequest } from "~/composables/types/NotepadTypes";

export default defineEventHandler(async (event) => {
  const api = getBackendAPI(event);
  const body = await readBody<NotepadEntryCreateRequest>(event);

  return await api("/notepad/create", {
    method: "POST",
    body,
  });
});
