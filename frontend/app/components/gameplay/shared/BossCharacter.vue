<script setup lang="ts">
import datatyvenSprite from '~/assets/images/datatyven.png'
import damageSound from '~/assets/sound/ow.wav'
import { useEffectSound } from '~/composables/useEffectSound'

type BossTaskImage = {
  src: string
  alt: string
  label?: string
}

const props = defineProps<{
  hp: number
  maxHp: number
  damaged: boolean
  images?: BossTaskImage[]
}>()

const taskImages = computed<BossTaskImage[]>(() => props.images ?? [])

const hpPercent = computed(() => {
  if (props.maxHp <= 0) return 0
  return Math.max(0, Math.min(100, (props.hp / props.maxHp) * 100))
})

const defeated = computed(() => props.hp <= 0)

const showBubble = ref(false)
const playDamageSound = useEffectSound(damageSound)
let bubbleTimeout: ReturnType<typeof setTimeout> | null = null

watch(() => props.damaged, (next) => {
  if (!next) return
  if (bubbleTimeout) clearTimeout(bubbleTimeout)
  playDamageSound()
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

    <div
        class="boss__stage"
        :class="{ 'boss__stage--with-images': taskImages.length > 0 }"
    >
      <div
          v-if="taskImages.length > 0"
          class="boss__task-images"
          :class="{ 'boss__task-images--multi': taskImages.length > 1 }"
      >
        <figure
            v-for="(image, index) in taskImages"
            :key="`${image.src}-${index}`"
            class="boss__task-image"
        >
          <img :src="image.src" :alt="image.alt" class="boss__task-image-img">
          <figcaption v-if="image.label" class="boss__task-image-label">
            {{ image.label }}
          </figcaption>
        </figure>
      </div>

      <div class="boss__sprite-wrapper">
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
  min-height: 20rem;
  padding-top: 2.5rem;
  width: 100%;
}

.boss__stage--with-images {
  grid-template-columns: minmax(0, 1fr) auto;
  align-items: center;
  justify-items: center;
  gap: clamp(1rem, 2.5vw, 2rem);
}

.boss__task-images {
  display: grid;
  gap: 0.9rem;
  width: 100%;
  max-width: 28rem;
  justify-self: end;
}

.boss__task-images--multi {
  grid-template-columns: repeat(2, minmax(0, 1fr));
  max-width: 36rem;
}

.boss__task-image {
  margin: 0;
  display: grid;
  gap: 0.4rem;
  padding: 0.55rem;
  background: #fffdfa;
  border: 2px solid #d6d3ce;
  border-radius: 1rem;
  box-shadow: 0 10px 20px rgba(47, 39, 28, 0.1);
}

.boss__task-image-img {
  width: 100%;
  aspect-ratio: 1.4 / 1;
  object-fit: cover;
  border-radius: 0.7rem;
  display: block;
}

.boss__task-image-label {
  font-size: 0.78rem;
  font-weight: 700;
  letter-spacing: 0.06em;
  text-transform: uppercase;
  color: #6b7280;
  text-align: center;
}

.boss__sprite-wrapper {
  position: relative;
  display: grid;
  place-items: center;
}

.boss__sprite {
  width: clamp(21rem, 48vw, 30rem);
  height: auto;
  display: block;
  animation: boss-bob 2.2s ease-in-out infinite;
  transform-origin: center bottom;
}

.boss__stage--with-images .boss__sprite {
  width: clamp(14rem, 28vw, 22rem);
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

@media (max-width: 760px) {
  .boss__stage--with-images {
    grid-template-columns: 1fr;
    justify-items: center;
  }

  .boss__task-images {
    justify-self: center;
  }

  .boss__stage--with-images .boss__sprite {
    width: clamp(12rem, 50vw, 18rem);
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
