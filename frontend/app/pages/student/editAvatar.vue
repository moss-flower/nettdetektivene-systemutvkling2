<script setup lang="ts">
import arrowLeft from "~/assets/ui-icons/arrow-left.png";
import AvatarImage from "~/components/avatar/avatarImage.vue";
import UiStudentHomeBackButton from "~/components/ui/UiStudentHomeBackButton.vue";
import { DEFAULT_OPTIONS } from "~/composables/types/AvatarTypes";
import { useAvatar } from "~/composables/useAvatar";
import {
  DEFAULT_AVATAR_CONFIG,
  type AvatarConfig,
} from "~/composables/types/AvatarTypes";

type EditableField = keyof AvatarConfig;

type EditorSection = {
  key: EditableField;
  label: string;
};

const sections: EditorSection[] = [
  { key: "skinColor", label: "Skin tone" },
  { key: "eyeColor", label: "Eye color" },
  { key: "hairColor", label: "Hair color" },
  { key: "hairStyle", label: "Hair style" },
  { key: "outfit", label: "Outfit" },
];

const { getMyAvatar, updateMyAvatar, getAvatarOptions } = useAvatar();

const { data: avatarOptionsData } = await useAsyncData(
  "student-avatar-options",
  async () => {
    try {
      return await getAvatarOptions();
    } catch {
      return DEFAULT_OPTIONS;
    }
  },
);

const { data: avatarData, error: avatarError } = await useAsyncData(
  "student-avatar-edit",
  async () => {
    try {
      return await getMyAvatar();
    } catch {
      return DEFAULT_AVATAR_CONFIG;
    }
  },
);

const avatar = ref<AvatarConfig>({
  ...(avatarData.value ?? DEFAULT_AVATAR_CONFIG),
});
const savedAvatar = ref<AvatarConfig>({
  ...(avatarData.value ?? DEFAULT_AVATAR_CONFIG),
});

const activeSection = ref<EditableField>("hairStyle");
const isSaving = ref(false);
const saveMessage = ref("");

const optionMap = computed(() => avatarOptionsData.value ?? DEFAULT_OPTIONS)

const activeSectionLabel = computed(() => {
  return sections.find((section) => section.key === activeSection.value)?.label ?? "Avatar";
});

const activeValue = computed(() => avatar.value[activeSection.value]);
const hasChanges = computed(() => {
  return JSON.stringify(avatar.value) !== JSON.stringify(savedAvatar.value);
});

function cycleValue(field: EditableField, direction: -1 | 1) {
  const options = optionMap.value[field] as string[]
  const current = avatar.value[field] as string
  const currentIndex = options.indexOf(current)
  const nextIndex = (currentIndex + direction + options.length) % options.length
  avatar.value = { ...avatar.value, [field]: options[nextIndex] }
}

function selectSection(field: EditableField) {
  activeSection.value = field;
}

function handlePrevious() {
  cycleValue(activeSection.value, -1);
}

function handleNext() {
  cycleValue(activeSection.value, 1);
}

async function saveAvatar() {
  if (!hasChanges.value || isSaving.value) {
    return;
  }

  isSaving.value = true;
  saveMessage.value = "";

  try {
    await updateMyAvatar(avatar.value);
    savedAvatar.value = { ...avatar.value };
    saveMessage.value = "Avatar saved.";
  } catch {
    saveMessage.value = "Could not save avatar right now.";
  } finally {
    isSaving.value = false;
  }
}
</script>

