<script setup lang="ts">
import type { AvatarConfig } from '~/composables/types/AvatarTypes'
import { DEFAULT_AVATAR_CONFIG } from '~/composables/types/AvatarTypes'
import AvatarImage from '~/components/avatar/avatarImage.vue'
import { useAvatar } from '~/composables/useAvatar'
import type { StudentProfileResponse } from '~/composables/types/StudentProfileTypes'

const { getMyAvatar } = useAvatar()
const { getMyProfile} = useStudentProfile()

const fallbackStats: StudentProfileResponse = {
  studentUid: '',
  studentName: 'Forhåndsvisning',
  score: 0,
  totalXp: 0,
  completedExercises: 0,
  unlockedAchievements: 0,
}

const avatar = ref(await getMyAvatar().catch(() => null));
const userStats = ref((await getMyProfile().catch(() => null)) ?? fallbackStats);

const resolvedAvatar = computed(() => avatar.value ?? DEFAULT_AVATAR_CONFIG)
</script>

<template>
  <div class="avatarElement">
    <div class="avatarPrint">
      <div class="avatarPhotoWindow">
        <div class="avatarPhotoInner">
          <AvatarImage :avatar="resolvedAvatar" :alt="userStats.studentName" />
        </div>
      </div>

      <p class="avatarUsername">{{ userStats.studentName }}</p>
      <p class="level">Level {{ userStats.totalXp / 300 }} </p>
      
    </div>
  </div>
</template>

<style scoped>
.avatarElement {
  width: 100%;
}

.avatarPrint {
  width: 100%;
  height: 100%;
  display: grid;
  grid-template-rows: minmax(0, 1fr) auto;
  gap: var(--space-3);
  padding: var(--space-4) var(--space-4) var(--space-5);
  border: var(--border-width-chunky) solid var(--palette-white);
  border-radius: var(--radius-2);
  background-color: white;
  box-shadow:
    0 10px 24px rgba(17, 24, 39, 0.18),
    inset 0 1px 0 rgba(255, 255, 255, 0.92);
}

.avatarPhotoWindow {
  position: relative;
  min-height: 0;
  aspect-ratio: 1 / 1;
  overflow: hidden;
  border: var(--border-width-thin) solid color-mix(in srgb, var(--palette-gray-300) 70%, white);
  border-radius: calc(var(--radius-2) - 2px);
  background-color: rgb(212, 212, 212);
}

.avatarPhotoInner {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: flex-start;
  justify-content: center;
  overflow: visible;
}

.avatarPhotoInner :deep(.avatarImage) {
  width: 100%;
  height: 100%;
  transform: translateY(8%);
}

.avatarUsername {
  margin: 0;
  padding-inline: var(--space-2);
  color: var(--palette-gray-900);
  font-family: var(--font-family-serif), serif;
  font-size: var(--font-size-18);
  font-weight: var(--font-weight-semibold);
  line-height: var(--font-leading-18);
  letter-spacing: var(--font-tracking-wide);
  text-align: center;
  text-wrap: balance;
}

.level {
  margin: 0;
  color: var(--palette-gray-500);
  font-size: var(--font-size-14);
  line-height: var(--font-leading-14);
  font-weight: var(--font-weight-medium);
  text-align: center;
}
</style>
