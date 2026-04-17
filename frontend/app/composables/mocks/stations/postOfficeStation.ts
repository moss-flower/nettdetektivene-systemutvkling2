import placeholderImage from '~/assets/images/placeholder.jpeg'
import { postOfficeExerciseMock } from '~/composables/mocks/exercises/postOfficeExercise'
import { Station } from '~/composables/types/StationType'

export const postOfficeStationMock = new Station({
  id: 'station-postoffice',
  slug: 'postoffice',
  title: 'Postkontoret',
  description: 'Her trener du på å oppdage rare e-poster. Trykk på det som virker mistenkelig før du bestemmer deg for hva du skal gjøre videre.',
  image: placeholderImage,
  exerciseIds: [postOfficeExerciseMock.id],
})
