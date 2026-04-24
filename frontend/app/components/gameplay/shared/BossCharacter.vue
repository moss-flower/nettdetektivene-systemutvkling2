<script setup lang="ts">
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
</script>

<template>
  <div class="boss">
    <div class="boss__figure" :class="{ 'boss__figure--damaged': damaged, 'boss__figure--defeated': defeated }">
      <span class="boss__emoji" aria-hidden="true">{{ defeated ? '💀' : '👹' }}</span>
    </div>
    <div class="boss__hp">
      <div class="boss__hp-label">
        <span>BOSS</span>
        <span>{{ Math.max(0, hp) }} / {{ maxHp }}</span>
      </div>
      <div class="boss__hp-bar" role="progressbar" :aria-valuenow="hp" :aria-valuemin="0" :aria-valuemax="maxHp">
        <div class="boss__hp-fill" :style="{ width: `${hpPercent}%` }" />
      </div>
    </div>
  </div>
</template>

<style scoped>
.boss {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 0.9rem 1.1rem;
  border-radius: 1rem;
  background: #fff;
  border: 2px solid #1d4f91;
  width: 100%;
}

.boss__figure {
  flex-shrink: 0;
  width: 4rem;
  height: 4rem;
  display: grid;
  place-items: center;
  border-radius: 999px;
  background: #fee2e2;
  transition: transform 0.25s ease;
}

.boss__emoji {
  font-size: 2.25rem;
  line-height: 1;
}

.boss__figure--damaged {
  animation: boss-shake 0.35s ease;
}

.boss__figure--defeated {
  filter: grayscale(1);
  opacity: 0.7;
}

.boss__hp {
  flex: 1;
  display: grid;
  gap: 0.35rem;
}

.boss__hp-label {
  display: flex;
  justify-content: space-between;
  font-size: 0.85rem;
  font-weight: 700;
  color: #1f2937;
  letter-spacing: 0.04em;
}

.boss__hp-bar {
  height: 0.9rem;
  border-radius: 999px;
  background: #e5e7eb;
  overflow: hidden;
}

.boss__hp-fill {
  height: 100%;
  background: linear-gradient(90deg, #ef4444, #f97316);
  border-radius: 999px;
  transition: width 0.4s ease;
}

@keyframes boss-shake {
  0%, 100% { transform: translateX(0); }
  25% { transform: translateX(-6px) rotate(-4deg); }
  50% { transform: translateX(6px) rotate(4deg); }
  75% { transform: translateX(-4px) rotate(-2deg); }
}
</style>
