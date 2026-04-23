export type StoredGameProgress = {
  completedExerciseUids: string[];
};

const storageKey = "student.gameProgress";

const defaultProgress = (): StoredGameProgress => ({
  completedExerciseUids: [],
});

const canUseStorage = () => typeof window !== "undefined" && typeof window.localStorage !== "undefined";

const normalizeExerciseUids = (uids: unknown): string[] => {
  if (!Array.isArray(uids)) {
    return [];
  }

  return Array.from(
    new Set(
      uids.filter((uid): uid is string => typeof uid === "string" && uid.trim().length > 0),
    ),
  );
};

const readStoredProgress = (): StoredGameProgress => {
  if (!canUseStorage()) {
    return defaultProgress();
  }

  const raw = window.localStorage.getItem(storageKey);

  if (!raw) {
    return defaultProgress();
  }

  try {
    const parsed = JSON.parse(raw) as Partial<StoredGameProgress>;

    return {
      completedExerciseUids: normalizeExerciseUids(parsed.completedExerciseUids),
    };
  } catch {
    return defaultProgress();
  }
};

const writeStoredProgress = (progress: StoredGameProgress) => {
  if (!canUseStorage()) {
    return;
  }

  window.localStorage.setItem(storageKey, JSON.stringify(progress));
};

export const getCompletedExerciseUids = (): string[] => readStoredProgress().completedExerciseUids;

export const isExerciseCompleted = (exerciseUid: string): boolean =>
  getCompletedExerciseUids().includes(exerciseUid);

export const getCompletedExercisesForStation = (exerciseUids: string[]): boolean[] => {
  const completedExerciseUidSet = new Set(getCompletedExerciseUids());

  return exerciseUids.map((exerciseUid) => completedExerciseUidSet.has(exerciseUid));
};

export const areAllExerciseUidsCompleted = (exerciseUids: string[]): boolean => {
  const completedExerciseUidSet = new Set(getCompletedExerciseUids());

  return exerciseUids.every((exerciseUid) => completedExerciseUidSet.has(exerciseUid));
};

export const markExerciseCompleted = (exerciseUid: string) => {
  const progress = readStoredProgress();

  if (progress.completedExerciseUids.includes(exerciseUid)) {
    return;
  }

  progress.completedExerciseUids.push(exerciseUid);
  writeStoredProgress({
    completedExerciseUids: normalizeExerciseUids(progress.completedExerciseUids),
  });
};
