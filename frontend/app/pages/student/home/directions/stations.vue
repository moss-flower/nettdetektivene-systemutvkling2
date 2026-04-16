<script setup lang="ts">
import StationCard from "~/components/station/station.vue";
import { routePaths } from "~/composables/routePaths";
import { StationFactory } from "~/composables/types/StationType";

//Props, temporary hardcoded content
const mockData = StationFactory.mockData();
const snakeColumns = 3;
const router = useRouter();

const stationStops = computed(() =>
  mockData.map((station, index) => {
    const row = Math.floor(index / snakeColumns);
    const positionInRow = index % snakeColumns;
    const visualColumn = row % 2 === 0
      ? positionInRow + 1
      : snakeColumns - positionInRow;
    const waveOffsets = ["-1.6rem", "1.1rem", "-1.1rem"];
    const stopOffset = row % 2 === 0
      ? waveOffsets[visualColumn - 1] ?? "0rem"
      : waveOffsets[snakeColumns - visualColumn] ?? "0rem";
    const nextIndex = index + 1;
    let connectorClass = "stationStop--end";

    if (nextIndex < mockData.length) {
      const nextRow = Math.floor(nextIndex / snakeColumns);
      if (nextRow !== row) {
        connectorClass = "stationStop--down";
      }
      else {
        const nextPositionInRow = nextIndex % snakeColumns;
        const nextVisualColumn = row % 2 === 0
          ? nextPositionInRow + 1
          : snakeColumns - nextPositionInRow;

        connectorClass = nextVisualColumn > visualColumn
          ? "stationStop--right"
          : "stationStop--left";
      }
    }

    return {
      station,
      label: index + 1,
      connectorClass,
      style: {
        gridColumn: String(visualColumn),
        gridRow: String(row + 1),
        "--stop-offset": stopOffset,
        "--trail-delay": `${index * 120}ms`,
      },
    };
  }),
);

function goBack() {
  router.push(routePaths.studentHome);
}

</script>

<template>
  <section class="stationsPage">
    <button class="goBack" type="button" @click="goBack">
      ← Tilbake
    </button>

    <header class="pageIntro">
      <p class="pageEyebrow">Stasjonskart</p>
      <h1 class="pageTitle">Følg stien fra venstre til høyre</h1>
      <p class="pageSummary">
        Hver stasjon ligger langs en tydelig spillsti. Start på den første åpne stasjonen og jobb deg videre bortover banen.
      </p>
    </header>

    <div class="pathBoard">
      <ol class="stationPath">
        <li
          v-for="stop in stationStops"
          :key="stop.station.id"
          class="stationStop"
          :class="stop.connectorClass"
          :style="stop.style"
        >
          <span class="pathNode" aria-hidden="true">{{ stop.label }}</span>
          <StationCard :station="stop.station" />
        </li>
      </ol>
    </div>
  </section>
</template>

<style scoped>
.stationsPage {
  display: grid;
  gap: 1.25rem;
  padding: clamp(1rem, 2vw, 2rem);
}

