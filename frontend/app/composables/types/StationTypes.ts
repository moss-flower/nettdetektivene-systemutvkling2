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
};

export type StationData = {
  uid: string;
  title: string;
  description: string;
  image: string;
  backgroundColor?: string;
  exerciseUids?: string[];
};

export type StationRequest = {
  title: string;
  description: string;
  image: string;
  backgroundColor: string | null;
  exerciseUids: string[];
};

export const mapStationFromResponse = (data: StationData): Station => ({
  uid: data.uid,
  title: data.title,
  description: data.description,
  image: data.image,
  backgroundColor: data.backgroundColor ?? "#cfe9ff",
  exerciseUids: data.exerciseUids ?? [],
});
