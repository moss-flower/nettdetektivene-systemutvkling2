<script setup lang="ts">
withDefaults(defineProps<{
  ariaLabel?: string
}>(), {
  ariaLabel: 'Dialog',
})
</script>

<template>
  <div class="dialog-template">
    <div class="dialog-template__backdrop" />
    <div
      class="dialog-template__panel"
      role="dialog"
      aria-modal="true"
      :aria-label="ariaLabel"
    >
      <slot name="badge" />
      <div class="dialog-template__content">
        <slot />
      </div>
      <div v-if="$slots.actions" class="dialog-template__actions">
        <slot name="actions" />
      </div>
    </div>
  </div>
</template>

<style scoped>
.dialog-template {
  position: fixed;
  inset: 0;
  z-index: 40;
  display: grid;
  place-items: center;
  padding: 1.5rem;
}

.dialog-template__backdrop {
  position: absolute;
  inset: 0;
  background: rgba(17, 24, 39, 0.34);
  backdrop-filter: blur(3px);
}

.dialog-template__panel {
  position: relative;
  width: min(100%, 32rem);
  display: grid;
  gap: 0.9rem;
  padding: 1.4rem;
  border-radius: 1.2rem;
  background: #fffdf9;
  box-shadow: 0 24px 70px rgba(15, 23, 42, 0.22);
}

.dialog-template__content {
  display: grid;
  gap: 0.9rem;
}

.dialog-template__actions {
  display: flex;
  justify-content: flex-end;
}

@media (max-width: 640px) {
  .dialog-template {
    padding: 1rem;
  }

  .dialog-template__panel {
    padding: 1.2rem;
  }

  .dialog-template__actions {
    justify-content: stretch;
  }
}
</style>
