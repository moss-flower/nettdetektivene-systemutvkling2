import type {
  EmailHotspotTaskData,
  ThreeOptionImageTaskData,
  XOrYTaskData,
} from "~/composables/types/TaskTypes";

export type ExerciseTask =
  | XOrYTaskData
  | ThreeOptionImageTaskData
  | EmailHotspotTaskData;

export type Exercise = {
  id: string;
  title: string;
  description?: string;
  tasks: ExerciseTask[];
};
