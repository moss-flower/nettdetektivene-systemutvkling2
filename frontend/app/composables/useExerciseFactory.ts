import type {
  EmailHotspotTaskData,
  ExerciseTask,
  ExerciseTemplateKind,
  MultipleChoiceTaskData,
  XOrYTaskData,
} from "~/composables/types/TaskTypes";
import { Exercise } from "~/composables/types/ExerciseType";

export type ExerciseFactoryPayload = {
  title: string;
  entryName: string;
  description: string;
  tasks: ExerciseTask[];
};

export type StoredExerciseFactoryPayload = ExerciseFactoryPayload & {
  localId: string;
  createdAt: string;
};

export const emailHotspotTargetIds = {
  senderName: "sender-name",
  senderAddress: "sender-address",
  receivedLabel: "received-label",
  subject: "subject-line",
  greeting: "greeting",
  bodyText: "body-text",
  bodyLink: "body-link",
  actionButton: "action-button",
  attachment: "attachment",
  closing: "closing",
} as const;

const storageKey = "teacher.exerciseFactory.payloads";
const placeholderImage = "https://placehold.co/1200x720?text=Oppgavebilde";

export const createDraftId = (prefix: string) => {
  if (typeof crypto !== "undefined" && typeof crypto.randomUUID === "function") {
    return `${prefix}-${crypto.randomUUID().slice(0, 8)}`;
  }

  return `${prefix}-${Date.now().toString(36)}-${Math.random().toString(36).slice(2, 8)}`;
};

export const createMultipleChoiceDraft = (index: number): MultipleChoiceTaskData => ({
  id: createDraftId(`multiple-choice-${index}`),
  templateKind: "multiple-choice",
  introText: "Les scenariet og velg det tryggeste svaret.",
  feedbackCorrect: "Riktig. Du valgte det tryggeste alternativet.",
  feedbackIncorrect: "Ikke helt. Se etter tydelige faresignaler og prøv igjen.",
  prompt: {
    title: `Flervalg ${index}`,
    text: "Hva er det beste valget i denne situasjonen?",
    imageUrl: placeholderImage,
    imageAlt: "Illustrasjon for oppgaven",
  },
  options: [
    { key: "a", title: "Trygt svaralternativ" },
    { key: "b", title: "Mistenkelig svaralternativ" },
  ],
  correctChoice: "a",
});

export const createXOrYDraft = (index: number): XOrYTaskData => ({
  id: createDraftId(`x-or-y-${index}`),
  templateKind: "x-or-y",
  introText: "Sammenlign de to alternativene før du velger.",
  feedbackCorrect: "Riktig. Dette alternativet virker mest troverdig.",
  feedbackIncorrect: "Ikke helt. Se etter hva som er mest trygt og troverdig.",
  prompt: {
    title: `X eller Y ${index}`,
    text: "Hvilket alternativ virker tryggest?",
  },
  options: {
    x: {
      imageUrl: placeholderImage,
      imageAlt: "Alternativ X",
      eyebrow: "Alternativ X",
      title: "Trygt eller usikkert?",
      text: "Beskriv hvorfor dette alternativet er trygt eller mistenkelig.",
    },
    y: {
      imageUrl: placeholderImage,
      imageAlt: "Alternativ Y",
      eyebrow: "Alternativ Y",
      title: "Trygt eller usikkert?",
      text: "Beskriv hvorfor dette alternativet er trygt eller mistenkelig.",
    },
  },
  correctChoice: "x",
});

