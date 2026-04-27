export type HairStyle = 'LONG' | 'SHORT';

export type HairColor = 'BLONDE' | 'BROWN' | 'ORANGE' | 'BLACK';

export type Outfit = 'NOIR' | 'SHERLOCK';

export type EyeColor = 'BLUE' | 'GREEN' | 'BROWN' | 'BLACK';

export type SkinColor = 'COLOR1' | 'COLOR2' | 'COLOR3' | 'COLOR4' | 'COLOR5' | 'COLOR6';

export type AvatarConfig = {
  hairStyle: HairStyle;
  hairColor: HairColor;
  outfit: Outfit;
  eyeColor: EyeColor;
  skinColor: SkinColor;
};

export const DEFAULT_AVATAR_CONFIG: AvatarConfig = {
  hairStyle: 'SHORT',
  hairColor: 'BROWN',
  outfit: 'SHERLOCK',
  eyeColor: 'BROWN',
  skinColor: 'COLOR1',
};
