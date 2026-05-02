export type IntroVideoMeta = {
  youtubeId: string
  titleNo: string
  stationName: string
  durationSec: number
}

export const STATION_VIDEOS: ReadonlyArray<IntroVideoMeta> = [
  { stationName: 'Nyhetskvartalet', titleNo: 'Nyhetskvartalet.', youtubeId: '8Yc5HRq4Vko', durationSec: 62 },
  { stationName: 'Fotografen', titleNo: 'Ekte eller AI-laget?', youtubeId: 'MLCg7N9wrmA', durationSec: 56 },
  { stationName: 'Postkontoret', titleNo: 'Ekte eller svindel?', youtubeId: 'dtbCee3AAIk', durationSec: 57 },
  { stationName: 'Passordbanken', titleNo: 'Hva er et sterkt passord?', youtubeId: 'OMk35smP50E', durationSec: 37 },
  { stationName: 'Markedsplassen', titleNo: 'Markedsplassen.', youtubeId: 'ePCgLkJAcQ8', durationSec: 60 },
  { stationName: 'Den sosiale møteplassen', titleNo: 'Bør du dele dette?', youtubeId: 'SSTdCZ6e5-8', durationSec: 55 },
  { stationName: 'Datasenteret', titleNo: 'Det siste oppdraget.', youtubeId: 'Kgi9qeF7jN4', durationSec: 27 },
]

export const HOME_INTRO_VIDEO: IntroVideoMeta = {
  stationName: 'Hjemskjermen',
  titleNo: 'Velkommen til detektivkontoret.',
  youtubeId: 'Y9MwhFD9PUc',
  durationSec: 46,
}