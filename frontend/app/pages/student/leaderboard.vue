<script setup lang="ts">
import UiStudentHomeBackButton from "~/components/ui/UiStudentHomeBackButton.vue";
</script>
<template>
  <section class="leaderboardPage">
    <header class="pageHeader">
      <UiStudentHomeBackButton class="backLink" />
      <p class="eyebrow">Detektivbyrået</p>
      <h1>leaderboard</h1>
      <p class="subtitle">Hvilken klasse leder etterforskningen?</p>
    </header>

    <!-- Top 3, hung plaques, center raised  -->
    <ol class="podium" aria-label="Topp 3 klasser">
      <li class="podiumStep podiumStep--silver" aria-label="Andreplass">
        <span class="medallion">II</span>
        <p class="className">5A</p>
        <p class="classScore">2 140</p>
        <p class="scoreUnit">poeng</p>
      </li>
      <li class="podiumStep podiumStep--gold" aria-label="Førsteplass">
        <span class="medallion">I</span>
        <p class="className">7B</p>
        <p class="classScore">2 860</p>
        <p class="scoreUnit">poeng</p>
      </li>
      <li class="podiumStep podiumStep--bronze" aria-label="Tredjeplass">
        <span class="medallion">III</span>
        <p class="className">6C</p>
        <p class="classScore">1 980</p>
        <p class="scoreUnit">poeng</p>
      </li>
    </ol>

    <!-- Rank 4+, single cream placard with file-cabinet rows -->
    <section class="roster" aria-label="Øvrig rangering">
      <header class="rosterHeader">
        <span>Rang</span>
        <span>Klasse</span>
        <span>Poeng</span>
      </header>
      <ol class="rosterList" start="4">
        <li class="rosterRow">
          <span class="rosterRank">4</span>
          <span class="rosterName">7A</span>
          <span class="rosterScore">1 725</span>
        </li>
        <li class="rosterRow">
          <span class="rosterRank">5</span>
          <span class="rosterName">4B</span>
          <span class="rosterScore">1 605</span>
        </li>
        <li class="rosterRow">
          <span class="rosterRank">6</span>
          <span class="rosterName">3A</span>
          <span class="rosterScore">1 430</span>
        </li>
      </ol>
    </section>
  </section>
</template>

<style scoped>
.leaderboardPage {
  /* Bureau honor board mounted on the wood wall */
  --leaderboard-wall: var(--palette-wood-900);
  --leaderboard-wall-seam: var(--palette-overlay-warm-glow);
  --leaderboard-wall-glow: var(--palette-overlay-warm-glow);

  /* wall-level text */
  --leaderboard-eyebrow: var(--palette-gold-500);
  --leaderboard-title: var(--palette-cream-100);
  --leaderboard-title-depth: var(--palette-overlay-dark-strong);
  --leaderboard-subtitle: var(--palette-cream-300);

  /* plaque anatomy */
  --plaque-gilt: var(--palette-gold-600);
  --plaque-highlight: var(--palette-gold-400);
  --plaque-liner: var(--palette-cork-900);
  --plaque-shadow: var(--palette-overlay-dark-deep);
  --plaque-mat-top: var(--palette-cream-100);
  --plaque-mat-bottom: var(--palette-cream-200);

  /* Tier accents, palette direct */
  --tier-gold-ring: var(--palette-gold-500);
  --tier-gold-ink: var(--palette-gold-700);
  --tier-silver-ring: var(--palette-gray-300);
  --tier-silver-ink: var(--palette-navy-700);
  --tier-bronze-ring: var(--palette-cork-580);
  --tier-bronze-ink: var(--palette-cork-700);

  /* Roster */
  --roster-header-ink: var(--palette-cork-500);
  --roster-row-ink: var(--palette-cork-900);
  --roster-rank-ink: var(--palette-cork-500);
  --roster-divider: var(--palette-overlay-cork-shadow);
  --roster-row-hover: var(--palette-overlay-cork-soft);

  position: relative;
  min-height: 100dvh;
  width: 100%;
  display: grid;
  gap: var(--space-6);
  justify-items: center;
  align-content: start;
  padding: var(--layout-page-padding);
  color: var(--leaderboard-title);
  background:
      radial-gradient(ellipse 70% 50% at 50% 15%, var(--leaderboard-wall-glow), transparent 70%),
      repeating-linear-gradient(
        90deg,
        transparent 0,
        transparent 200px,
        var(--leaderboard-wall-seam) 200px,
        var(--leaderboard-wall-seam) 201px
      ),
      var(--leaderboard-wall);
  overflow-x: hidden;
}

