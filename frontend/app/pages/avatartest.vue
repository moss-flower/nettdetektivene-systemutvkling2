<!-- your test page -->
<template>
  <div class="page">
    <div class="avatar-wrapper">
      <AvatarImage :avatar="config" />
    </div>

    <div class="controls">
      <section>
        <h3>Skin</h3>
        <div class="swatches">
          <button
            v-for="(vars, key) in SKIN_THEMES"
            :key="key"
            :style="{ background: vars['--skin'] }"
            :class="{ selected: config.skinColor === key }"
            @click="config.skinColor = key as SkinColor"
          />
        </div>
      </section>
      <section>
        <h3>Eyes</h3>
        <div class="swatches">
          <button
            v-for="(vars, key) in EYE_COLORS"
            :key="key"
            :style="{ background: vars['--eye-color'] }"
            :class="{ selected: config.eyeColor === key }"
            @click="config.eyeColor = key as EyeColor"
          />
        </div>
      </section>

      <section>
        <h3>Hair color</h3>
        <div class="swatches">
          <button
            v-for="(vars, key) in HAIR_THEMES"
            :key="key"
            :style="{ background: vars['--hair'] }"
            :class="{ selected: config.hairColor === key }"
            @click="config.hairColor = key as HairColor"
          />
        </div>
      </section>

      <section>
        <h3>Hair style</h3>
        <div class="options">
          <button
            v-for="style in hairStyles"
            :key="style"
            :class="{ selected: config.hairStyle === style }"
            @click="config.hairStyle = style"
          >
            {{ style }}
          </button>
        </div>
      </section>

      <section>
        <h3>Outfit</h3>
        <div class="options">
          <button
            v-for="outfit in outfits"
            :key="outfit"
            :class="{ selected: config.outfit === outfit }"
            @click="config.outfit = outfit"
          >
            {{ outfit }}
          </button>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { DEFAULT_AVATAR_CONFIG } from "~/composables/types/AvatarTypes";
import type { SkinColor, HairColor, EyeColor } from "~/composables/types/AvatarTypes";
import { SKIN_THEMES, HAIR_THEMES, EYE_COLORS } from "~/composables/useAvatarThemes";

const config = ref({ ...DEFAULT_AVATAR_CONFIG });

const hairStyles = ["SHORT", "LONG", "CURLY"] as const;
const outfits = ["SHERLOCK", "NOIR"] as const;
</script>

<style scoped>
.page {
  display: flex;
  gap: 2rem;
  height: 100vh;
  padding: 1.5rem;
  box-sizing: border-box;
  align-items: flex-start;
}

.avatar-wrapper {
  flex-shrink: 0;
  width: 220px;        /* adjust to taste */
  height: 100%;
  max-height: 500px;
}

.avatar-wrapper > * {
  height: 100%;
}

.controls {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  overflow-y: auto;
}

.swatches {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.swatches button {
  width: 2rem;
  height: 2rem;
  border-radius: 50%;
  border: 2px solid transparent;
  cursor: pointer;
}

.swatches button.selected {
  border-color: #000;
  outline: 2px solid #fff;
  outline-offset: -4px;
}

.options {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.options button {
  padding: 0.25rem 0.75rem;
  border: 2px solid #ccc;
  border-radius: 4px;
  cursor: pointer;
  background: none;
}

.options button.selected {
  border-color: #000;
  background: #000;
  color: #fff;
}
</style>