<script setup lang="ts">
import { useStudentProfile } from '~/composables/useStudentProfile'
import { useAvatar } from '~/composables/useAvatar'
import { getLevel } from '~/composables/utils/studentProgress'
import { routePaths } from '~/composables/utils/routePaths'
import { useAvatarProfilePopup } from '~/composables/useAvatarProfilePopup'

defineProps<{
  closable?: boolean
}>()

const emit = defineEmits<{
  close: []
}>()

const { getMyProfile, updateUsername } = useStudentProfile()
const { getMyAvatar } = useAvatar()
const { closeAvatarProfile } = useAvatarProfilePopup()

const studentName = ref('Laster...')
const totalXp = ref(0)
const isLoading = ref(true)
const profileError = ref('')
const isEditing = ref(false)
const pendingName = ref('')
const avatar = ref()

const initial = computed(() => studentName.value.charAt(0).toUpperCase())
const progress = computed(() => getLevel(totalXp.value))
const progressPercentDisplay = computed(() => Math.floor(progress.value.progressPercent))

function closeProfile() {
  emit('close')
}

function startEditing() {
  pendingName.value = studentName.value
  isEditing.value = true
}

function cancelEditing() {
  isEditing.value = false
  pendingName.value = ''
}

async function saveEditing() {
  const newName = pendingName.value.trim()
  if (!newName) return

  try {
    const response = await updateUsername(newName)
    studentName.value = response.username
    cancelEditing()
  } catch {
    profileError.value = 'Kunne ikke oppdatere navnet. Prøv igjen senere.'
  }
}

onMounted(async () => {
  isLoading.value = true
  profileError.value = ''

  try {
    const profile = await getMyProfile()
    studentName.value = profile.studentName
    totalXp.value = profile.totalXp
    avatar.value = await getMyAvatar();
  } catch {
    profileError.value = 'Kunne ikke laste profildata akkurat nå.'
  } finally {
    isLoading.value = false
  }
})

</script>

<template>
  <section class="profilePage">
    <button
      v-if="closable"
      class="profileBackButton"
      type="button"
      aria-label="Lukk profil"
      @click="closeProfile"
    >
      Tilbake
    </button>

    <header class="pageHeader">
      <p v-if="isLoading" class="statusText">Laster profil...</p>
      <p v-if="profileError" class="errorText">{{ profileError }}</p>
    </header>

    <article class="idCard">
      <div class="idCard__left">
        <NuxtLink 
          :to="routePaths.studentEditAvatar" 
          class="idCard__avatarLink"
          @click="closeAvatarProfile()"
        >
          <AvatarImage :avatar="avatar" :alt="studentName" />
        </NuxtLink>
      </div>

      <div class="idCard__right">
        <h2 class="idCard__title">Detektiv</h2>
        <div class="idCard__field" v-if="!isEditing">
          <p class="idCard__fieldLabel">Navn</p>
          <p class="idCard__fieldValue">{{ studentName }}</p>
        </div>
        <div class="idCard__field" v-else>
          <input
            class="idCard__input"
            type="text"
            v-model="pendingName"
            autofocus
          />
          <p class="idCard__fieldLabel">Navn</p>
        </div>

        <p class="idCard__levelHeading">Level: {{ progress.level }} </p>

        <div class="idCard__xpRow">
          <span class="idCard__xpText">{{ totalXp }} / {{ progress.nextLevelXp }} XP</span>
          <span class="idCard__xpPercent">{{ progressPercentDisplay }}%</span>
        </div>
        <div class="idCard__progressBar" role="progressbar"
             :aria-valuenow="progress.progressPercent" aria-valuemin="0" aria-valuemax="100">
          <div class="idCard__progressFill" :style="{ width: `${progress.progressPercent}%` }"></div>
        </div>
      </div>

      <div class="idCard__actions">
        <NuxtLink class="idCard__actionLink" to="/student/achievements">Medaljer</NuxtLink>
        <template v-if="!isEditing">
          <button class="idCard__actionLink idCard__actionBtn" type="button" @click="startEditing">Rediger</button>
        </template>
        <template v-else>
          <button class="idCard__actionLink idCard__actionBtn" type="button" @click="cancelEditing">Avbryt</button>
          <button class="idCard__actionLink idCard__actionBtn idCard__actionBtn--primary" type="button" @click="saveEditing">Lagre</button>
        </template>
      </div>
    </article>
  </section>
