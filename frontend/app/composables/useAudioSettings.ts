const STORAGE_KEY = 'audio-settings:v1'

type StoredSettings = {
  musicVolume: number
  effectsVolume: number
}

const DEFAULTS: StoredSettings = {
  musicVolume: 65,
  effectsVolume: 80,
}

const clamp = (value: number) => Math.max(0, Math.min(100, Math.round(value)))

const readFromStorage = (): StoredSettings => {
  if (typeof window === 'undefined') return DEFAULTS
  try {
    const raw = window.localStorage.getItem(STORAGE_KEY)
    if (!raw) return DEFAULTS
    const parsed = JSON.parse(raw) as Partial<StoredSettings>
    return {
      musicVolume: clamp(parsed.musicVolume ?? DEFAULTS.musicVolume),
      effectsVolume: clamp(parsed.effectsVolume ?? DEFAULTS.effectsVolume),
    }
  } catch {
    return DEFAULTS
  }
}

export const useAudioSettings = () => {
  const musicVolume = useState<number>('audio:music-volume', () => readFromStorage().musicVolume)
  const effectsVolume = useState<number>('audio:effects-volume', () => readFromStorage().effectsVolume)

  if (import.meta.client) {
    watch(
        [musicVolume, effectsVolume],
        ([music, effects]) => {
          try {
            window.localStorage.setItem(
                STORAGE_KEY,
                JSON.stringify({
                  musicVolume: clamp(music),
                  effectsVolume: clamp(effects),
                }),
            )
          } catch(error) {
            console.warn('Failed to save audio settings: ', error);
          }
        },
        {flush: 'post'},
    )
  }

  const musicGain = computed(() => clamp(musicVolume.value) / 100)
  const effectsGain = computed(() => clamp(effectsVolume.value) / 100)

  const bossfightActive = useState<boolean>('audio:bossfight-active', () => false)

  return {
    musicVolume,
    effectsVolume,
    musicGain,
    effectsGain,
    bossfightActive,
  }
}
