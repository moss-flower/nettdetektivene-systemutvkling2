import type {
  TaskBase,
} from "~/composables/types/TaskTypes";

export type Exercise = {
  id: string;
  title: string;
  difficultyName: string;
  description?: string;
  tasks: TaskBase[];
};
