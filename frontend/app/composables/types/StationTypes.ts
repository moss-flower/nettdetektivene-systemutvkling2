export type StationStatus = {
  completedExercises: boolean[];
};

export type Station = {
  uid: string;
  title: string;
  description: string;
  image: string;
  backgroundColor: string;
  exerciseUids: string[];
  isBoss: boolean;
};

export type StationExerciseData = {
  uid: string;
  title: string;
  difficultyText: string;
  description?: string;
  isCompleted: boolean;
  isUnlocked: boolean;
};

export type StationProgressData = Station & {
  completedExercises: boolean[];
  completedExerciseCount: number;
  totalExerciseCount: number;
  isCompleted: boolean;
  isUnlocked: boolean;
  isTeacherPreview: boolean;
  exercises: StationExerciseData[];
};

export type StationData = {
  uid: string;
  title: string;
  description: string;
  image: string;
  backgroundColor?: string;
  exerciseUids?: string[];
  isBoss?: boolean;
};

export type StationProgressResponse = StationData & {
  completedExercises?: boolean[];
  completedExerciseCount?: number;
  totalExerciseCount?: number;
  isCompleted?: boolean;
  isUnlocked?: boolean;
  isTeacherPreview?: boolean;
  exercises?: StationExerciseData[];
};

export type StationRequest = {
  title: string;
  description: string;
  image: string;
  backgroundColor: string | null;
  exerciseUids: string[];
  isBoss: boolean;
};

export const mapStationFromResponse = (data: StationData): Station => ({
  uid: data.uid,
  title: data.title,
  description: data.description,
  image: data.image,
  backgroundColor: data.backgroundColor ?? "#cfe9ff",
  exerciseUids: data.exerciseUids ?? [],
  isBoss: data.isBoss ?? false,
});

export const mapStationProgressFromResponse = (data: StationProgressResponse): StationProgressData => ({
  ...mapStationFromResponse(data),
  completedExercises: data.completedExercises ?? [],
  completedExerciseCount: data.completedExerciseCount ?? 0,
  totalExerciseCount: data.totalExerciseCount ?? data.exerciseUids?.length ?? 0,
  isCompleted: data.isCompleted ?? false,
  isUnlocked: data.isUnlocked ?? true,
  isTeacherPreview: data.isTeacherPreview ?? false,
  exercises: data.exercises ?? [],
});
