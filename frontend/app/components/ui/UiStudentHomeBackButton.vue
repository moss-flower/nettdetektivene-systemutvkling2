<script setup lang="ts">
import leftArrowImage from '~/assets/images/leftArrow.png'
import { routePaths } from "~/composables/utils/routePaths";
import type { RouteLocationRaw } from "vue-router";

type Variant = "primary" | "reward" | "ghost" | "danger" | "onLight";
type Size = "sm" | "md" | "lg";

const props = withDefaults(
  defineProps<{
    label?: string;
    disabled?: boolean;
    ariaLabel?: string;
    to?: RouteLocationRaw;
  }>(),
  {
    label: "",
    variant: "onLight",
    size: "sm",
    block: false,
    disabled: false,
    ariaLabel: "Tilbake til elevhjem",
    to: routePaths.studentHome,
  },
);
</script>

<template>
  <NuxtLink
    class="homeBackButton"
    :class="{ 'backButtonDisabled': props.disabled }"
    :to="props.disabled ? undefined : props.to"
    :aria-label="props.ariaLabel"
  >
    <img class="backButtonIcon" :src="leftArrowImage" alt="" aria-hidden="true">
    <span class="srOnly">{{ props.label }}</span>
  </NuxtLink>
</template>

<style scoped>
.homeBackButton {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 4.5rem;
  height: 4.5rem;
  border-radius: var(--radius-pill);
  border: var(--border-width-hairline) solid var(--palette-cork-900);
  background: var(--palette-cream-100);
  box-shadow: var(--shadow-2);
}

.backButtonIcon {
  display: block;
  width: 2.6rem;
  height: auto;
}

.backButtonDisabled {
  pointer-events: none;
  opacity: 0.6;
}
</style>
