<script setup lang="ts">
import PopupNotYetUnlocked from "~/components/shared/PopupNotYetUnlocked.vue";
import StationCard from "~/components/station/station.vue";
import { useGamePreview } from "~/composables/useGamePreview";
import { useStations } from "~/composables/useStations";

const { teacher } = useClassrooms();
const { getMyStations } = useStations();
const route = useRoute();
const { isPreview, classroomId } = useGamePreview();
const previewWorldUid = computed(() => typeof route.query.worldUid === "string" ? route.query.worldUid : undefined);

const { data: stationData } = await useAsyncData(
  "student-world",
  async () => {
    if (previewWorldUid.value) {
      return await getMyStations(previewWorldUid.value);
    }

    if (!isPreview.value) {
      return await getMyStations();
    }

    const classroom = await teacher.getClassroomById(classroomId.value).catch(() => null);
    const worldUid = classroom?.worldUid?.trim() ?? "";

    if (!worldUid) {
      return {
        world: null,
        stations: [],
      };
    }

    return await getMyStations(worldUid);
  },
  {
    watch: [previewWorldUid, isPreview, classroomId],
  },
);

const pageTitle = computed(() => stationData.value?.world?.title || "Stasjonskart");

type ConnectorPath = {
  id: string;
  d: string;
};

const stationPathElement = ref<HTMLElement | null>(null);
const stationNodeElements = ref<HTMLElement[]>([]);
const connectorPaths = ref<ConnectorPath[]>([]);
const connectorCanvas = ref({ width: 0, height: 0 });
const showNotYetUnlockedPopup = ref(false);

let resizeObserver: ResizeObserver | null = null;

const stationCards = computed(() => {
  return (stationData.value?.stations ?? []).map((station) => ({
    station,
    completedExercises: station.completedExercises,
    isUnlocked: station.isUnlocked,
  }));
});

const hasStations = computed(() => stationCards.value.length > 0);

const registerStationNode = (element: Element | null, index: number) => {
  if (element instanceof HTMLElement) {
    stationNodeElements.value[index] = element;
    return;
  }

  stationNodeElements.value.splice(index, 1);
};

const getStationAnchorRect = (node: HTMLElement) => {
  const cardElement = node.firstElementChild;

  if (cardElement instanceof HTMLElement) {
    return cardElement.getBoundingClientRect();
  }

  return node.getBoundingClientRect();
};

const buildHorizontalConnector = (startRect: DOMRect, endRect: DOMRect, containerRect: DOMRect, index: number) => {
  const shouldExitRight = endRect.left >= startRect.left;
  const startX = shouldExitRight
    ? startRect.right - containerRect.left - 12
    : startRect.left - containerRect.left + 12;
  const startY = startRect.top - containerRect.top + startRect.height * 0.68;
  const endX = shouldExitRight
    ? endRect.left - containerRect.left + 12
    : endRect.right - containerRect.left - 12;
  const endY = endRect.top - containerRect.top + endRect.height * 0.32;
  const horizontalPull = Math.max(48, Math.abs(endX - startX) * 0.38);
  const sway = index % 2 === 0 ? 26 : -26;
  const pullDirection = shouldExitRight ? 1 : -1;

  return `M ${startX} ${startY} C ${startX + horizontalPull * pullDirection} ${startY + sway}, ${endX - horizontalPull * pullDirection} ${endY - sway}, ${endX} ${endY}`;
};

const buildVerticalConnector = (startRect: DOMRect, endRect: DOMRect, containerRect: DOMRect, index: number) => {
  const shouldExitBottom = endRect.top >= startRect.top;
  const startX = startRect.left - containerRect.left + startRect.width * 0.68;
  const startY = shouldExitBottom
    ? startRect.bottom - containerRect.top - 8
    : startRect.top - containerRect.top + 8;
  const endX = endRect.left - containerRect.left + endRect.width * 0.32;
  const endY = shouldExitBottom
    ? endRect.top - containerRect.top + 8
    : endRect.bottom - containerRect.top - 8;
  const verticalPull = Math.max(28, Math.abs(endY - startY) * 0.35);
  const sway = index % 2 === 0 ? 18 : -18;
  const pullDirection = shouldExitBottom ? 1 : -1;

  return `M ${startX} ${startY} C ${startX + sway} ${startY + verticalPull * pullDirection}, ${endX - sway} ${endY - verticalPull * pullDirection}, ${endX} ${endY}`;
};

