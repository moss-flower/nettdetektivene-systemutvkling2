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
  <section class="studentHelp">
    <header class="studentHelpHeader">
      <p class="studentHelpEyebrow">Hjelp</p>
      <h2>Hjelpemeldinger for elev</h2>
      <p class="studentHelpIntro">
        Denne siden er en mal for hjelpetekster som vises ved forste innlogging.
        Selve tekstene legges inn senere.
      </p>
    </header>

    <article class="studentHelpCard">
      <h3>Førstegangsveiledning</h3>
      <div class="studentHelpPlaceholder">
        <p>Placeholder: Her kommer stegvise hjelpetekster for nye brukere.</p>
      </div>

      <button
        type="button"
        class="studentHelpReplay"
        :disabled="!canReplay"
        @click="replayHelp"
      >
        Replay guidance
      </button>
    </article>
  </section>
</template>

<style scoped>
.studentHelp {
  min-height: 100%;
  display: grid;
  gap: var(--space-3);
  padding: var(--space-3);
}

.studentHelpHeader {
  display: grid;
  gap: var(--space-3);
}

.studentHelpEyebrow {
  color: var(--color-brand-primary);
  font-size: 0.9rem;
  font-weight: 800;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.studentHelpIntro {
  max-width: 65ch;
  color: var(--color-text-secondary);
  line-height: 1.5;
}

.studentHelpCard {
  display: grid;
  gap: var(--space-4);
  padding: var(--space-4);
  border: var(--border-width-thin) solid var(--color-border-default);
  border-radius: var(--radius-3);
  background: var(--color-bg-surface);
  box-shadow: var(--shadow-1);
}

.studentHelpPlaceholder {
  min-height: 7rem;
  padding: var(--space-4);
  border: var(--border-width-hairline) dashed var(--color-border-default);
  border-radius: var(--radius-2);
  background: color-mix(in srgb, var(--color-bg-surface) 94%, var(--color-semantic-info) 6%);
  color: var(--color-text-secondary);
  display: grid;
  align-items: center;
}

.studentHelpReplay {
  width: fit-content;
  padding: 0.7rem var(--space-4);
  border: var(--border-width-hairline) solid var(--color-brand-primary);
  border-radius: var(--radius-2);
  background: color-mix(in srgb, var(--color-bg-surface) 92%, var(--color-semantic-info) 8%);
  color: var(--color-brand-primary);
  font-weight: 700;
}

.studentHelpReplay:disabled {
  opacity: 0.6;
}

</style>