.pageHeader {
  width: 100%;
  max-width: 880px;
  display: grid;
  gap: var(--space-2);
  justify-items: start;
}

.backLink {
  margin-bottom: var(--space-3);
  /* default UiStudentHomeBackButton variant (white pill), reads on wood.
     DO NOT pass variant="onLight"; variant is for light surfaces like notepad */
}

.eyebrow {
  margin: 0;
  color: var(--leaderboard-eyebrow);
  font-family: var(--font-family-body), sans-serif;
  font-size: var(--font-size-14);
  line-height: var(--font-leading-14);
  font-weight: var(--font-weight-bold);
  letter-spacing: var(--font-tracking-wider);
  text-transform: uppercase;
}

h1 {
  margin: 0;
  font-family: var(--font-family-serif), sans-serif;
  /* fluid hero scale, same curve as settings */
  font-size: clamp(2rem, 4vw, 3rem);
  line-height: 1.1;
  font-weight: var(--font-weight-regular);
  color: var(--leaderboard-title);
  letter-spacing: var(--font-tracking-normal);
  text-shadow: 0 1px 2px var(--leaderboard-title-depth);
}

.subtitle {
  margin: 0;
  max-width: 60ch;
  color: var(--leaderboard-subtitle);
  font-family: var(--font-family-body), sans-serif;
  line-height: var(--font-leading-base);
}

/* --- Podium: three hung brass plaques --- */
.podium {
  list-style: none;
  margin: 0;
  padding: 0;
  width: 100%;
  max-width: 880px;
  display: grid;
  grid-template-columns: 1fr 1.2fr 1fr;
  align-items: end;
  gap: var(--space-4);
}

.podiumStep {
  position: relative;
  display: grid;
  gap: var(--space-3);
  justify-items: center;
  padding: var(--space-6) var(--space-4) var(--space-5);
  background: linear-gradient(
    180deg,
    var(--plaque-mat-top) 0%,
    var(--plaque-mat-bottom) 100%
  );
  border: var(--border-width-chunky) solid var(--plaque-gilt);
  border-radius: var(--radius-2);
  box-shadow:
    inset 0 0 0 var(--border-width-thin) var(--plaque-liner),
    inset 0 var(--border-width-thin) 0 var(--plaque-highlight),
    var(--space-2) var(--space-4) var(--space-6) var(--plaque-shadow);
  color: var(--palette-cork-900);
  transition: transform var(--transition-fast);
}

.podiumStep:hover {
  transform: translateY(-2px);
}

/* gilt nail hanging each plaque */
.podiumStep::before {
  content: "";
  position: absolute;
  top: calc(-1 * var(--space-2));
  left: 50%;
  width: var(--space-2);
  height: var(--space-2);
  border-radius: var(--radius-pill);
  background: var(--plaque-highlight);
  box-shadow: 0 1px 2px var(--palette-overlay-dark-deep);
  transform: translateX(-50%);
}

/* roman-numeral medallion, cream disc inside tier colored brass ring */
.medallion {
  display: grid;
  place-items: center;
  width: var(--size-control-lg);
  height: var(--size-control-lg);
  border-radius: var(--radius-pill);
  background: radial-gradient(
    circle at 35% 30%,
    var(--palette-cream-100) 0,
    var(--palette-cream-200) 70%
  );
  font-family: var(--font-family-serif), sans-serif;
  font-size: var(--font-size-28);
  line-height: 1;
  font-weight: var(--font-weight-bold);
  letter-spacing: var(--font-tracking-wide);
}

.className {
  margin: 0;
  font-family: var(--font-family-serif), sans-serif;
  font-size: var(--font-size-36);
  line-height: var(--font-leading-36);
  font-weight: var(--font-weight-semibold);
  color: var(--palette-cork-900);
}

.classScore {
  margin: 0;
  font-family: var(--font-family-serif), sans-serif;
  font-size: var(--font-size-22);
  line-height: var(--font-leading-22);
  font-weight: var(--font-weight-semibold);
  font-variant-numeric: tabular-nums;
  color: var(--palette-cork-900);
}

.scoreUnit {
  margin: 0;
  color: var(--palette-cork-500);
  font-family: var(--font-family-body), sans-serif;
  font-size: var(--font-size-14);
  line-height: var(--font-leading-14);
  font-weight: var(--font-weight-bold);
  letter-spacing: var(--font-tracking-wider);
  text-transform: uppercase;
}

