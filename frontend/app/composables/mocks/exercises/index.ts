import type { Exercise } from '~/composables/types/ExerciseType'
import { newsStandExerciseMock, newsStandMediumExerciseMock } from '~/composables/mocks/exercises/newsStandExercise'
import { postOfficeExerciseMock } from '~/composables/mocks/exercises/postOfficeExercise'

export const exerciseMocks: Exercise[] = [
  newsStandExerciseMock,
  newsStandMediumExerciseMock,
  postOfficeExerciseMock,
]

export const getExerciseMockById = (exerciseId: string) => {
  return exerciseMocks.find((exercise) => exercise.id === exerciseId) ?? null
}

export const getExerciseMocksByIds = (exerciseIds: string[]) => {
  return exerciseIds
    .map((exerciseId) => getExerciseMockById(exerciseId))
    .filter((exercise): exercise is Exercise => exercise !== null)
}
