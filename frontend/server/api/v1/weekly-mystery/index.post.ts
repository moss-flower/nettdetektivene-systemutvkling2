import { getBackendAPI } from "~~/server/utils/backendAPI";

export default defineEventHandler(async (event) => {
	const api = getBackendAPI(event);
	const formData = await readFormData(event);

	return await api("/WeeklyMystery/", {
		method: "POST",
		body: formData,
	});
});
