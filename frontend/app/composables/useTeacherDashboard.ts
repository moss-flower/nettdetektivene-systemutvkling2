import { Exercise } from "~/composables/types/ExerciseType";
import { Station } from "~/composables/types/StationType";
import type { Classroom, ClassroomUser } from "~/composables/types/ClassroomTypes";
import { World } from "~/composables/types/WorldTypes";

export type ClassroomSummary = {
  id: string;
  name: string;
  description: string;
  worldUid: string;
  joinCode: string;
  ownerCount: number;
  studentCount: number;
};

export type ClassroomExercise = {
  uid: string;
  title: string;
  stationTitle: string;
  difficultyName: string;
  taskCount: number;
};

export const toClassroomSummary = (classroom: Classroom): ClassroomSummary => ({
  id: classroom.uid,
  name: classroom.name,
  description: classroom.description,
  worldUid: classroom.worldUid,
  joinCode: classroom.code,
  ownerCount: classroom.owners.length,
  studentCount: classroom.members.length,
});

export const toClassroomSummaries = (classrooms: Classroom[]): ClassroomSummary[] => {
  return [...classrooms]
    .map(toClassroomSummary)
    .sort((left, right) => left.name.localeCompare(right.name, "nb"));
};

export const getClassroomMemberById = (classroom: Classroom | null, studentId: string): ClassroomUser | null => {
  if (!classroom) {
    return null;
  }

  return classroom.members.find((member) => member.publicUuid === studentId) ?? null;
};

export const getClassroomExercises = async (classroom: Classroom | null): Promise<ClassroomExercise[]> => {
  if (!classroom?.worldUid) {
    return [];
  }

  const world = await World.backendFetchWorldByUid(classroom.worldUid);

  if (!world || world.stationUids.length === 0) {
    return [];
  }

  const stations = await Promise.all(world.stationUids.map((stationUid) => Station.backendFetchStationByUid(stationUid)));

  const stationEntries = stations
    .filter((station): station is Station => station !== null)
    .map((station) => ({
      station,
      exerciseUids: station.exerciseUids,
    }));

  const exercisesByStation = await Promise.all(
    stationEntries.map(async ({ station, exerciseUids }) => ({
      station,
      exercises: await Exercise.backendFetchExercisesByUids(exerciseUids),
    })),
  );

  return exercisesByStation.flatMap(({ station, exercises }) =>
    exercises.map((exercise) => ({
      uid: exercise.uid,
      title: exercise.title,
      stationTitle: station.title,
      difficultyName: exercise.difficultyName,
      taskCount: exercise.tasks.length,
    })),
  );
};
