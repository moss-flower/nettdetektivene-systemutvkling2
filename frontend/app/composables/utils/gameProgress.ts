type CompletedExerciseResponse = {
  exerciseUid: string;
};

export const useCompletedExerciseUids = async (): Promise<string[]> => {
  const { $api } = useNuxtApp();

  try {
    const completedExercises = await $api<CompletedExerciseResponse[]>("/completed-exercises/me", {
      method: "GET",
    });

    return completedExercises.map((entry) => entry.exerciseUid);
  } catch {
    return [];
  }
};

export const markExerciseCompleted = async (exerciseUid: string) => {
  const { $api } = useNuxtApp();

  try {
    await $api("/completed-exercises", {
      method: "POST",
      body: { exerciseUid },
    });

    await Promise.all([
      refreshNuxtData("completed-exercises"),
      refreshNuxtData("student-completed-exercises"),
    ]);
  } catch (error: any) {
    const statusCode = error?.statusCode ?? error?.status ?? error?.data?.status;

    // Replaying an already completed exercise should not block the end-of-exercise flow.
    if (statusCode === 409) {
      await Promise.all([
        refreshNuxtData("completed-exercises"),
        refreshNuxtData("student-completed-exercises"),
      ]);

      return;
    }

    throw error;
  }
};
