import { getBackendAPI } from "~~/server/utils/backendAPI";

type WeeklyMysteryPromptCreatePayload = {
	title: string;
	prompt: string;
	clue: string;
	date: string;
	publicClassroomIds: string[];
};

export default defineEventHandler(async (event) => {
	const api = getBackendAPI(event);
	const body = await readBody<WeeklyMysteryPromptCreatePayload>(event);

	return await api("/WeeklyMystery/prompts", {
		method: "POST",
		body,
	});
});

