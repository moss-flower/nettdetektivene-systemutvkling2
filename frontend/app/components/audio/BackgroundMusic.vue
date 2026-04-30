<script setup lang="ts">
import {useAudioSettings} from "~/composables/useAudioSettings";
import AMBIENT_SRC from '~/assets/sound/background-music.wav'
import BOSSFIGHT_SRC from '~/assets/sound/boss-fight.mp3' //TODO: Currently a royalty free brazilian phonk track, please find something better

const {musicGain, bossfightActive} = useAudioSettings()

const audioRef = ref<HTMLAudioElement | null>(null)

const currentSrc = computed(() => bossfightActive.value ? BOSSFIGHT_SRC : AMBIENT_SRC)

const tryPlay = async () => {
  const audio = audioRef.value
  if (!audio) return
  try {
    await audio.play()
  } catch {
    //In case browser blocks autoplay
    const resume = () => {
      audio.play().catch(() => {
      })
      window.removeEventListener('pointerdown', resume)
      window.removeEventListener('keydown', resume)
    }
    window.addEventListener('pointerdown', resume, {once: true})
    window.addEventListener('keydown', resume, {once: true})
  }
}

watch(musicGain, (gain) => {
  const audio = audioRef.value
  if (audio) audio.volume = gain
}, {immediate: false})

watch(currentSrc, () => {
  const audio = audioRef.value
  if (!audio) return
  audio.volume = musicGain.value
  audio.load()
  tryPlay()
})

onMounted(() => {
  const audio = audioRef.value
  if (!audio) return
  audio.volume = musicGain.value
  tryPlay()
})
</script>

<template>
  <audio
      ref="audioRef"
      :src="currentSrc"
      loop
      preload="auto"
      aria-hidden="true"
  />
</template>