<template>
  <section class="avatarEditorPage">
    <header class="avatarEditorHeader">
      <UiStudentHomeBackButton class="backButton" />
      <p class="eyebrow">Avatar</p>
      <h1>Edit Avatar</h1>
      <p class="intro">
        Use the arrows beside the avatar to cycle through each look.
      </p>
      <p v-if="avatarError" class="statusText">
        Could not load your saved avatar, so the default look is shown.
      </p>
    </header>

    <article class="avatarEditorCard">
      <div class="avatarStage">
        <button
          class="arrowButton"
          type="button"
          aria-label="Previous option"
          @click="handlePrevious"
        >
          <img :src="arrowLeft" alt="" />
        </button>

        <div class="avatarWindow">
          <AvatarImage :avatar="avatar" :alt="'Avatar preview'" />
        </div>

        <button
          class="arrowButton arrowButtonRight"
          type="button"
          aria-label="Next option"
          @click="handleNext"
        >
          <img :src="arrowLeft" alt="" />
        </button>
      </div>

      <div class="selectionInfo">
        <p class="selectionLabel">Currently editing</p>
        <p class="selectionValue">{{ activeSectionLabel }}: {{ activeValue }}</p>
      </div>

      <div class="sectionGrid">
        <button
          v-for="section in sections"
          :key="section.key"
          class="sectionButton"
          :class="{ sectionButtonActive: section.key === activeSection }"
          type="button"
          @click="selectSection(section.key)"
        >
          {{ section.label }}
        </button>
      </div>

      <div class="actions">
        <p class="saveMessage" :class="{ saveMessageMuted: !saveMessage }">
          {{ saveMessage || "Changes are only saved when you press Save avatar." }}
        </p>
        <button
          class="saveButton"
          type="button"
          :disabled="!hasChanges || isSaving"
          @click="saveAvatar"
        >
          {{ isSaving ? "Saving..." : "Save avatar" }}
        </button>
      </div>
    </article>
  </section>
</template>

<style scoped>
.avatarEditorPage {
  min-height: 100dvh;
  display: grid;
  gap: var(--space-6);
  align-content: start;
  justify-items: center;
  padding: var(--layout-page-padding);
  background:
    radial-gradient(ellipse 70% 50% at 50% 20%, var(--palette-overlay-warm-glow), transparent 70%),
    repeating-linear-gradient(
      0deg,
      transparent 0,
      transparent 117px,
      var(--palette-overlay-dark-strong) 117px,
      var(--palette-overlay-dark-strong) 120px,
      var(--palette-overlay-warm-glow) 120px,
      var(--palette-overlay-warm-glow) 122px
    ),
    var(--palette-wood-900);
}

.avatarEditorHeader,
.avatarEditorCard {
  width: min(100%, 860px);
}

.avatarEditorHeader {
  display: grid;
  gap: var(--space-2);
  color: var(--palette-cream-100);
}

.backButton {
  width: fit-content;
  padding: var(--space-3) var(--space-4);
  border-radius: var(--radius-pill);
  background: var(--palette-white);
  color: var(--palette-navy-900);
}

.eyebrow {
  margin: 0;
  color: var(--palette-gold-500);
  font-size: var(--font-size-14);
  font-weight: var(--font-weight-bold);
  letter-spacing: var(--font-tracking-wider);
  text-transform: uppercase;
}

h1,
.intro,
.statusText,
.selectionLabel,
.selectionValue {
  margin: 0;
}

.intro {
  color: var(--palette-cream-300);
}

.statusText {
  color: var(--palette-gold-400);
}

.avatarEditorCard {
  --mirror-frame-outer: var(--palette-gold-700);
  --mirror-frame-inner: var(--palette-gold-400);
  --mirror-frame-shadow: var(--palette-overlay-dark-deep);
  --mirror-glass-top: rgba(170, 220, 255, 0.95);
  --mirror-glass-bottom: rgba(149, 187, 214, 0.9);
  --mirror-reflection: rgba(255, 255, 255, 0.34);
  --mirror-reflection-soft: rgba(255, 255, 255, 0.14);
  --mirror-back-panel: var(--palette-navy-900);

  display: grid;
  gap: var(--space-5);
  padding: clamp(1.25rem, 2vw, 2rem);
  border-radius: var(--radius-3);
  background: linear-gradient(180deg, var(--palette-cream-100) 0%, var(--palette-cream-200) 100%);
  box-shadow: 0 var(--space-4) var(--space-6) var(--palette-overlay-dark-deep);
}

.avatarStage {
  display: grid;
  grid-template-columns: auto minmax(0, 1fr) auto;
  align-items: center;
  gap: var(--space-4);
}

.avatarWindow {
  position: relative;
  width: min(100%, 24rem);
  justify-self: center;
  aspect-ratio: 330 / 460;
  overflow: hidden;
  padding: clamp(0.8rem, 1.8vw, 1.15rem);
  box-sizing: border-box;
  border: clamp(0.28rem, 0.6vw, 0.45rem) solid var(--mirror-frame-outer);
  border-radius: calc(var(--radius-3) + 0.35rem);
  background:
    linear-gradient(135deg, var(--mirror-frame-inner), var(--mirror-frame-outer));
  box-shadow:
    0 var(--space-5) var(--space-6) var(--mirror-frame-shadow),
    inset 0 0 0 1px rgba(255, 255, 255, 0.22),
    inset 0 var(--space-2) var(--space-3) rgba(255, 255, 255, 0.18);
}

