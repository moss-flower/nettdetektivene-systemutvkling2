import { useSound } from '@vueuse/sound'
import { watch } from 'vue'
import { useAudioSettings } from '~/composables/useAudioSettings'

export const useEffectSound = (src: string) => {
  const { effectsGain } = useAudioSettings()
  const { play, sound } = useSound(src, { volume: effectsGain.value })

  watch(effectsGain, (gain) => {
    sound.value?.volume(gain)
  })

  return () => {
    sound.value?.volume(effectsGain.value)
    play()
  }
}
