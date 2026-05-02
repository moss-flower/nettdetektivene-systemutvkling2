<script setup lang="ts">
import StoppestedIntroVideo from '~/components/gameplay/shared/StoppestedIntroVideo.vue'
import { HOME_INTRO_VIDEO, STATION_VIDEOS } from '~/composables/utils/stationVideos'
import type { IntroVideoMeta } from '~/composables/utils/stationVideos'

const selectedVideo = ref<IntroVideoMeta | null>(null)

function thumbnailUrl(video: IntroVideoMeta) {
  return `https://i.ytimg.com/vi/${video.youtubeId}/hqdefault.jpg`
}

function selectVideo(video: IntroVideoMeta) {
  selectedVideo.value = video
}
</script>

<template>
  <section class="student-help">
    <header class="student-help__header">
      <p class="student-help__eyebrow">Hjelp</p>
      <h2>Hjelpemeldinger for elev</h2>
      <p class="student-help__intro">
        Denne siden er en mal for hjelpetekster som vises ved forste innlogging.
        Selve tekstene legges inn senere.
      </p>
    </header>

    <article class="student-help__card">
      <h3>Førstegangsveiledning</h3>
      <div class="student-help__placeholder">
        <p>Placeholder: Her kommer stegvise hjelpetekster for nye brukere.</p>
      </div>
    </article>

    <section class="student-help__gallery" aria-labelledby="student-help-gallery-title">
      <div class="student-help__gallery-header">
        <p class="student-help__eyebrow">Videoer</p>
        <h3 id="student-help-gallery-title">Se introvideoer igjen</h3>
        <p>
          Trykk på "Hjemskjermen" for å spille av introduksjon på nytt,
          eller trykk på de andre videoene hvis du vil lære om nettvett.
          For å se en introduksjon til datasenteret, finner du den nederst på siden.
        </p>
      </div>

      <div class="student-help__video-grid">
        <button
          type="button"
          class="student-help__video-card"
          @click="selectVideo(HOME_INTRO_VIDEO)"
        >
          <img :src="thumbnailUrl(HOME_INTRO_VIDEO)" :alt="HOME_INTRO_VIDEO.titleNo">
          <span class="student-help__video-text">
            <span class="student-help__video-station">{{ HOME_INTRO_VIDEO.stationName }}</span>
            <span class="student-help__video-title">{{ HOME_INTRO_VIDEO.titleNo }}</span>
          </span>
          <span class="student-help__video-action">Spill av</span>
        </button>

        <button
          v-for="video in STATION_VIDEOS"
          :key="`${video.stationName}-${video.titleNo}`"
          type="button"
          class="student-help__video-card"
          @click="selectVideo(video)"
        >
          <img :src="thumbnailUrl(video)" :alt="video.titleNo">
          <span class="student-help__video-text">
            <span class="student-help__video-station">{{ video.stationName }}</span>
            <span class="student-help__video-title">{{ video.titleNo }}</span>
          </span>
          <span class="student-help__video-action">Spill av</span>
        </button>
      </div>
    </section>

    <StoppestedIntroVideo
      :youtube-id="selectedVideo?.youtubeId ?? ''"
      :title-no="selectedVideo?.titleNo ?? ''"
      :open="selectedVideo !== null"
      @close="selectedVideo = null"
    />
  </section>
</template>

<style scoped>
.student-help {
  /* station reception wall */
  --help-wall: var(--palette-wood-900);
  --help-wall-seam: var(--palette-overlay-warm-glow);
  --help-wall-glow: var(--palette-overlay-warm-glow);
  --help-counter: var(--palette-cork-700);
  --help-counter-edge: var(--palette-cork-900);

  /* Page text on dark wall */
  --help-eyebrow: var(--palette-gold-500);
  --help-title: var(--palette-cream-100);
  --help-title-depth: var(--palette-overlay-dark-strong);
  --help-intro: var(--palette-cream-300);

  /* card surface, reception window */
  --help-card-bg-top: var(--palette-cream-100);
  --help-card-bg-bottom: var(--palette-cream-200);
  --help-card-border: var(--palette-overlay-cork-medium);
  --help-card-text: var(--palette-cork-900);
  --help-card-muted: var(--palette-cork-580);
  --help-card-shadow: var(--palette-overlay-dark-deep);

  /* placeholder dashed region */
  --help-placeholder-border: var(--palette-gray-300);
  --help-placeholder-text: var(--palette-cork-500);

  /* Video gallery */
  --help-gallery-bg: var(--color-bg-onLight);
  --help-gallery-border: var(--color-border-default);
  --help-gallery-text: var(--color-text-onLight);
  --help-gallery-muted: var(--color-text-secondary);
  --help-gallery-card-bg: var(--color-bg-surface);
  --help-gallery-card-hover: var(--color-bg-surface-hover);
  --help-gallery-card-border: var(--color-border-default);
  --help-gallery-action-bg: var(--color-bg-onLight);
  --help-gallery-action-text: var(--color-text-onLight);
  --help-gallery-shadow: var(--shadow-1);

  min-height: 100%;
  display: grid;
  gap: var(--space-5);
  align-content: start;
  padding: var(--layout-page-padding);
  padding-bottom: 120px;
  border-radius: var(--radius-2);
  color: var(--help-title);
  background:
    /* warm desk-lamp glow, low & narrow; like a counter-lamp */
    radial-gradient(ellipse 50% 40% at 50% 65%, var(--help-wall-glow), transparent 70%),
    /* vertical panel seams */
    repeating-linear-gradient(
      90deg,
      transparent 0,
      transparent 240px,
      var(--help-wall-seam) 240px,
      var(--help-wall-seam) 241px
    ),
    var(--help-wall);
  overflow-x: hidden;
}

