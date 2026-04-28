<template>
  <canvas id="game-confetti-canvas" class="game-confetti-canvas" aria-hidden="true" />
  <NuxtPage />
</template>

<style>
.game-confetti-canvas {
  position: fixed;
  inset: 0;
  width: 100vw;
  height: 100vh;
  pointer-events: none;
  z-index: 9999;
}
</style>
