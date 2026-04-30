<script setup lang="ts">
import {useAudioSettings} from "~/composables/useAudioSettings";

const { musicVolume, effectsVolume } = useAudioSettings()

const musicLabel = computed(() => `${musicVolume.value}%`)
const effectsLabel = computed(() => `${effectsVolume.value}%`)

function startDialDrag(volumeRef: { value: number }, event: PointerEvent) {
  if (event.pointerType === 'mouse' && event.button !== 0) {
    return;
  }

  const face = event.currentTarget as HTMLElement
  event.preventDefault()
  face.setPointerCapture(event.pointerId)

  const input = face.parentElement?.querySelector('input')
  input?.focus()

  const rectangle = face.getBoundingClientRect()
  const centerX = rectangle.left + rectangle.width / 2
  const centerY = rectangle.top + rectangle.height / 2

  const angleToValue = (clientX: number, clientY: number) => {
    const deltaX = clientX - centerX
    const deltaY = clientY - centerY
    // 0deg = 12 o'clock, increasing clockwise; raw range -180deg - 180deg
    let degree = (Math.atan2(deltaX, -deltaY) * 180) / Math.PI
    degree = Math.max(-135, Math.min(135, degree))
    return Math.round(((degree + 135) / 270) * 100)
  }

  volumeRef.value = angleToValue(event.clientX, event.clientY)

  const onMove = (e: PointerEvent) => {
    volumeRef.value = angleToValue(e.clientX, e.clientY)
  }
  const onEnd = (e: PointerEvent) => {
    face.releasePointerCapture(e.pointerId)
    face.removeEventListener('pointermove', onMove)
    face.removeEventListener('pointerup', onEnd)
    face.removeEventListener('pointercancel', onEnd)
  }
  face.addEventListener('pointermove', onMove)
  face.addEventListener('pointerup', onEnd)
  face.addEventListener('pointercancel', onEnd)
}

const onMusicPointerDown = (event: PointerEvent) => startDialDrag(musicVolume, event)
const onEffectsPointerDown = (event: PointerEvent) => startDialDrag(effectsVolume, event)
</script>

<template>
  <section class="student-settings">
    <header class="student-settings__header">
      <p class="student-settings__eyebrow">Innstillinger</p>
      <h2>Lyd-konsoll</h2>
      <p class="student-settings__intro">
        Vri på rattene for å justere musikk og lydeffekt volum.
      </p>
    </header>

    <article class="student-settings__card">
      <div class="student-settings__dialBank">
        <label class="student-settings__dial" :style="{ '--dial-value': musicVolume }">
          <span class="student-settings__dialLabel">Musikk</span>
          <span class="student-settings__dialFace" aria-hidden="true" @pointerdown="onMusicPointerDown">
            <span class="student-settings__dialTicks"></span>
            <span class="student-settings__dialKnob"></span>
            <span class="student-settings__dialIndicator"></span>
          </span>
          <input
              v-model.number="musicVolume"
              class="student-settings__dialInput"
              type="range"
              min="0"
              max="100"
              step="1"
              aria-label="Justér musikkvolum"
          />
          <span class="student-settings__dialReadout" v-text="musicLabel"></span>
        </label>

        <label class="student-settings__dial" :style="{ '--dial-value': effectsVolume }">
          <span class="student-settings__dialLabel">Lydeffekter</span>
          <span class="student-settings__dialFace" aria-hidden="true" @pointerdown="onEffectsPointerDown">
            <span class="student-settings__dialTicks"></span>
            <span class="student-settings__dialKnob"></span>
            <span class="student-settings__dialIndicator"></span>
          </span>
          <input
              v-model.number="effectsVolume"
              class="student-settings__dialInput"
              type="range"
              min="0"
              max="100"
              step="1"
              aria-label="Justér lydeffekt volum"
          />
          <span class="student-settings__dialReadout" v-text="effectsLabel"></span>
        </label>
      </div>

      <p class="student-settings__note">
        bare en mal rn, vri ratt eller bruk pil-taster.
      </p>
    </article>
  </section>
</template>