const syncConnectors = () => {
  const container = stationPathElement.value;

  if (!container) {
    connectorPaths.value = [];
    return;
  }

  const nodes = stationNodeElements.value.filter((node): node is HTMLElement => node instanceof HTMLElement);

  if (nodes.length < 2) {
    connectorCanvas.value = {
      width: container.clientWidth,
      height: container.offsetHeight,
    };
    connectorPaths.value = [];
    return;
  }

  const containerRect = container.getBoundingClientRect();
  const isCompactLayout = window.matchMedia("(max-width: 800px)").matches;

  connectorCanvas.value = {
    width: container.clientWidth,
    height: container.offsetHeight,
  };

  connectorPaths.value = nodes.slice(0, -1).map((node, index) => {
    const nextNode = nodes[index + 1];
    const nodeRect = getStationAnchorRect(node);
    const nextNodeRect = getStationAnchorRect(nextNode);
    const d = isCompactLayout
      ? buildVerticalConnector(nodeRect, nextNodeRect, containerRect, index)
      : buildHorizontalConnector(nodeRect, nextNodeRect, containerRect, index);

    return {
      id: `${stationCards.value[index]?.station.uid ?? index}-${stationCards.value[index + 1]?.station.uid ?? index + 1}`,
      d,
    };
  });
};

const observeStationLayout = () => {
  resizeObserver?.disconnect();

  if (!stationPathElement.value || typeof ResizeObserver === "undefined") {
    return;
  }

  resizeObserver = new ResizeObserver(() => {
    syncConnectors();
  });

  resizeObserver.observe(stationPathElement.value);

  stationNodeElements.value.forEach((node) => {
    if (node instanceof HTMLElement) {
      resizeObserver?.observe(node);
    }
  });
};

const refreshConnectors = async () => {
  await nextTick();
  syncConnectors();
  observeStationLayout();
};

onMounted(() => {
  refreshConnectors();
  window.addEventListener("resize", syncConnectors);
});

onBeforeUnmount(() => {
  window.removeEventListener("resize", syncConnectors);
  resizeObserver?.disconnect();
});

watch(stationCards, () => {
  refreshConnectors();
}, { flush: "post" });

</script>

<template>
  <section class="stationsPage">
    <header class="pageIntro">
      <p class="pageEyebrow">Stasjonskart</p>
      <h1 class="pageTitle">{{ pageTitle }}</h1>
      <p class="pageSummary">
        Start på den første åpne stasjonen og jobb deg videre bortover banen.
      </p>
    </header>

    <section ref="stationPathElement" v-if="hasStations" class="stationPath" aria-label="Stasjonsoversikt">
      <svg
        class="stationPath__connectors"
        :viewBox="`0 0 ${Math.max(connectorCanvas.width, 1)} ${Math.max(connectorCanvas.height, 1)}`"
        :width="connectorCanvas.width"
        :height="connectorCanvas.height"
        aria-hidden="true"
        preserveAspectRatio="none"
      >
        <g v-for="connector in connectorPaths" :key="connector.id">
          <path :d="connector.d" class="stationPath__connectorGlow" />
          <path :d="connector.d" class="stationPath__connectorStroke" />
        </g>
      </svg>
      <template v-for="(entry, index) in stationCards" :key="entry.station.uid">
        <div
          :class="['stationPath__node', `stationPath__node--${index % 2 === 0 ? 'left' : 'right'}`]"
          :ref="(element) => registerStationNode(element, index)"
        >
          <StationCard
            :station="entry.station"
            :is-unlocked="entry.isUnlocked"
            :completed-exercises="entry.completedExercises"
            @locked-click="showNotYetUnlockedPopup = true"
          />
        </div>
      </template>
    </section>

    <section v-else class="emptyState">
      <h2>Ingen stasjoner tilgjengelig</h2>
      <p>Dette stasjonskartet har ingen stasjoner ennå, eller du er ikke koblet til et klasserom.</p>
    </section>

    <PopupNotYetUnlocked
      v-if="showNotYetUnlockedPopup"
      title="Denne stasjonen er fortsatt låst"
      description="Fullfør stasjonen før denne for å låse opp neste stopp på kartet."
      @close="showNotYetUnlockedPopup = false"
    />
  </section>
