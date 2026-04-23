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
      <UiStudentHomeBackButton class="student-help__back" />
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
        Replay guidance
      </button>
    </article>
  </section>
</template>

<style scoped>
.student-help {
  min-height: 100%;
  display: grid;
  gap: var(--space-3);
  padding: var(--space-3);
}

.student-help__header {
  display: grid;
  gap: 0.75rem;
}

.student-help__back {
  width: fit-content;
}

.student-help__eyebrow {
  color: var(--primary);
  font-size: 0.9rem;
  font-weight: 800;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.student-help__intro {
  max-width: 65ch;
  color: var(--muted);
  line-height: 1.5;
}

.student-help__card {
  display: grid;
  gap: 1rem;
  padding: 1rem;
  border: 2px solid var(--border);
  border-radius: var(--radius);
  background: var(--surface);
}

.student-help__placeholder {
  min-height: 7rem;
  padding: 1rem;
  border: 1px dashed var(--border);
  border-radius: var(--radius);
  color: var(--muted);
  display: grid;
  align-items: center;
}

.student-help__replay {
  width: fit-content;
  padding: 0.7rem 1rem;
  border: 1px solid var(--primary);
  border-radius: var(--radius);
  background: transparent;
  color: var(--primary);
  font-weight: 700;
}

.student-help__replay:disabled {
  opacity: 0.6;
}

@media (max-width: 560px) {
  .student-help {
    padding: var(--space-2);
  }
}
</style>
