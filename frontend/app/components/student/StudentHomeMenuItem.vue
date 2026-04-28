<script setup lang="ts">
const props = withDefaults(
  defineProps<{
    imagePath?: string;
    alt?: string;
    tooltipText?: string;
    tooltipPlacement?: "top" | "bottom";
    rotation?: number;
    onClick?: () => void;
  }>(),
  {
    imagePath: "",
    alt: "",
    tooltipText: "",
    tooltipPlacement: "top",
    rotation: 0,
    onClick: () => {},
  },
);

function handleClick(event: MouseEvent) {
  event.preventDefault();
  props.onClick?.();
}
</script>

<template>
  <button type="button" class="menuItem" @click="handleClick">
    <img
      :src="props.imagePath"
      :alt="props.alt"
      class="menuItemImage"
      :style="{ transform: `rotate(${props.rotation}deg)` }"
    >
    <span
      v-if="props.tooltipText"
      :class="[
        'menuItemTooltip',
        props.tooltipPlacement === 'top' ? 'itemTooltipTop' : 'itemTooltipBottom',
      ]"
    >{{ props.tooltipText }}</span>
  </button>
</template>

<style scoped>
.menuItem {
  position: relative;
  isolation: isolate;
  width: 100%;
  display: block;
  padding: 0;
  border: 0;
  background: transparent;
  appearance: none;
  cursor: pointer;
  transition: transform var(--transition);
}

.menuItem:hover {
  transform: translateY(-4px);
}

.menuItem:focus-visible {
  outline: var(--border-width-thick) solid color-mix(in srgb, var(--color-brand-primary) 65%, white);
  outline-offset: 0.35rem;
  border-radius: var(--radius-2);
}

.menuItemImage {
  width: 100%;
  height: auto;
  display: block;
  filter: drop-shadow(0 0.9rem 0.8rem rgb(48 24 9 / 0.22));
  transition: filter var(--transition);
}

.menuItem:hover .menuItemImage,
.menuItem:focus-visible .menuItemImage {
  filter:
    drop-shadow(0 0 0.5rem var(--primary-hover));
}

.menuItemTooltip {
  position: absolute;
  left: 50%;
  z-index: 20;
  filter: none;
  width: max-content;
  max-width: min(15rem, calc(100vw - 2rem));
  padding: var(--space-2) var(--space-3);
  border: var(--border-width-thin) solid rgb(98 163 120 / 0.48);
  border-radius: var(--radius-2) var(--radius-2) var(--radius-2) 0.45rem;
  background: linear-gradient(180deg, #e9ffd9 0%, #bde9a3 100%);
  color: #244b2d;
  font-size: 0.92rem;
  line-height: 1.35;
  text-align: left;
  opacity: 0;
  pointer-events: none;
  transition:
    opacity 160ms ease,
    transform 160ms ease;
}

.menuItemTooltip::after {
  content: "";
  position: absolute;
  left: 50%;
  top: 100%;
  filter: none;
  width: 0.85rem;
  height: 0.85rem;
  border-right: var(--border-width-thin) solid rgb(98 163 120 / 0.48);
  border-bottom: var(--border-width-thin) solid rgb(98 163 120 / 0.48);
  background: #bde9a3;
}

.itemTooltipTop {
  bottom: calc(100%);
  transform: translateX(-50%) translateY(0.35rem) rotate(-2deg);
  transform-origin: bottom center;
}

.itemTooltipTop::after {
  top: 100%;
  transform: translateX(-50%) translateY(-55%) rotate(45deg);
}

.itemTooltipBottom {
  top: calc(100% - var(--space-8));
  transform: translateX(-50%) translateY(-0.35rem) rotate(-2deg);
  transform-origin: top center;
}

.itemTooltipBottom::after {
  top: 0;
  transform: translateX(-50%) translateY(-45%) rotate(225deg);
}

.menuItem:hover .menuItemTooltip,
.menuItem:focus-visible .menuItemTooltip {
  opacity: 1;
}

.menuItem:hover .itemTooltipTop,
.menuItem:focus-visible .itemTooltipTop,
.menuItem:hover .itemTooltipBottom,
.menuItem:focus-visible .itemTooltipBottom {
  transform: translateX(-50%) translateY(0) rotate(-2deg);
}
</style>
