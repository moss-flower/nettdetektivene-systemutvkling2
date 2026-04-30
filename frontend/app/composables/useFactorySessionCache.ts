import type { ExerciseFactoryPayload } from "~/composables/useExerciseFactory";
import type { StationFactoryPayload } from "~/composables/useStationFactory";
import type { WorldFactoryPayload } from "~/composables/useWorldFactory";

type FactoryKind = "world" | "station" | "exercise";

type FactoryPayloadByKind = {
	world: WorldFactoryPayload;
	station: StationFactoryPayload;
	exercise: ExerciseFactoryPayload;
};

type FactoryReturnTarget = {
	fullPath: string;
};

const draftCache = new Map<string, unknown>();
const returnStack: FactoryReturnTarget[] = [];

const cacheKey = (kind: FactoryKind, selectionKey: string) => `${kind}:${selectionKey}`;

const clonePayload = <Payload>(payload: Payload): Payload => JSON.parse(JSON.stringify(payload)) as Payload;

export const useFactorySessionCache = () => {
	const setDraft = <Kind extends FactoryKind>(
		kind: Kind,
		selectionKey: string,
		payload: FactoryPayloadByKind[Kind],
	) => {
		draftCache.set(cacheKey(kind, selectionKey), clonePayload(payload));
	};

	const getDraft = <Kind extends FactoryKind>(
		kind: Kind,
		selectionKey: string,
	): FactoryPayloadByKind[Kind] | null => {
		const cached = draftCache.get(cacheKey(kind, selectionKey));

		return cached ? clonePayload(cached as FactoryPayloadByKind[Kind]) : null;
	};

	const moveDraft = (kind: FactoryKind, fromSelectionKey: string, toSelectionKey: string) => {
		const fromKey = cacheKey(kind, fromSelectionKey);
		const cached = draftCache.get(fromKey);

		if (!cached) {
			return;
		}

		draftCache.set(cacheKey(kind, toSelectionKey), cached);
		draftCache.delete(fromKey);
	};

	const pushReturnTarget = (fullPath: string) => {
		returnStack.push({ fullPath });
	};

	const popReturnTarget = () => returnStack.pop() ?? null;

	const clearFactorySessionCache = () => {
		draftCache.clear();
		returnStack.splice(0, returnStack.length);
	};

	return {
		setDraft,
		getDraft,
		moveDraft,
		pushReturnTarget,
		popReturnTarget,
		clearFactorySessionCache,
	};
};
