<script setup lang="ts">
import { routePaths } from "~/composables/utils/routePaths";

type Variant = "primary" | "reward" | "ghost" | "danger";
type Size = "sm" | "md" | "lg";

const props = withDefaults(
  defineProps<{
    label?: string;
    variant?: Variant;
    size?: Size;
    block?: boolean;
    disabled?: boolean;
    loading?: boolean;
    iconLeft?: boolean;
    ariaLabel?: string;
  }>(),
  {
    label: "Tilbake til hjem",
    variant: "ghost",
    size: "sm",
    block: false,
    disabled: false,
    loading: false,
    iconLeft: true,
    ariaLabel: "Tilbake til elevhjem",
  },
);
</script>

<template>
  <UiButton
    :to="routePaths.studentHome"
    :variant="props.variant"
    :size="props.size"
    :block="props.block"
    :disabled="props.disabled"
    :loading="props.loading"
    :aria-label="props.ariaLabel"
  >
    <template v-if="props.iconLeft" #iconLeft>&larr;</template>
    {{ props.label }}
  </UiButton>
</template>
