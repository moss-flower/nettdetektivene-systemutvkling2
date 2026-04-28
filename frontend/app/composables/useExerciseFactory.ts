import type {
  EmailHotspotTaskData,
  ExerciseTask,
  ExerciseTemplateKind,
  MultipleChoiceTaskData,
  PasswordStrengthTaskData,
  SortIntoBucketsTaskData,
  XOrYTaskData,
} from "~/composables/types/TaskTypes";
import { Exercise } from "~/composables/types/ExerciseType";

export type ExerciseFactoryPayload = {
  title: string;
  difficultyName: string;
  description: string;
  tasks: ExerciseTask[];
};

export type BackendExerciseRequestPayload = {
  title: string;
  difficultyName: string;
  description: string;
  tasks: BackendTaskRequestPayload[];
};

type BackendExerciseResponsePayload = {
  uid: string;
  title: string;
  difficultyName: string;
  description?: string;
  tasks: Array<{
    internalId?: number | null;
    templateKind: ExerciseTemplateKind;
    introText: string;
    feedbackCorrect: string;
    feedbackIncorrect: string;
    content: Record<string, unknown>;
  }>;
};

export type BackendTaskRequestPayload = {
  introText: string;
  feedbackCorrect: string;
  feedbackIncorrect: string;
  content: Record<string, unknown>;
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
const placeholderImage = "/images/placeholders/task.svg";

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

export const createSortIntoBucketsDraft = (index: number): SortIntoBucketsTaskData => ({
  id: createDraftId(`sort-into-buckets-${index}`),
  templateKind: "sort-into-buckets",
  introText: "Sorter kortene i riktig boks.",
  feedbackCorrect: "Riktig. Du sorterte kortene riktig.",
  feedbackIncorrect: "Ikke helt. Se om alle kortene ligger i riktig boks.",
  prompt: {
    title: `Sorter ${index}`,
    text: "Dra eller trykk kortene inn i riktig kategori.",
  },
  unplacedLabel: "Kort som skal sorteres",
  buckets: [
    {
      key: "bucket-a",
      title: "Kategori A",
      description: "Første kategori.",
    },
    {
      key: "bucket-b",
      title: "Kategori B",
      description: "Andre kategori.",
    },
  ],
  items: [
    {
      id: createDraftId("sortable-item"),
      label: "Eksempel 1",
      correctBucketKey: "bucket-a",
      explanation: "Hvorfor dette kortet hører hit.",
    },
    {
      id: createDraftId("sortable-item"),
      label: "Eksempel 2",
      correctBucketKey: "bucket-b",
      explanation: "Hvorfor dette kortet hører hit.",
    },
  ],
});

export const createPasswordStrengthDraft = (index: number): PasswordStrengthTaskData => ({
  id: createDraftId(`password-strength-${index}`),
  templateKind: "password-strength",
  introText: "Skriv et passord som oppgaven godkjenner.",
  feedbackCorrect: "Bra jobbet. Passordet oppfylte kravene i oppgaven.",
  feedbackIncorrect: "Passordet trenger mer arbeid før det blir godkjent.",
  prompt: {
    title: `Passord ${index}`,
    text: "Lag eller forbedre et passord.",
    helperText: "Bruk mange tegn og bland bokstaver, tall og symbol.",
  },
  mode: "create",
  placeholder: "Skriv passordet her",
  minLength: 10,
  minScoreToPass: 4,
  commonPatterns: ["123", "qwerty", "passord"],
});

export const createTaskDraft = (kind: ExerciseTemplateKind, index: number): ExerciseTask => {
  if (kind === "multiple-choice") {
    return createMultipleChoiceDraft(index);
  }

  if (kind === "email-hotspots") {
    return createEmailHotspotsDraft(index);
  }

  if (kind === "sort-into-buckets") {
    return createSortIntoBucketsDraft(index);
  }

  if (kind === "password-strength") {
    return createPasswordStrengthDraft(index);
  }

  return createXOrYDraft(index);
};

export const createExerciseDraft = (): ExerciseFactoryPayload => ({
  title: "",
  difficultyName: "",
  description: "",
  tasks: [],
});

const canUseStorage = () => typeof window !== "undefined" && typeof window.localStorage !== "undefined";

const normalizeStoredPayload = (payload: Record<string, unknown>): StoredExerciseFactoryPayload | null => {
  const title = typeof payload.title === "string" ? payload.title : "";
  const difficultyName = typeof payload.difficultyName === "string"
    ? payload.difficultyName
    : typeof payload.entryName === "string"
      ? payload.entryName
    : typeof payload.difficultyText === "string"
      ? payload.difficultyText
      : "";
  const description = typeof payload.description === "string" ? payload.description : "";
  const tasks = Array.isArray(payload.tasks) ? payload.tasks as ExerciseTask[] : [];
  const localId = typeof payload.localId === "string" ? payload.localId : createDraftId("exercise");
  const createdAt = typeof payload.createdAt === "string" ? payload.createdAt : new Date().toISOString();

  if (!title && !difficultyName && !description && tasks.length === 0) {
    return null;
  }

  return {
    title,
    difficultyName,
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
      difficultyName: payload.difficultyName,
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
          difficultyName: payload.difficultyName,
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
  const existingOptions = Object.values(store.overrides).map((record) => ({
    selectionKey: makeExerciseFactoryExistingSelectionKey(record.sourceUid ?? record.localId),
    label: `${record.payload.title} (${record.payload.difficultyName})`,
    description: record.sourceUid ?? record.localId,
    kind: "existing" as const,
    badge: "Eksisterende oppgave · lokal override",
  }));
  const draftOptions = [...store.drafts]
    .sort((left, right) => right.updatedAt.localeCompare(left.updatedAt))
    .map((draft) => ({
      selectionKey: makeExerciseFactoryDraftSelectionKey(draft.localId),
      label: draft.payload.title || "Ny oppgave uten tittel",
      description: draft.payload.difficultyName || draft.localId,
      kind: "draft" as const,
      badge: "Ny lokal oppgave",
    }));

  return [...existingOptions, ...draftOptions];
};

export const getExerciseFactorySelectionOptionsFromExercises = (
  exercises: Exercise[],
): ExerciseFactorySelectableOption[] => {
  const store = readExerciseFactoryStore();
  const existingOptions = exercises.map((exercise) => ({
    selectionKey: makeExerciseFactoryExistingSelectionKey(exercise.uid),
    label: `${exercise.title} (${exercise.difficultyName})`,
    description: exercise.uid,
    kind: "existing" as const,
    badge: store.overrides[exercise.uid] ? "Eksisterende oppgave · lokal override" : "Eksisterende oppgave",
  }));
  const draftOptions = [...store.drafts]
    .sort((left, right) => right.updatedAt.localeCompare(left.updatedAt))
    .map((draft) => ({
      selectionKey: makeExerciseFactoryDraftSelectionKey(draft.localId),
      label: draft.payload.title || "Ny oppgave uten tittel",
      description: draft.payload.difficultyName || draft.localId,
      kind: "draft" as const,
      badge: "Ny lokal oppgave",
    }));

  return [...existingOptions, ...draftOptions];
};

const toExercisePayloadFromBackend = (exercise: Exercise): ExerciseFactoryPayload => ({
  title: exercise.title,
  difficultyName: exercise.difficultyName,
  description: exercise.description ?? "",
  tasks: exercise.tasks,
});

export const loadExerciseFactoryPayload = async (selectionKey: string): Promise<{
  payload: ExerciseFactoryPayload;
  localId: string;
}> => {
  const selection = parseExerciseFactorySelectionKey(selectionKey);
  const store = readExerciseFactoryStore();

  if (selection.kind === "existing") {
    const override = store.overrides[selection.uid] ?? null;

    if (override) {
      return {
        payload: {
          ...override.payload,
          tasks: [...override.payload.tasks],
        },
        localId: "",
      };
    }

    const backendExercise = await Exercise.backendFetchExerciseByUid(selection.uid);

    return {
      payload: backendExercise ? toExercisePayloadFromBackend(backendExercise) : createExerciseDraft(),
      localId: "",
    };
  }

  if (selection.kind === "draft") {
    const storedDraft = store.drafts.find((draft) => draft.localId === selection.localId) ?? null;

    return {
      payload: storedDraft?.payload ?? createExerciseDraft(),
      localId: storedDraft?.localId ?? "",
    };
  }

  return {
    payload: createExerciseDraft(),
    localId: "",
  };
};

export const fetchAllExerciseFactoryExercises = () => Exercise.backendFetchAllExercises();

export const saveExerciseFactoryPayload = async (
  selectionKey: string,
  payload: ExerciseFactoryPayload,
): Promise<{ selectionKey: string; payload: ExerciseFactoryPayload }> => {
  const selection = parseExerciseFactorySelectionKey(selectionKey);
  const requestBody = mapExerciseToBackendRequest(payload);
  const { $api } = useNuxtApp();

  if (selection.kind === "existing") {
    const response = await $api<BackendExerciseResponsePayload>(`/exercises/${selection.uid}`, {
      method: "PUT",
      body: requestBody,
    });

    clearExerciseFactoryOverride(selection.uid);

    return {
      selectionKey: makeExerciseFactoryExistingSelectionKey(response.uid),
      payload: {
        title: response.title,
        difficultyName: response.difficultyName,
        description: response.description ?? "",
        tasks: payload.tasks,
      },
    };
  }

  const response = await $api<BackendExerciseResponsePayload>("/exercises", {
    method: "POST",
    body: requestBody,
  });

  if (selection.kind === "draft") {
    removeExerciseFactoryDraft(selection.localId);
  }

  return {
    selectionKey: makeExerciseFactoryExistingSelectionKey(response.uid),
    payload: {
      title: response.title,
      difficultyName: response.difficultyName,
      description: response.description ?? "",
      tasks: payload.tasks,
    },
  };
};

export const saveExerciseFactoryPayloadAsNew = async (
  payload: ExerciseFactoryPayload,
): Promise<{ selectionKey: string; payload: ExerciseFactoryPayload }> => {
  const { $api } = useNuxtApp();
  const response = await $api<BackendExerciseResponsePayload>("/exercises", {
    method: "POST",
    body: mapExerciseToBackendRequest(payload),
  });

  return {
    selectionKey: makeExerciseFactoryExistingSelectionKey(response.uid),
    payload: {
      title: response.title,
      difficultyName: response.difficultyName,
      description: response.description ?? "",
      tasks: payload.tasks,
    },
  };
};

export const deleteExerciseFactorySelection = async (selectionKey: string) => {
  const selection = parseExerciseFactorySelectionKey(selectionKey);

  if (selection.kind === "existing") {
    const { $api } = useNuxtApp();

    await $api(`/exercises/${selection.uid}`, {
      method: "DELETE",
    });

    clearExerciseFactoryOverride(selection.uid);
    return;
  }

  if (selection.kind === "draft") {
    removeExerciseFactoryDraft(selection.localId);
  }
};

const mapTaskToBackendContent = (task: ExerciseTask): Record<string, unknown> => {
  const templateKind = task.templateKind;

  if (templateKind === "multiple-choice") {
    return {
      templateKind,
      prompt: task.prompt,
      options: task.options,
      correctChoice: task.correctChoice,
    };
  }

  if (templateKind === "x-or-y") {
    return {
      templateKind,
      prompt: task.prompt ?? {},
      options: task.options,
      correctChoice: task.correctChoice,
    };
  }

  if (templateKind === "email-hotspots") {
    return {
      templateKind,
      prompt: task.prompt ?? {},
      email: task.email,
      targets: task.targets,
    };
  }

  if (templateKind === "sort-into-buckets") {
    return {
      templateKind,
      prompt: task.prompt ?? {},
      unplacedLabel: task.unplacedLabel,
      buckets: task.buckets,
      items: task.items,
    };
  }

  return {
    templateKind,
    prompt: task.prompt ?? {},
    mode: task.mode,
    placeholder: task.placeholder,
    startingPassword: task.startingPassword,
    minLength: task.minLength,
    minScoreToPass: task.minScoreToPass,
    commonPatterns: task.commonPatterns,
  };
};

export const mapTaskToBackendTaskRequest = (task: ExerciseTask): BackendTaskRequestPayload => ({
  introText: task.introText,
  feedbackCorrect: task.feedbackCorrect,
  feedbackIncorrect: task.feedbackIncorrect,
  content: mapTaskToBackendContent(task),
});

export const mapExerciseToBackendRequest = (
  payload: ExerciseFactoryPayload,
): BackendExerciseRequestPayload => ({
  title: payload.title,
  difficultyName: payload.difficultyName,
  description: payload.description,
  tasks: payload.tasks.map(mapTaskToBackendTaskRequest),
});
