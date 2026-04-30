import { getBackendAPI } from "~~/server/utils/backendAPI";

type AssignWeeklyMysteryPayload = {
	promptId: string;
};

export default defineEventHandler(async (event) => {
	const api = getBackendAPI(event);
	const classroomUid = getRouterParam(event, "classroomUid");
	const body = await readBody<AssignWeeklyMysteryPayload>(event);

	return await api(`/classrooms/${classroomUid}/ukens-mysterium`, {
		method: "PATCH",
		body: JSON.stringify(body.promptId),
		headers: {
			"Content-Type": "application/json",
		},
	});
});

