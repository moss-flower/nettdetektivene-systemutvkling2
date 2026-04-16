import type { InjectionKey } from 'vue'
import type { VueConfettiOptions } from 'vue-confetti'

export interface GameConfettiController {
  celebrateCorrectAnswer: (options?: VueConfettiOptions) => void
  celebrateLevelComplete: (options?: VueConfettiOptions) => void
  stop: () => void
}

const gameConfettiKey: InjectionKey<GameConfettiController> = Symbol('game-confetti')

const createNoopController = (): GameConfettiController => ({
  celebrateCorrectAnswer: () => {},
  celebrateLevelComplete: () => {},
  stop: () => {},
})

export const provideGameConfetti = () => {
  const { $confetti } = useNuxtApp()
  let confettiTimeout: ReturnType<typeof setTimeout> | null = null

  const stop = () => {
    if (confettiTimeout) {
      clearTimeout(confettiTimeout)
      confettiTimeout = null
    }

    $confetti.stop()
  }

  const startBurst = (options: VueConfettiOptions, durationMs: number) => {
    stop()
    $confetti.start(options)
    confettiTimeout = setTimeout(() => {
      $confetti.stop()
      confettiTimeout = null
    }, durationMs)
  }

  const controller: GameConfettiController = {
    celebrateCorrectAnswer: (options = {}) => {
      startBurst({
        particlesPerFrame: 4,
        defaultDropRate: 10,
        defaultSize: 10,
        defaultColors: ['#4c7bb0', '#f2c94c', '#9ed8db', '#f2994a', '#d96c75'],
        ...options,
      }, 1200)
    },
    celebrateLevelComplete: (options = {}) => {
      startBurst({
        particlesPerFrame: 8,
        defaultDropRate: 17,
        defaultSize: 12,
        defaultColors: ['#4c7bb0', '#f2c94c', '#9ed8db', '#f2994a', '#d96c75'],
        ...options,
      }, 2600)
    },
    stop,
  }

  provide(gameConfettiKey, controller)

  onBeforeUnmount(() => {
    stop()
    $confetti.remove()
  })

  return controller
}

export const useGameConfetti = () => {
  return inject(gameConfettiKey, createNoopController())
}
