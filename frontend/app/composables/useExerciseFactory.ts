import type {
  EmailHotspotTaskData,
  ExerciseTask,
  ExerciseTemplateKind,
  MultipleChoiceTaskData,
  XOrYTaskData,
} from "~/composables/types/TaskTypes";

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
