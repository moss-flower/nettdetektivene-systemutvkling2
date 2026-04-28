<script setup lang="ts">
import UiStudentHomeBackButton from "~/components/ui/UiStudentHomeBackButton.vue";
import type { ClassroomLeaderboardEntry } from "~/composables/types/LeaderboardTypes";

const leaderboard = useLeaderboard();

const pageSize = 25;
const rankings = ref<ClassroomLeaderboardEntry[]>([]);
const pageNumber = ref(0);
const isLastPage = ref(true);
const isLoading = ref(false);
const loadError = ref<string | null>(null);

const orderedRankings = computed(() =>
  [...rankings.value].sort(
    (a, b) => a.rank - b.rank || b.score - a.score || a.className.localeCompare(b.className),
  ),
);
const podiumRankings = computed(() => orderedRankings.value.slice(0, 3));
const podiumDisplayRankings = computed(() => {
  const [first, second, third] = podiumRankings.value;

  return [second, first, third].filter((entry): entry is ClassroomLeaderboardEntry => Boolean(entry));
});
const rosterRankings = computed(() => orderedRankings.value.slice(3));
const hasRankings = computed(() => orderedRankings.value.length > 0);

const formatScore = (score: number) => new Intl.NumberFormat("nb-NO").format(score);

const romanRank = (rank: number) => {
  if (rank === 1) return "I";
  if (rank === 2) return "II";
  if (rank === 3) return "III";
  return String(rank);
};

const podiumClass = (rank: number) => {
  if (rank === 1) return "podiumStep--gold";
  if (rank === 2) return "podiumStep--silver";
  if (rank === 3) return "podiumStep--bronze";
  return "";
};

const placeLabel = (rank: number) => {
  if (rank === 1) return "Førsteplass";
  if (rank === 2) return "Andreplass";
  if (rank === 3) return "Tredjeplass";
  return `${rank}. plass`;
};

const loadRankingsPage = async (page: number) => {
  isLoading.value = true;
  loadError.value = null;

  try {
    const response = await leaderboard.getClassroomRankings(page, pageSize);
    rankings.value = page === 0 ? response.content : [...rankings.value, ...response.content];
    pageNumber.value = response.number;
    isLastPage.value = response.last;
  } catch {
    loadError.value = "Kunne ikke hente leaderboard akkurat nå.";
  } finally {
    isLoading.value = false;
  }
};

const loadMoreRankings = async () => {
  if (isLoading.value || isLastPage.value) return;

  await loadRankingsPage(pageNumber.value + 1);
};

await loadRankingsPage(0);
</script>
<template>
  <section class="leaderboardPage">
    <header class="pageHeader">
      <UiStudentHomeBackButton class="backLink" />
      <p class="eyebrow">Detektivbyrået</p>
      <h1>leaderboard</h1>
      <p class="subtitle">Hvilken klasse leder etterforskningen?</p>
    </header>

    <p v-if="loadError && !hasRankings" class="statusMessage">{{ loadError }}</p>
    <p v-else-if="isLoading && !hasRankings" class="statusMessage">Henter klassepoeng...</p>
    <p v-else-if="!hasRankings" class="statusMessage">Ingen klasser har poeng ennå.</p>

    <!-- Top 3, hung plaques, center raised  -->
    <ol v-else class="podium" aria-label="Topp 3 klasser">
      <li
        v-for="entry in podiumDisplayRankings"
        :key="entry.classroomPublicUuid"
        class="podiumStep"
        :class="podiumClass(entry.rank)"
        :aria-label="placeLabel(entry.rank)"
      >
        <span class="medallion">{{ romanRank(entry.rank) }}</span>
        <p class="className">{{ entry.className }}</p>
        <p class="classScore">{{ formatScore(entry.score) }}</p>
        <p class="scoreUnit">XP</p>
      </li>
    </ol>

    <!-- Rank 4+, single cream placard with file-cabinet rows -->
    <section v-if="rosterRankings.length" class="roster" aria-label="Øvrig rangering">
      <header class="rosterHeader">
        <span>Rang</span>
        <span>Klasse</span>
        <span>XP</span>
      </header>
      <ol class="rosterList" start="4">
        <li
          v-for="entry in rosterRankings"
          :key="entry.classroomPublicUuid"
          class="rosterRow"
        >
          <span class="rosterRank">{{ entry.rank }}</span>
          <span class="rosterName">{{ entry.className }}</span>
          <span class="rosterScore">{{ formatScore(entry.score) }}</span>
        </li>
      </ol>
      <p v-if="loadError" class="rosterStatus">{{ loadError }}</p>
      <button
        v-if="!isLastPage"
        class="loadMoreButton"
        type="button"
        :disabled="isLoading"
        @click="loadMoreRankings"
      >
        {{ isLoading ? "Henter flere..." : "Vis flere klasser" }}
      </button>
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
  --roster-header-ink: var(--palette-cork-580);
  --roster-row-ink: var(--palette-cork-900);
  --roster-rank-ink: var(--palette-cork-580);
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
  border-radius: var(--radius-2);
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

.statusMessage {
  margin: 0;
  width: 100%;
  max-width: 880px;
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
  color: var(--palette-cork-900);
  font-family: var(--font-family-body), sans-serif;
  font-weight: var(--font-weight-bold);
  text-align: center;
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
  color: var(--palette-cork-580);
  font-family: var(--font-family-body), sans-serif;
  font-size: var(--font-size-14);
  line-height: var(--font-leading-14);
  font-weight: var(--font-weight-bold);
  letter-spacing: var(--font-tracking-wider);
  text-transform: uppercase;
}

/* --- Tier differentiation: ring color on medallion + border on plaque --- */
.podiumStep--gold {
  order: 2;
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
  order: 1;
  border-color: var(--tier-silver-ring);
}

.podiumStep--silver .medallion {
  box-shadow:
    0 0 0 var(--border-width-thick) var(--tier-silver-ring),
    0 var(--space-1) var(--space-2) var(--palette-overlay-dark-medium);
  color: var(--tier-silver-ink);
}

.podiumStep--bronze {
  order: 3;
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

.rosterStatus {
  margin: var(--space-3) 0 0;
  color: var(--roster-row-ink);
  font-family: var(--font-family-body), sans-serif;
  font-weight: var(--font-weight-bold);
  text-align: center;
}

.loadMoreButton {
  justify-self: center;
  margin-top: var(--space-4);
  padding: var(--space-2) var(--space-5);
  border: var(--border-width-thick) solid var(--plaque-gilt);
  border-radius: var(--radius-pill);
  background: var(--palette-cream-100);
  color: var(--palette-cork-900);
  font-family: var(--font-family-body), sans-serif;
  font-weight: var(--font-weight-bold);
  cursor: pointer;
  transition:
    background var(--transition-fast),
    transform var(--transition-fast);
}

.loadMoreButton:hover:not(:disabled) {
  background: var(--palette-gold-400);
  transform: translateY(-1px);
}

.loadMoreButton:disabled {
  cursor: wait;
  opacity: 0.7;
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
