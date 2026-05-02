export type HairStyle = 'LONG' | 'SHORT' | 'CURLY';

export type HairColor = 'BLONDE' | 'BROWN' | 'ORANGE' | 'BLACK';

export type Outfit = 'NOIR' | 'SHERLOCK';

export type EyeColor = 'BLUE' | 'GREEN' | 'BROWN' | 'BLACK';

export type SkinColor = 'COLOR1' | 'COLOR2' | 'COLOR3' | 'COLOR4' | 'COLOR5' | 'COLOR6';

export const HAIR_STYLES = ["SHORT", "LONG", "CURLY"] as const
export const HAIR_COLORS = ["BROWN", "BLACK", "BLONDE", "ORANGE"] as const
export const OUTFITS = ["SHERLOCK", "NOIR"] as const
export const EYE_COLORS_LIST = ["BROWN", "BLACK", "BLUE", "GREEN"] as const
export const SKIN_COLORS = ["COLOR1", "COLOR2", "COLOR3", "COLOR4", "COLOR5", "COLOR6"] as const


export type AvatarConfig = {
  hairStyle: HairStyle;
  hairColor: HairColor;
  outfit: Outfit;
  eyeColor: EyeColor;
  skinColor: SkinColor;
};

export type AvatarOptions = {
  hairStyle: HairStyle[];
  hairColor: HairColor[];
  outfit: Outfit[];
  eyeColor: EyeColor[];
  skinColor: SkinColor[];
};

export const DEFAULT_AVATAR_CONFIG: AvatarConfig = {
  hairStyle: 'SHORT',
  hairColor: 'BROWN',
  outfit: 'SHERLOCK',
  eyeColor: 'BROWN',
  skinColor: 'COLOR1',
};

export const DEFAULT_OPTIONS: AvatarOptions = {
  hairStyle: [...HAIR_STYLES],
  hairColor: [...HAIR_COLORS],
  outfit: [...OUTFITS],
  eyeColor: [...EYE_COLORS_LIST],
  skinColor: [...SKIN_COLORS],
}