.student-help__header {
  display: grid;
  gap: var(--space-3);
  max-width: 680px;
}

.student-help__back {
  width: fit-content;
  margin-bottom: var(--space-2);
}

.student-help__eyebrow {
  margin: 0;
  color: var(--help-eyebrow);
  font-family: var(--font-family-body), sans-serif;
  font-size: var(--font-size-14);
  line-height: var(--font-leading-14);
  font-weight: var(--font-weight-bold);
  letter-spacing: var(--font-tracking-wider);
  text-transform: uppercase;
}

.student-help__header h2 {
  margin: 0;
  font-family: var(--font-family-display), sans-serif;
  font-size: var(--font-size-36);
  line-height: var(--font-leading-36);
  font-weight: var(--font-weight-bold);
  color: var(--help-title);
  text-shadow: 0 1px 2px var(--help-title-depth);
}

.student-help__intro {
  margin: 0;
  max-width: 60ch;
  color: var(--help-intro);
  font-family: var(--font-family-body), sans-serif;
  font-size: var(--font-size-16);
  line-height: var(--font-leading-16);
}

.student-help__card {
  display: grid;
  gap: var(--space-5);
  max-width: 680px;
  padding: var(--space-6);
  border: var(--border-width-hairline) solid var(--help-card-border);
  border-radius: var(--radius-2);
  background: linear-gradient(
    180deg,
    var(--help-card-bg-top) 0%,
    var(--help-card-bg-bottom) 100%
  );
  box-shadow: var(--space-2) var(--space-4) var(--space-6) var(--help-card-shadow);
  color: var(--help-card-text);
}

.student-help__card h3 {
  margin: 0;
  font-family: var(--font-family-display), sans-serif;
  font-size: var(--font-size-22);
  line-height: var(--font-leading-22);
  font-weight: var(--font-weight-bold);
  color: var(--help-card-text);
}

.student-help__placeholder {
  min-height: var(--space-8);
  padding: var(--space-4);
  border: var(--border-width-hairline) dashed var(--help-placeholder-border);
  border-radius: var(--radius-1);
  color: var(--help-placeholder-text);
  font-family: var(--font-family-body), sans-serif;
  font-size: var(--font-size-16);
  line-height: var(--font-leading-16);
  display: grid;
  align-items: center;
}

.student-help__gallery {
  display: grid;
  gap: var(--space-5);
  padding: var(--space-6);
  border: var(--border-width-hairline) solid var(--help-gallery-border);
  border-radius: var(--radius-2);
  background: var(--help-gallery-bg);
  color: var(--help-gallery-text);
  box-shadow: var(--help-gallery-shadow);
}

.student-help__gallery-header {
  display: grid;
  gap: var(--space-2);
}

.student-help__gallery-header .student-help__eyebrow {
  color: var(--help-gallery-text);
}

.student-help__gallery-header h3,
.student-help__gallery-header p {
  margin: 0;
}

.student-help__gallery-header h3 {
  color: var(--help-gallery-text);
  font-family: var(--font-family-display), sans-serif;
  font-size: var(--font-size-22);
  line-height: var(--font-leading-22);
}

.student-help__gallery-header p:not(.student-help__eyebrow) {
  max-width: 60ch;
  color: var(--help-gallery-muted);
  font-family: var(--font-family-body), sans-serif;
  font-size: var(--font-size-16);
  line-height: var(--font-leading-16);
}

.student-help__video-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: var(--space-4);
}

.student-help__video-card {
  display: grid;
  grid-template-rows: auto 1fr auto;
  gap: var(--space-3);
  min-height: 100%;
  padding: var(--space-3);
  border: var(--border-width-hairline) solid var(--help-gallery-card-border);
  border-radius: var(--radius-2);
  background: var(--help-gallery-card-bg);
  color: var(--help-gallery-text);
  text-align: left;
  box-shadow: var(--help-gallery-shadow);
  transition: background var(--transition-fast), transform var(--transition-fast);
}

.student-help__video-card:hover {
  background: var(--help-gallery-card-hover);
  transform: translateY(calc(-1 * var(--border-width-hairline)));
}

.student-help__video-card:active {
  transform: translateY(var(--border-width-hairline));
}

.student-help__video-card img {
  width: 100%;
  aspect-ratio: 16 / 9;
  display: block;
  object-fit: cover;
  border-radius: var(--radius-1);
}

.student-help__video-text {
  display: grid;
  gap: var(--space-1);
}

.student-help__video-station,
.student-help__video-title,
.student-help__video-action {
  font-family: var(--font-family-body), sans-serif;
}

.student-help__video-station {
  color: var(--help-gallery-text);
  font-weight: var(--font-weight-bold);
}

.student-help__video-title {
  color: var(--help-gallery-muted);
  font-size: var(--font-size-14);
  line-height: var(--font-leading-14);
}

.student-help__video-action {
  justify-self: start;
  padding: var(--space-2) var(--space-3);
  border: var(--border-width-thin) solid var(--help-gallery-card-border);
  border-radius: var(--radius-pill);
  background: var(--help-gallery-action-bg);
  color: var(--help-gallery-action-text);
  font-weight: var(--font-weight-bold);
}

@media (max-width: 920px) {
  .student-help__video-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 560px) {
  .student-help {
    padding: var(--space-5) var(--space-4);
  }

  .student-help__gallery {
    padding: var(--space-4);
  }

  .student-help__video-grid {
    grid-template-columns: 1fr;
  }
}
</style>
