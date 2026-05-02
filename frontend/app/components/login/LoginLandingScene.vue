<script setup lang="ts">
import loginBackgroundImage from '~/assets/images/login/spBackgroundImageLogin.png';
import loginPlateImage from '~/assets/images/login/NamePlate.png';
import loginLogoImage from '~/assets/images/login/spLogoGame.png';
import { onMounted, onBeforeUnmount } from 'vue';

type LoginOption = {
  label: string;
  to: string;
  variant?: 'student' | 'teacher';
};

const props = withDefaults(
  defineProps<{
    title?: string;
    options: LoginOption[];
  }>(),
  {
    title: 'Detektiv Innlogging',
  },
);

const landingPageStyle = {
  '--login-background-image': `url(${loginBackgroundImage})`,
  '--login-plate-image': `url(${loginPlateImage})`,
};

const titleTarget = computed(() => props.options[0]?.to ?? '/login');

onMounted(() => {
  if (typeof document !== 'undefined') {
    document.documentElement.classList.add('no-scroll-login');
  }
});

onBeforeUnmount(() => {
  if (typeof document !== 'undefined') {
    document.documentElement.classList.remove('no-scroll-login');
  }
});
</script>

<template>
  <section class="landingPage" :style="landingPageStyle">
    <div class="sceneVignette" />

    <div class="loginStage">
      <img class="gameLogo" :src="loginLogoImage" alt="Nettdetektivene" />

      <div class="loginPlate">
        <div class="plateContent">
          <NuxtLink :to="titleTarget" class="titleBadge">
            {{ props.title }}
          </NuxtLink>
        </div>
      </div>
    </div>
  </section>
</template>

<style scoped>
.landingPage {
  position: relative;
  min-height: 100vh;
  overflow: hidden;
  display: grid;
  place-items: start center;
  padding: clamp(var(--space-6), 3vw, 2.5rem);
  background-image:
    linear-gradient(180deg, rgba(25, 17, 39, 0.32) 0%, rgba(17, 12, 26, 0.58) 100%),
    var(--login-background-image);
  background-position: center;
  background-repeat: no-repeat;
  background-size: cover;
}

.sceneVignette {
  position: absolute;
  inset: 0;
  background:
    radial-gradient(circle at top, rgba(255, 255, 255, 0.18), transparent 34%),
    radial-gradient(circle at bottom, rgba(0, 0, 0, 0.24), transparent 42%);
  pointer-events: none;
}

.loginStage {
  position: relative;
  z-index: 1;
  width: min(100%, 66rem);
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: clamp(var(--space-4), 4vh, 2.5rem);
  animation: stage-enter 500ms ease-out;
}

.gameLogo {
  width: min(100%, 33rem);
  margin-bottom: clamp(-1.25rem, -2.6vw, -var(--space-8));
  filter: drop-shadow(0 14px 18px rgba(14, 18, 38, 0.32));
  animation: logo-float 4.5s ease-in-out infinite;
}

.loginPlate {
  position: relative;
  width: min(100%, 58rem);
  aspect-ratio: 1117 / 612;
  display: grid;
  place-items: center;
}

.loginPlate::before {
  content: '';
  position: absolute;
  inset: 0;
  background: center / contain no-repeat var(--login-plate-image);
  filter: drop-shadow(0 18px 26px rgba(6, 9, 19, 0.28));
}

.plateContent {
  position: absolute;
  z-index: 1;
  inset: 18% 15% 17%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: var(--space-4);
  padding: 0;
  transform: translateY(-1.5rem);
}

.titleBadge {
  width: min(100%, 27rem);
  border-radius: var(--radius-2);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 1.35rem 1.75rem;
  border: var(--border-width-heavy) solid var(--palette-gold-400);
  background: linear-gradient(180deg, var(--palette-orange-500) 0%, var(--palette-orange-600) 100%);
  box-shadow:
    inset 0 2px 0 rgba(255, 255, 255, 0.22),
    0 10px 18px rgba(104, 49, 0, 0.28);
  color: var(--palette-white);
  font-family: Georgia, 'Times New Roman', serif;
  font-size: clamp(var(--font-size-28), 2.1vw, 2.5rem);
  line-height: 1.1;
  text-align: center;
  text-decoration: none;
  text-shadow: 0 2px 3px rgba(94, 26, 0, 0.25);
}

.loginOptions {
  width: min(100%, 16.5rem);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.9rem;
}

.loginOption {
  width: 100%;
  min-height: 3.95rem;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 0.9rem;
  border: var(--border-width-thick) solid rgba(255, 255, 255, 0.18);
  font-family: Georgia, 'Times New Roman', serif;
  font-size: 1.6rem;
  line-height: 1;
  text-decoration: none;
  color: var(--palette-white);
  box-shadow: 0 8px 18px rgba(27, 22, 18, 0.18);
  transition:
    transform 160ms ease,
    box-shadow 160ms ease,
    filter 160ms ease;
}

.titleBadge:hover,
.titleBadge:focus-visible {
  transform: translateY(-2px);
  box-shadow:
    inset 0 2px 0 rgba(255, 255, 255, 0.22),
    0 14px 24px rgba(104, 49, 0, 0.34);
  filter: brightness(1.04);
}

.loginOption:focus-visible {
  outline: var(--border-width-thick) solid #ffe38d;
  outline-offset: var(--border-width-thick);
}

.loginOption.student {
  background: linear-gradient(180deg, var(--palette-orange-500) 0%, var(--palette-orange-600) 100%);
}

.loginOption.teacher {
  background: linear-gradient(180deg, #6c6c6c 0%, #5b5b5b 100%);
}

@keyframes stage-enter {
  from {
    opacity: 0;
    transform: translateY(18px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes logo-float {
  0%,
  100% {
    transform: translateY(0);
  }

  50% {
    transform: translateY(-6px);
  }
}

@media (max-width: 900px) {
  .gameLogo {
    width: min(100%, 29rem);
    margin-bottom: -var(--space-4);
  }

  .loginPlate {
    width: min(100%, 60rem);
  }

  .plateContent {
    inset: 18% 14% 16%;
    transform: translateY(-1.2rem);
  }

  .titleBadge {
    width: min(100%, 24rem);
  }
}

@media (max-width: 640px) {
  .landingPage {
    padding: 1rem;
    background-position: 54% center;
  }

  .loginStage {
    width: 100%;
    margin-top: 0.5rem;
  }

  .gameLogo {
    width: min(100%, 22rem);
    margin-bottom: -0.35rem;
  }

  .loginPlate {
    width: 100%;
    max-width: 26.5rem;
  }

  .plateContent {
    inset: 16% 12% 14%;
    gap: 0.75rem;
    transform: translateY(-0.8rem);
  }

  .titleBadge {
    width: min(100%, 17rem);
    border-width: 3px;
    border-radius: 0.8rem;
    padding: 0.9rem 0.7rem;
    font-size: clamp(1.2rem, 5vw, 1.55rem);
  }
}
</style>

<!-- Global rule to disable page scrolling while landing scene is mounted -->
<style>
html.no-scroll-login,
html.no-scroll-login body {
  height: 100%;
  overflow: hidden !important;
  touch-action: none !important;
}
</style>
