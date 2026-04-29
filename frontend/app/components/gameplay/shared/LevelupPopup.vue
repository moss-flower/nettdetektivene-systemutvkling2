<script setup lang="ts">
import levelUpSound from '~/assets/sound/level-up.mp3'
import { useEffectSound } from '~/composables/useEffectSound'

defineProps<{
  level: number
}>()

const emit = defineEmits<{
  (e: 'close'): void
}>()

const playLevelUpSound = useEffectSound(levelUpSound)

onMounted(() => {
  playLevelUpSound()
})
</script>

<template>
  <div class="levelup-popup" role="dialog" aria-modal="true" aria-labelledby="levelup-popup-title">
    <div class="levelup-popup__card">
      <p class="levelup-popup__badge">Nytt nivå</p>
      <div class="levelup-popup__burst" aria-hidden="true">{{ level }}</div>
      <h2 id="levelup-popup-title" class="levelup-popup__title">Du gikk opp i level!</h2>
      <p class="levelup-popup__message">
        Bra jobbet, detektiv. Du er nå level {{ level }}.
      </p>
      <button class="levelup-popup__button" type="button" @click="emit('close')">
        Fortsett
      </button>
    </div>
  </div>
</template>

<style scoped>
.levelup-popup {
  position: fixed;
  inset: 0;
  z-index: 40;
  display: grid;
  place-items: center;
  padding: 1rem;
  background: rgba(15, 23, 42, 0.45);
}

.levelup-popup__card {
  width: min(100%, 25rem);
  display: grid;
  justify-items: center;
  gap: 1rem;
  padding: clamp(1.5rem, 5vw, 2.25rem);
  border: 4px solid #1f3657;
  border-radius: 2rem;
  background:
    radial-gradient(circle at top, #fef3c7 0, #fff7ed 38%, #ffffff 72%);
  color: #172033;
  text-align: center;
  box-shadow: 0 28px 70px rgba(15, 23, 42, 0.28);
}

.levelup-popup__badge {
  margin: 0;
  padding: 0.35rem 0.75rem;
  border-radius: 999px;
  background: #dbeafe;
  color: #1d4f91;
  font-size: 0.85rem;
  font-weight: 800;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.levelup-popup__burst {
  width: 6.5rem;
  height: 6.5rem;
  display: grid;
  place-items: center;
  border: 0.35rem solid #f59e0b;
  border-radius: 50%;
  background: #fef08a;
  color: #1f3657;
  font-size: 3rem;
  font-weight: 900;
  box-shadow: 0 0 0 0.65rem rgba(245, 158, 11, 0.18);
}

.levelup-popup__title {
  margin: 0;
  font-size: clamp(1.6rem, 6vw, 2.25rem);
  line-height: 1.05;
}

.levelup-popup__message {
  margin: 0;
  max-width: 18rem;
  color: #374151;
  font-size: 1rem;
  line-height: 1.5;
}

.levelup-popup__button {
  min-width: 9rem;
  padding: 0.9rem 1.25rem;
  border: 0;
  border-radius: 999px;
  background: #1f3657;
  color: #f9fbff;
  font-weight: 800;
  cursor: pointer;
  transition: transform 0.2s ease, background-color 0.2s ease;
}

.levelup-popup__button:hover,
.levelup-popup__button:focus-visible {
  background: #27456f;
  transform: translateY(-1px);
  outline: none;
}

@media (max-width: 640px) {
  .levelup-popup__card {
    border-radius: 1.35rem;
  }

  .levelup-popup__button {
    width: 100%;
  }
}
</style>
