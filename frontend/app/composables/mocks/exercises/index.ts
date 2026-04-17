import type { Exercise } from '~/composables/types/ExerciseType'
import { newsStandExerciseMock } from '~/composables/mocks/exercises/newsStandExercise'
import { postOfficeExerciseMock } from '~/composables/mocks/exercises/postOfficeExercise'

export const exerciseMocks: Exercise[] = [
  newsStandExerciseMock,
  postOfficeExerciseMock,
]

export const getExerciseMockById = (exerciseId: string) => {
  return exerciseMocks.find((exercise) => exercise.id === exerciseId) ?? null
}
