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
  <button type="button" class="menu-item" @click="handleClick">
    <img
      :src="props.imagePath"
      :alt="props.alt"
      class="menu-item__image"
      :style="{ transform: `rotate(${props.rotation}deg)` }"
    >
    <span
      v-if="props.tooltipText"
      :class="[
        'menu-item__tooltip',
        `menu-item__tooltip--${props.tooltipPlacement}`,
      ]"
    >{{ props.tooltipText }}</span>
  </button>
</template>

<style scoped>
.menu-item {
  position: relative;
  width: 100%;
  display: block;
  padding: 0;
  border: 0;
  background: transparent;
  appearance: none;
  cursor: pointer;
  transition: transform var(--transition);
}

.menu-item:hover {
  transform: translateY(-4px);
}

.menu-item:focus-visible {
  outline: 3px solid color-mix(in srgb, var(--primary) 65%, white);
  outline-offset: 0.35rem;
  border-radius: 1rem;
}

.menu-item__image {
  width: 100%;
  height: auto;
  display: block;
  filter: drop-shadow(0 0.9rem 0.8rem rgb(48 24 9 / 0.22));
}

.menu-item__tooltip {
  position: absolute;
  left: 50%;
  z-index: 20;
  width: max-content;
  max-width: min(15rem, calc(100vw - 2rem));
  padding: 0.5rem 0.75rem;
  border: 2px solid rgb(154 112 60 / 0.45);
  border-radius: 1rem 1rem 1rem 0.45rem;
  background: linear-gradient(180deg, #fff8d9 0%, #f5e4a8 100%);
  color: #5f3b17;
  font-size: 0.92rem;
  line-height: 1.35;
  text-align: left;
  box-shadow:
    0 1rem 1.8rem rgb(71 43 17 / 0.22),
    0 0.3rem 0.5rem rgb(71 43 17 / 0.12),
    inset 0 1px 0 rgb(255 255 255 / 0.65);
  opacity: 0;
  pointer-events: none;
  transition:
    opacity 160ms ease,
    transform 160ms ease;
}

.menu-item__tooltip::after {
  content: "";
  position: absolute;
  left: 50%;
  top: 100%;
  width: 0.85rem;
  height: 0.85rem;
  border-right: 2px solid rgb(154 112 60 / 0.45);
  border-bottom: 2px solid rgb(154 112 60 / 0.45);
  background: #f5e4a8;
  box-shadow: 0 0.45rem 0.75rem rgb(71 43 17 / 0.14);
}

.menu-item__tooltip--top {
  bottom: calc(100%);
  transform: translateX(-50%) translateY(0.35rem) rotate(-2deg);
  transform-origin: bottom center;
}

.menu-item__tooltip--top::after {
  top: 100%;
  transform: translateX(-50%) translateY(-55%) rotate(45deg);
}

.menu-item__tooltip--bottom {
  top: calc(100% - 2rem);
  transform: translateX(-50%) translateY(-0.35rem) rotate(-2deg);
  transform-origin: top center;
}

.menu-item__tooltip--bottom::after {
  top: 0;
  transform: translateX(-50%) translateY(-45%) rotate(225deg);
}

.menu-item:hover .menu-item__tooltip,
.menu-item:focus-visible .menu-item__tooltip {
  opacity: 1;
}

.menu-item:hover .menu-item__tooltip--top,
.menu-item:focus-visible .menu-item__tooltip--top,
.menu-item:hover .menu-item__tooltip--bottom,
.menu-item:focus-visible .menu-item__tooltip--bottom {
  transform: translateX(-50%) translateY(0) rotate(-2deg);
}
</style>
