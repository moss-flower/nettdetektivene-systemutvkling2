// @ts-expect-error vue-confetti does not ship usable TypeScript declarations.
import { Confetti } from 'vue-confetti'

export default defineNuxtPlugin(() => {
  const confetti = new Confetti()

  return {
    provide: {
      confetti,
    },
  }
})