<style scoped>
.student-settings {
  /* Detective's field wall amplifier, wood cabinet wall + brass meter face */
  --settings-wall: var(--palette-wood-900);
  --settings-wall-glow: var(--palette-overlay-warm-glow);
  --settings-wall-seam: var(--palette-overlay-dark-strong);
  --settings-wall-bevel: var(--palette-overlay-warm-glow);

  /* wall level text */
  --settings-eyebrow: var(--palette-gold-500);
  --settings-title: var(--palette-cream-100);
  --settings-title-depth: var(--palette-overlay-dark-strong);
  --settings-summary: var(--palette-cream-300);

  /* Gilt face plate anatomy */
  --settings-plate-gilt: var(--palette-gold-600);
  --settings-plate-highlight: var(--palette-gold-400);
  --settings-plate-liner: var(--palette-cork-900);
  --settings-plate-shadow: var(--palette-overlay-dark-deep);
  --settings-plate-mat-top: var(--palette-cream-100);
  --settings-plate-mat-bottom: var(--palette-cream-200);

  /* inside-plate text */
  --settings-label: var(--palette-cork-580);
  --settings-label-ink: var(--palette-cork-900);
  --settings-note: var(--palette-cork-700);
  --settings-divider: var(--palette-overlay-cork-shadow);

  /* Recessed digital readout */
  --settings-readout-bg: var(--palette-navy-900);
  --settings-readout-ink: var(--palette-gold-500);
  --settings-readout-glow: var(--palette-gold-400);
  --settings-readout-ring: var(--palette-gold-600);

  /* dial scale tick marks */
  --settings-tick: var(--palette-cream-300);
  --settings-tick-strong: var(--palette-gold-400);

  /* brass knob (dial face) */
  --settings-knob: var(--palette-gold-500);
  --settings-knob-highlight: var(--palette-gold-400);
  --settings-knob-ring: var(--palette-cork-900);
  --settings-knob-shadow: var(--palette-overlay-dark-deep);
  --settings-knob-indicator: var(--palette-cork-900);

  --settings-dial-well: var(--palette-overlay-dark-deep);

  /* back pill, palette direct; migrate to button files later */
  --settings-back-bg: var(--palette-white);
  --settings-back-border: var(--palette-gray-200);
  --settings-back-text: var(--palette-navy-900);

  position: relative;
  min-height: 100%;
  width: 100%;
  display: grid;
  gap: var(--space-6);
  align-content: start;
  justify-items: center;
  padding: var(--layout-page-padding);
  border-radius: var(--radius-2);
  color: var(--settings-title);
  background:
    radial-gradient(ellipse 70% 50% at 50% 20%, var(--settings-wall-glow), transparent 70%),
    repeating-linear-gradient(
      0deg,
      transparent 0,
      transparent 117px,
      var(--settings-wall-seam) 117px,
      var(--settings-wall-seam) 120px,
      var(--settings-wall-bevel) 120px,
      var(--settings-wall-bevel) 122px
    ),
    var(--settings-wall);
  overflow-x: hidden;
}

.student-settings__header {
  width: 100%;
  max-width: 680px;
  display: grid;
  gap: var(--space-2);
  justify-items: start;
}

.student-settings__back {
  /* back pill stub */
  display: inline-flex;
  width: fit-content;
  padding: var(--space-3) var(--space-4);
  margin-bottom: var(--space-3);
  border: var(--border-width-hairline) solid var(--settings-back-border);
  border-radius: var(--radius-pill);
  background: var(--settings-back-bg);
  color: var(--settings-back-text);
  font-family: var(--font-family-body), sans-serif;
  font-weight: var(--font-weight-bold);
  text-decoration: none;
}

.student-settings__eyebrow {
  margin: 0;
  color: var(--settings-eyebrow);
  font-family: var(--font-family-body), sans-serif;
  font-size: var(--font-size-14);
  line-height: var(--font-leading-14);
  font-weight: var(--font-weight-bold);
  letter-spacing: var(--font-tracking-wider);
  text-transform: uppercase;
}

