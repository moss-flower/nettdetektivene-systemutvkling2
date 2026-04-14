<script setup lang="ts">
const achievements = [
  { title: 'Første steg', description: 'Fullførte første oppgave.' },
  { title: 'XP-jeger', description: 'Kom over 500 XP.' },
  { title: 'På vei opp', description: 'Nådde nivå 7.' }
]
</script>

<template>
  <section class="achievementsPage">
    <header class="pageHeader">
      <NuxtLink class="backLink" to="/student/profile">Tilbake til profil</NuxtLink>
      <p class="eyebrow">Achievements</p>
      <h1>Mine prestasjoner</h1>
      <p class="subtitle">Foreløpige plassholdere til vi får ekte data.</p>
    </header>

    <div class="achievementsGrid">
      <article v-for="achievement in achievements" :key="achievement.title" class="achievementCard">
        <h2>{{ achievement.title }}</h2>
        <p>{{ achievement.description }}</p>
      </article>
    </div>
  </section>
</template>

<style scoped>
.achievementsPage {
  width: 100%;
  max-width: 900px;
  margin: 0 auto;
  padding: var(--space-4);
}

.pageHeader {
  margin-bottom: var(--space-4);
}

.backLink {
  display: inline-block;
  margin-bottom: 0.75rem;
  color: var(--primary);
  font-weight: 600;
}

.eyebrow {
  color: var(--primary);
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  margin-bottom: 0.5rem;
}

h1 {
  margin: 0 0 0.5rem 0;
  color: var(--text);
}

.subtitle {
  margin: 0;
  color: var(--muted);
}

.achievementsGrid {
  display: grid;
  gap: 1rem;
}

.achievementCard {
  padding: 1rem;
  border: 1px solid var(--border);
  border-radius: 1rem;
  background: linear-gradient(180deg, #ffffff 0%, #f9fbff 100%);
}

.achievementCard h2 {
  margin-bottom: 0.35rem;
  color: var(--text);
}

.achievementCard p {
  color: var(--muted);
}
</style>