.avatarWindow::before {
  content: "";
  position: absolute;
  inset: 0.55rem;
  border-radius: calc(var(--radius-3) - 0.1rem);
  border: 1px solid rgba(255, 255, 255, 0.22);
  pointer-events: none;
}

.avatarWindow::after {
  content: "";
  position: absolute;
  inset: calc(0.8rem + 1px);
  border-radius: calc(var(--radius-3) - 0.2rem);
  background:
    linear-gradient(
      112deg,
      rgba(255, 255, 255, 0.42) 0%,
      rgba(255, 255, 255, 0.08) 18%,
      transparent 34%,
      transparent 100%
    ),
    linear-gradient(
      180deg,
      var(--mirror-reflection-soft) 0%,
      transparent 26%,
      transparent 72%,
      rgba(20, 33, 51, 0.08) 100%
    );
  mix-blend-mode: screen;
  pointer-events: none;
}

.avatarWindow :deep(.avatarImage) {
  height: 100%;
}

.avatarWindow :deep(.avatarSvg) {
  border-radius: calc(var(--radius-3) - 0.35rem);
  background:
    radial-gradient(circle at 50% 22%, var(--mirror-reflection) 0%, transparent 34%),
    linear-gradient(180deg, var(--mirror-glass-top) 0%, var(--mirror-glass-bottom) 100%);
}

.arrowButton {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 3.5rem;
  height: 3.5rem;
  border: var(--border-width-thin) solid var(--mirror-frame-outer);
  border-radius: var(--radius-pill);
  background: linear-gradient(180deg, var(--palette-cream-100) 0%, var(--palette-gold-400) 100%);
  box-shadow:
    0 var(--space-2) var(--space-3) rgba(17, 24, 39, 0.22),
    inset 0 1px 0 rgba(255, 255, 255, 0.4);
  cursor: pointer;
}

.arrowButton:hover {
  transform: translateY(-1px);
}

.arrowButton img {
  width: 1.4rem;
  height: 1.4rem;
}

.arrowButtonRight img {
  transform: scaleX(-1);
}

.selectionInfo {
  display: grid;
  gap: var(--space-1);
  text-align: center;
}

.selectionLabel {
  color: var(--palette-cork-580);
  font-size: var(--font-size-14);
  font-weight: var(--font-weight-bold);
  letter-spacing: var(--font-tracking-wider);
  text-transform: uppercase;
}

.selectionValue {
  color: var(--palette-cork-900);
  font-family: var(--font-family-serif), sans-serif;
  font-size: var(--font-size-28);
  font-weight: var(--font-weight-semibold);
}

.sectionGrid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(9rem, 1fr));
  gap: var(--space-3);
}

.actions {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--space-4);
  flex-wrap: wrap;
}

.saveMessage {
  color: var(--palette-cork-700);
  font-size: var(--font-size-14);
}

.saveMessageMuted {
  opacity: 0.9;
}

.saveButton {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 10rem;
  padding: 0.85rem 1.15rem;
  border: 0;
  border-radius: var(--radius-pill);
  background: var(--palette-gold-600);
  color: var(--palette-cork-900);
  font: inherit;
  font-weight: var(--font-weight-bold);
  cursor: pointer;
}

.saveButton:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.sectionButton {
  min-height: var(--size-control-md);
  padding: var(--space-3) var(--space-4);
  border: var(--border-width-thin) solid var(--palette-cork-700);
  border-radius: var(--radius-pill);
  background: var(--palette-white);
  color: var(--palette-cork-900);
  font: inherit;
  font-weight: var(--font-weight-semibold);
  cursor: pointer;
}

.sectionButtonActive {
  background: var(--palette-navy-900);
  color: var(--palette-cream-100);
  border-color: var(--palette-gold-500);
}

@media (max-width: 720px) {
  .avatarStage {
    grid-template-columns: 1fr;
  }

  .arrowButton,
  .arrowButtonRight {
    justify-self: center;
  }
}
</style>
