<script setup lang="ts">
import type { NotepadEntry } from '~/composables/types/NotepadTypes.ts';

//Takes in a single NotepadEntry
const props = defineProps<{
  entry: NotepadEntry
}>();

//Changes title masking based on entry type
const titlePrefix = computed(() => {
  if (props.entry.type === "tip") return "Tip:";
  if (props.entry.type === "info") return "Info:";
  if (props.entry.type === "observation") return "Observation:";
  return "";
});

const formattedDate = computed(() => {
  return new Date(props.entry.date).toLocaleString(undefined, {
    year: "numeric",
    month: "short",
    day: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
});

</script>

<template>
  <article
    :class="[
      'notepadEntry',
      {
        tip: props.entry.type === 'tip',
        info: props.entry.type === 'info',
        observation: props.entry.type === 'observation',
      },
    ]"
  >
    <div class="meta">
      <h3 class="date">{{ formattedDate }}</h3>
      <h3 class="source">{{ entry.source }}</h3>
    </div>
    <h4 class="title">{{ titlePrefix }} {{ props.entry.title }}</h4>
    <p class="content">{{ props.entry.content }}</p>
  </article>
</template>

<style scoped>
.notepadEntry {
  --entry-surface-top: var(--palette-cream-100);
  --entry-surface-bottom: var(--palette-cream-200);
  --entry-text: var(--palette-cork-900);
  --entry-text-muted: var(--palette-cork-700);
  --entry-divider: var(--palette-overlay-cork-shadow);
  --entry-lift: var(--palette-overlay-cork-shadow);

  /* classification color, overridden by .tip .info .observation below
     rule paints left edge & the title label */
  --entry-rule: var(--palette-cork-900);
  --entry-wash: transparent;
  --entry-title-ink: var(--entry-text);

  position: relative;
  width: 100%;
  display: grid;
  gap: var(--space-3);
  padding: var(--space-4) var(--space-5);
  border-left: var(--border-width-heavy) solid var(--entry-rule);
  border-radius: var(--radius-1);
  background:
    linear-gradient(var(--entry-wash), var(--entry-wash)),
    linear-gradient(
      180deg,
      var(--entry-surface-top) 0%,
      var(--entry-surface-bottom) 100%
    );
  color: var(--entry-text);
  box-sizing: border-box;
  box-shadow: 0 var(--space-1) var(--space-3) var(--entry-lift);
}

.meta {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: var(--space-3);
  width: 100%;
  padding-bottom: var(--space-2);
  border-bottom: var(--border-width-hairline) solid var(--entry-divider);
}

.date {
  margin: 0;
  color: var(--entry-text-muted);
  font-family: var(--font-family-body), sans-serif;
  font-size: var(--font-size-14);
  line-height: var(--font-leading-14);
  font-weight: var(--font-weight-bold);
  letter-spacing: var(--font-tracking-wider);
  text-transform: uppercase;
}

.source {
  margin: 0;
  color: var(--entry-text);
  font-family: var(--font-family-serif), sans-serif;
  font-size: var(--font-size-22);
  line-height: var(--font-leading-22);
  font-weight: var(--font-weight-semibold);
  text-align: right;
}

.title {
  margin: 0;
  color: var(--entry-title-ink);
  font-family: var(--font-family-body), sans-serif;
  font-size: var(--font-size-16);
  line-height: var(--font-leading-16);
  font-weight: var(--font-weight-bold);
}

.content {
  margin: 0;
  color: var(--entry-text-muted);
  font-family: var(--font-family-body), sans-serif;
  font-size: var(--font-size-16);
  line-height: var(--font-leading-16);
  overflow-wrap: break-word;
  word-break: break-word;
  min-width: 0;
}

.tip {
  --entry-rule: var(--palette-gold-600);
  --entry-wash: var(--palette-overlay-warm-glow);
}

.info {
  --entry-rule: var(--palette-red-700);
  --entry-wash: var(--palette-overlay-red-soft);
  --entry-title-ink: var(--palette-red-700);
}

.observation {
  --entry-rule: var(--palette-green-700);
  --entry-wash: var(--palette-overlay-green-soft);
  --entry-title-ink: var(--palette-green-900);
}
</style>
