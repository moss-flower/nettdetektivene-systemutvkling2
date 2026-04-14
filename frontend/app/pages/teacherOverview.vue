<template>
  <section class="overviewPage">
    <header class="overviewHeader">
      <p class="eyebrow">Læreroversikt</p>
      <h1>Hjemskjerm</h1>
      <p class="subtitle">Administrer klasserom og følg aktivitet.</p>
    </header>

    <div class="dashboardGrid">
      <article class="panel classesPanel">
        <h2>Dine klasserom</h2>
        <ul class="classList">
          <li>
            <NuxtLink to="/classroomOverview?classroomId=ronvik-6a">Ronvik skole 6A</NuxtLink>
          </li>
          <li>
            <NuxtLink to="/classroomOverview?classroomId=ronvik-6b">Ronvik skole 6B</NuxtLink>
          </li>
          <li>
            <NuxtLink to="/classroomOverview?classroomId=ronvik-6c">Ronvik skole 6C</NuxtLink>
          </li>
        </ul>
      </article>

      <article class="panel">
        <h2>Innstillinger</h2>
        <p>Profil, språk, varslingstyper og klasseregler.</p>
      </article>

      <article class="panel">
        <h2>Varslinger</h2>
        <ul class="notificationList">
          <li>3 elever har levert nye oppgaver.</li>
        </ul>
      </article>

      <article class="panel panel--highlight">
        <h2>Opprett nytt klasserom</h2>
        <p>Start et nytt klasserom og inviter elever.</p>
        <button type="button">+ Opprett klasserom</button>
      </article>
    </div>
  </section>
</template>

<style scoped>
.overviewPage {
  width: 100%;
  max-width: 1100px;
  margin: 0 auto;
  padding: var(--space-4);
}

.overviewHeader {
  margin-bottom: var(--space-4);
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

.dashboardGrid {
  display: grid;
  gap: var(--space-4);
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.panel {
  border: 1px solid var(--border);
  border-radius: 1rem;
  background: #222;
  padding: var(--space-4);
}

.classesPanel {
  grid-column: 1 / -1;
}

h2 {
  margin: 0 0 0.75rem 0;
}

.classList,
.notificationList {
  margin: 0;
  padding-left: 1rem;
  display: grid;
  gap: 0.4rem;
}

.classList a {
  font-weight: 700;
  color: var(--primary);
}

.panel--highlight {
  background: #222;
}

button {
  margin-top: 0.9rem;
  padding: 0.65rem 1rem;
  border: 1px solid var(--primary);
  border-radius: 0.5rem;
  background: var(--primary);
  color: #111;
  font-weight: 600;
  cursor: pointer;
}

@media (max-width: 900px) {
  .dashboardGrid {
    grid-template-columns: 1fr;
  }
}
</style>
