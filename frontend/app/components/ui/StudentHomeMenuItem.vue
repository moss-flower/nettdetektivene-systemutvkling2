<script setup lang="ts">
const props = withDefaults(
  defineProps<{
    imagePath?: string;
    alt?: string;
    rotation?: number;
    onClick?: () => void;
  }>(),
  {
    imagePath: "",
    alt: "",
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
  </button>
</template>

<style scoped>
.menu-item {
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
</style>
