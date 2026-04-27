<script setup lang="ts">
import { computed, useSlots } from 'vue'
import type { RouteLocationRaw } from 'vue-router'

type Variant = 'primary' | 'reward' | 'ghost' | 'danger' | 'onLight'
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
    'ui-btn',
    `ui-btn--${props.variant}`,
    `ui-btn--${props.size}`,
  {
    'ui-btn--block': props.block,
    'ui-btn--loading': props.loading,
  },
])

const bindings = computed(() => {
  const shared: Record<string, unknown> = {
    class: classes.value,
    'aria-busy': props.loading || undefined,
    'aria-disabled': isInert.value || undefined,
    'data-variant': props.variant,
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
  const hasAriaLabel = typeof (props as any)['aria-label'] === 'string' && (props as any)['aria-label'].length > 0

  if (!hasText && !hasAriaLabel) {
    console.warn('[UiButton] Icon-only button is missing `Aria-label`. ' +
        'Screen readers will have no accessible name')
  }
}
</script>

<template>
  <component :is="tag" v-bind="bindings">
    <span v-if="loading" class="ui-btn__spinner" aria-hidden="true"/>
    <span v-else-if="$slots.iconLeft" class="ui-btn__icon ui-btn__icon--left">
      <slot name="iconLeft"/>
    </span>

    <span class="ui-btn__label">
      <slot />
    </span>

    <span v-if="$slots.iconRight && !loading" class="ui-btn__icon ui-btn__icon--right">
      <slot name="iconRight"/>
    </span>
  </component>
</template>

<style scoped>
.ui-btn {
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

.ui-btn--block {
  width: 100%;
}

/* -------- Sizes -------- */
.ui-btn--sm {
  height: var(--size-control-sm);
  padding-inline: var(--space-4);
  font-size: var(--font-size-14);
  border-radius: var(--radius-1);
}

.ui-btn--md {
  height: var(--size-control-md);
  padding-inline: var(--space-5);
  font-size: var(--font-size-16);
  border-radius: var(--radius-2);
}

.ui-btn--lg {
  height: var(--size-control-lg);
  padding-inline: var(--space-6);
  font-size: var(--font-size-18);
  border-radius: var(--radius-2);
}

/* -------- Variants -------- */
/* Primary */
.ui-btn--primary {
  background: var(--color-brand-primary);
  color: var(--color-text-onDark);
}

.ui-btn--primary:hover:not([aria-disabled='true']) {
  background: color-mix(in srgb, var(--color-brand-primary) 92%, white);
}

.ui-btn--primary:active:not([aria-disabled='true']) {
  box-shadow: var(--shadow-pressed);
}

/* reward */
.ui-btn--reward {
  background: var(--color-accent-reward);
  color: var(--color-text-primary);
}

.ui-btn--reward:hover:not([aria-disabled='true']) {
  background: var(--color-accent-reward-hover);
}

.ui-btn--reward:active:not([aria-disabled='true']) {
  background: var(--color-accent-reward-pressed);
  box-shadow: var(--shadow-pressed);
  /* Subtle pupil-deligth press, skipped under reduced motion via global rule */
  transform: scale(0.98);
}

/* Ghost */
.ui-btn--ghost {
  background: transparent;
  color: var(--color-text-primary);
  box-shadow: none;
}

.ui-btn--ghost:hover:not([aria-disabled='true']) {
  background: var(--color-bg-surface-hover);
}

.ui-btn--ghost:active:not([aria-disabled='true']) {
  box-shadow: var(--shadow-pressed);
}

/* onLight; cream/white pills on dark surface */
.ui-btn--onLight {
  background: var(--color-bg-onLight);
  color: var(--color-text-onLight);
  border: var(--border-width-hairline) solid var(--color-border-strong);
  border-radius: var(--radius-pill);
}

.ui-btn--onLight:hover:not([aria-disabled='true']) {
  background: var(--color-bg-onLight-hover);
}

.ui-btn--onLight:active:not([aria-disabled='true']) {
  box-shadow: var(--shadow-pressed);
}

/* Danger */
.ui-btn--danger {
  background: var(--color-semantic-error);
  color: var(--color-text-onDark);
}

.ui-btn--danger:hover:not([aria-disabled='true']) {
  background: var(--color-semantic-error-hover);
}

.ui-btn--danger:active:not([aria-disabled='true']) {
  box-shadow: var(--shadow-pressed);
}

/* -------- Disabled / Loading -------- */
.ui-btn[aria-disabled='true'],
.ui-btn:disabled {
  background: var(--color-disabled-bg);
  color: var(--color-disabled-text);
  cursor: not-allowed;
  box-shadow: none;
  transform: none;
}

.ui-btn--loading {
  cursor: progress;
}

/* -------- Icon + Spinner -------- */
.ui-btn__icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: var(--size-icon-sm);
  height: var(--size-icon-sm);
  flex: 0 0 auto;
}

.ui-btn--lg .ui-btn__icon {
  width: var(--size-icon-md);
  height: var(--size-icon-md);
}

.ui-btn__label {
  display: inline-flex;
  align-items: center;
}

.ui-btn__spinner {
  width: var(--size-icon-sm);
  height: var(--size-icon-sm);
  border-radius: var(--radius-pill);
  border: 2px solid currentColor;
  border-right-color: transparent;
  animation: ui-btn-spin 0.6s linear infinite;
}

@keyframes ui-btn-spin {
  to {
    transform: rotate(360deg);
  }
}
</style>