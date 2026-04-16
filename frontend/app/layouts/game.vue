<template>
  <div class="game-layout">
    <aside class="game-layout__rail game-layout__rail--left" aria-label="Spillerpanel">
      <div class="game-layout__rail-card game-layout__rail-card--avatar">
        <div class="game-layout__avatar-placeholder" aria-hidden="true">
          <span class="game-layout__avatar-face">:)</span>
        </div>
        <p class="game-layout__rail-title">Detektiv</p>
        <p class="game-layout__rail-text">Avatar og navn kommer her.</p>
      </div>
    </aside>

    <main class="game-layout__content" aria-label="Spillinnhold">
      <div class="game-layout__content-shell">
        <slot />
      </div>
    </main>

    <aside class="game-layout__rail game-layout__rail--right" aria-label="Ekstrainformasjon">
      <div class="game-layout__rail-card">
        <p class="game-layout__rail-title">På vei</p>
        <p class="game-layout__rail-text">Her kan vi senere vise flere spillvalg eller annen info.</p>
      </div>
    </aside>
  </div>
</template>

<style scoped>
.game-layout {
  min-height: 100vh;
  display: grid;
  grid-template-columns: clamp(7rem, 16vw, 13rem) minmax(0, 1fr) clamp(6rem, 14vw, 12rem);
  background:
    linear-gradient(90deg, #cfe9ff 0, #cfe9ff 18%, #f8fbff 18%, #f8fbff 82%, #cfe9ff 82%, #cfe9ff 100%);
}

.game-layout__rail,
.game-layout__content {
  min-height: 100vh;
}

.game-layout__rail {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1.5rem 1rem;
}

.game-layout__content {
  padding: 1.5rem 0;
}

.game-layout__content-shell {
  min-height: calc(100vh - 3rem);
  background: #ffffff;
  border-radius: 1.75rem;
  box-shadow: 0 16px 40px rgba(29, 56, 96, 0.18);
  padding: 2rem;
  overflow: hidden;
}

.game-layout__rail-card {
  width: min(100%, 10rem);
  padding: 1rem 0.9rem;
  border-radius: 1.25rem;
  border: 1px solid rgba(54, 99, 155, 0.18);
  background: rgba(255, 255, 255, 0.75);
  text-align: center;
  color: #183153;
  box-shadow: 0 10px 24px rgba(34, 74, 126, 0.12);
}

.game-layout__rail-card--avatar {
  display: grid;
  gap: 0.75rem;
}

.game-layout__avatar-placeholder {
  width: 4.5rem;
  height: 4.5rem;
  margin: 0 auto;
  border: 3px solid #183153;
  border-radius: 1rem;
  display: grid;
  place-items: center;
  background: #fff;
}

.game-layout__avatar-face {
  font-size: 1.35rem;
  font-weight: 700;
}

.game-layout__rail-title {
  font-size: 1rem;
  font-weight: 700;
}

.game-layout__rail-text {
  font-size: 0.95rem;
  line-height: 1.4;
}

@media (max-width: 1100px) {
  .game-layout {
    grid-template-columns: clamp(5.5rem, 14vw, 9rem) minmax(0, 1fr) clamp(5rem, 13vw, 8rem);
  }

  .game-layout__content-shell {
    padding: 1.5rem;
  }

  .game-layout__rail-card {
    width: 100%;
    padding: 0.85rem 0.75rem;
  }

  .game-layout__rail-text {
    font-size: 0.85rem;
  }
}

@media (max-width: 780px) {
  .game-layout {
    grid-template-columns: 1fr;
    grid-template-rows: auto 1fr auto;
    background: #cfe9ff;
  }

  .game-layout__rail,
  .game-layout__content {
    min-height: auto;
  }

  .game-layout__rail {
    padding: 1rem;
  }

  .game-layout__content {
    padding: 0 1rem;
  }

  .game-layout__content-shell {
    min-height: auto;
    border-radius: 1.5rem;
  }

  .game-layout__rail-card {
    width: min(100%, 24rem);
  }
}
</style>