export const createEmailHotspotsDraft = (index: number): EmailHotspotTaskData => ({
  id: createDraftId(`email-hotspots-${index}`),
  templateKind: "email-hotspots",
  introText: "Trykk på de delene av e-posten som virker mistenkelige.",
  feedbackCorrect: "Bra jobbet. Du fant faresignalene i meldingen.",
  feedbackIncorrect: "Ikke helt. Se på avsender, lenke og eventuelle vedlegg.",
  prompt: {
    title: `E-post ${index}`,
    text: "Finn det som gjør e-posten utrygg.",
  },
  email: {
    senderName: "Skole-IT",
    senderNameTargetId: emailHotspotTargetIds.senderName,
    senderAddress: "hjelp@ukjent-avsender.net",
    senderAddressTargetId: emailHotspotTargetIds.senderAddress,
    subject: "Viktig innlogging kreves i dag",
    subjectTargetId: emailHotspotTargetIds.subject,
    senderAvatarText: "IT",
    receivedLabel: "I dag 08:30",
    receivedLabelTargetId: emailHotspotTargetIds.receivedLabel,
    greeting: "Hei!",
    greetingTargetId: emailHotspotTargetIds.greeting,
    bodyBlocks: [
      {
        id: createDraftId("email-text"),
        type: "text",
        text: "Vi har oppdatert kontoen din. Se informasjonen under.",
        targetId: emailHotspotTargetIds.bodyText,
      },
      {
        id: createDraftId("email-link"),
        type: "link",
        label: "Oppdater kontoen din",
        urlText: "skole-login-verify.click",
        helperText: "Svar innen 30 minutter",
        targetId: emailHotspotTargetIds.bodyLink,
      },
    ],
    actionButton: {
      label: "Les mer",
      helperText: "skoleportalen.no",
      targetId: emailHotspotTargetIds.actionButton,
    },
    attachments: [
      {
        id: createDraftId("email-attachment"),
        name: "informasjon.pdf",
        details: "Trygg filtype",
        targetId: emailHotspotTargetIds.attachment,
      },
    ],
    closing: "Hilsen skoleadministrasjonen",
    closingTargetId: emailHotspotTargetIds.closing,
  },
  targets: [
    { id: emailHotspotTargetIds.senderName, isSuspicious: false },
    { id: emailHotspotTargetIds.senderAddress, isSuspicious: true },
    { id: emailHotspotTargetIds.receivedLabel, isSuspicious: false },
    { id: emailHotspotTargetIds.subject, isSuspicious: false },
    { id: emailHotspotTargetIds.greeting, isSuspicious: false },
    { id: emailHotspotTargetIds.bodyText, isSuspicious: false },
    { id: emailHotspotTargetIds.bodyLink, isSuspicious: true },
    { id: emailHotspotTargetIds.actionButton, isSuspicious: false },
    { id: emailHotspotTargetIds.attachment, isSuspicious: false },
    { id: emailHotspotTargetIds.closing, isSuspicious: false },
  ],
});

export const createTaskDraft = (kind: ExerciseTemplateKind, index: number): ExerciseTask => {
  if (kind === "multiple-choice") {
    return createMultipleChoiceDraft(index);
  }

  if (kind === "email-hotspots") {
    return createEmailHotspotsDraft(index);
  }

  return createXOrYDraft(index);
};

export const createExerciseDraft = (): ExerciseFactoryPayload => ({
  title: "",
  entryName: "",
  description: "",
  tasks: [],
});

const canUseStorage = () => typeof window !== "undefined" && typeof window.localStorage !== "undefined";

const normalizeStoredPayload = (payload: Record<string, unknown>): StoredExerciseFactoryPayload | null => {
  const title = typeof payload.title === "string" ? payload.title : "";
  const entryName = typeof payload.entryName === "string"
    ? payload.entryName
    : typeof payload.difficultyText === "string"
      ? payload.difficultyText
      : "";
  const description = typeof payload.description === "string" ? payload.description : "";
  const tasks = Array.isArray(payload.tasks) ? payload.tasks as ExerciseTask[] : [];
  const localId = typeof payload.localId === "string" ? payload.localId : createDraftId("exercise");
  const createdAt = typeof payload.createdAt === "string" ? payload.createdAt : new Date().toISOString();

  if (!title && !entryName && !description && tasks.length === 0) {
    return null;
  }

  return {
    title,
    entryName,
    description,
    tasks,
    localId,
    createdAt,
  };
};

export const readStoredExercisePayloads = (): StoredExerciseFactoryPayload[] => {
  if (!canUseStorage()) {
    return [];
  }

  const raw = window.localStorage.getItem(storageKey);

  if (!raw) {
    return [];
  }

  try {
    const parsed = JSON.parse(raw) as Record<string, unknown>[];

    if (!Array.isArray(parsed)) {
      return [];
    }

    return parsed
      .map((payload) => normalizeStoredPayload(payload))
      .filter((payload): payload is StoredExerciseFactoryPayload => payload !== null);
  } catch {
    return [];
  }
};