</template>

<style scoped>
.stationsPage {
  --stations-eyebrow: var(--palette-cork-580);
  --stations-title: var(--color-brand-primary);
  --stations-summary: var(--palette-gray-900);
  min-height: fit-content;
  display: grid;
  gap: var(--space-5);
  align-content: start;
  padding: var(--layout-page-padding);
  border-radius: var(--radius-3);
  position: relative; /* needed so z-index on ::before works */
  isolation: isolate; 
}

.stationsPage::before {
  content: '';
  position: fixed;
  inset: 0;
  z-index: -1;
  background:
    linear-gradient(var(--palette-overlay-dark-strong), var(--palette-overlay-dark-strong)),
    linear-gradient(var(--palette-overlay-dark-medium), var(--palette-overlay-dark-medium)),
    url("~/assets/images/background_2_blurred.png") center / cover no-repeat;
  pointer-events: none;
}


.pageIntro {
  display: grid;
  gap: var(--space-3);
  align-content: start;
  justify-self: start;
  max-width: 60ch;
  padding: var(--space-4) var(--space-5);
  border-radius: var(--radius-2);
  background: var(--palette-cream-100);
}

.pageEyebrow {
  margin: 0;
  font-family: var(--font-family-body), sans-serif;
  font-size: var(--font-size-14);
  line-height: var(--font-leading-14);
  font-weight: var(--font-weight-bold);
  letter-spacing: var(--font-tracking-wider);
  text-transform: uppercase;
  color: var(--stations-eyebrow);
}

.pageTitle {
  margin: 0;
  color: var(--stations-title);
}

.pageSummary {
  margin: 0;
  max-width: 60ch;
  color: var(--stations-summary);
}

.stationPath {
  position: relative;
  display: grid;
  gap: var(--space-5);
  padding-block: var(--space-2);
}

.stationPath__connectors {
  position: absolute;
  inset: 0;
  overflow: visible;
  pointer-events: none;
}

.stationPath__connectorGlow,
.stationPath__connectorStroke {
  fill: none;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.stationPath__connectorGlow {
  stroke: rgba(44, 28, 18, 0.28);
  stroke-width: 1rem;
}

.stationPath__connectorStroke {
  stroke: rgba(230, 180, 96, 0.9);
  stroke-width: 0.45rem;
  stroke-dasharray: 0.1 1rem;
}

.stationPath__node {
  position: relative;
  z-index: 1;
  width: min(100%, 18rem);
}

.stationPath__node--left {
  justify-self: start;
  margin-left: max(0rem, 10%);
}

.stationPath__node--right {
  justify-self: end;
  margin-right: max(0rem, 10%);
}

.emptyState {
  display: grid;
  gap: var(--space-2);
  padding: var(--space-5);
  border: var(--border-width-hairline) solid var(--palette-gray-200);
  border-radius: var(--radius-3);
  background: var(--palette-overlay-light-soft);
}

.emptyState h2,
.emptyState p {
  margin: 0;
}

@media (max-width: 800px) {
  .stationPath__node,
  .stationPath__node--left,
  .stationPath__node--right {
    width: 100%;
    justify-self: stretch;
    margin-left: 0;
    margin-right: 0;
  }
}
</style>
