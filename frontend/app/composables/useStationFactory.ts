import { createDraftId } from "~/composables/useExerciseFactory";
import { Exercise } from "~/composables/types/ExerciseType";
import { Station } from "~/composables/types/StationType";

export type StationFactoryPayload = {
	title: string;
	description: string;
	image: string;
	exerciseUids: Exercise["uid"][];
};

export type StationFactoryStoredRecord = {
	localId: string;
	sourceUid?: string;
	payload: StationFactoryPayload;
	createdAt: string;
	updatedAt: string;
};

type StationFactoryStore = {
	overrides: Record<string, StationFactoryStoredRecord>;
	drafts: StationFactoryStoredRecord[];
};

export type StationFactorySelectableOption = {
	selectionKey: string;
	label: string;
	description: string;
	kind: "existing" | "draft";
	badge: string;
};

export const stationFactoryNewSelectionKey = "__new_station__";

const storageKey = "teacher.stationFactory.records";
const previousStorageKey = "teacher.stationFactory.payloads";
const defaultImage = "https://placehold.co/1200x720?text=Stasjon";

export const stationFactoryDefaultImage = defaultImage;

export const createStationDraft = (source?: Partial<StationFactoryPayload>): StationFactoryPayload => ({
	title: source?.title ?? "",
	description: source?.description ?? "",
	image: source?.image?.trim() ? source.image : defaultImage,
	exerciseUids: source?.exerciseUids ?? [],
});

const canUseStorage = () => typeof window !== "undefined" && typeof window.localStorage !== "undefined";

export const normalizeStationExerciseUids = (uids: string[]) => {
	const seen = new Set<string>();

	return uids
		.map((uid) => uid.trim())
		.filter((uid) => {
			if (uid.length === 0 || seen.has(uid)) {
				return false;
			}

			seen.add(uid);
			return true;
		});
};

const normalizeStationPayload = (payload: Record<string, unknown>): StationFactoryPayload | null => {
	const title = typeof payload.title === "string" ? payload.title : "";
	const description = typeof payload.description === "string" ? payload.description : "";
	const image = typeof payload.image === "string" && payload.image.trim().length > 0
		? payload.image
		: defaultImage;
	const exerciseUids = Array.isArray(payload.exerciseUids)
		? normalizeStationExerciseUids(payload.exerciseUids.filter((uid): uid is string => typeof uid === "string"))
		: [];

	if (!title && !description && exerciseUids.length === 0) {
		return null;
	}

	return {
		title,
		description,
		image,
		exerciseUids,
	};
};

const normalizeStationRecord = (record: Record<string, unknown>): StationFactoryStoredRecord | null => {
	const payload = normalizeStationPayload(record);

	if (!payload) {
		return null;
	}

	const localId = typeof record.localId === "string" ? record.localId : createDraftId("station");
	const sourceUid = typeof record.sourceUid === "string" && record.sourceUid.trim().length > 0
		? record.sourceUid.trim()
		: undefined;
	const createdAt = typeof record.createdAt === "string" ? record.createdAt : new Date().toISOString();
	const updatedAt = typeof record.updatedAt === "string"
		? record.updatedAt
		: typeof record.createdAt === "string"
			? record.createdAt
			: new Date().toISOString();

	return {
		localId,
		sourceUid,
		payload,
		createdAt,
		updatedAt,
	};
};

const normalizePreviousStationRecord = (record: Record<string, unknown>): StationFactoryStoredRecord | null => {
	const payload = normalizeStationPayload(record);

	if (!payload) {
		return null;
	}

	const localId = typeof record.localId === "string" ? record.localId : createDraftId("station");
	const createdAt = typeof record.createdAt === "string" ? record.createdAt : new Date().toISOString();

	return {
		localId,
		payload,
		createdAt,
		updatedAt: createdAt,
	};
};

const readPreviousStationDrafts = (): StationFactoryStoredRecord[] => {
	if (!canUseStorage()) {
		return [];
	}

	const raw = window.localStorage.getItem(previousStorageKey);

	if (!raw) {
		return [];
	}

	try {
		const parsed = JSON.parse(raw) as Record<string, unknown>[];

		if (!Array.isArray(parsed)) {
			return [];
		}

		return parsed
			.map((record) => normalizePreviousStationRecord(record))
			.filter((record): record is StationFactoryStoredRecord => record !== null);
	} catch {
		return [];
	}
};

