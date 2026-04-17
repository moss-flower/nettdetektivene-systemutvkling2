import type {
  ExerciseTask,
} from "~/composables/types/TaskTypes";

export type Exercise = {
  id: string;
  title: string;
  difficultyText: string;
  description?: string;
  tasks: ExerciseTask[];
};
