<script setup lang="ts">
import { computed, useSlots } from 'vue'
import type { RouteLocationRaw } from 'vue-router'

type Variant = 'primary' | 'reward' | 'ghost' | 'danger'
type Size = 'sm' | 'md' | 'lg'

const props = withDefaults(
    defineProps<{
      variant?: Variant
      size?: Size
      type?: 'button' | 'submit' | 'reset'
      disabled?: boolean
      loading?: boolean
      block?: boolean
      href?: string
      to?: RouteLocationRaw
    }>(),
    {
      variant: 'primary',
      size: 'md',
      type: 'button',
      disabled: false,
      loading: false,
      block: false,
    },
)

const emit = defineEmits<{
  (e: 'click', event: MouseEvent): void
}>()

const slots = useSlots()

// Polymorphic: <NuxtLink> if to, <a> if href, otherwise button
const tag = computed(() => {
  if (props.to) return resolveComponent('NuxtLink') as unknown as string
  if (props.href) return 'a'
  return 'button'
})

const isExternal = computed(() => !!props.href && /^https?:\/\//i.test(props.href))

const isInert = computed(() => props.disabled || props.loading)

const classes = computed(() => [
    'uiBtn',
    `uiBtn${props.variant}`,
    `uiBtn${props.size}`,
  {
    'uiBtnBlock': props.block,
    'uiBtnLoading': props.loading,
  },
])

const bindings = computed(() => {
  const shared: Record<string, unknown> = {
    class: classes.value,
    'ariaBusy': props.loading || undefined,
    'ariaDisabled': isInert.value || undefined,
    'dataVariant': props.variant,
    onClick: (e: MouseEvent) => {
      if (isInert.value) {
        e.preventDefault()
        e.stopImmediatePropagation()
        return
      }
      emit('click', e)
    },
  }

  if (props.to) {
    return {
      ...shared,
      to: props.to,
    }
  }

  if (props.href) {
    return {
      ...shared,
      href: props.href,
      ...(isExternal.value && {
        target: '_blank',
        rel: 'noopener noreferrer',
      }),
    }
  }

  return {
    ...shared,
    type: props.type,
    disabled: props.disabled,
  }
})

// Dev-only (a11y nudge): icon only buttons must be labeled
if (import.meta.dev) {
  const hasText = !!slots.default
  const hasAriaLabel = typeof (props as any)['ariaLabel'] === 'string' && (props as any)['ariaLabel'].length > 0

  if (!hasText && !hasAriaLabel) {
    console.warn('[UiButton] Icon-only button is missing `Aria-label`. ' +
        'Screen readers will have no accessible name')
  }
}
</script>

<template>
  <component :is="tag" v-bind="bindings">
    <span v-if="loading" class="uiBtnSpinner" aria-hidden="true"/>
    <span v-else-if="$slots.iconLeft" class="uiBtnIcon btnIconLeft">
      <slot name="iconLeft"/>
    </span>

    <span class="uiBtnLabel">
      <slot />
    </span>

    <span v-if="$slots.iconRight && !loading" class="uiBtnIcon btnIconRight">
      <slot name="iconRight"/>
    </span>
  </component>
</template>

<style scoped>
.uiBtn {
  /* layout */
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-2);

  /* Typography */
  font-family: var(--font-family-display), sans-serif;
  font-weight: var(--font-weight-semibold);
  line-height: 1;
  text-decoration: none;
  white-space: nowrap;

  /* Chrome */
  border: 0;
  cursor: pointer;
  user-select: none;
  box-shadow: var(--shadow-1);
  transition:
    background-color var(--transition-fast),
    color var(--transition-fast),
    box-shadow var(--transition-fast);
}

.uiBtnBlock {
  width: 100%;
}

/* -------- Sizes -------- */
.uiBtnSm {
  height: var(--size-control-sm);
  padding-inline: var(--space-4);
  font-size: var(--font-size-14);
  border-radius: var(--radius-1);
}
.uiBtnMd {
  height: var(--size-control-md);
  padding-inline: var(--space-5);
  font-size: var(--font-size-16);
  border-radius: var(--radius-2);
}
.uiBtnLg {
  height: var(--size-control-lg);
  padding-inline: var(--space-6);
  font-size: var(--font-size-18);
  border-radius: var(--radius-2);
}

/* -------- Variants -------- */
/* Primary */
.uiBtnPrimary {
  background: var(--color-brand-primary);
  color: var(--color-text-onDark);
}
.uiBtnPrimary:hover:not([aria-disabled='true']) {
  background: color-mix(in srgb, var(--color-brand-primary) 92%, white);
}

.uiBtnPrimary:active:not([aria-disabled='true']) {
  box-shadow: var(--shadow-pressed);
}

/* reward */
.uiBtnReward {
  background: var(--color-accent-reward);
  color: var(--color-text-primary);
}
.uiBtnReward:hover:not([aria-disabled='true']) {
  background: var(--color-accent-reward-hover);
}
.uiBtnReward:active:not([aria-disabled='true']) {
  background: var(--color-accent-reward-pressed);
  box-shadow: var(--shadow-pressed);
  /* Subtle pupil-deligth press, skipped under reduced motion via global rule */
  transform: scale(0.98);
}

/* Ghost */
.uiBtnGhost {
  background: transparent;
  color: var(--color-text-primary);
  box-shadow: none;
}
.uiBtnGhost:hover:not([aria-disabled='true']) {
  background: var(--color-bg-surface-hover);
}
.uiBtnGhost:active:not([aria-disabled='true']) {
  box-shadow: var(--shadow-pressed);
}

/* Danger */
.uiBtnDanger {
  background: var(--color-semantic-error);
  color: var(--color-text-onDark);
}
.uiBtnDanger:hover:not([aria-disabled='true']) {
  background: var(--color-semantic-error-hover);
}
.uiBtnDanger:active:not([aria-disabled='true']) {
  box-shadow: var(--shadow-pressed);
}

/* -------- Disabled / Loading -------- */
.uiBtn[aria-disabled='true'],
.uiBtn:disabled {
  background: var(--color-disabled-bg);
  color: var(--color-disabled-text);
  cursor: not-allowed;
  box-shadow: none;
  transform: none;
}

.uiBtnLoading {
  cursor: progress;
}

/* -------- Icon + Spinner -------- */
.uiBtnIcon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: var(--size-icon-sm);
  height: var(--size-icon-sm);
  flex: 0 0 auto;
}
.uiBtnLg .uiBtnIcon {
  width: var(--size-icon-md);
  height: var(--size-icon-md);
}

.uiBtnLabel {
  display: inline-flex;
  align-items: center;
}

.uiBtnSpinner {
  width: var(--size-icon-sm);
  height: var(--size-icon-sm);
  border-radius: var(--radius-pill);
  border: var(--border-width-thin) solid currentColor;
  border-right-color: transparent;
  animation: ui-btn-spin 0.6s linear infinite;
}

@keyframes ui-btn-spin {
  to {
    transform: rotate(360deg);
  }
}
</style>