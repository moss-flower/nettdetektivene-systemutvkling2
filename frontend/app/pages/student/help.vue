<script setup lang="ts">
const canReplay = ref(true)

const replayHelp = () => {
  // Placeholder handler for first-login guidance replay.
  canReplay.value = false
  setTimeout(() => {
    canReplay.value = true
  }, 500)
}
</script>

<template>
  <section class="student-help">
    <header class="student-help__header">
      <p class="student-help__eyebrow">Hjelp</p>
      <h2>Hjelpemeldinger for elev</h2>
      <p class="student-help__intro">
        Denne siden er en mal for hjelpetekster som vises ved forste innlogging.
        Selve tekstene legges inn senere.
      </p>
    </header>

    <article class="student-help__card">
      <h3>Førstegangsveiledning</h3>
      <div class="student-help__placeholder">
        <p>Placeholder: Her kommer stegvise hjelpetekster for nye brukere.</p>
      </div>

      <button
        type="button"
        class="student-help__replay"
        :disabled="!canReplay"
        @click="replayHelp"
      >
        Spill hjelpesekvens på nytt
      </button>
    </article>
  </section>
</template>

<style scoped>
.student-help {
  /* station reception wall */
  --help-wall: var(--palette-wood-900);
  --help-wall-seam: var(--palette-overlay-warm-glow);
  --help-wall-glow: var(--palette-overlay-warm-glow);
  --help-counter: var(--palette-cork-700);
  --help-counter-edge: var(--palette-cork-900);

  /* Page text on dark wall */
  --help-eyebrow: var(--palette-gold-500);
  --help-title: var(--palette-cream-100);
  --help-title-depth: var(--palette-overlay-dark-strong);
  --help-intro: var(--palette-cream-300);

  /* card surface, reception window */
  --help-card-bg-top: var(--palette-cream-100);
  --help-card-bg-bottom: var(--palette-cream-200);
  --help-card-border: var(--palette-overlay-cork-medium);
  --help-card-text: var(--palette-cork-900);
  --help-card-muted: var(--palette-cork-580);
  --help-card-shadow: var(--palette-overlay-dark-deep);

  /* placeholder dashed region */
  --help-placeholder-border: var(--palette-gray-300);
  --help-placeholder-text: var(--palette-cork-580);

  /* Replay button */
  --help-replay-border: var(--palette-cork-900);
  --help-replay-text: var(--palette-cork-900);
  --help-replay-hover: var(--palette-overlay-cork-soft);

  min-height: 100%;
  display: grid;
  gap: var(--space-5);
  align-content: start;
  padding: var(--layout-page-padding);
  padding-bottom: 120px;
  border-radius: var(--radius-2);
  color: var(--help-title);
  background:
    /* warm desk-lamp glow, low & narrow; like a counter-lamp */
    radial-gradient(ellipse 50% 40% at 50% 65%, var(--help-wall-glow), transparent 70%),
    /* vertical panel seams */
    repeating-linear-gradient(
      90deg,
      transparent 0,
      transparent 240px,
      var(--help-wall-seam) 240px,
      var(--help-wall-seam) 241px
    ),
    var(--help-wall);
  overflow-x: hidden;
}

.student-help__header {
  display: grid;
  gap: var(--space-3);
  max-width: 680px;
}

.student-help__back {
  width: fit-content;
  margin-bottom: var(--space-2);
}

.student-help__eyebrow {
  margin: 0;
  color: var(--help-eyebrow);
  font-family: var(--font-family-body), sans-serif;
  font-size: var(--font-size-14);
  line-height: var(--font-leading-14);
  font-weight: var(--font-weight-bold);
  letter-spacing: var(--font-tracking-wider);
  text-transform: uppercase;
}

.student-help__header h2 {
  margin: 0;
  font-family: var(--font-family-display), sans-serif;
  font-size: var(--font-size-36);
  line-height: var(--font-leading-36);
  font-weight: var(--font-weight-bold);
  color: var(--help-title);
  text-shadow: 0 1px 2px var(--help-title-depth);
}

.student-help__intro {
  margin: 0;
  max-width: 60ch;
  color: var(--help-intro);
  font-family: var(--font-family-body), sans-serif;
  font-size: var(--font-size-16);
  line-height: var(--font-leading-16);
}

.student-help__card {
  display: grid;
  gap: var(--space-5);
  max-width: 680px;
  padding: var(--space-6);
  border: var(--border-width-hairline) solid var(--help-card-border);
  border-radius: var(--radius-2);
  background: linear-gradient(
    180deg,
    var(--help-card-bg-top) 0%,
    var(--help-card-bg-bottom) 100%
  );
  box-shadow: var(--space-2) var(--space-4) var(--space-6) var(--help-card-shadow);
  color: var(--help-card-text);
}

.student-help__card h3 {
  margin: 0;
  font-family: var(--font-family-display), sans-serif;
  font-size: var(--font-size-22);
  line-height: var(--font-leading-22);
  font-weight: var(--font-weight-bold);
  color: var(--help-card-text);
}

.student-help__placeholder {
  min-height: var(--space-8);
  padding: var(--space-4);
  border: var(--border-width-hairline) dashed var(--help-placeholder-border);
  border-radius: var(--radius-1);
  color: var(--help-placeholder-text);
  font-family: var(--font-family-body), sans-serif;
  font-size: var(--font-size-16);
  line-height: var(--font-leading-16);
  display: grid;
  align-items: center;
}

.student-help__replay {
  width: fit-content;
  min-height: var(--size-control-md);
  padding: var(--space-3) var(--space-4);
  border: var(--border-width-thin) solid var(--help-replay-border);
  border-radius: var(--radius-pill);
  background: transparent;
  color: var(--help-replay-text);
  font-family: var(--font-family-body), sans-serif;
  font-weight: var(--font-weight-bold);
  cursor: pointer;
  transition: background var(--transition-fast);
}

.student-help__replay:hover {
  background: var(--help-replay-hover);
}

.student-help__replay:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

@media (max-width: 560px) {
  .student-help {
    padding: var(--space-5) var(--space-4);
  }
}
</style>
