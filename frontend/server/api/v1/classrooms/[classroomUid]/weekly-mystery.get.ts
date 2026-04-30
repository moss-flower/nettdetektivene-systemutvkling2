import { getBackendAPI } from "~~/server/utils/backendAPI";

export default defineEventHandler(async (event) => {
	const api = getBackendAPI(event);
	const classroomUid = getRouterParam(event, "classroomUid");

	return await api(`/classrooms/${classroomUid}/WeeklyMystery`, {
		method: "GET",
	});
});