.student-settings__header h2 {
  margin: 0;
  font-family: var(--font-family-serif), sans-serif;
  /* fluid hero scale, deliberate exception */
  font-size: clamp(2rem, 4vw, 3rem);
  line-height: 1.1;
  font-weight: var(--font-weight-regular);
  color: var(--settings-title);
  letter-spacing: var(--font-tracking-normal);
  text-shadow: 0 1px 2px var(--settings-title-depth);
}

.student-settings__intro {
  margin: 0;
  max-width: 60ch;
  color: var(--settings-summary);
  font-family: var(--font-family-body), sans-serif;
  line-height: var(--font-leading-base);
}

/* --- brass face plate (instrument itself) --- */
.student-settings__card {
  position: relative;
  width: 100%;
  max-width: 680px;
  display: grid;
  gap: var(--space-5);
  padding: var(--space-7) var(--space-6) var(--space-6);
  background: linear-gradient(
    180deg,
    var(--settings-plate-mat-top) 0%,
    var(--settings-plate-mat-bottom) 100%
  );
  border: var(--border-width-chunky) solid var(--settings-plate-gilt);
  border-radius: var(--radius-3);
  /* inner dark liner + top gilt highlight + heavy drop shadow
     (mounted plate, lit from top left) */
  box-shadow:
    inset 0 0 0 var(--border-width-thin) var(--settings-plate-liner),
    inset 0 var(--border-width-thin) 0 var(--settings-plate-highlight),
    var(--space-2) var(--space-4) var(--space-6) var(--settings-plate-shadow);
  color: var(--settings-label-ink);
}

/* two brass mounting screws in top corners of the plate */
.student-settings__card::before,
.student-settings__card::after {
  content: '';
  position: absolute;
  top: var(--space-3);
  width: var(--space-3);
  height: var(--space-3);
  border-radius: var(--radius-pill);
  background: radial-gradient(
    circle at 35% 30%,
    var(--settings-plate-highlight) 0%,
    var(--settings-plate-gilt) 60%,
    var(--settings-plate-liner) 100%
  );
  box-shadow: inset 0 0 0 1px var(--settings-plate-liner);
}

.student-settings__card::before {
  left: var(--space-3);
}

.student-settings__card::after {
  right: var(--space-3);
}

/* --- dial bank --- */
.student-settings__dialBank {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--space-6);
  justify-items: center;
  padding: var(--space-3) 0 var(--space-2);
}

.student-settings__dial {
  --dial-value: 0;
  position: relative;
  display: grid;
  gap: var(--space-3);
  justify-items: center;
}

.student-settings__dial:focus-within .student-settings__dialFace {
  outline: var(--border-width-thick) solid var(--color-focus-ring);
  outline-offset: var(--space-2);
}

.student-settings__dialLabel {
  color: var(--settings-label);
  font-family: var(--font-family-body), sans-serif;
  font-size: var(--font-size-14);
  line-height: var(--font-leading-14);
  letter-spacing: var(--font-tracking-wider);
  text-transform: uppercase;
}

/* dial face: 160px brass meter with engraved tick scale + indicator
   160px intentional deviation from token, mainly decorative so */
.student-settings__dialFace {
  --dial-size: 160px;
  --dial-min-rotation: -135deg;
  --dial-sweep: 270deg;
  --dial-angle: calc(var(--dial-min-rotation) + (var(--dial-value) / 100) * var(--dial-sweep));

  position: relative;
  width: var(--dial-size);
  height: var(--dial-size);
  border-radius: var(--radius-pill);
  background: var(--settings-dial-well);
  box-shadow:
    inset 0 var(--border-width-thin) var(--space-2) var(--palette-overlay-dark-deep),
    0 var(--space-2) var(--space-4) var(--settings-plate-shadow);
  cursor: grab;
  touch-action: none;
  user-select: none;
  -webkit-user-select: none;
}

.student-settings__dialFace:active {
  cursor: grabbing;
}

