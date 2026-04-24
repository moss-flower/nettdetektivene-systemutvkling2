<script setup lang="ts">
import { routePaths } from '~/composables/utils/routePaths'

const studentName = 'Saddam Hussein'
const currentLevel = 7
const currentXp = 840
const nextLevelXp = 1000
const xpProgress = Math.round((currentXp / nextLevelXp) * 100)
</script>

<template>
  <section class="profilePage">
    <header class="pageHeader">
      <p class="eyebrow">Min profil</p>
      <h1>{{ studentName }}</h1>
      <p class="subtitle">Oversikt over navn, nivå og progresjon.</p>
    </header>

    <div class="profileCard">
      <div class="avatarWrap" aria-hidden="true">placeholder</div>

      <div class="infoBlock">
        <p class="label">Navn</p>
        <p class="value">{{ studentName }}</p>
      </div>

      <div class="infoBlock">
        <p class="label">Nivå</p>
        <p class="value">{{ currentLevel }}</p>
      </div>

      <div class="infoBlock">
        <p class="label">XP</p>
        <p class="value">{{ currentXp }} / {{ nextLevelXp }}</p>
      </div>

      <div class="progressSection">
        <div class="progressHeader">
          <span>Fremdrift til neste nivå</span>
          <span>{{ xpProgress }}%</span>
        </div>
        <div class="progressBar" role="progressbar" :aria-valuenow="xpProgress" aria-valuemin="0" aria-valuemax="100">
          <div class="progressFill" :style="{ width: `${xpProgress}%` }"></div>
        </div>
      </div>

      <div class="actions">
        <NuxtLink class="secondaryButton" :to="routePaths.studentAchievements">Achievements</NuxtLink>
      </div>
    </div>
  </section>
</template>

<style scoped>
.profilePage {
  width: 100%;
  max-width: 900px;
  margin: 0 auto;
  padding: var(--space-4);
}

.pageHeader {
  margin-bottom: var(--space-4);
}

.eyebrow {
  color: var(--color-brand-primary);
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  margin-bottom: var(--space-2);
}

h1 {
  margin: 0 0 var(--space-2) 0;
  color: var(--color-text-primary);
}

.subtitle {
  margin: 0;
  color: var(--color-text-secondary);
}

.profileCard {
  display: grid;
  gap: var(--space-4);
  padding: var(--space-4);
  border: var(--border-width-hairline) solid var(--color-border-default);
  border-radius: var(--radius-2);
  background: linear-gradient(180deg, var(--palette-white) 0%, var(--palette-white) 100%);
}

.avatarWrap {
  width: 88px;
  height: 88px;
  display: grid;
  place-items: center;
  border-radius: var(--radius-pill);
  background: var(--color-brand-primary);
  color: #111827;
  font-size: var(--font-size-36);
  font-weight: 700;
}

.infoBlock {
  display: grid;
  gap: var(--space-1);
}

.label {
  color: var(--color-text-secondary);
  font-size: 0.9rem;
}

.value {
  font-size: 1.2rem;
  font-weight: 700;
  color: var(--color-text-primary);
}

.progressSection {
  display: grid;
  gap: var(--space-2);
}

.progressHeader {
  display: flex;
  justify-content: space-between;
  gap: var(--space-4);
  color: var(--color-text-secondary);
}

.progressBar {
  width: 100%;
  height: 0.9rem;
  border-radius: var(--radius-pill);
  overflow: hidden;
  background: #dbe4ee;
}

.progressFill {
  height: 100%;
  background: linear-gradient(90deg, var(--color-brand-primary), #8afcc6);
}

.actions {
  display: flex;
  gap: var(--space-3);
  margin-top: var(--space-2);
}

.secondaryButton {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 0.8rem 1.1rem;
  border-radius: 0.6rem;
  border: var(--border-width-hairline) solid var(--color-brand-primary);
  background: transparent;
  color: var(--color-brand-primary);
  font-weight: 700;
}
</style>