</template>

<style scoped>
.profilePage {
  --profile-wall: var(--palette-wood-900);
  --profile-wall-seam: var(--palette-overlay-warm-glow);
  --profile-wall-glow: var(--palette-overlay-warm-glow);
  --profile-text-onWall: var(--palette-cream-100);
  --profile-status-text: var(--palette-cream-300);
  --profile-error-text: var(--palette-red-600);
  --id-border: var(--palette-cork-900);
  --id-bg-top: var(--palette-cream-100);
  --id-bg-bottom: var(--palette-cream-200);
  --id-shadow: var(--palette-overlay-dark-deep);
  --id-inner-ring: var(--palette-overlay-cork-medium);
  --id-title: var(--palette-cork-900);
  --id-label: var(--palette-cork-580);
  --id-value: var(--palette-cork-900);
  --id-field-rule: var(--palette-cork-500);
  --id-avatar-bg: var(--palette-gray-200);
  --id-avatar-border: var(--palette-cork-500);
  --id-avatar-text: var(--palette-cork-700);
  --id-progress-track: var(--palette-gray-200);
  --id-progress-fill: var(--palette-gold-500);
  --id-action-border: var(--palette-cork-900);
  --id-action-text: var(--palette-cork-900);
  --id-action-hover: var(--palette-overlay-cork-soft);

  position: relative;
  min-height: 100%;
  width: 100%;
  display: grid;
  gap: var(--space-6);
  justify-items: center;
  align-content: center;
  padding: var(--layout-page-padding);
  border-radius: var(--radius-2);
  color: var(--profile-text-onWall);
  background:
    radial-gradient(ellipse 70% 50% at 50% 25%, var(--profile-wall-glow), transparent 70%),
    repeating-linear-gradient(90deg, transparent 0, transparent 200px, var(--profile-wall-seam) 200px, var(--profile-wall-seam) 201px),
    var(--profile-wall);
  overflow-x: hidden;
}

.profileBackButton {
  position: absolute;
  top: var(--space-5);
  left: var(--space-5);
  z-index: 2;
  min-height: var(--size-control-md);
  padding: var(--space-3) var(--space-4);
  border: var(--border-width-thin) solid var(--palette-cork-900);
  border-radius: var(--radius-pill);
  background: var(--palette-cream-100);
  color: var(--palette-cork-900);
  box-shadow: var(--shadow-2);
  cursor: pointer;
  font-family: var(--font-family-body), sans-serif;
  font-weight: var(--font-weight-bold);
}

.pageHeader {
  width: 100%;
  max-width: 680px;
  display: grid;
  gap: var(--space-2);
  justify-items: start;
}

.statusText {
  margin: 0;
  color: var(--profile-status-text);
  font-family: var(--font-family-body), sans-serif;
  font-size: var(--font-size-14);
  line-height: var(--font-leading-14);
}

.errorText {
  margin: 0;
  color: var(--profile-error-text);
  font-family: var(--font-family-body), sans-serif;
  font-size: var(--font-size-14);
  line-height: var(--font-leading-14);
}

.idCard {
  position: relative;
  width: 100%;
  max-width: 680px;
  display: grid;
  grid-template-columns: 180px 1fr;
  grid-template-rows: 1fr auto;
  gap: var(--space-5);
  padding: var(--space-6);
  background: linear-gradient(180deg, var(--id-bg-top) 0%, var(--id-bg-bottom) 100%);
  border: var(--border-width-chunky) solid var(--id-border);
  border-radius: var(--radius-2);
  box-shadow:
    inset 0 0 0 var(--border-width-thin) var(--id-inner-ring),
    var(--space-2) var(--space-4) var(--space-6) var(--id-shadow);
  color: var(--id-value);
}

.idCard__left {
  display: flex;
  align-items: flex-start;
  justify-items: center;
}

.idCard__avatarLink {
  display: block;
  width: 100%;
  aspect-ratio: 3 / 4;
  border-radius: var(--radius-1);
  overflow: hidden;
  transition: opacity var(--transition-fast);
}

.idCard__avatarLink:hover {
  opacity: 0.85;
}