export const readStationFactoryStore = (): StationFactoryStore => {
	if (!canUseStorage()) {
		return {
			overrides: {},
			drafts: [],
		};
	}

	const raw = window.localStorage.getItem(storageKey);

	if (!raw) {
		return {
			overrides: {},
			drafts: readPreviousStationDrafts(),
		};
	}

	try {
		const parsed = JSON.parse(raw) as Record<string, unknown>;

		if (!parsed || typeof parsed !== "object") {
			return {
				overrides: {},
				drafts: readPreviousStationDrafts(),
			};
		}

		const rawOverrides = parsed.overrides;
		const rawDrafts = parsed.drafts;

		const overrides = rawOverrides && typeof rawOverrides === "object"
			? Object.entries(rawOverrides as Record<string, unknown>).reduce<Record<string, StationFactoryStoredRecord>>(
				(accumulator, [uid, record]) => {
					if (!record || typeof record !== "object") {
						return accumulator;
					}

					const normalized = normalizeStationRecord({ ...record, sourceUid: uid });

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
				.map((record) => (record && typeof record === "object") ? normalizeStationRecord(record as Record<string, unknown>) : null)
				.filter((record): record is StationFactoryStoredRecord => record !== null && !record.sourceUid)
			: [];

		return {
			overrides,
			drafts,
		};
	} catch {
		return {
			overrides: {},
			drafts: readPreviousStationDrafts(),
		};
	}
};

const writeStationFactoryStore = (store: StationFactoryStore) => {
	if (!canUseStorage()) {
		return;
	}

	window.localStorage.setItem(storageKey, JSON.stringify(store));
	window.localStorage.removeItem(previousStorageKey);
};

export const saveStationFactoryOverride = (sourceUid: string, payload: StationFactoryPayload): StationFactoryStoredRecord => {
	const store = readStationFactoryStore();
	const existing = store.overrides[sourceUid];
	const record: StationFactoryStoredRecord = {
		localId: existing?.localId ?? createDraftId("station-override"),
		sourceUid,
		payload: createStationDraft(payload),
		createdAt: existing?.createdAt ?? new Date().toISOString(),
		updatedAt: new Date().toISOString(),
	};

	store.overrides[sourceUid] = record;
	writeStationFactoryStore(store);

	return record;
};

export const clearStationFactoryOverride = (sourceUid: string) => {
	const store = readStationFactoryStore();

	delete store.overrides[sourceUid];
	writeStationFactoryStore(store);
};

export const saveStationFactoryDraft = (payload: StationFactoryPayload, localId?: string): StationFactoryStoredRecord => {
	const store = readStationFactoryStore();
	const existing = localId ? store.drafts.find((draft) => draft.localId === localId) ?? null : null;
	const record: StationFactoryStoredRecord = {
		localId: existing?.localId ?? createDraftId("station"),
		payload: createStationDraft(payload),
		createdAt: existing?.createdAt ?? new Date().toISOString(),
		updatedAt: new Date().toISOString(),
	};

	const nextDrafts = store.drafts.filter((draft) => draft.localId !== record.localId);
	nextDrafts.unshift(record);
	store.drafts = nextDrafts;
	writeStationFactoryStore(store);

	return record;
};

export const removeStationFactoryDraft = (localId: string) => {
	const store = readStationFactoryStore();

	store.drafts = store.drafts.filter((draft) => draft.localId !== localId);
	writeStationFactoryStore(store);
};

export const makeStationFactoryExistingSelectionKey = (uid: string) => `existing:${uid}`;

export const makeStationFactoryDraftSelectionKey = (localId: string) => `draft:${localId}`;

export const parseStationFactorySelectionKey = (selectionKey: string) => {
	if (!selectionKey || selectionKey === stationFactoryNewSelectionKey) {
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

export const getStationFactorySelectionOptions = (): StationFactorySelectableOption[] => {
	const store = readStationFactoryStore();
	const existingOptions = Station.backendFetchAllStations().map((station) => ({
		selectionKey: makeStationFactoryExistingSelectionKey(station.uid),
		label: station.title,
		description: station.uid,
		kind: "existing" as const,
		badge: store.overrides[station.uid] ? "Eksisterende stasjon · lokal override" : "Eksisterende stasjon",
	}));
	const draftOptions = [...store.drafts]
		.sort((left, right) => right.updatedAt.localeCompare(left.updatedAt))
		.map((draft) => ({
			selectionKey: makeStationFactoryDraftSelectionKey(draft.localId),
			label: draft.payload.title || "Ny stasjon uten tittel",
			description: draft.payload.description || draft.localId,
			kind: "draft" as const,
			badge: "Ny lokal stasjon",
		}));

	return [...existingOptions, ...draftOptions];
};