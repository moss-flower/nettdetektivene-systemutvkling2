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

  await $api("/completed-exercises", {
    method: "POST",
    body: { exerciseUid },
  });
};
