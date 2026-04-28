<script setup lang="ts">
import type { AvatarConfig, HairColor, HairStyle, Outfit, SkinColor } from "~/composables/types/AvatarTypes";
import { DEFAULT_AVATAR_CONFIG } from "~/composables/types/AvatarTypes";
import bodyImage from "~/assets/svg-files/body/body_comp.vue";
import eyebrowsImage from "~/assets/svg-files/face/eyebrows.vue";
import eyesImage from "~/assets/svg-files/face/eyes.vue";
import hairImage from "~/assets/svg-files/hair/hair.vue";
import hairCurlyBack from "~/assets/svg-files/hair/hair_curly_back.vue";
import hairStraightBack from "~/assets/svg-files/hair/hair_straight_back.vue";
import hatNoirFront from "~/assets/svg-files/hat/hat_noir_front.svg";
import hatSherlockFront from "~/assets/svg-files/hat/hat_sherlock_front.svg";
import outfitNoir from "~/assets/svg-files/outfit/outfit_noir.svg";
import outfitSherlock from "~/assets/svg-files/outfit/outfit_sherlock.svg";
import hatNoirBack from "~/assets/svg-files/hat/hat_noir_back.svg";
import hatSherlockBack from "~/assets/svg-files/hat/hat_sherlock_back.svg";
import { SKIN_THEMES, HAIR_THEMES, EYE_COLORS } from "~/composables/useAvatarThemes";

const props = withDefaults(
  defineProps<{
    avatar?: AvatarConfig;
    alt?: string;
  }>(),
  {
    alt: "Avatar",
  },
);

const outfitImageMap: Record<Outfit, string> = {
  SHERLOCK: outfitSherlock,
  NOIR: outfitNoir,
};

const hatImageMap: Record<Outfit, string> = {
  SHERLOCK: hatSherlockFront,
  NOIR: hatNoirFront,
};

const hatBackImageMap: Record<Outfit, string> = {
  SHERLOCK: hatSherlockBack,
  NOIR: hatNoirBack,
};

const hairBackComponentMap: Partial<Record<HairStyle, Component>> = {
  LONG: hairStraightBack,
  CURLY: hairCurlyBack
};


const avatar = computed(() => props.avatar ?? DEFAULT_AVATAR_CONFIG);
const outfitImage = computed(() => outfitImageMap[avatar.value.outfit]);
const hatFrontImage = computed(() => hatImageMap[avatar.value.outfit]);
const hatBackImage = computed(() => hatBackImageMap[avatar.value.outfit]);
const hairBackComponent = computed(() => hairBackComponentMap[avatar.value.hairStyle] ?? null)
const skinLayerStyle = computed(() => SKIN_THEMES[avatar.value.skinColor]);
const hairLayerStyle = computed(() => HAIR_THEMES[avatar.value.hairColor]);
const eyeLayerStyle = computed(() => EYE_COLORS[avatar.value.eyeColor]);
</script>

<template>
  <div
    class="avatarImage"
    :aria-label="props.alt"
    :data-hair-style="avatar.hairStyle"
    :data-hair-color="avatar.hairColor"
    :data-eye-color="avatar.eyeColor"
    :data-skin-color="avatar.skinColor"
    :data-outfit="avatar.outfit"
    role="img"
  >
    <svg
      class="avatarSvg"
      viewBox="0 0 330 460"
      preserveAspectRatio="xMidYMid meet"
      aria-hidden="true"
      overflow="hidden"
    >
      <defs>
        <clipPath id="avatar-clip">
          <rect x="0" y="0" width="330" height="460" />
        </clipPath>
      </defs>

      <g clip-path="url(#avatar-clip)">
        <g v-if="hairBackComponent" class="avatarLayerHair avatarLayerHairBack" :style="hairLayerStyle">
          <hairBackComponent/>
        </g>

        <g v-if="hatBackImage" class="avatarLayerHat avatarLayerHatBack">
          <image
            :href="hatBackImage"
            x="0"
            y="0"
            width="330"
            height="460"
            preserveAspectRatio="xMidYMin meet"
          />
        </g>
        <g class="avatarLayerBody" :style="skinLayerStyle"><bodyImage/></g>
        <g class="avatarLayerFace">
          <g class="avatarFeatureEyebrows" :style="hairLayerStyle"><eyebrowsImage/></g>
          <g class="avatarFeatureEyes" :style="eyeLayerStyle"><eyesImage/> </g>
        </g>

        <g class="avatarLayerHair avatarLayerHairFront" :style="hairLayerStyle">
          <hairImage/>
        </g>

        <g v-if="outfitImage" class="avatarLayerOutfit">
          <image
            :href="outfitImage"
            x="0"
            y="0"
            width="330"
            height="460"
            preserveAspectRatio="xMidYMax meet"
          />
        </g>

        <g v-if="hatFrontImage" class="avatarLayerHat avatarLayerHatFront">
          <image
            :href="hatFrontImage"
            x="0"
            y="0"
            width="330"
            height="460"
            preserveAspectRatio="xMidYMin meet"
          />
        </g>
      </g>
    </svg>
  </div>
</template>

<style scoped>
.avatarImage {
  position: relative;
  width: 100%;
}

.avatarSvg {
  display: block;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.avatarLayerBody {
  width: 100%;
}

.avatarLayerFace {
  width: 100%;
}

.avatarFeatureEyebrows {
  width: 100%;
}

.avatarFeatureEyes {
  width: 100%;
}

.avatarLayerHair {
  width: 100%;
}

.avatarLayerOutfit {
  width: 100%;
}

.avatarLayerHat {
  width: 100%;
}
</style>
