<script setup lang="ts">
withDefaults(defineProps<{
  ariaLabel?: string
}>(), {
  ariaLabel: 'Dialog',
})
</script>

<template>
  <div class="dialogTemplate">
    <div class="dialogTemplateBackdrop" />
    <div
      class="dialogTemplatePanel"
      role="dialog"
      aria-modal="true"
      :aria-label="ariaLabel"
    >
      <slot name="badge" />
      <div class="dialogTemplateContent">
        <slot />
      </div>
      <div v-if="$slots.actions" class="dialogTemplateActions">
        <slot name="actions" />
      </div>
    </div>
  </div>
</template>

<style scoped>
.dialogTemplate {
  position: fixed;
  inset: 0;
  z-index: 40;
  display: grid;
  place-items: center;
  padding: var(--space-6);
}

.dialogTemplateBackdrop {
  position: absolute;
  inset: 0;
  background: rgba(17, 24, 39, 0.34);
  backdrop-filter: blur(3px);
}

.dialogTemplatePanel {
  position: relative;
  width: min(100%, 32rem);
  display: grid;
  gap: 0.9rem;
  padding: 1.4rem;
  border-radius: 1.2rem;
  background: var(--color-bg-surface);
  box-shadow: 0 24px 70px rgba(15, 23, 42, 0.22);
}

.dialogTemplateContent {
  display: grid;
  gap: 0.9rem;
}

.dialogTemplateActions {
  display: flex;
  justify-content: flex-end;
}

</style>
