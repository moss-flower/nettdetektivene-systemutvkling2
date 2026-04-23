<script setup lang="ts">
import { routePaths } from "~/composables/utils/routePaths"

const volume = ref(65)
const volumeLabel = computed(() => `${volume.value}%`)
</script>

<template>
  <section class="student-settings">
    <header class="student-settings__header">
      <NuxtLink class="student-settings__back" :to="routePaths.studentHome">Tilbake til hjem</NuxtLink>
      <p class="student-settings__eyebrow">Innstillinger</p>
      <h2>Volum på nettsiden</h2>
      <p class="student-settings__intro">
        Midlertidig settings-side med volum.
      </p>
    </header>

    <article class="student-settings__card">
      <div class="student-settings__volume-row">
        <label class="student-settings__label" for="volume">Volum</label>
        <span class="student-settings__value">{{ volumeLabel }}</span>
      </div>

      <input
        id="volume"
        v-model="volume"
        class="student-settings__slider"
        type="range"
        min="0"
        max="100"
        step="1"
        aria-label="Juster volum for nettsiden"
      >

      <p class="student-settings__note">
        bare en mal rn.
      </p>
    </article>
  </section>
</template>

<style scoped>
.student-settings {
  min-height: 100%;
  display: grid;
  gap: var(--space-3);
  padding: var(--space-3);
}

.student-settings__header {
  display: grid;
  gap: 0.75rem;
}

.student-settings__back {
  width: fit-content;
  color: var(--primary);
  font-weight: 700;
}

.student-settings__eyebrow {
  color: var(--primary);
  font-size: 0.9rem;
  font-weight: 800;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.student-settings__intro,
.student-settings__note {
  max-width: 65ch;
  color: var(--muted);
  line-height: 1.5;
}

.student-settings__card {
  display: grid;
  gap: 1rem;
  max-width: 36rem;
  padding: 1rem;
  border: 2px solid var(--border);
  border-radius: var(--radius);
  background: var(--surface);
}

.student-settings__volume-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
}

.student-settings__label {
  font-weight: 800;
}

.student-settings__value {
  color: var(--primary);
  font-weight: 800;
}

.student-settings__slider {
  width: 100%;
  accent-color: var(--primary);
}

@media (max-width: 560px) {
  .student-settings {
    padding: var(--space-2);
  }

  .student-settings__volume-row {
    align-items: flex-start;
    flex-direction: column;
  }
}
</style>