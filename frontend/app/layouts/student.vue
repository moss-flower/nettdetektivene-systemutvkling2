<script setup lang="ts">
import { useGamePreview } from '~/composables/useGamePreview'
import { routePaths } from '~/composables/utils/routePaths'


const route = useRoute()
const { isPreview, classroomId } = useGamePreview()

const showBackToHome = computed(() => ![
  routePaths.studentHome,
  routePaths.studentMysteryPlay,
].includes(route.path as routePaths))

const backButtonRoute = computed(() => {
  if (isPreview.value && route.path === routePaths.studentLevels && classroomId.value) {
    return {
      path: routePaths.teacherClassroomOverview,
      query: { classroomId: classroomId.value },
    }
  }

  if (route.path === routePaths.studentExerciseDifficultySelect) {
    if (isPreview.value && classroomId.value) {
      return {
        path: routePaths.studentLevels,
        query: { preview: '1', classroomId: classroomId.value },
      }
    }

    const worldUid = typeof route.query.worldUid === 'string' ? route.query.worldUid : undefined

    return {
      path: routePaths.studentLevels,
      query: worldUid ? { worldUid } : undefined,
    }
  }

  return routePaths.studentHome
})
</script>

<template>
  <div class="studentShell">
    <div class="studentShellFrame">
      <UiStudentHomeBackButton
        v-if="showBackToHome"
        class="shellBackButton"
        :to="backButtonRoute"
      />

      <div class="studentShellContent studentShellPage">
        <slot />
      </div>
    </div>
  </div>
</template>

<style>
.studentShell {
  --board-border: var(--palette-cork-470);
  --board-gradient-top: var(--palette-cork-450);
  --board-gradient-bottom: var(--palette-cork-470);
  --board-inner-frame: var(--palette-overlay-cork-strong);
  --board-hatch: var(--palette-overlay-cork-soft);
  --board-highlight: var(--palette-overlay-light-soft);
  --board-inset-ring: var(--palette-overlay-cork-medium);
  --board-shadow: var(--palette-overlay-cork-shadow);

  width: 100%;
  height: 100%;
  display: grid;
  place-items: center;
  overflow: hidden;
}

.studentShellFrame {
  position: relative;
  width: min(90vw, 100%);
  height: 90vh;
  padding: var(--space-5);
  overflow: hidden;
  border: var(--border-width-chunky) solid var(--board-border);
  border-radius: var(--radius-4);
  background: linear-gradient(180deg, var(--board-gradient-top) 0%, var(--board-gradient-bottom) 100%);
  box-shadow:
    0 var(--space-5) var(--space-8) var(--board-shadow),
    inset 0 0 0 var(--border-width-thin) var(--board-inset-ring);
}

.studentShellFrame::before {
  content: '';
  position: absolute;
  inset: 0;
  background:
    radial-gradient(circle at top left, var(--board-highlight) 0, transparent 28%),
    repeating-linear-gradient(
      135deg,
      var(--board-hatch) 0,
      var(--board-hatch) 7px,
      transparent 7px,
      transparent 20px
    );
  pointer-events: none;
}

.studentShellFrame::after {
  content: '';
  position: absolute;
  inset: var(--space-4);
  border: var(--border-width-thick) solid var(--board-inner-frame);
  border-radius: var(--radius-3);
  pointer-events: none;
}

.studentShellContent {
  position: relative;
  z-index: 1;
  width: 100%;
  height: 100%;
  overflow-x: hidden;
  overflow-y: auto;
}

.shellBackButton {
  position: absolute;
  bottom: var(--space-5);
  left: var(--space-5);
  z-index: 3;
}

.studentShellPage > * {
  width: 100%;
  height: 100%;
}

@media (max-width: 980px) {
  .studentShellFrame {
    padding: var(--space-4);
  }

  .shellBackButton {
    bottom: var(--space-4);
    left: var(--space-4);
  }
}
</style>
