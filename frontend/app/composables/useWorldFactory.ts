import { createDraftId } from "~/composables/useExerciseFactory";
import { Station } from "~/composables/types/StationType";
import { World } from "~/composables/types/WorldTypes";

export type WorldFactoryPayload = {
	title: string;
	uid: string;
	description: string;
	image: string;
	stationUids: Station["uid"][];
};

type BackendWorldRequestPayload = {
	title: string;
	description: string;
	image: string;
	stationUids: Station["uid"][];
};

export type WorldFactoryStoredRecord = {
	localId: string;
	sourceUid?: string;
	payload: WorldFactoryPayload;
	createdAt: string;
	updatedAt: string;
};

type WorldFactoryStore = {
	overrides: Record<string, WorldFactoryStoredRecord>;
	drafts: WorldFactoryStoredRecord[];
};

export type WorldFactorySelectableOption = {
	selectionKey: string;
	label: string;
	description: string;
	kind: "existing" | "draft";
	badge: string;
};

export const worldFactoryNewSelectionKey = "__new_world__";

const storageKey = "teacher.worldFactory.records";
const previousStorageKey = "teacher.worldFactory.payloadsByClassroom";
const defaultImage = "/images/placeholders/world.svg";

export const worldFactoryDefaultImage = defaultImage;

export const createWorldDraft = (source?: Partial<WorldFactoryPayload>): WorldFactoryPayload => ({
	title: source?.title?.trim() ?? "",
	uid: source?.uid?.trim() ?? "",
	description: source?.description ?? "",
	image: source?.image?.trim() ? source.image : defaultImage,
	stationUids: source?.stationUids ?? [],
});

const canUseStorage = () => typeof window !== "undefined" && typeof window.localStorage !== "undefined";

