<script setup lang="ts">
import AvatarProfileCard from '~/components/avatar/AvatarProfileCard.vue'

const { isAvatarProfileOpen, closeAvatarProfile } = useAvatarProfilePopup()

function handleKeydown(event: KeyboardEvent) {
  if (event.key === 'Escape') {
    closeAvatarProfile()
  }
}

watch(isAvatarProfileOpen, (isOpen) => {
  if (!import.meta.client) return

  if (isOpen) {
    window.addEventListener('keydown', handleKeydown)
    return
  }

  window.removeEventListener('keydown', handleKeydown)
})

onBeforeUnmount(() => {
  if (!import.meta.client) return
  window.removeEventListener('keydown', handleKeydown)
})
</script>

<template>
  <Teleport to="body">
    <div
      v-if="isAvatarProfileOpen"
      class="avatarProfilePopup"
      role="dialog"
      aria-modal="true"
      aria-label="Profil"
    >
      <button class="avatarProfilePopup__backdrop" type="button" aria-label="Lukk profil" @click="closeAvatarProfile" />
      <div class="avatarProfilePopup__panel">
        <AvatarProfileCard closable @close="closeAvatarProfile" />
      </div>
    </div>
  </Teleport>
</template>

<style scoped>
.avatarProfilePopup {
  position: fixed;
  inset: 0;
  z-index: 80;
  display: grid;
  place-items: center;
  padding: clamp(1rem, 3vw, 2.5rem);
}

.avatarProfilePopup__backdrop {
  position: absolute;
  inset: 0;
  border: 0;
  background: rgb(17 24 39 / 0.62);
  cursor: pointer;
}

.avatarProfilePopup__panel {
  position: relative;
  z-index: 1;
  width: min(980px, 100%);
  max-height: min(760px, 92dvh);
  overflow: auto;
  border-radius: var(--radius-4);
  box-shadow: 0 24px 70px rgb(0 0 0 / 0.36);
}

.avatarProfilePopup__panel :deep(.profilePage) {
  min-height: min(720px, 92dvh);
}
</style>
