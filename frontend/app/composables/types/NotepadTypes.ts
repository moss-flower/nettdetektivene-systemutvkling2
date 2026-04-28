export type NotepadEntry = {
  publicUuid: string;
  title: string;
  date: string;
  source: string;
  type: "tip" | "info" | "observation";
  content: string;
};

export type NotepadEntryType = NotepadEntry["type"];

export type BackendNotepadEntryType = "TIP" | "INFO" | "OBSERVATION";

export type NotepadEntryResponse = {
  publicUuid: string;
  title: string;
  dateCreated: string;
  source: string | null;
  type: BackendNotepadEntryType;
  content: string;
};

export type NotepadEntryPageResponse = {
  content: NotepadEntryResponse[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  empty: boolean;
};

export type NotepadEntryQuery = {
  page?: number;
  size?: number;
};

export type NotepadEntryCreateRequest = {
  title: string;
  type: BackendNotepadEntryType;
  content: string;
  source: string;
};

export type NotepadEntryUpdateRequest = NotepadEntryCreateRequest;
