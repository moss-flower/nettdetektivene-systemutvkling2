import { getBackendAPI } from "~~/server/utils/backendAPI";

export default defineEventHandler(async (event) => {
	const api = getBackendAPI(event);
	const classroomUid = getQuery(event).classroomUid;

	if (typeof classroomUid !== "string" || classroomUid.length === 0) {
		throw createError({
			statusCode: 400,
			statusMessage: "classroomUid is required",
		});
	}

	return await api(`/classrooms/${classroomUid}/WeeklyMystery/history`, {
		method: "GET",
	});
});

