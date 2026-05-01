import { Exercise } from '~/composables/types/ExerciseType'
import type { ExerciseTask } from '~/composables/types/TaskTypes'
import type { WeeklyMysteryPrompt } from '~/composables/types/WeeklyMysteryTypes'

export type WeeklyMysteryPlaySession = {
  id: string
  weekLabel: string
  title: string
  summary: string
  outroSuccess: string
  outroRetry: string
  exerciseUid: string
  tasks: ExerciseTask[]
}

export function useStudentMysteryPlay() {
  const { $api } = useNuxtApp()
  const { student } = useClassrooms()

  const loadMysteryPlaySession = async (): Promise<WeeklyMysteryPlaySession | null> => {
    const classroom = await student.getMyClassroom()
    const mystery = await $api<WeeklyMysteryPrompt | null>(`/classrooms/${classroom.uid}/weekly-mystery`, {
      method: 'GET',
    })

    if (!mystery?.exerciseUid) {
      return null
    }

    const exercise = await Exercise.backendFetchExerciseByUid(mystery.exerciseUid)

    if (!exercise) {
      return null
    }

    return {
      id: mystery.publicId,
      weekLabel: mystery.weekLabel?.trim() || 'Ukens mysterium',
      title: mystery.title || exercise.title,
      summary: exercise.description || mystery.prompt,
      outroSuccess: 'Saken er løst. Du fullførte ukens mysterium.',
      outroRetry: 'Du er på sporet. Prøv igjen og se ekstra nøye på ledetrådene.',
      exerciseUid: exercise.uid,
      tasks: exercise.tasks,
    }
  }

  return {
    loadMysteryPlaySession,
  }
}
