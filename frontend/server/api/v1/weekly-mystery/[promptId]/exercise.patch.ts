import { getBackendAPI } from "~~/server/utils/backendAPI";

type LinkWeeklyMysteryExercisePayload = {
	exerciseUid: string;
};

export default defineEventHandler(async (event) => {
	const api = getBackendAPI(event);
	const promptId = getRouterParam(event, "promptId");
	const body = await readBody<LinkWeeklyMysteryExercisePayload>(event);

	return await api(`/WeeklyMystery/${promptId}/exercise`, {
		method: "PATCH",
		body,
	});
});