/* engraved tick scale */
.student-settings__dialTicks {
  position: absolute;
  inset: 0;
  border-radius: var(--radius-pill);
  background: conic-gradient(
    from -135deg,
    var(--settings-tick) 0 1.5deg, transparent 1.5deg 27deg,
    var(--settings-tick) 27deg 28.5deg, transparent 28.5deg 54deg,
    var(--settings-tick) 54deg 55.5deg, transparent 55.5deg 81deg,
    var(--settings-tick) 81deg 82.5deg, transparent 82.5deg 108deg,
    var(--settings-tick-strong) 108deg 109.5deg, transparent 109.5deg 135deg,
    var(--settings-tick) 135deg 136.5deg, transparent 136.5deg 162deg,
    var(--settings-tick) 162deg 163.5deg, transparent 163.5deg 189deg,
    var(--settings-tick) 189deg 190.5deg, transparent 190.5deg 216deg,
    var(--settings-tick) 216deg 217.5deg, transparent 217.5deg 243deg,
    var(--settings-tick) 243deg 244.5deg, transparent 244.5deg 270deg,
    var(--settings-tick) 270deg 271.5deg, transparent 271.5deg 360deg
  );
  -webkit-mask: radial-gradient(
    closest-side,
    transparent calc(50% - var(--space-3)),
    black calc(50% - var(--space-3)) calc(50% - var(--border-width-thick)),
    transparent calc(50% - var(--border-width-thick))
  );
  mask: radial-gradient(
    closest-side,
    transparent calc(50% - var(--space-3)),
    black calc(50% - var(--space-3)) calc(50% - var(--border-width-thick)),
    transparent calc(50% - var(--border-width-thick))
  );
  pointer-events: none;
}

/* Brass knob, gilded ball with cork ring + warm-glow inset */
.student-settings__dialKnob {
  position: absolute;
  inset: var(--space-4);
  border-radius: var(--radius-pill);
  border: var(--border-width-thick) solid var(--settings-knob-ring);
  background: radial-gradient(
    circle at 30% 25%,
    var(--settings-knob-highlight) 0%,
    var(--settings-knob) 55%,
    var(--palette-gold-700) 100%
  );
  box-shadow:
    0 var(--space-1) var(--space-3) var(--settings-knob-shadow),
    inset 0 0 0 2px var(--palette-overlay-warm-glow);
  pointer-events: none;
}

/* pointer indicator, notched line from dial center  */
.student-settings__dialIndicator {
  position: absolute;
  bottom: 50%;
  left: 50%;
  width: var(--border-width-chunky);
  height: 38%;
  border-radius: var(--radius-pill);
  background: linear-gradient(
    180deg,
    var(--settings-knob-indicator) 0%,
    var(--palette-cork-900) 100%
  );
  transform-origin: 50% 100%;
  transform: translateX(-50%) rotate(var(--dial-angle));
  transition: transform var(--transition-fast);
  pointer-events: none;
}

/* native range input, sr-only */
.student-settings__dialInput {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0 0 0 0);
  white-space: nowrap;
  border: 0;
}

/* digital readout under each dial */
.student-settings__dialReadout {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  /* 5ch holds 100% wit breathing room, exception for numeric readout */
  min-width: 5ch;
  padding: var(--space-1) var(--space-3);
  background: var(--settings-readout-bg);
  color: var(--settings-readout-ink);
  font-family: var(--font-family-serif), sans-serif;
  font-size: var(--font-size-22);
  line-height: 1;
  font-weight: var(--font-weight-semibold);
  letter-spacing: var(--font-tracking-wide);
  border: var(--border-width-thin) solid var(--settings-readout-ring);
  border-radius: var(--radius-1);
  box-shadow:
    inset 0 var(--space-1) var(--space-2) var(--palette-overlay-dark-deep),
    0 0 var(--space-3) var(--settings-readout-glow);
  text-shadow:
    0 0 var(--space-1) var(--settings-readout-glow),
    0 0 var(--space-3) var(--settings-readout-glow);
}

.student-settings__note {
  margin: 0;
  max-width: 60ch;
  color: var(--settings-note);
  font-family: var(--font-family-body), sans-serif;
  font-size: var(--font-size-14);
  line-height: var(--font-leading-14);
  font-style: italic;
}

@media (max-width: 560px) {
  .student-settings {
    padding: var(--space-6) var(--space-5) var(--space-5);
  }

  .student-settings__dialBank {
    grid-template-columns: 1fr;
    gap: var(--space-7);
  }
}
</style>
