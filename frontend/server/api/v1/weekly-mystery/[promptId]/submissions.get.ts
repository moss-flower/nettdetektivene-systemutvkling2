import { getBackendAPI } from "~~/server/utils/backendAPI";

export default defineEventHandler(async (event) => {
	const api = getBackendAPI(event);
	const promptId = getRouterParam(event, "promptId");

	return await api(`/WeeklyMystery/${promptId}/submissions`, {
		method: "GET",
	});
});
