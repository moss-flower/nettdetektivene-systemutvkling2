import type {
	MysterySubmission,
	WeeklyMysteryPrompt,
	WeeklyMysterySubmissionResponse,
} from "~/composables/types/WeeklyMysteryTypes";

const mapSubmission = (submission: WeeklyMysterySubmissionResponse): MysterySubmission => ({
	id: submission.publicUid,
	message: submission.message,
	screenshotName: submission.fileName,
	screenshotUrl: submission.fileUrl,
	status: "Til vurdering",
	studentPublicUid: submission.studentPublicUid,
});

const normalizePrompt = (prompt: WeeklyMysteryPrompt): WeeklyMysteryPrompt => ({
	publicId: prompt.publicId,
	weekLabel: prompt.weekLabel?.trim() || "Ukens mysterium",
	title: prompt.title,
	prompt: prompt.prompt,
	clue: prompt.clue,
});

export function useStudentMystery() {
	const { $api } = useNuxtApp();
	const { student } = useClassrooms();
	const { getMyProfile } = useStudentProfile();

	const currentMystery = useState<WeeklyMysteryPrompt | null>("student-mystery-current", () => null);
	const previousMysteries = useState<WeeklyMysteryPrompt[]>("student-mystery-history", () => []);
	const submissionHistory = useState<MysterySubmission[]>("student-mystery-submissions", () => []);
	const isLoading = useState<boolean>("student-mystery-loading", () => false);
	const currentStudentUid = useState<string>("student-mystery-student-uid", () => "");

	const loadMysteryData = async () => {
		isLoading.value = true;

		try {
			const [classroom, profile] = await Promise.all([
				student.getMyClassroom(),
				getMyProfile(),
			]);

			currentStudentUid.value = profile.studentUid;

			const currentPromptResponse = await $api<WeeklyMysteryPrompt | null>(`/classrooms/${classroom.uid}/weekly-mystery`, {
				method: "GET",
			});

			const historyResponse = await $api<WeeklyMysteryPrompt[]>("/weekly-mystery/classroom-history", {
				method: "GET",
				query: {
					classroomUid: classroom.uid,
				},
			}).catch(() => []);

			currentMystery.value = currentPromptResponse && typeof currentPromptResponse === "object"
				? normalizePrompt(currentPromptResponse)
				: null;

			const normalizedHistory = (Array.isArray(historyResponse) ? historyResponse : [])
				.map(normalizePrompt)
				.filter((prompt) => prompt.publicId !== currentMystery.value?.publicId);

			previousMysteries.value = normalizedHistory;

			if (!currentMystery.value) {
				submissionHistory.value = [];
				return;
			}

			const submissions = await $api<WeeklyMysterySubmissionResponse[]>(`/weekly-mystery/${currentMystery.value.publicId}/submissions`, {
				method: "GET",
			});

			submissionHistory.value = submissions
				.filter((submission) => submission.studentPublicUid === currentStudentUid.value)
				.map(mapSubmission);
		} catch {
			currentMystery.value = null;
			previousMysteries.value = [];
			submissionHistory.value = [];
		} finally {
			isLoading.value = false;
		}
	};

	const submitMysteryExample = async (message: string, file: File) => {
		if (!currentMystery.value) {
			throw new Error("Det finnes ikke noe aktivt mysterium akkurat nå.");
		}

		const trimmedMessage = message.trim();
		if (!trimmedMessage) {
			throw new Error("Skriv en kort forklaring før du sender inn.");
		}

		const formData = new FormData();
		formData.append("file", file);
		formData.append("message", trimmedMessage);
		formData.append("weeklyMysterPromptUuid", currentMystery.value.publicId);

		await $api("/weekly-mystery", {
			method: "POST",
			body: formData,
		});

		const submissions = await $api<WeeklyMysterySubmissionResponse[]>(`/weekly-mystery/${currentMystery.value.publicId}/submissions`, {
			method: "GET",
		});

		submissionHistory.value = submissions
			.filter((submission) => submission.studentPublicUid === currentStudentUid.value)
			.map(mapSubmission);
	};

	return {
		currentMystery,
		previousMysteries,
		submissionHistory,
		isLoading,
		loadMysteryData,
		submitMysteryExample,
	};
}

export type WeeklyMystery = WeeklyMysteryPrompt;
export type { MysterySubmission };
