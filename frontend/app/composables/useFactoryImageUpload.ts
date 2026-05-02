export type FactoryImageUploadResponse = {
	objectKey: string;
	url: string;
	contentType: string;
	size: number;
};

const uploadErrorMessage = (error: unknown) => {
	if (error && typeof error === "object") {
		const data = "data" in error ? error.data : null;
		if (data && typeof data === "object") {
			const detail = "detail" in data ? data.detail : null;
			if (typeof detail === "string" && detail.trim().length > 0) {
				return detail;
			}
		}

		const message = "message" in error ? error.message : null;
		if (typeof message === "string" && message.trim().length > 0) {
			return message;
		}
	}

	return "Bildet kunne ikke lastes opp.";
};

export const useFactoryImageUpload = () => {
	const { $api } = useNuxtApp();

	const uploadImage = async (file: File, folder: string) => {
		const formData = new FormData();
		formData.append("file", file);
		formData.append("folder", folder);

		try {
			return await $api<FactoryImageUploadResponse>("/media/uploads/public", {
				method: "POST",
				body: formData,
			});
		} catch (error) {
			throw new Error(uploadErrorMessage(error));
		}
	};

	return {
		uploadImage,
	};
};
