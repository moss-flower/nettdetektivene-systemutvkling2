<script setup lang="ts">
import {NuxtLink} from '#components'
import type {RouteLocationRaw} from 'vue-router'
import DialogTemplate from '~/components/gameplay/shared/DialogTemplate.vue'
import datatyvenSprite from '~/assets/images/datatyven.png'

defineProps<{
  to?: RouteLocationRaw
}>()

const emit = defineEmits<{
  (e: 'next'): void
}>()
</script>

<template>
  <DialogTemplate aria-label="Datatyven er beseiret">
    <template #badge>
      <p class="boss-victory__badge">Datatyven er beseiret</p>
    </template>

    <div class="boss-victory__hero">
      <img
          :src="datatyvenSprite"
          alt="Datatyven beseiret"
          class="boss-victory__sprite"
      >
    </div>

    <h2 class="boss-victory__title">Du beseiret Datatyven!</h2>
    <p class="boss-victory__message">
      Du brukte alt du har lært til å hindre datatyven fra å stjele dataen din! Med din nye kunnskap
      vil datatyver frykte deg!
    </p>

    <template #actions>
      <component
          :is="to ? NuxtLink : 'button'"
          class="boss-victory__button"
          :to="to"
          :type="to ? undefined : 'button'"
          @click="to ? undefined : emit('next')"
      >
        Tilbake til stasjonskartet
      </component>
    </template>
  </DialogTemplate>
</template>

<style scoped>
.boss-victory__badge {
  justify-self: start;
  padding: 0.35rem 0.8rem;
  border-radius: 999px;
  background: #fef3c7;
  color: #92400e;
  font-size: 0.88rem;
  font-weight: 700;
  letter-spacing: 0.04em;
  text-transform: uppercase;
}

.boss-victory__hero {
  position: relative;
  display: grid;
  place-items: center;
  padding: 1.2rem 0 0.4rem;
}

.boss-victory__sprite {
  width: clamp(10rem, 32vw, 14rem);
  height: auto;
  filter: grayscale(0.4) drop-shadow(0 12px 22px rgba(15, 23, 42, 0.25));
  transform: rotate(15deg);
  opacity: 0.85;
  animation: boss-victory-fall 0.9s ease-out both;
}

.boss-victory__title {
  margin: 0;
  font-size: 1.6rem;
  line-height: 1.1;
  color: #1f2937;
  text-align: center;
}

.boss-victory__message {
  margin: 0;
  font-size: 1rem;
  line-height: 1.5;
  color: #374151;
  text-align: center;
}

.boss-victory__button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 0.9rem 1.4rem;
  border: 0;
  border-radius: 999px;
  background: linear-gradient(90deg, #f59e0b, #ef4444);
  color: #fffdf9;
  font-weight: 800;
  letter-spacing: 0.02em;
  text-decoration: none;
  transition: transform 0.2s ease, filter 0.2s ease;
}

.boss-victory__button:hover,
.boss-victory__button:focus-visible {
  transform: translateY(-1px);
  filter: brightness(1.05);
  outline: none;
}

@keyframes boss-victory-fall {
  0% {
    transform: rotate(0deg) translateY(-30px);
    opacity: 0;
  }
  60% {
    transform: rotate(20deg) translateY(6px);
    opacity: 1;
  }
  100% {
    transform: rotate(15deg) translateY(0);
    opacity: 0.85;
  }
}

@media (max-width: 640px) {
  .boss-victory__button {
    width: 100%;
  }
}
</style>
