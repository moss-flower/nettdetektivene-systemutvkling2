<script setup lang="ts">
import avatarImage from '~/assets/images/elev_avatar.png'

const props = withDefaults(defineProps<{
  username?: string
}>(), {
  username: 'Detektiv Navn',
})
</script>

<template>
  <div class="avatarBadge">
    <img class="avatarImage" :src="avatarImage" alt="Elev avatar">
    <p class="avatarName">{{ props.username }}</p>
  </div>
</template>

<style scoped>
.avatarBadge {
  display: grid;
  justify-items: center;
  gap: var(--space-2);
}

.avatarImage {
  width: 4.5rem;
  height: 4.5rem;
  object-fit: cover;
  border: var(--border-width-thick) solid var(--color-brand-primary);
  border-radius: var(--radius-2);
  background: var(--palette-white);
}

.avatarName {
  margin: 0;
  font-size: var(--font-size-16);
  font-weight: var(--font-weight-bold);
  color: var(--color-text-primary);
  text-align: center;
}
</style>
