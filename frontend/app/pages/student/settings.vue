<script setup lang="ts">
const volume = ref(65)
const volumeLabel = computed(() => `${volume.value}%`)
</script>

<template>
  <section class="studentSettings">
    <header class="studentSettingsHeader">
      <p class="studentSettingsEyebrow">Innstillinger</p>
      <h2>Volum på nettsiden</h2>
      <p class="studentSettingsIntro">
        Midlertidig settings-side med volum.
      </p>
    </header>

    <article class="studentSettingsCard">
      <div class="settingsVolumeRow">
        <label class="studentSettingsLabel" for="volume">Volum</label>
        <span class="studentSettingsValue">{{ volumeLabel }}</span>
      </div>

      <input
        id="volume"
        v-model="volume"
        class="studentSettingsSlider"
        type="range"
        min="0"
        max="100"
        step="1"
        aria-label="Juster volum for nettsiden"
      >

      <p class="studentSettingsNote">
        bare en mal rn.
      </p>
    </article>
  </section>
</template>

<style scoped>
.studentSettings {
  min-height: 100%;
  display: grid;
  gap: var(--space-3);
  padding: var(--space-3);
}

.studentSettingsHeader {
  display: grid;
  gap: var(--space-3);
}

.studentSettingsEyebrow {
  color: var(--color-brand-primary);
  font-size: 0.9rem;
  font-weight: 800;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.studentSettingsIntro,
.studentSettingsNote {
  max-width: 65ch;
  color: var(--color-text-secondary);
  line-height: 1.5;
}

.studentSettingsCard {
  display: grid;
  gap: var(--space-4);
  max-width: 36rem;
  padding: var(--space-4);
  border: var(--border-width-thin) solid var(--color-border-default);
  border-radius: var(--radius-3);
  background: var(--color-bg-surface);
  box-shadow: var(--shadow-1);
}

.settingsVolumeRow {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--space-4);
}

.studentSettingsLabel {
  font-weight: 800;
}

.studentSettingsValue {
  color: var(--color-brand-primary);
  font-weight: 800;
}

.studentSettingsSlider {
  width: 100%;
  accent-color: var(--color-brand-primary);
}

</style>