export const normalizeWorldStationUids = (uids: string[]) => {
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

const normalizeWorldPayload = (payload: Record<string, unknown>, uidFallback = ""): WorldFactoryPayload | null => {
	const title = typeof payload.title === "string" && payload.title.trim().length > 0
		? payload.title.trim()
		: typeof payload.uid === "string" && payload.uid.trim().length > 0
			? payload.uid.trim()
			: uidFallback;
	const uid = typeof payload.uid === "string" && payload.uid.trim().length > 0
		? payload.uid.trim()
		: typeof payload.classroomUid === "string" && payload.classroomUid.trim().length > 0
			? payload.classroomUid.trim()
			: uidFallback;
	const description = typeof payload.description === "string" ? payload.description : "";
	const image = typeof payload.image === "string" && payload.image.trim().length > 0
		? payload.image
		: defaultImage;
	const stationUids = Array.isArray(payload.stationUids)
		? normalizeWorldStationUids(payload.stationUids.filter((stationUid): stationUid is string => typeof stationUid === "string"))
		: [];

	if (!title && !uid && !description && stationUids.length === 0) {
		return null;
	}

	return {
		title,
		uid,
		description,
		image,
		stationUids,
	};
};

const normalizeWorldRecord = (record: Record<string, unknown>, uidFallback = ""): WorldFactoryStoredRecord | null => {
	const payload = normalizeWorldPayload(record, uidFallback);

	if (!payload) {
		return null;
	}

	const localId = typeof record.localId === "string" ? record.localId : createDraftId("world");
	const sourceUid = typeof record.sourceUid === "string" && record.sourceUid.trim().length > 0
		? record.sourceUid.trim()
		: undefined;
	const createdAt = typeof record.createdAt === "string" ? record.createdAt : new Date().toISOString();
	const updatedAt = typeof record.updatedAt === "string"
		? record.updatedAt
		: createdAt;

	return {
		localId,
		sourceUid,
		payload,
		createdAt,
		updatedAt,
	};
};

const readPreviousWorldDrafts = (): WorldFactoryStoredRecord[] => {
	if (!canUseStorage()) {
		return [];
	}

	const raw = window.localStorage.getItem(previousStorageKey);

	if (!raw) {
		return [];
	}

	try {
		const parsed = JSON.parse(raw) as unknown;

		if (Array.isArray(parsed)) {
			return parsed
				.map((record) => (record && typeof record === "object") ? normalizeWorldRecord(record as Record<string, unknown>) : null)
				.filter((record): record is WorldFactoryStoredRecord => record !== null && !record.sourceUid);
		}

		if (!parsed || typeof parsed !== "object") {
			return [];
		}

		return Object.entries(parsed as Record<string, unknown>)
			.map(([uid, record]) => {
				if (!record || typeof record !== "object") {
					return null;
				}

				return normalizeWorldRecord(record as Record<string, unknown>, uid);
			})
			.filter((record): record is WorldFactoryStoredRecord => record !== null && !record.sourceUid);
	} catch {
		return [];
	}
};

export const readWorldFactoryStore = (): WorldFactoryStore => {
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
			drafts: readPreviousWorldDrafts(),
		};
	}

	try {
		const parsed = JSON.parse(raw) as Record<string, unknown>;

		if (!parsed || typeof parsed !== "object") {
			return {
				overrides: {},
				drafts: readPreviousWorldDrafts(),
			};
		}

		const rawOverrides = parsed.overrides;
		const rawDrafts = parsed.drafts;

		const overrides = rawOverrides && typeof rawOverrides === "object"
			? Object.entries(rawOverrides as Record<string, unknown>).reduce<Record<string, WorldFactoryStoredRecord>>(
				(accumulator, [uid, record]) => {
					if (!record || typeof record !== "object") {
						return accumulator;
					}

					const normalized = normalizeWorldRecord({ ...record, sourceUid: uid }, uid);

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
				.map((record) => (record && typeof record === "object") ? normalizeWorldRecord(record as Record<string, unknown>) : null)
				.filter((record): record is WorldFactoryStoredRecord => record !== null && !record.sourceUid)
			: [];

		return {
			overrides,
			drafts,
		};
	} catch {
		return {
			overrides: {},
			drafts: readPreviousWorldDrafts(),
		};
	}
};

const writeWorldFactoryStore = (store: WorldFactoryStore) => {
	if (!canUseStorage()) {
		return;
	}

	window.localStorage.setItem(storageKey, JSON.stringify(store));
	window.localStorage.removeItem(previousStorageKey);
};

export const saveWorldFactoryOverride = (sourceUid: string, payload: WorldFactoryPayload): WorldFactoryStoredRecord => {
	const store = readWorldFactoryStore();
	const existing = store.overrides[sourceUid];
	const record: WorldFactoryStoredRecord = {
		localId: existing?.localId ?? createDraftId("world-override"),
		sourceUid,
		payload: createWorldDraft({ ...payload, uid: sourceUid }),
		createdAt: existing?.createdAt ?? new Date().toISOString(),
		updatedAt: new Date().toISOString(),
	};

	store.overrides[sourceUid] = record;
	writeWorldFactoryStore(store);

	return record;
};

export const clearWorldFactoryOverride = (sourceUid: string) => {
	const store = readWorldFactoryStore();

	delete store.overrides[sourceUid];
	writeWorldFactoryStore(store);
};

export const saveWorldFactoryDraft = (payload: WorldFactoryPayload, localId?: string): WorldFactoryStoredRecord => {
	const store = readWorldFactoryStore();
	const existing = localId ? store.drafts.find((draft) => draft.localId === localId) ?? null : null;
	const record: WorldFactoryStoredRecord = {
		localId: existing?.localId ?? createDraftId("world"),
		payload: createWorldDraft(payload),
		createdAt: existing?.createdAt ?? new Date().toISOString(),
		updatedAt: new Date().toISOString(),
	};

	const nextDrafts = store.drafts.filter((draft) => draft.localId !== record.localId);
	nextDrafts.unshift(record);
	store.drafts = nextDrafts;
	writeWorldFactoryStore(store);

	return record;
};

export const removeWorldFactoryDraft = (localId: string) => {
	const store = readWorldFactoryStore();

	store.drafts = store.drafts.filter((draft) => draft.localId !== localId);
	writeWorldFactoryStore(store);
};

export const makeWorldFactoryExistingSelectionKey = (uid: string) => `existing:${uid}`;

export const makeWorldFactoryDraftSelectionKey = (localId: string) => `draft:${localId}`;

export const parseWorldFactorySelectionKey = (selectionKey: string) => {
	if (!selectionKey || selectionKey === worldFactoryNewSelectionKey) {
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

export const getWorldFactorySelectionOptions = (): WorldFactorySelectableOption[] => {
	const store = readWorldFactoryStore();
	const existingOptions = Object.values(store.overrides).map((record) => ({
		selectionKey: makeWorldFactoryExistingSelectionKey(record.sourceUid ?? record.localId),
		label: record.payload.title || record.payload.uid,
		description: record.payload.description || `${record.payload.stationUids.length} stasjoner`,
		kind: "existing" as const,
		badge: "Eksisterende verden · lokal override",
	}));
	const draftOptions = [...store.drafts]
		.sort((left, right) => right.updatedAt.localeCompare(left.updatedAt))
		.map((draft) => ({
			selectionKey: makeWorldFactoryDraftSelectionKey(draft.localId),
			label: draft.payload.title || "Ny verden uten tittel",
			description: draft.payload.description || `${draft.payload.stationUids.length} stasjoner`,
			kind: "draft" as const,
			badge: "Ny lokal verden",
		}));

	return [...existingOptions, ...draftOptions];
};

export const getWorldFactorySelectionOptionsFromWorlds = (
	worlds: World[],
): WorldFactorySelectableOption[] => {
	const store = readWorldFactoryStore();
	const existingOptions = worlds.map((world) => ({
		selectionKey: makeWorldFactoryExistingSelectionKey(world.uid),
		label: world.title || world.uid,
		description: world.description || `${world.stationUids.length} stasjoner`,
		kind: "existing" as const,
		badge: store.overrides[world.uid] ? "Eksisterende verden · lokal override" : "Eksisterende verden",
	}));
	const draftOptions = [...store.drafts]
		.sort((left, right) => right.updatedAt.localeCompare(left.updatedAt))
		.map((draft) => ({
			selectionKey: makeWorldFactoryDraftSelectionKey(draft.localId),
			label: draft.payload.title || "Ny verden uten tittel",
			description: draft.payload.description || `${draft.payload.stationUids.length} stasjoner`,
			kind: "draft" as const,
			badge: "Ny lokal verden",
		}));

	return [...existingOptions, ...draftOptions];
};

export const fetchAllWorldFactoryWorlds = () => World.backendFetchAllWorlds();

export const loadWorldFactoryPayload = async (selectionKey: string): Promise<{
	payload: WorldFactoryPayload;
	localId: string;
}> => {
	const selection = parseWorldFactorySelectionKey(selectionKey);
	const store = readWorldFactoryStore();

	if (selection.kind === "existing") {
		const override = store.overrides[selection.uid] ?? null;

		if (override) {
			return {
				payload: createWorldDraft(override.payload),
				localId: "",
			};
		}

		const backendWorld = await World.backendFetchWorldByUid(selection.uid);

		return {
			payload: createWorldDraft({
				title: backendWorld?.title ?? backendWorld?.uid ?? selection.uid,
				uid: backendWorld?.uid ?? selection.uid,
				description: backendWorld?.description ?? "",
				image: backendWorld?.image ?? defaultImage,
				stationUids: backendWorld?.stationUids ?? [],
			}),
			localId: "",
		};
	}

	if (selection.kind === "draft") {
		const storedDraft = store.drafts.find((draft) => draft.localId === selection.localId) ?? null;

		return {
			payload: storedDraft?.payload ?? createWorldDraft(),
			localId: storedDraft?.localId ?? "",
		};
	}

	return {
		payload: createWorldDraft(),
		localId: "",
	};
};

const mapWorldToBackendRequest = (payload: WorldFactoryPayload): BackendWorldRequestPayload => ({
	title: payload.title.trim(),
	description: payload.description.trim(),
	image: payload.image.trim() || defaultImage,
	stationUids: normalizeWorldStationUids(payload.stationUids),
});

const toWorldFactoryPayload = (world: World): WorldFactoryPayload => createWorldDraft({
	title: world.title,
	uid: world.uid,
	description: world.description,
	image: world.image,
	stationUids: world.stationUids,
});

export const saveWorldFactoryPayload = async (
	selectionKey: string,
	payload: WorldFactoryPayload,
): Promise<{ selectionKey: string; payload: WorldFactoryPayload }> => {
	const selection = parseWorldFactorySelectionKey(selectionKey);
	const requestBody = mapWorldToBackendRequest(payload);
	const { $api } = useNuxtApp();

	if (selection.kind === "existing") {
		await $api<World>(`/worlds/${selection.uid}`, {
			method: "PUT",
			body: requestBody,
		});

		clearWorldFactoryOverride(selection.uid);
		const world = await World.backendFetchWorldByUid(selection.uid);

		return {
			selectionKey: makeWorldFactoryExistingSelectionKey(selection.uid),
			payload: world ? toWorldFactoryPayload(world) : createWorldDraft({ ...payload, uid: selection.uid }),
		};
	}

	const createdWorld = await $api<World>("/worlds", {
		method: "POST",
		body: requestBody,
	});

	if (selection.kind === "draft") {
		removeWorldFactoryDraft(selection.localId);
	}

	return {
		selectionKey: makeWorldFactoryExistingSelectionKey(createdWorld.uid),
		payload: toWorldFactoryPayload(createdWorld),
	};
};

export const saveWorldFactoryPayloadAsNew = async (
	payload: WorldFactoryPayload,
): Promise<{ selectionKey: string; payload: WorldFactoryPayload }> => {
	const { $api } = useNuxtApp();
	const createdWorld = await $api<World>("/worlds", {
		method: "POST",
		body: mapWorldToBackendRequest(payload),
	});

	return {
		selectionKey: makeWorldFactoryExistingSelectionKey(createdWorld.uid),
		payload: toWorldFactoryPayload(createdWorld),
	};
};

export const deleteWorldFactorySelection = async (selectionKey: string) => {
	const selection = parseWorldFactorySelectionKey(selectionKey);

	if (selection.kind === "existing") {
		const { $api } = useNuxtApp();

		await $api(`/worlds/${selection.uid}`, {
			method: "DELETE",
		});

		clearWorldFactoryOverride(selection.uid);
		return;
	}

	if (selection.kind === "draft") {
		removeWorldFactoryDraft(selection.localId);
	}
};
