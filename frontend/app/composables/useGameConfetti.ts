import type { VueConfettiOptions } from 'vue-confetti'

export interface GameConfettiController {
  celebrateCorrectAnswer: (options?: VueConfettiOptions) => void
  celebrateLevelComplete: (options?: VueConfettiOptions) => void
  stop: () => void
}

const GAME_CONFETTI_COLORS = [
  '#4c7bb0',
  '#f2c94c',
  '#9ed8db',
  '#f2994a',
  '#d96c75',
]

let confettiTimeout: ReturnType<typeof setTimeout> | null = null

const createNoopController = (): GameConfettiController => ({
  celebrateCorrectAnswer: () => {},
  celebrateLevelComplete: () => {},
  stop: () => {},
})

const createController = (): GameConfettiController => {
  const { $confetti } = useNuxtApp()

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

  return {
    celebrateCorrectAnswer: (options = {}) => {
      startBurst({
        particlesPerFrame: 4,
        defaultDropRate: 10,
        defaultSize: 10,
        defaultColors: GAME_CONFETTI_COLORS,
        ...options,
      }, 1200)
    },
    celebrateLevelComplete: (options = {}) => {
      startBurst({
        particlesPerFrame: 8,
        defaultDropRate: 17,
        defaultSize: 12,
        defaultColors: GAME_CONFETTI_COLORS,
        ...options,
      }, 2600)
    },
    stop,
  }
}

export const provideGameConfetti = () => {
  if (import.meta.server) {
    return createNoopController()
  }

  const { $confetti } = useNuxtApp()
  const controller = createController()

  onBeforeUnmount(() => {
    controller.stop()
    $confetti.remove()
  })

  return controller
}

export const useGameConfetti = () => {
  if (import.meta.server) {
    return createNoopController()
  }

  return createController()
}
