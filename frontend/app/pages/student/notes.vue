<template>
  <section class="pageWrap">
    <NuxtLink :to="routePaths.studentHome" class="returnButton">Tilbake til hjem</NuxtLink>
    <h1>notes</h1>
  </section>
</template>

<script setup lang="ts">
import { routePaths } from "~/composables/utils/routePaths";
</script>

<style scoped>
.pageWrap {
  position: relative;
  min-height: 100vh;
}

.returnButton {
  position: absolute;
  top: 1rem;
  left: 1rem;
  border: 1px solid #d4d4d8;
  border-radius: 999px;
  padding: 0.45rem 0.9rem;
  text-decoration: none;
  color: #18181b;
  background: #fff;
  font-weight: 600;
}
</style>