export type Level = {
  name: string,
  description: string,
  image: string,
  route?: string,
  locked: boolean,
  state: LevelState
}

export type LevelState = {
  completed: boolean,
  stars: number
}