/* --- Tier differentiation: ring color on medallion + border on plaque --- */
.podiumStep--gold {
  transform: translateY(calc(-1 * var(--space-4)));
  border-color: var(--tier-gold-ring);
  box-shadow:
    inset 0 0 0 var(--border-width-thin) var(--plaque-liner),
    inset 0 var(--border-width-thin) 0 var(--plaque-highlight),
    0 0 var(--space-4) var(--palette-overlay-warm-glow),
    var(--space-2) var(--space-4) var(--space-7) var(--plaque-shadow);
}

.podiumStep--gold:hover {
  transform: translateY(calc(-1 * var(--space-4) - 2px));
}

.podiumStep--gold .medallion {
  box-shadow:
    0 0 0 var(--border-width-thick) var(--tier-gold-ring),
    0 var(--space-1) var(--space-2) var(--palette-overlay-dark-medium);
  color: var(--tier-gold-ink);
}

.podiumStep--silver {
  border-color: var(--tier-silver-ring);
}

.podiumStep--silver .medallion {
  box-shadow:
    0 0 0 var(--border-width-thick) var(--tier-silver-ring),
    0 var(--space-1) var(--space-2) var(--palette-overlay-dark-medium);
  color: var(--tier-silver-ink);
}

.podiumStep--bronze {
  border-color: var(--tier-bronze-ring);
}

.podiumStep--bronze .medallion {
  box-shadow:
    0 0 0 var(--border-width-thick) var(--tier-bronze-ring),
    0 var(--space-1) var(--space-2) var(--palette-overlay-dark-medium);
  color: var(--tier-bronze-ink);
}

/* --- Roster: cream placard, ranked rows 4+ --- */
.roster {
  width: 100%;
  max-width: 880px;
  display: grid;
  padding: var(--space-5) var(--space-6);
  background: linear-gradient(
    180deg,
    var(--plaque-mat-top) 0%,
    var(--plaque-mat-bottom) 100%
  );
  border: var(--border-width-chunky) solid var(--plaque-gilt);
  border-radius: var(--radius-2);
  box-shadow:
    inset 0 0 0 var(--border-width-thin) var(--plaque-liner),
    inset 0 var(--border-width-thin) 0 var(--plaque-highlight),
    var(--space-2) var(--space-4) var(--space-6) var(--plaque-shadow);
}

.rosterHeader {
  display: grid;
  grid-template-columns: 4rem 1fr auto;
  gap: var(--space-4);
  padding-bottom: var(--space-3);
  border-bottom: var(--border-width-hairline) solid var(--roster-divider);
  color: var(--roster-header-ink);
  font-family: var(--font-family-body), sans-serif;
  font-size: var(--font-size-14);
  line-height: var(--font-leading-14);
  font-weight: var(--font-weight-bold);
  letter-spacing: var(--font-tracking-wider);
  text-transform: uppercase;
}

.rosterList {
  list-style: none;
  margin: 0;
  padding: 0;
}

.rosterRow {
  display: grid;
  grid-template-columns: 4rem 1fr auto;
  gap: var(--space-4);
  align-items: center;
  padding: var(--space-3) var(--space-2);
  margin: 0 calc(-1 * var(--space-2));
  border-bottom: var(--border-width-hairline) solid var(--roster-divider);
  color: var(--roster-row-ink);
  border-radius: var(--radius-1);
  transition: background var(--transition-fast);
}

.rosterRow:last-child {
  border-bottom: none;
}

.rosterRow:hover {
  background: var(--roster-row-hover);
}

.rosterRank {
  font-family: var(--font-family-serif), sans-serif;
  font-size: var(--font-size-22);
  line-height: var(--font-leading-22);
  font-weight: var(--font-weight-semibold);
  font-variant-numeric: tabular-nums;
  color: var(--roster-rank-ink);
}

.rosterName {
  font-family: var(--font-family-serif), sans-serif;
  font-size: var(--font-size-22);
  line-height: var(--font-leading-22);
  font-weight: var(--font-weight-semibold);
}

.rosterScore {
  font-family: var(--font-family-serif), sans-serif;
  font-size: var(--font-size-22);
  line-height: var(--font-leading-22);
  font-weight: var(--font-weight-semibold);
  font-variant-numeric: tabular-nums;
  text-align: right;
}

/* narrow: collapse podium to a stack, winner first */
@media (max-width: 720px) {
  .podium {
    grid-template-columns: 1fr;
    align-items: stretch;
    gap: var(--space-3);
  }

  .podiumStep--gold {
    transform: none;
    order: -1;
  }

  .podiumStep--gold:hover {
    transform: translateY(-2px);
  }

  .rosterHeader,
  .rosterRow {
    grid-template-columns: 3rem 1fr auto;
  }
}
</style>
