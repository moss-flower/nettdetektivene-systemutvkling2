export type WeeklyMysteryPrompt = {
	publicId: string;
	weekLabel: string;
	title: string;
	prompt: string;
	clue: string;
};

export type WeeklyMysterySubmissionResponse = {
	publicUid: string;
	message: string;
	studentPublicUid: string;
	fileName: string;
	fileUrl: string;
};

export type MysterySubmission = {
	id: string;
	message: string;
	screenshotName: string;
	screenshotUrl: string;
	status: "Til vurdering";
	studentPublicUid: string;
};
