import type {
  EmailHotspotTaskData,
  MultipleChoiceTaskData,
  XOrYTaskData,
} from "~/composables/types/TaskTypes";

export type ExerciseTask =
  | XOrYTaskData
  | MultipleChoiceTaskData
  | EmailHotspotTaskData;

export type Exercise = {
  id: string;
  title: string;
  difficultyText: string;
  description?: string;
  tasks: ExerciseTask[];
};