.pageIntro {
  display: grid;
  gap: 0.75rem;
  max-width: 58rem;
  padding: clamp(1.25rem, 2.5vw, 2rem);
  border: 1px solid color-mix(in srgb, var(--border) 68%, transparent);
  border-radius: 1.75rem;
  background:
    radial-gradient(circle at top left, rgb(255 255 255 / 0.85), transparent 32%),
    radial-gradient(circle at 85% 10%, rgb(255 240 184 / 0.7), transparent 22%),
    linear-gradient(135deg, #dff7ff 0%, #cfe4ff 42%, #ffe4f6 100%);
  box-shadow: var(--shadow);
}

.pageEyebrow {
  margin: 0;
  font-size: 0.85rem;
  font-weight: 800;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: #3a7a99;
}

.pageTitle {
  margin: 0;
  font-size: clamp(2rem, 4vw, 3.4rem);
  line-height: 1;
  color: #234567;
}

.pageSummary {
  margin: 0;
  max-width: 58ch;
  line-height: 1.55;
  color: #4e607c;
}

.goBack {
  width: fit-content;
  padding: 0.75rem 1rem;
  border: 1px solid var(--border);
  border-radius: 999px;
  background: white;
  color: var(--text);
  font-weight: 700;
  cursor: pointer;
  box-shadow: var(--shadow);
}

.goBack:hover {
  transform: translateY(-2px);
}

.goBack:focus-visible {
  outline: 3px solid color-mix(in srgb, var(--primary) 65%, white);
  outline-offset: 0.2rem;
}

.pathBoard {
  position: relative;
  overflow: hidden;
  padding: 1.5rem;
  border: 1px solid color-mix(in srgb, var(--border) 64%, transparent);
  border-radius: 2rem;
  background:
    radial-gradient(circle at 12% 14%, rgb(255 255 255 / 0.85), transparent 15%),
    radial-gradient(circle at 85% 18%, rgb(255 223 173 / 0.55), transparent 14%),
    radial-gradient(circle at 65% 72%, rgb(224 255 215 / 0.6), transparent 16%),
    linear-gradient(180deg, #f5fdff 0%, #eaf5ff 48%, #fef3ff 100%);
  box-shadow: var(--shadow);
}

.pathBoard::before,
.pathBoard::after {
  content: "";
  position: absolute;
  border-radius: 999px;
  background: rgb(255 255 255 / 0.55);
  pointer-events: none;
}

.pathBoard::before {
  top: 1rem;
  right: 2rem;
  width: 5rem;
  height: 1.2rem;
}

.pathBoard::after {
  bottom: 1.25rem;
  left: 2.5rem;
  width: 3.5rem;
  height: 0.95rem;
}

.stationPath {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  grid-auto-rows: minmax(15rem, auto);
  gap: 2.75rem 1rem;
  list-style: none;
  margin: 0;
  padding: 0;
}

.stationStop {
  position: relative;
  display: grid;
  place-items: center;
  min-height: 15rem;
  transform: translateY(var(--stop-offset, 0rem));
}

.stationStop::before {
  content: "";
  position: absolute;
  left: 50%;
  top: 50%;
  width: 1rem;
  height: 1rem;
  transform: translate(-50%, -50%);
  border-radius: 999px;
  background: white;
  border: 0.28rem solid #f8aa2c;
  box-shadow: 0 0 0 0.2rem rgb(255 240 196 / 0.9);
  z-index: 1;
}

.stationStop::after {
  content: "";
  position: absolute;
  z-index: 0;
  border-radius: 999px;
  background:
    repeating-linear-gradient(
      90deg,
      #9be56a 0,
      #9be56a 1rem,
      #70cf4b 1rem,
      #70cf4b 2rem,
      #b3f482 2rem,
      #b3f482 3rem
    );
  box-shadow:
    0 0.2rem 0 #57a835,
    0 0 0 0.18rem rgb(239 255 219 / 0.9);
  animation: snakeTrail 3.2s linear infinite;
  animation-delay: var(--trail-delay, 0ms);
}

.stationStop--right::after {
  top: 50%;
  left: 50%;
  width: calc(100% + 1rem);
  height: 0.9rem;
  transform: translateY(-50%);
}

.stationStop--left::after {
  top: 50%;
  right: 50%;
  width: calc(100% + 1rem);
  height: 0.9rem;
  transform: translateY(-50%);
}

.stationStop--down::after {
  left: 50%;
  top: 50%;
  width: 0.9rem;
  height: calc(100% + 2.25rem);
  transform: translateX(-50%);
  background:
    repeating-linear-gradient(
      180deg,
      #9be56a 0,
      #9be56a 1rem,
      #70cf4b 1rem,
      #70cf4b 2rem,
      #b3f482 2rem,
      #b3f482 3rem
    );
}

.pathNode {
  position: absolute;
  left: 50%;
  top: 50%;
  display: grid;
  place-items: center;
  width: 2rem;
  height: 2rem;
  transform: translate(-50%, -50%);
  border-radius: 999px;
  background: linear-gradient(180deg, #ff8ac0 0%, #ff5c99 100%);
  color: white;
  font-size: 0.9rem;
  font-weight: 800;
  box-shadow: 0 0.2rem 0 #cf3f74;
  z-index: 2;
}

.stationStop :deep(.levelCard) {
  position: relative;
  width: min(100%, 14rem);
  min-height: clamp(10.5rem, 18vw, 13rem);
  z-index: 3;
  border-radius: 1.5rem;
  box-shadow: 0 1rem 1.6rem rgb(71 91 132 / 0.16);
}

@keyframes snakeTrail {
  from {
    background-position: 0 0;
  }

  to {
    background-position: 3rem 0;
  }
}

@media (max-width: 900px) {
  .pageTitle {
    font-size: clamp(1.75rem, 7vw, 2.5rem);
  }

  .pathBoard {
    padding: 1rem;
  }

  .stationPath {
    gap: 1.75rem 0.75rem;
  }

  .stationStop {
    min-height: 13rem;
  }

  .stationStop :deep(.levelCard) {
    width: min(100%, 12rem);
    min-height: 10rem;
  }
}

@media (max-width: 700px) {
  .pathBoard {
    padding: 1rem 0.85rem;
  }

  .stationPath {
    grid-template-columns: 1fr;
    grid-auto-rows: auto;
    gap: 1rem;
  }

  .stationStop {
    min-height: auto;
    transform: none;
  }

  .stationStop::after,
  .stationStop::before,
  .pathNode {
    display: none;
  }

  .stationStop :deep(.levelCard) {
    width: 100%;
    min-height: 11rem;
  }
}
</style>