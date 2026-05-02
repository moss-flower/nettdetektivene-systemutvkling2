import { getBackendAPI } from "~~/server/utils/backendAPI";

export default defineEventHandler(async (event) => {
	const api = getBackendAPI(event);
	const formData = await readFormData(event);

	return await api("/media/uploads/public", {
		method: "POST",
		body: formData,
	});
});
