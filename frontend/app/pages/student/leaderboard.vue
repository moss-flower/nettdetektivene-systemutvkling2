<template>
  <section class="pageWrap">
    <h1>leaderboard</h1>
    <NuxtLink :to="routePaths.studentHome" class="returnButton">Tilbake til hjem</NuxtLink>
  </section>
</template>

<script setup lang="ts">
import { routePaths } from "~/composables/utils/routePaths";
</script>

<style scoped>
.pageWrap {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  min-height: 100vh;
}

.returnButton {
  width: fit-content;
  border: 1px solid #d4d4d8;
  border-radius: 999px;
  padding: 0.45rem 0.9rem;
  text-decoration: none;
  color: #18181b;
  background: #fff;
  font-weight: 600;
}
</style>