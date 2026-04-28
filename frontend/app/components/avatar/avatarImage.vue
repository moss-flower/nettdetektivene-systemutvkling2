<script setup lang="ts">
import type { AvatarConfig, HairColor, HairStyle, Outfit, SkinColor } from "~/composables/types/AvatarTypes";
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

const hairBackImageMap: Partial<Record<HairStyle, string>> = {
  LONG: hairStraightBack,
};

const hairFilterMap: Record<HairColor, string> = {
  BROWN: "none",
  BLACK: "brightness(0.45) saturate(0.65) contrast(1.15)",
  // Source hair art is a muted mud-brown, so these filters have to both lift
  // brightness and shift hue harder than a neutral base would need.
  BLONDE: "sepia(1) saturate(12) hue-rotate(2deg) brightness(1.82) contrast(0.94)",
  ORANGE: "sepia(1) saturate(13.5) hue-rotate(330deg) brightness(1.26) contrast(1.1)",
};

const skinFilterMap: Record<SkinColor, string> = {
  COLOR1: "none",
  // Keep the progression dark, but add a slight warm-red bias so the tones
  // read more naturally brown instead of drifting green on the light base art.
  COLOR2: "sepia(0.04) hue-rotate(345deg) brightness(0.9) saturate(0.96) contrast(1.04)",
  COLOR3: "sepia(0.08) hue-rotate(343deg) brightness(0.76) saturate(0.94) contrast(1.08)",
  COLOR4: "sepia(0.12) hue-rotate(341deg) brightness(0.62) saturate(0.92) contrast(1.12)",
  COLOR5: "sepia(0.16) hue-rotate(339deg) brightness(0.48) saturate(0.9) contrast(1.16)",
  COLOR6: "sepia(0.2) hue-rotate(337deg) brightness(0.34) saturate(0.88) contrast(1.22)",
};

const avatar = computed(() => props.avatar ?? DEFAULT_AVATAR_CONFIG);
const outfitImage = computed(() => outfitImageMap[avatar.value.outfit]);
const hatFrontImage = computed(() => hatImageMap[avatar.value.outfit]);
const hatBackImage = computed(() => hatBackImageMap[avatar.value.outfit]);
const hairFrontImage = computed(() => hairImage);
const hairBackImage = computed(() => hairBackImageMap[avatar.value.hairStyle] ?? null);
const hairLayerStyle = computed(() => ({
  filter: hairFilterMap[avatar.value.hairColor],
}));
const skinLayerStyle = computed(() => ({
  filter: skinFilterMap[avatar.value.skinColor],
}));
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
        <g v-if="hairBackImage" class="avatarLayerHair avatarLayerHairBack" :style="hairLayerStyle">
          <image
            :href="hairBackImage"
            x="0"
            y="0"
            width="330"
            height="460"
            preserveAspectRatio="xMidYMin meet"
          />
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
        <g class="avatarLayerBody" :style="skinLayerStyle">
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

        <g v-if="hairFrontImage" class="avatarLayerHair avatarLayerHairFront" :style="hairLayerStyle">
          <image
            :href="hairFrontImage"
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
