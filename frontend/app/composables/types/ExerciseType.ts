import type { ExerciseTask } from "~/composables/types/TaskTypes";
import type { NotepadEntryResponse } from "~/composables/types/NotepadTypes";

type BackendTaskContent = Partial<Omit<ExerciseTask, "id" | "templateKind" | "introText" | "feedbackCorrect" | "feedbackIncorrect">>;

type BackendTask = Omit<ExerciseTask, "id"> & {
  internalId?: number | null;
  content?: BackendTaskContent;
};

type BackendExercisePage = {
  content: ExerciseData[];
  last?: boolean;
  totalPages?: number;
};

export type ExerciseData = {
  uid: string;
  title: string;
  difficultyName: string;
  description?: string;
  hint: string;
  notepadEntry?: NotepadEntryResponse | null;
  tasks: BackendTask[];
};

export class Exercise {
  uid: string;
  title: string;
  difficultyName: string;
  description?: string;
  hint: string;
  notepadEntry?: NotepadEntryResponse | null;
  tasks: ExerciseTask[];

  constructor(data: {
    uid?: string;
    title: string;
    difficultyName?: string;
    entryName?: string;
      description?: string;
      hint?: string;
      notepadEntry?: NotepadEntryResponse | null;
      tasks: ExerciseTask[];
  }) {
    this.uid = data.uid ?? "";
    this.title = data.title;
    this.difficultyName = data.difficultyName ?? data.entryName ?? "";
    this.description = data.description;
    this.hint = data.hint ?? "";
    this.notepadEntry = data.notepadEntry ?? null;
    this.tasks = data.tasks;
  }

  get entryName() {
    return this.difficultyName;
  }

  static fromBackend(data: ExerciseData): Exercise {
    return new Exercise({
      uid: data.uid,
      title: data.title,
      entryName: data.difficultyName,
      description: data.description,
      hint: data.hint,
      notepadEntry: data.notepadEntry ?? null,
      tasks: data.tasks.map((task, index) => {
        const { content, ...taskBase } = task;

        return {
          ...taskBase,
          ...(content ?? {}),
          id: String(task.internalId ?? `${data.uid}-${index + 1}`),
        };
      }) as ExerciseTask[],
    });
  }

  static async backendFetchAllExercises(): Promise<Exercise[]> {
    const { $api } = useNuxtApp();
    const exercises: Exercise[] = [];
    let page = 0;

    while (true) {
      const response = await $api<BackendExercisePage>("/exercises", {
        method: "GET",
        query: {
          page,
          size: 100,
        },
      });

      exercises.push(...response.content.map((exercise) => Exercise.fromBackend(exercise)));

      if (response.last ?? page + 1 >= (response.totalPages ?? 1)) {
        return exercises;
      }

      page += 1;
    }
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
