<script setup lang="ts">
const props = withDefaults(defineProps<{
  title?: string;
  description?: string;
  buttonLabel?: string;
}>(), {
  title: "Ikke tilgjengelig ennå",
  description: "Du må fullføre det forrige steget før du får tilgang til denne.",
  buttonLabel: "Skjønner",
});

const emit = defineEmits<{
  (event: "close"): void;
}>();
</script>

<template>
  <div class="popupOverlay" @click.self="emit('close')">
    <article class="popupCard" role="dialog" aria-modal="true" aria-labelledby="popup-not-yet-unlocked-title">
      <p class="popupEyebrow">Låst</p>
      <h2 id="popup-not-yet-unlocked-title" class="popupTitle">{{ props.title }}</h2>
      <p class="popupDescription">{{ props.description }}</p>

      <button class="closeButton" type="button" @click="emit('close')">
        {{ props.buttonLabel }}
      </button>
    </article>
  </div>
</template>

<style scoped>
.popupOverlay {
  position: fixed;
  inset: 0;
  z-index: 200;
  display: grid;
  place-items: center;
  padding: var(--space-5);
  background: rgba(15, 23, 42, 0.62);
  backdrop-filter: blur(4px);
}

.popupCard {
  width: min(100%, 30rem);
  display: grid;
  gap: var(--space-4);
  padding: var(--space-5);
  border-radius: var(--radius-3);
  background: linear-gradient(180deg, var(--palette-cream-100) 0%, var(--palette-cream-200) 100%);
  box-shadow: 0 var(--space-4) var(--space-6) var(--palette-overlay-dark-deep);
}

.popupEyebrow,
.popupTitle,
.popupDescription {
  margin: 0;
}

.popupEyebrow {
  color: var(--palette-cork-580);
  font-size: var(--font-size-14);
  font-weight: var(--font-weight-bold);
  letter-spacing: var(--font-tracking-wider);
  text-transform: uppercase;
}

.popupTitle {
  color: var(--palette-cork-900);
}

.popupDescription {
  color: var(--palette-cork-700);
  line-height: var(--font-leading-base);
}

.closeButton {
  justify-self: end;
  padding: 0.8rem 1.1rem;
  border: 0;
  border-radius: var(--radius-pill);
  background: var(--palette-navy-900);
  color: var(--palette-cream-100);
  font: inherit;
  font-weight: var(--font-weight-bold);
  cursor: pointer;
}
</style>
