<template>
  <section class="landingPage">
    <header class="pageHeader">
      <p class="eyebrow">Velkommen til</p>
      <h1>NETTDETEKTIVENE</h1>
      <p class="subtitle">Jeg er:</p>
    </header>

    <div class="loginServiceWrapper">
      <div class="loginServiceBubble">
        <p class="serviceTitle">Elev</p>
        <p class="serviceText">
          Gå inn i spillet, løs oppgaver og følg progresjonen din.
        </p>
        <NuxtLink to="/login?role=elev">
          <button>Velg</button>
        </NuxtLink>
      </div>

      <div class="loginServiceBubble">
        <span class="serviceTitle">Lærer</span>
        <span class="serviceText">
          Administrer klasserom, følg elever og se fremgang.
        </span>
        <NuxtLink to="/login?role=laerer">
          <button>Velg</button>
        </NuxtLink>
      </div>
    </div>
  </section>
</template>

<style scoped>
.landingPage {
  min-height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: space-around;
  padding: var(--space-4);
}

.pageHeader {
  display: flex;
  flex-direction: column;
  margin-bottom: var(--space-4);
  height: fit-content;
  align-items: center;
  justify-content: center;
  padding: var(--space-4);
}

.eyebrow {
  color: var(--primary);
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  margin-bottom: 0.5rem;
}

h1 {
  font-size: 2.5rem;
  font-weight: 900;
  margin-bottom: 0.75rem;
  color: var(--text);
}

.subtitle {
  max-width: 560px;
  margin: 0 auto 2rem auto;
  color: var(--muted);
  line-height: 1.5;
}

.loginServiceWrapper {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 6rem;
}

.loginServiceBubble {
  display: flex;
  min-height: 400px;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 1.5rem;
  border-radius: 1rem;
  border: 1px solid var(--border);
  background: linear-gradient(180deg, #ffffff 0%, #f9fbff 100%);
  color: var(--text);
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.06);
  transition:
    transform var(--transition),
    box-shadow var(--transition),
    border-color var(--transition);
}

.loginServiceBubble:hover {
  transform: translateY(-4px);
  border-color: var(--primary);
  box-shadow: 0 14px 30px rgba(37, 99, 235, 0.14);
}

.serviceTitle {
  font-size: 2rem;
  font-weight: 800;
  margin-bottom: 0.5rem;
  color: var(--primary)
}

.serviceText {
  color: var(--muted);
  line-height: 1.5;
}

@media (max-width: 700px) {
  .loginCard {
    padding: 2rem 1.25rem;
  }

  h1 {
    font-size: 2rem;
  }

  .loginServiceWrapper {
    grid-template-columns: 1fr;
  }

  .loginServiceBubble {
    min-height: 150px;
  }
}
</style>