export const storeExercisePayload = (payload: ExerciseFactoryPayload): StoredExerciseFactoryPayload => {
  const storedPayload: StoredExerciseFactoryPayload = {
    ...payload,
    localId: createDraftId("exercise"),
    createdAt: new Date().toISOString(),
  };

  const storedPayloads = [storedPayload, ...readStoredExercisePayloads()];

  if (canUseStorage()) {
    window.localStorage.setItem(storageKey, JSON.stringify(storedPayloads));
  }

  return storedPayload;
};

export const removeStoredExercisePayload = (localId: string) => {
  const remainingPayloads = readStoredExercisePayloads().filter((payload) => payload.localId !== localId);

  if (canUseStorage()) {
    window.localStorage.setItem(storageKey, JSON.stringify(remainingPayloads));
  }

  return remainingPayloads;
};

export type ExerciseFactoryStoredRecord = {
  localId: string;
  sourceUid?: string;
  payload: ExerciseFactoryPayload;
  createdAt: string;
  updatedAt: string;
};

type ExerciseFactoryStore = {
  overrides: Record<string, ExerciseFactoryStoredRecord>;
  drafts: ExerciseFactoryStoredRecord[];
};

export type ExerciseFactorySelectableOption = {
  selectionKey: string;
  label: string;
  description: string;
  kind: "existing" | "draft";
  badge: string;
};

export const exerciseFactoryNewSelectionKey = "__new_exercise__";

const recordStorageKey = "teacher.exerciseFactory.records";

const normalizeExerciseFactoryRecord = (record: Record<string, unknown>): ExerciseFactoryStoredRecord | null => {
  const payload = normalizeStoredPayload(record);

  if (!payload) {
    return null;
  }

  return {
    localId: payload.localId,
    sourceUid: typeof record.sourceUid === "string" && record.sourceUid.trim().length > 0 ? record.sourceUid.trim() : undefined,
    payload: {
      title: payload.title,
      entryName: payload.entryName,
      description: payload.description,
      tasks: payload.tasks,
    },
    createdAt: payload.createdAt,
    updatedAt: typeof record.updatedAt === "string"
      ? record.updatedAt
      : payload.createdAt,
  };
};

export const readExerciseFactoryStore = (): ExerciseFactoryStore => {
  if (!canUseStorage()) {
    return {
      overrides: {},
      drafts: [],
    };
  }

  const raw = window.localStorage.getItem(recordStorageKey);

  if (!raw) {
    return {
      overrides: {},
      drafts: readStoredExercisePayloads().map((payload) => ({
        localId: payload.localId,
        payload: {
          title: payload.title,
          entryName: payload.entryName,
          description: payload.description,
          tasks: payload.tasks,
        },
        createdAt: payload.createdAt,
        updatedAt: payload.createdAt,
      })),
    };
  }

  try {
    const parsed = JSON.parse(raw) as Record<string, unknown>;

    if (!parsed || typeof parsed !== "object") {
      return {
        overrides: {},
        drafts: [],
      };
    }

    const rawOverrides = parsed.overrides;
    const rawDrafts = parsed.drafts;

    const overrides = rawOverrides && typeof rawOverrides === "object"
      ? Object.entries(rawOverrides as Record<string, unknown>).reduce<Record<string, ExerciseFactoryStoredRecord>>(
        (accumulator, [uid, record]) => {
          if (!record || typeof record !== "object") {
            return accumulator;
          }

          const normalized = normalizeExerciseFactoryRecord({ ...record, sourceUid: uid });

          if (normalized) {
            accumulator[uid] = normalized;
          }

          return accumulator;
        },
        {},
      )
      : {};

    const drafts = Array.isArray(rawDrafts)
      ? rawDrafts
        .map((record) => (record && typeof record === "object") ? normalizeExerciseFactoryRecord(record as Record<string, unknown>) : null)
        .filter((record): record is ExerciseFactoryStoredRecord => record !== null && !record.sourceUid)
      : [];

    return {
      overrides,
      drafts,
    };
  } catch {
    return {
      overrides: {},
      drafts: [],
    };
  }
};

