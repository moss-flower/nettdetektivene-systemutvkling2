<script setup lang="ts">
defineProps<{
  hint: string;
  title?: string;
  emptyText?: string;
}>();

const emit = defineEmits<{
  (event: 'close'): void;
}>();
</script>

<template>
  <div class="tipsOverlay" role="dialog" aria-modal="true" aria-labelledby="tips-title" @click.self="emit('close')">
    <article class="tipsCard">
      <button class="tipsClose" type="button" aria-label="Lukk tips" @click="emit('close')">
        x
      </button>

      <img
        class="tipsImage"
        src="https://placehold.co/720x360?text=Tipsbrev"
        alt="Illustrasjon av et brev med tips"
      >

      <div class="tipsContent">
        <p class="tipsEyebrow">Tipsbrev</p>
        <h2 id="tips-title">{{ title ?? 'Tips til oppgaven' }}</h2>
        <p>{{ hint || emptyText || 'Det finnes ikke noe tips for denne oppgaven ennå.' }}</p>
      </div>
    </article>
  </div>
</template>

<style scoped>
.tipsOverlay {
  position: fixed;
  inset: 0;
  z-index: 1000;
  display: grid;
  place-items: center;
  padding: 1rem;
  background: rgba(15, 23, 42, 0.62);
  backdrop-filter: blur(4px);
}

.tipsCard {
  position: relative;
  width: min(92vw, 34rem);
  overflow: hidden;
  border: 3px solid #78350f;
  border-radius: 1.4rem;
  background: #fff7ed;
  color: #431407;
  box-shadow: 0 24px 70px rgba(0, 0, 0, 0.35);
}

.tipsClose {
  position: absolute;
  top: 0.75rem;
  right: 0.75rem;
  z-index: 1;
  width: 2.4rem;
  height: 2.4rem;
  border: 2px solid rgba(67, 20, 7, 0.3);
  border-radius: 999px;
  background: rgba(255, 247, 237, 0.92);
  color: #431407;
  font-size: 1rem;
  font-weight: 900;
  cursor: pointer;
}

.tipsImage {
  display: block;
  width: 100%;
  height: auto;
}

.tipsContent {
  display: grid;
  gap: 0.7rem;
  padding: clamp(1.2rem, 4vw, 2rem);
}

.tipsEyebrow,
.tipsContent h2,
.tipsContent p {
  margin: 0;
}

.tipsEyebrow {
  color: #b45309;
  font-size: 0.82rem;
  font-weight: 900;
  letter-spacing: 0.12em;
  text-transform: uppercase;
}

.tipsContent h2 {
  font-size: clamp(1.5rem, 5vw, 2.1rem);
}

.tipsContent p {
  font-size: 1.05rem;
  line-height: 1.65;
}
</style>