.idCard__avatar {
  width: 100%;
  height: 100%;
  display: grid;
  place-items: center;
  border: var(--border-width-thick) solid var(--id-avatar-border);
  border-radius: var(--radius-1);
  background: var(--id-avatar-bg);
}

.idCard__initial {
  font-family: var(--font-family-serif), sans-serif;
  font-size: var(--font-size-36);
  line-height: 1;
  font-weight: var(--font-weight-bold);
  color: var(--id-avatar-text);
  user-select: none;
}

.idCard__right {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
}

.idCard__title {
  margin: 0;
  font-family: var(--font-family-display), sans-serif;
  font-size: var(--font-size-36);
  line-height: var(--font-leading-36);
  font-weight: var(--font-weight-bold);
  letter-spacing: var(--font-tracking-wider);
  text-transform: uppercase;
  color: var(--id-title);
}

.idCard__field {
  display: grid;
  gap: var(--space-1);
  border-bottom: var(--border-width-thin) solid var(--id-field-rule);
  padding-bottom: var(--space-2);
}

.idCard__fieldValue {
  margin: 0;
  font-family: var(--font-family-serif), sans-serif;
  font-size: var(--font-size-22);
  line-height: var(--font-leading-22);
  font-weight: var(--font-weight-semibold);
  color: var(--id-value);
}

.idCard__fieldLabel {
  margin: 0;
  font-family: var(--font-family-body), sans-serif;
  font-size: var(--font-size-14);
  line-height: var(--font-leading-14);
  font-weight: var(--font-weight-medium);
  color: var(--id-label);
}

.idCard__input {
  margin: 0;
  font-family: var(--font-family-serif), sans-serif;
  font-size: var(--font-size-22);
  line-height: var(--font-leading-22);
  font-weight: var(--font-weight-semibold);
  color: var(--id-value);
  background: transparent;
  border: 0;
  border-bottom: var(--border-width-thin) solid var(--id-field-rule);
  padding-bottom: var(--space-1);
  outline: none;
  width: 100%;
}

.idCard__actionBtn {
  border: none;
  cursor: pointer;
  font-family: var(--font-family-body), sans-serif;
  font-weight: var(--font-weight-bold);
  background: transparent;
  color: var(--id-action-text);
  transition: background var(--transition-fast);
}

.idCard__actionBtn:hover {
  background: var(--id-action-hover);
}

.idCard__actionBtn--primary {
  background: var(--id-action-text);
  color: var(--id-bg-top);
}

.idCard__actionBtn--primary:hover {
  background: var(--id-value);
}

.idCard__levelHeading {
  margin: 0;
  font-family: var(--font-family-display), sans-serif;
  font-size: var(--font-size-18);
  line-height: var(--font-leading-18);
  font-weight: var(--font-weight-bold);
  letter-spacing: var(--font-tracking-wider);
  text-transform: uppercase;
  color: var(--id-title);
}

.idCard__xpRow {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  gap: var(--space-3);
}

.idCard__xpText,
.idCard__xpPercent {
  font-family: var(--font-family-body), sans-serif;
  font-size: var(--font-size-14);
  line-height: var(--font-leading-14);
  font-weight: var(--font-weight-medium);
  color: var(--id-label);
}

.idCard__progressBar {
  width: 100%;
  height: var(--space-2);
  border-radius: var(--radius-pill);
  overflow: hidden;
  background: var(--id-progress-track);
}

.idCard__progressFill {
  height: 100%;
  background: var(--id-progress-fill);
  transition: width var(--transition-slow);
}

.idCard__actions {
  grid-column: 1 / -1;
  display: flex;
  gap: var(--space-3);
}

.idCard__actionLink {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: var(--size-control-md);
  padding: var(--space-3) var(--space-4);
  border: var(--border-width-thin) solid var(--id-action-border);
  border-radius: var(--radius-pill);
  background: transparent;
  color: var(--id-action-text);
  font-family: var(--font-family-body), sans-serif;
  font-weight: var(--font-weight-bold);
  text-decoration: none;
  transition: background var(--transition-fast);
}

.idCard__actionLink:hover {
  background: var(--id-action-hover);
}

@media (max-width: 560px) {
  .idCard {
    grid-template-columns: 1fr;
  }

  .idCard__avatar {
    max-width: 160px;
    aspect-ratio: 1 / 1;
  }
}
</style>
