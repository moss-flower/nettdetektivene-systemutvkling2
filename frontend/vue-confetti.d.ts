declare module 'vue-confetti' {
  import type { App } from 'vue'

  export interface VueConfettiOptions {
    canvasId?: string
    canvasElement?: HTMLCanvasElement
    particlesPerFrame?: number
    windSpeedMax?: number
    defaultType?: 'circle' | 'rect' | 'heart' | 'image'
    defaultSize?: number
    defaultDropRate?: number
    defaultColors?: string[]
    particles?: Array<{
      type?: 'circle' | 'rect' | 'heart' | 'image'
      size?: number
      dropRate?: number
      colors?: string[]
      url?: string | null
    }>
  }

  export class Confetti {
    start(options?: VueConfettiOptions): void
    stop(): void
    update(options?: VueConfettiOptions): void
    remove(): void
  }

  const VueConfetti: {
    install(app: App, options?: VueConfettiOptions): void
  }

  export default VueConfetti
}

declare module '#app' {
  interface NuxtApp {
    $confetti: import('vue-confetti').Confetti
  }
}

declare module 'vue' {
  interface ComponentCustomProperties {
    $confetti: import('vue-confetti').Confetti
  }
}

export {}
