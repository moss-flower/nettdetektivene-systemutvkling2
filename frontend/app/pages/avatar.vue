<script setup lang="ts">
import type { AvatarConfig } from '~/composables/types/AvatarTypes';
import AvatarImage from '~/components/avatar/avatarImage.vue';

const mockAvatar: AvatarConfig = {
  hairStyle: 'SHORT',
  hairColor: 'BROWN',
  outfit: 'SHERLOCK',
  eyeColor: 'BLUE',
  skinColor: 'COLOR1',
};
</script>


<template>
  <section class="avatarPage">
    <div class="avatarFrame">
      <AvatarElement :avatar="mockAvatar" username="Detektiv test" alt="Mock avatar" />
    </div>
  </section>
</template>

<style scoped>
.avatarPage {
  min-height: 100dvh;
  display: grid;
  place-items: center;
  padding: var(--space-6);
}

</style>
