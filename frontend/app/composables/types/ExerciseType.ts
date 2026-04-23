import type { ExerciseTask } from "~/composables/types/TaskTypes";

type BackendTask = Omit<ExerciseTask, "id"> & {
  internalId?: number | null;
};

type ExerciseData = {
  publicId: string;
  title: string;
  difficultyName: string;
  description?: string;
  tasks: BackendTask[];
};

export class Exercise {
  publicId: string;
  title: string;
  difficultyName: string;
  description?: string;
  tasks: ExerciseTask[];

  constructor(data: {
    publicId?: string;
    uid?: string;
    title: string;
    difficultyName?: string;
    entryName?: string;
    description?: string;
    tasks: ExerciseTask[];
  }) {
    this.publicId = data.publicId ?? data.uid ?? "";
    this.title = data.title;
    this.difficultyName = data.difficultyName ?? data.entryName ?? "";
    this.description = data.description;
    this.tasks = data.tasks;
  }

  static fromBackend(data: ExerciseData): Exercise {
    return new Exercise({
      uid: data.publicId,
      title: data.title,
      entryName: data.difficultyName,
      description: data.description,
      tasks: data.tasks.map((task, index) => ({
        ...task,
        id: String(task.internalId ?? `${data.publicId}-${index + 1}`),
      })) as ExerciseTask[],
    });

  }

  static async backendFetchExerciseByUid(uid: string): Promise<Exercise | null> {
    const { $api } = useNuxtApp();

    try {
      const exercise = await $api<ExerciseData>(`/exercises/${uid}`, {
        method: "GET",
      });

      return Exercise.fromBackend(exercise);
    } catch {
      return null;
    }
  }

  static async backendFetchExercisesByUids(uids: string[]): Promise<Exercise[]> {
    const exercises = await Promise.all(uids.map((uid) => Exercise.backendFetchExerciseByUid(uid)));

    return exercises.filter((exercise): exercise is Exercise => exercise !== null);
  }
}
