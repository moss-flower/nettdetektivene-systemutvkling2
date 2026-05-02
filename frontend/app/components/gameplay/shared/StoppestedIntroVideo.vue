<script setup lang="ts">
const props = defineProps<{
  youtubeId: string
  titleNo: string
  open: boolean
}>()

const emit = defineEmits<{
  close: []
}>()

const loaded = ref(false)

const posterUrl = computed(() => `https://i.ytimg.com/vi/${props.youtubeId}/hqdefault.jpg`)
const embedUrl = computed(() => `https://www.youtube-nocookie.com/embed/${props.youtubeId}?rel=0&modestbranding=1&autoplay=1&playsinline=1&color=white`)

function handleDialogKeydown(event: KeyboardEvent) {
  if (event.key === 'Escape') {
    emit('close')
  }
}

watch(
  () => props.open,
  (isOpen) => {
    if (!isOpen) {
      loaded.value = false
    }

    if (import.meta.client) {
      document.body.style.overflow = isOpen ? 'hidden' : ''
    }
  },
)

onMounted(() => {
  if (import.meta.client) {
    window.addEventListener('keydown', handleDialogKeydown)
  }
})

onBeforeUnmount(() => {
  if (import.meta.client) {
    window.removeEventListener('keydown', handleDialogKeydown)
    document.body.style.overflow = ''
  }
})
</script>

<template>
  <div
    v-if="open"
    class="introVideoOverlay"
    @click.self="emit('close')"
  >
    <div
      class="introVideoPanel"
      role="dialog"
      aria-modal="true"
      aria-labelledby="introVideoTitle"
    >
      <h2 id="introVideoTitle" class="introVideoTitle">{{ titleNo }}</h2>

      <div class="introVideoFrame">
        <button
          v-if="!loaded"
          type="button"
          class="introVideoPoster"
          :aria-label="`Spill av ${titleNo}`"
          @click="loaded = true"
        >
          <img :src="posterUrl" :alt="titleNo">
          <span class="introVideoPlayGlyph" aria-hidden="true">▶</span>
        </button>

        <iframe
          v-else
          class="introVideoEmbed"
          :src="embedUrl"
          :title="titleNo"
          allow="autoplay; encrypted-media; picture-in-picture; fullscreen"
          allowfullscreen
          loading="lazy"
        />
      </div>

      <button
        type="button"
        class="introVideoClose"
        @click="emit('close')"
      >
        Lukk
      </button>
    </div>
  </div>
</template>

<style scoped>
.introVideoOverlay {
  --video-overlay-bg: var(--palette-overlay-dark-strong);
  --video-panel-bg: var(--color-bg-surface);
  --video-panel-border: var(--color-border-default);
  --video-panel-shadow: var(--shadow-card);
  --video-pill-bg: var(--color-bg-surface);
  --video-pill-ink: var(--color-text-primary);
  --video-pill-border: var(--color-border-default);
  --video-pill-hover: var(--color-bg-surface-hover);
  --video-frame-bg: var(--color-text-primary);
  --video-play-bg: var(--color-bg-surface);
  --video-play-ink: var(--color-text-primary);

  position: fixed;
  inset: 0;
  z-index: 50;
  display: grid;
  place-items: center;
  padding: var(--space-4);
  background: var(--video-overlay-bg);
}

.introVideoPanel {
  /* Deliberate fluid exception so the 16:9 video fits small and large screens. */
  width: clamp(320px, 80vw, 880px);
  max-height: 90vh;
  overflow: auto;
  display: grid;
  gap: var(--space-4);
  padding: var(--space-5);
  border: var(--border-width-hairline) solid var(--video-panel-border);
  border-radius: var(--radius-3);
  background: var(--video-panel-bg);
  box-shadow: var(--video-panel-shadow);
  color: var(--color-text-primary);
}

.introVideoTitle {
  margin: 0;
  color: var(--color-text-primary);
  font-family: var(--font-family-display), sans-serif;
  font-size: var(--font-size-22);
  line-height: var(--font-leading-22);
}

.introVideoFrame {
  position: relative;
  aspect-ratio: 16 / 9;
  overflow: hidden;
  border-radius: var(--radius-2);
  background: var(--video-frame-bg);
}

.introVideoPoster,
.introVideoEmbed {
  width: 100%;
  height: 100%;
}

.introVideoPoster {
  position: relative;
  display: block;
  overflow: hidden;
  color: var(--video-play-ink);
}

.introVideoPoster img {
  width: 100%;
  height: 100%;
  display: block;
  object-fit: cover;
}

.introVideoPlayGlyph {
  position: absolute;
  inset: 50% auto auto 50%;
  display: grid;
  place-items: center;
  min-width: var(--size-control-lg);
  min-height: var(--size-control-lg);
  padding: var(--space-3) var(--space-4);
  border-radius: var(--radius-pill);
  background: var(--video-play-bg);
  color: var(--video-play-ink);
  transform: translate(-50%, -50%);
  box-shadow: var(--video-panel-shadow);
}

.introVideoEmbed {
  display: block;
  border: 0;
}

.introVideoClose {
  justify-self: end;
  min-height: var(--size-control-md);
  padding: var(--space-3) var(--space-5);
  border: var(--border-width-thin) solid var(--video-pill-border);
  border-radius: var(--radius-pill);
  background: var(--video-pill-bg);
  color: var(--video-pill-ink);
  font-family: var(--font-family-body), sans-serif;
  font-weight: var(--font-weight-bold);
  transition: background var(--transition-fast), transform var(--transition-fast);
}

.introVideoClose:hover {
  background: var(--video-pill-hover);
}

.introVideoClose:active {
  transform: translateY(var(--border-width-hairline));
}

@media (max-width: 560px) {
  .introVideoOverlay {
    padding: var(--space-3);
  }

  .introVideoPanel {
    width: 100%;
    padding: var(--space-4);
  }
}
</style>
