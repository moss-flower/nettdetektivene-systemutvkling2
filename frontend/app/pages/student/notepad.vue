<script setup lang="ts">
import leftArrow from "~/assets/images/leftArrow.png";
import NewNote from "~/components/notepad/NewNote.vue";
import NotepadFilters from "~/components/notepad/NotepadFilters.vue";
import NotepadFrame from "~/components/notepad/NotepadFrame.vue";
import { routePaths } from "~/composables/utils/routePaths";
import type { NotepadEntry } from "~/composables/types/NotepadTypes.ts";

//Holds selected filter as string
const selectedFilter = ref<string | null>(null);

//Temporary mockdata
const mockEntries: NotepadEntry[] = [
  {
    title: "Se etter rare lenker",
    date: "2026-04-17 09:15",
    source: "Postkontoret",
    type: "tip",
    content:
      "Sjekk alltid lenken før du klikker. Rare bokstaver, tall eller feilstavede nettadresser kan være tegn på svindel.",
  },
  {
    title: "Ikke del alt med en gang",
    date: "2026-04-17 10:40",
    source: "Den sosiale møteplassen",
    type: "info",
    content:
      "Innlegg som gjør deg veldig sint eller redd, prøver ofte å få deg til å dele raskt. Stopp opp og sjekk kilden først.",
  },
  {
    title: "Jeg la merke til et svakt passord",
    date: "2026-04-17 11:20",
    source: "Passordbanken",
    type: "observation",
    content:
      "Passord som bare inneholder navn eller fødselsår er lette å gjette. Det er tryggere å bruke flere ulike tegn.",
  },
  {
    title: "Bilder kan være manipulerte",
    date: "2026-04-17 12:05",
    source: "Fotografen",
    type: "tip",
    content:
      "Se etter detaljer som hender, bakgrunner eller skygger som ser unaturlige ut. Det kan være tegn på KI eller manipulering.",
  },
  {
    title: "Vurder nettbutikken nøye",
    date: "2026-04-17 13:30",
    source: "Markedsplassen",
    type: "info",
    content:
      "Hvis en butikk har ekstremt lave priser, dårlige anmeldelser eller ukjente betalingsmåter, bør du undersøke mer før du handler.",
  },
  {
    title: "Vurder nettbutikken nøye",
    date: "2026-04-17 13:30",
    source: "Markedsplassen",
    type: "info",
    content:
      "Hvis en butikk har ekstremt lave priser, dårlige anmeldelser eller ukjente betalingsmåter, bør du undersøke mer før du handler.",
  },
  {
    title: "Vurder nettbutikken nøye",
    date: "2026-04-17 13:30",
    source: "Markedsplassen",
    type: "info",
    content:
      "Hvis en butikk har ekstremt lave priser, dårlige anmeldelser eller ukjente betalingsmåter, bør du undersøke mer før du handler.",
  },
  {
    title: "Vurder nettbutikken nøye",
    date: "2026-04-17 13:30",
    source: "Markedsplassen",
    type: "info",
    content:
      "Hvis en butikk har ekstremt lave priser, dårlige anmeldelser eller ukjente betalingsmåter, bør du undersøke mer før du handler.",
  },
];

//Dynamically updates entries based on current selectedFilter value
const filteredEntries = computed(() => {
  if (!selectedFilter.value) {
    return mockEntries;
  }

  return mockEntries.filter((entry) => entry.source === selectedFilter.value);
});

//Called on filter button click, updates selectedFilter value
function handleFilterSelect(level: string | null) {
  selectedFilter.value = level;
}

function handleNewNoteClick() {
  // TODO:
  // Open "new note" input bubble / modal when that feature is implemented.
}
</script>

<template>
  <section class="notepadPage">
    <div class="leftColumn">
      <div></div>
      <p>PLACEHOLDER FOR PROFILE</p>
      <NuxtLink :to="routePaths.studentHome" class="arrowButton">
        <img :src="leftArrow" alt="Back" />
      </NuxtLink>
    </div>

    <div class="notepadWrapper">
      <NotepadFrame :notes="filteredEntries" />
    </div>

    <div class="rightColumn">
      <NewNote @click="handleNewNoteClick" />

      <NotepadFilters
        :active-filter="selectedFilter"
        @select="handleFilterSelect"
      />
    </div>
  </section>
</template>

<style scoped>
.notepadPage {
  /* desk surface */
  --notepad-desk: var(--palette-wood-900);
  --notepad-desk-glow: var(--palette-overlay-warm-glow);
  --notepad-desk-seam: var(--palette-overlay-dark-strong);
  --notepad-desk-bevel: var(--palette-overlay-warm-glow);

  /* shared text on desk*/
  --notepad-text-onDesk: var(--palette-cream-100);
  --notepad-text-onDesk-muted: var(--palette-cream-300);

  /* arrow pager (physical desk button) */
  --notepad-arrow-bg: var(--palette-cream-100);
  --notepad-arrow-border: var(--palette-cork-900);
  --notepad-arrow-shadow: var(--palette-overlay-dark-deep);
  --notepad-arrow-hover-ring: var(--palette-gold-500);

  display: grid;
  grid-template-columns: 0.2fr 1fr 0.2fr;
  gap: var(--space-4);
  height: 100dvh;
  min-height: 0;
  padding: var(--layout-page-padding);
  box-sizing: border-box;
  color: var(--notepad-text-onDesk);
  background:
      linear-gradient(
        135deg,
        var(--notepad-desk-glow) 0%,
        transparent 40%,
        transparent 60%,
        var(--palette-overlay-dark-strong) 100%
      ),
      repeating-linear-gradient(
        0deg,
        transparent 0,
        transparent 117px,
        var(--notepad-desk-seam) 117px,
        var(--notepad-desk-seam) 120px,
        var(--notepad-desk-bevel) 120px,
        var(--notepad-desk-bevel) 122px
      ),
      var(--notepad-desk);
  overflow: hidden;
}

.notepadWrapper {
  min-width: 0;
  min-height: 0;
  height: 100%;
}

/* side rails, transparent; widgets inside carry their own surfaces */
.leftColumn,
.rightColumn {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
  height: 100%;
  min-height: 0;
}

.leftColumn {
  /* three children: top spacer + profile placeholder + back to menu arrow
     space between = arrow pushed to bottom of rail + lets placeholder sit in the middle */
  justify-content: space-between;
}

.rightColumn {
  justify-content: space-around;
}

.arrowButton {
  width: 70%;
  margin: 0 auto 15%;
  padding: var(--space-1);
  border: var(--border-width-hairline) solid var(--notepad-arrow-border);
  border-radius: var(--radius-pill);
  background: var(--notepad-arrow-bg);
  box-shadow: 0 var(--space-1) var(--space-3) var(--notepad-arrow-shadow);
  cursor: pointer;
  transition:
    box-shadow var(--transition-fast),
    transform var(--transition-fast);
}

.arrowButton:hover {
  transform: translateY(-1px);
  box-shadow:
    0 0 0 var(--border-width-thin) var(--notepad-arrow-hover-ring),
    0 var(--space-1) var(--space-4) var(--notepad-arrow-shadow);
}

.arrowButton img {
  display: block;
  max-width: 100%;
}
</style>
