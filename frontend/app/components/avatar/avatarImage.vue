<script setup lang="ts">
import type { AvatarConfig, HairStyle, Outfit } from "~/composables/types/AvatarTypes";
import { DEFAULT_AVATAR_CONFIG } from "~/composables/types/AvatarTypes";
import bodyImage from "~/assets/svg-files/body/body_comp.svg";
import eyebrowsImage from "~/assets/svg-files/face/eyebrows.svg";
import eyesImage from "~/assets/svg-files/face/eyes.svg";
import hairImage from "~/assets/svg-files/hair/hair.svg";
import hairCurlyBack from "~/assets/svg-files/hair/hair_curly_back.svg";
import hairStraightBack from "~/assets/svg-files/hair/hair_straight_back.svg";
import hatNoirFront from "~/assets/svg-files/hat/hat_noir_front.svg";
import hatSherlockFront from "~/assets/svg-files/hat/hat_sherlock_front.svg";
import outfitNoir from "~/assets/svg-files/outfit/outfit_noir.svg";
import outfitSherlock from "~/assets/svg-files/outfit/outfit_sherlock.svg";
import hatNoirBack from "~/assets/svg-files/hat/hat_noir_back.svg";
import hatSherlockBack from "~/assets/svg-files/hat/hat_sherlock_back.svg";

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

const hairImageMap: Record<HairStyle, string> = {
  SHORT: hairImage,
  LONG: hairStraightBack,
};

const avatar = computed(() => props.avatar ?? DEFAULT_AVATAR_CONFIG);
const outfitImage = computed(() => outfitImageMap[avatar.value.outfit]);
const hatFrontImage = computed(() => hatImageMap[avatar.value.outfit]);
const hatBackImage = computed(() => hatBackImageMap[avatar.value.outfit]);
const selectedHairImage = computed(() => hairImageMap[avatar.value.hairStyle]);
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
        <g class="avatarLayerBody">
          <image
            :href="bodyImage"
            x="0"
            y="0"
            width="330"
            height="460"
            preserveAspectRatio="xMidYMid meet"
          />
        </g>

        <g class="avatarLayerFace">
          <g class="avatarFeatureEyebrows">
            <image
              :href="eyebrowsImage"
              x="0"
              y="0"
              width="330"
              height="460"
              preserveAspectRatio="xMidYMid meet"
            />
          </g>

          <g class="avatarFeatureEyes">
            <image
              :href="eyesImage"
              x="0"
              y="0"
              width="330"
              height="460"
              preserveAspectRatio="xMidYMid meet"
            />
          </g>
        </g>

        <g v-if="selectedHairImage" class="avatarLayerHair">
          <image
            :href="selectedHairImage"
            x="0"
            y="0"
            width="330"
            height="460"
            preserveAspectRatio="xMidYMin meet"
          />
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
