<script setup lang="ts">
import { useGamePreview } from '~/composables/useGamePreview'
import { useStudentProfile } from '~/composables/useStudentProfile'
import { useStations } from '~/composables/useStations'
import { getLevel } from '~/composables/utils/studentProgress'

provideGameConfetti()
const { getMyProfile } = useStudentProfile()
const { getStationByUid } = useStations()
const { openAvatarProfile } = useAvatarProfilePopup()

const route = useRoute()
const { isPreview } = useGamePreview()
const profileName = ref('Detektiv')
const profileLevelText = ref('Laster nivå...')

const stationUid = computed(() => {
  const routeParam = route.params.stationUid

  if (Array.isArray(routeParam)) {
    return routeParam[0] ?? ''
  }

  return typeof routeParam === 'string' ? routeParam : ''
})

const { data: station } = await useAsyncData(
  () => `layout-station-${stationUid.value}`,
  async () => stationUid.value ? await getStationByUid(stationUid.value) : null,
  { watch: [stationUid] },
)

const layoutStyle = computed(() => {
  const backgroundColor = station.value?.backgroundColor ?? '#cfe9ff'

  return {
    '--game-layout-side-color': backgroundColor,
    '--game-layout-center-color': `color-mix(in srgb, ${backgroundColor} 22%, white)`,
  }
})

onMounted(async () => {
  if (isPreview.value) {
    profileName.value = 'Lærer'
    profileLevelText.value = 'Forhåndsvisning'
    return
  }

  try {
    const profile = await getMyProfile()
    profileName.value = profile.studentName
    profileLevelText.value = `Level ${getLevel(profile.totalXp).level}`
  } catch (error) {
    console.error('Error fetching student profile:', error)
  }
})
</script>

<template>
  <div class="game-layout" :style="layoutStyle">
    <aside class="game-layout__rail game-layout__rail--left" aria-label="Spillerpanel">
      <component
        :is="isPreview ? 'div' : 'button'"
        type="button"
        :disabled="isPreview"
        class="game-layout__rail-card game-layout__rail-card--avatar"
        :class="{ 'game-layout__rail-link': !isPreview }"
        :aria-label="isPreview ? 'Lærerforhåndsvisning' : 'Åpne elevprofil'"
        @click="!isPreview && openAvatarProfile()"
      >
        <AvatarElement v-if="!isPreview" />
        <div v-else class="game-layout__avatar-placeholder" aria-hidden="true">
          <img
            src="/assets/images/elev_avatar.png"
            alt="Elev avatar"
            class="game-layout__avatar-image"
          />
        </div>
        <template v-if="isPreview">
          <p class="game-layout__rail-title">{{ profileName }}</p>
          <p class="game-layout__rail-text">{{ profileLevelText }}</p>
        </template>
      </component>
    </aside>

    <main class="game-layout__content" aria-label="Spillinnhold">
      <div :key="route.fullPath" class="game-layout__content-shell">
        <slot />
      </div>
    </main>
  </div>
</template>

<style scoped>
.game-layout {
  height: 100dvh;
  display: grid;
  grid-template-columns: clamp(7rem, 16vw, 13rem) minmax(0, 1fr) clamp(6rem, 14vw, 12rem);
  background:
    linear-gradient(
      90deg,
      var(--game-layout-side-color) 0,
      var(--game-layout-side-color) 18%,
      var(--game-layout-center-color) 18%,
      var(--game-layout-center-color) 82%,
      var(--game-layout-side-color) 82%,
      var(--game-layout-side-color) 100%
    );
  overflow: hidden;
}

.game-layout__rail,
.game-layout__content {
  min-height: 0;
  height: 100dvh;
}

.game-layout__rail {
  display: flex;
  align-items: flex-end;
  justify-content: center;
  padding:0  1rem 5rem;
  overflow: hidden;
}

.game-layout__content {
  padding: 0;
  overflow: hidden;
}

.game-layout__content-shell {
  height: 100%;
  background: #ffffff;
  box-shadow: none;
  padding: 2rem;
  overflow-y: auto;
  overflow-x: hidden;
}

.game-layout__rail-card {
  appearance: none;
  width: min(100%, 10rem);
  padding: 1rem 0.9rem;
  border-radius: 1.25rem;
  border: 1px solid rgba(54, 99, 155, 0.18);
  background: rgba(255, 255, 255, 0.75);
  text-align: center;
  color: #183153;
  box-shadow: 0 10px 24px rgba(34, 74, 126, 0.12);
  font: inherit;
}

.game-layout__rail-link {
  text-decoration: none;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.game-layout__rail-link:hover,
.game-layout__rail-link:focus-visible {
  transform: translateY(-2px);
  box-shadow: 0 14px 28px rgba(34, 74, 126, 0.16);
  outline: none;
}

.game-layout__rail-card--avatar {
  display: grid;
  gap: 0.75rem;
  margin-top: 0;
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

.game-layout__avatar-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
  background: #e3eefa;
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
    grid-template-rows: auto minmax(0, 1fr);
    background: var(--game-layout-side-color);
  }

  .game-layout__rail,
  .game-layout__content {
    height: auto;
  }

  .game-layout__rail {
    padding: 2rem 1rem 1rem;
  }

  .game-layout__content {
    padding: 0 1rem;
  }

  .game-layout__content-shell {
    height: 100%;
    border-radius: 0;
  }

  .game-layout__rail-card {
    width: min(100%, 24rem);
  }
}
</style>
