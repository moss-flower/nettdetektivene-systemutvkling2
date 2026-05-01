import type {
  NotepadEntry,
  NotepadEntryCreateRequest,
  NotepadEntryPageResponse,
  NotepadEntryQuery,
  NotepadEntryResponse,
  NotepadEntryType,
  NotepadEntryUpdateRequest,
} from "~/composables/types/NotepadTypes";

function mapNotepadEntryType(type: NotepadEntryResponse["type"]): NotepadEntryType {
  if (type === "TIP") return "tip";
  if (type === "INFO") return "info";
  return "observation";
}

export function mapNotepadEntryFromResponse(entry: NotepadEntryResponse): NotepadEntry {
  return {
    publicUuid: entry.publicUuid,
    title: entry.title,
    date: entry.dateCreated,
    source: entry.source ?? "Unknown source",
    type: mapNotepadEntryType(entry.type),
    content: entry.content,
  };
}

export function useNotepad() {
  const { $api } = useNuxtApp();

  const getMyNotes = async (
    query?: NotepadEntryQuery,
  ): Promise<NotepadEntryPageResponse> => {
    return await $api("/notepad/notes", {
      method: "GET",
      query,
    });
  };

  const getStudentNotes = async (
    studentUuid: string,
    query?: NotepadEntryQuery,
  ): Promise<NotepadEntryPageResponse> => {
    return await $api(`/notepad/students/${studentUuid}/notes`, {
      method: "GET",
      query,
    });
  };

  const createNote = async (
    payload: NotepadEntryCreateRequest,
  ): Promise<NotepadEntryResponse> => {
    return await $api("/notepad/create", {
      method: "POST",
      body: payload,
    });
  };

  const updateNote = async (
    uuid: string,
    payload: NotepadEntryUpdateRequest,
  ): Promise<NotepadEntryResponse> => {
    return await $api(`/notepad/update/${uuid}`, {
      method: "PUT",
      body: payload,
    });
  };

  const deleteNote = async (uuid: string): Promise<void> => {
    await $api(`/notepad/${uuid}`, {
      method: "DELETE",
    });
  };

  return {
    getMyNotes,
    getStudentNotes,
    createNote,
    updateNote,
    deleteNote,
  };
}
