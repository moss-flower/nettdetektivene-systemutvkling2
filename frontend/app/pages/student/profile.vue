<script setup lang="ts">
import { routePaths } from '~/composables/utils/routePaths'
import { useStudentProfile } from '~/composables/useStudentProfile'
import { getLevel } from '~/composables/utils/studentProgress'

const { getMyProfile } = useStudentProfile()

const studentName = ref('Laster...')
const score = ref(0)

const isLoading = ref(true)
const profileError = ref('')

const progress = computed(() => getLevel(score.value))
const progressWidth = computed(() => `${progress.value.progressPercent}%`)

onMounted(async () => {
  isLoading.value = true
  profileError.value = ''

  try {
    const profile = await getMyProfile()
    studentName.value = profile.studentName
    score.value = profile.score
  } catch {
    profileError.value = 'Kunne ikke laste profildata akkurat nå.'
  } finally {
    isLoading.value = false
  }
})
</script>

<template>
  <section class="profilePage">
    <header class="pageHeader">
      <UiStudentHomeBackButton class="backLink" label="Tilbake" />
      <p v-if="isLoading" class="statusText">Laster profil...</p>
      <p v-if="profileError" class="errorText">{{ profileError }}</p>
    </header>

    <div class="profileCard">
        <p class="eyebrow">Min profil</p>
      <p class="subtitle">Oversikt over navn og poengsum.</p>
      

      <div class="infoBlock">
        <p class="label">Navn</p>
        <p class="value">{{ studentName }}</p>
      </div>

      <div class="infoBlock">
        <p class="label">Score</p>
        <p class="value">{{ score }}</p>
      </div>

      <div class="infoBlock">
        <p class="label">Level</p>
        <p class="value">{{ progress.level }}</p>
      </div>

      <div class="progressSection">
        <div class="progressHeader">
          <span>{{ progress.currentXp }} / {{ progress.nextLevelXp }} XP</span>
          <span>{{ progress.xpToNextLevel }} XP til level {{ progress.level + 1 }}</span>
        </div>
        <div class="progressBar" role="progressbar" :aria-valuenow="progress.progressPercent" aria-valuemin="0" aria-valuemax="100">
          <div class="progressFill" :class="{ hasProgress: progress.progressPercent > 0 }" :style="{ width: progressWidth }"></div>
        </div>
      </div>

      <div class="actions">
        <NuxtLink class="secondaryButton" :to="routePaths.studentAchievements">Achievements</NuxtLink>
      </div>
    </div>
  </section>
</template>

<style scoped>
.profilePage {
  width: 100%;
  max-width: 900px;
  margin: 0 auto;
  padding: var(--space-4);
}

.pageHeader {
  margin-bottom: var(--space-4);
}

.backLink {
  display: inline-flex;
  margin-bottom: 0.75rem;
}

.eyebrow {
  color: var(--primary);
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  margin-bottom: 0.5rem;
}

h1 {
  margin: 0 0 0.5rem 0;
  color: var(--text);
}

.subtitle {
  margin: 0;
  color: var(--muted);
}

.statusText,
.errorText {
  margin: 0;
  font-size: 0.95rem;
}

.statusText {
  color: var(--muted);
}

.errorText {
  color: #b42318;
}

.profileCard {
  display: grid;
  gap: 1rem;
  padding: var(--space-4);
  border: 1px solid var(--border);
  border-radius: 1rem;
  background: linear-gradient(180deg, #ffffff 0%, #f8fbff 100%);
}

.avatarWrap {
  width: 88px;
  height: 88px;
  display: grid;
  place-items: center;
  border-radius: 999px;
  background: var(--primary);
  color: #111827;
  font-size: 2.25rem;
  font-weight: 700;
}

.infoBlock {
  display: grid;
  gap: 0.25rem;
}

.label {
  color: var(--muted);
  font-size: 0.9rem;
}

.value {
  font-size: 1.2rem;
  font-weight: 700;
  color: var(--text);
}

.progressSection {
  display: grid;
  gap: 0.5rem;
}

.progressHeader {
  display: flex;
  justify-content: space-between;
  gap: 1rem;
  color: var(--muted);
}

.progressBar {
  width: 100%;
  height: 0.9rem;
  border-radius: 999px;
  overflow: hidden;
  background: #e8edf4;
  border: 1px solid #c7d2e1;
}

.progressFill {
  height: 100%;
  background: linear-gradient(90deg, #2563eb, #22c55e);
}

.progressFill.hasProgress {
  min-width: 0.35rem;
}

.actions {
  display: flex;
  gap: 0.75rem;
  margin-top: 0.5rem;
}

.secondaryButton {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 0.8rem 1.1rem;
  border-radius: 0.6rem;
  border: 1px solid var(--primary);
  background: transparent;
  color: var(--primary);
  font-weight: 700;
}
</style>