const writeExerciseFactoryStore = (store: ExerciseFactoryStore) => {
  if (!canUseStorage()) {
    return;
  }

  window.localStorage.setItem(recordStorageKey, JSON.stringify(store));
};

export const saveExerciseFactoryOverride = (sourceUid: string, payload: ExerciseFactoryPayload): ExerciseFactoryStoredRecord => {
  const store = readExerciseFactoryStore();
  const existing = store.overrides[sourceUid];
  const record: ExerciseFactoryStoredRecord = {
    localId: existing?.localId ?? createDraftId("exercise-override"),
    sourceUid,
    payload,
    createdAt: existing?.createdAt ?? new Date().toISOString(),
    updatedAt: new Date().toISOString(),
  };

  store.overrides[sourceUid] = record;
  writeExerciseFactoryStore(store);

  return record;
};

export const clearExerciseFactoryOverride = (sourceUid: string) => {
  const store = readExerciseFactoryStore();

  delete store.overrides[sourceUid];
  writeExerciseFactoryStore(store);
};

export const saveExerciseFactoryDraft = (payload: ExerciseFactoryPayload, localId?: string): ExerciseFactoryStoredRecord => {
  const store = readExerciseFactoryStore();
  const existing = localId ? store.drafts.find((draft) => draft.localId === localId) ?? null : null;
  const record: ExerciseFactoryStoredRecord = {
    localId: existing?.localId ?? createDraftId("exercise"),
    payload,
    createdAt: existing?.createdAt ?? new Date().toISOString(),
    updatedAt: new Date().toISOString(),
  };

  const nextDrafts = store.drafts.filter((draft) => draft.localId !== record.localId);
  nextDrafts.unshift(record);
  store.drafts = nextDrafts;
  writeExerciseFactoryStore(store);

  return record;
};

export const removeExerciseFactoryDraft = (localId: string) => {
  const store = readExerciseFactoryStore();

  store.drafts = store.drafts.filter((draft) => draft.localId !== localId);
  writeExerciseFactoryStore(store);
};

export const makeExerciseFactoryExistingSelectionKey = (uid: string) => `existing:${uid}`;

export const makeExerciseFactoryDraftSelectionKey = (localId: string) => `draft:${localId}`;

export const parseExerciseFactorySelectionKey = (selectionKey: string) => {
  if (!selectionKey || selectionKey === exerciseFactoryNewSelectionKey) {
    return { kind: "new" as const };
  }

  if (selectionKey.startsWith("existing:")) {
    return {
      kind: "existing" as const,
      uid: selectionKey.slice("existing:".length),
    };
  }

  if (selectionKey.startsWith("draft:")) {
    return {
      kind: "draft" as const,
      localId: selectionKey.slice("draft:".length),
    };
  }

  return { kind: "new" as const };
};

export const getExerciseFactorySelectionOptions = (): ExerciseFactorySelectableOption[] => {
  const store = readExerciseFactoryStore();
  const existingOptions = Exercise.backendFetchAllExercises().map((exercise) => ({
    selectionKey: makeExerciseFactoryExistingSelectionKey(exercise.uid),
    label: `${exercise.title} (${exercise.entryName})`,
    description: exercise.uid,
    kind: "existing" as const,
    badge: store.overrides[exercise.uid] ? "Eksisterende oppgave · lokal override" : "Eksisterende oppgave",
  }));
  const draftOptions = [...store.drafts]
    .sort((left, right) => right.updatedAt.localeCompare(left.updatedAt))
    .map((draft) => ({
      selectionKey: makeExerciseFactoryDraftSelectionKey(draft.localId),
      label: draft.payload.title || "Ny oppgave uten tittel",
      description: draft.payload.entryName || draft.localId,
      kind: "draft" as const,
      badge: "Ny lokal oppgave",
    }));

  return [...existingOptions, ...draftOptions];
};