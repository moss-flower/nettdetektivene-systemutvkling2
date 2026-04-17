import type { StationMock } from '~/composables/types/TaskTypes'
import { newsStandStationMock } from '~/composables/mocks/stations/newsStandStation'
import { postOfficeStationMock } from '~/composables/mocks/stations/postOfficeStation'

export const stationMocks: StationMock[] = [
  newsStandStationMock,
  postOfficeStationMock,
]

export const normalizeStationSlug = (slug: string) => slug.trim().toLowerCase()

export const getStationMockBySlug = (slug: string) => {
  const normalizedSlug = normalizeStationSlug(slug)

  return stationMocks.find((station) => normalizeStationSlug(station.slug) === normalizedSlug) ?? null
}

export const getStationRoutePath = (slug: string) => `/games/${normalizeStationSlug(slug)}`
