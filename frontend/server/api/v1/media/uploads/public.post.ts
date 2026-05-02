import { getBackendAPI } from "~~/server/utils/backendAPI";

export default defineEventHandler(async (event) => {
	const api = getBackendAPI(event);
	const formData = await readFormData(event);

	try {
		return await api("/media/uploads/public", {
			method: "POST",
			body: formData,
		});
	} catch (error) {
		const statusCode = typeof error === "object" && error !== null && "statusCode" in error
			? (error as { statusCode?: number }).statusCode
			: undefined;

		if (statusCode === 401) {
			await api("/auth/refresh", {
				method: "POST",
			});

			return await api("/media/uploads/public", {
				method: "POST",
				body: formData,
			});
		}

		throw error;
	}
});
