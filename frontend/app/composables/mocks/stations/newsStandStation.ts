import placeholderImage from '~/assets/images/placeholder.jpeg'
import { newsStandExerciseMock, newsStandMediumExerciseMock } from '~/composables/mocks/exercises/newsStandExercise'
import { Station } from '~/composables/types/StationType'

export const newsStandStationMock = new Station({
  id: 'station-newsstand',
  slug: 'newsstand',
  title: 'Nyhetskvartalet',
  description: 'I oppgaven under ser du nyhetssaker og skjermbilder. Bruk detektivblikket ditt for å finne ut hva som virker troverdig og hva som virker mistenkelig.',
  image: placeholderImage,
  exerciseIds: [newsStandExerciseMock.id, newsStandMediumExerciseMock.id],
})
