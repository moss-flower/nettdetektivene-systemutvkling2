<script setup lang="ts">
import datatyvenSprite from '~/assets/images/datatyven.png'
import {useSound} from '@vueuse/sound'
import damageSound from '~/assets/sound/ow.wav'

const props = defineProps<{
  hp: number
  maxHp: number
  damaged: boolean
}>()

const hpPercent = computed(() => {
  if (props.maxHp <= 0) return 0
  return Math.max(0, Math.min(100, (props.hp / props.maxHp) * 100))
})

const defeated = computed(() => props.hp <= 0)

const showBubble = ref(false)
const { play } =   useSound(damageSound, {volume: 1})
let bubbleTimeout: ReturnType<typeof setTimeout> | null = null

watch(() => props.damaged, (next) => {
  if (!next) return
  if (bubbleTimeout) clearTimeout(bubbleTimeout)
  play()
  showBubble.value = true
  bubbleTimeout = setTimeout(() => {
    showBubble.value = false
  }, 2800)
})

onBeforeUnmount(() => {
  if (bubbleTimeout) clearTimeout(bubbleTimeout)
})
</script>

<template>
  <div class="boss">
    <h2 class="boss__title">Datatyven</h2>

    <div class="boss__hp" role="progressbar" :aria-valuenow="hp" :aria-valuemin="0"
         :aria-valuemax="maxHp">
      <div class="boss__hp-fill" :style="{ width: `${hpPercent}%` }"/>
      <span class="boss__hp-text">{{ Math.max(0, hp) }} / {{ maxHp }} HP</span>
    </div>

    <div class="boss__stage">
      <Transition name="bubble">
        <div v-if="showBubble" class="boss__bubble">Au!</div>
      </Transition>

      <img
          :src="datatyvenSprite"
          alt="Datatyven"
          class="boss__sprite"
          :class="{
          'boss__sprite--damaged': damaged,
          'boss__sprite--defeated': defeated,
        }"
      >
    </div>
  </div>
</template>

<style scoped>
.boss {
  display: grid;
  justify-items: center;
  gap: 0.75rem;
  width: 100%;
}

.boss__title {
  margin: 0;
  font-size: 1.5rem;
  font-weight: 800;
  letter-spacing: 0.06em;
  text-transform: uppercase;
  color: #1f2937;
}

.boss__hp {
  position: relative;
  width: min(100%, 28rem);
  height: 1.4rem;
  border-radius: 999px;
  background: #e5e7eb;
  border: 2px solid #1f2937;
  overflow: hidden;
}

.boss__hp-fill {
  height: 100%;
  background: linear-gradient(90deg, #ef4444, #f97316);
  transition: width 0.4s ease;
}

.boss__hp-text {
  position: absolute;
  inset: 0;
  display: grid;
  place-items: center;
  font-size: 0.85rem;
  font-weight: 700;
  color: #111827;
  letter-spacing: 0.04em;
}

.boss__stage {
  position: relative;
  display: grid;
  place-items: center;
  min-height: 11rem;
  padding-top: 2.5rem;
}

.boss__sprite {
  width: clamp(7rem, 18vw, 10rem);
  height: auto;
  display: block;
  animation: boss-bob 2.2s ease-in-out infinite;
  transform-origin: center bottom;
}

.boss__sprite--damaged {
  animation: boss-shake 0.45s ease;
  filter: brightness(1.15) sepia(1) hue-rotate(-50deg) saturate(6) drop-shadow(0 0 12px rgba(239, 68, 68, 0.8));
}

.boss__sprite--defeated {
  animation: none;
  filter: grayscale(1);
  opacity: 0.65;
  transform: rotate(15deg);
}

.boss__bubble {
  position: absolute;
  top: 0;
  left: 50%;
  transform: translateX(-50%);
  padding: 0.4rem 0.9rem;
  background: #ffffff;
  border: 2px solid #1f2937;
  border-radius: 1rem;
  font-weight: 800;
  font-size: 1rem;
  color: #1f2937;
  box-shadow: 0 6px 14px rgba(0, 0, 0, 0.12);
  white-space: nowrap;
}

.boss__bubble::after {
  content: '';
  position: absolute;
  bottom: -10px;
  left: 50%;
  transform: translateX(-50%);
  border: 6px solid transparent;
  border-top-color: #ffffff;
}

.boss__bubble::before {
  content: '';
  position: absolute;
  bottom: -13px;
  left: 50%;
  transform: translateX(-50%);
  border: 7px solid transparent;
  border-top-color: #1f2937;
}

.bubble-enter-from,
.bubble-leave-to {
  opacity: 0;
  transform: translate(-50%, 6px) scale(0.9);
}

.bubble-enter-active,
.bubble-leave-active {
  transition: opacity 0.18s ease, transform 0.18s ease;
}

@keyframes boss-bob {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

@keyframes boss-shake {
  0%, 100% {
    transform: translate(0, 0) rotate(0);
  }
  20% {
    transform: translate(-8px, -2px) rotate(-5deg);
  }
  40% {
    transform: translate(8px, 2px) rotate(5deg);
  }
  60% {
    transform: translate(-6px, -1px) rotate(-3deg);
  }
  80% {
    transform: translate(6px, 1px) rotate(3deg);
  }
}
</style>
