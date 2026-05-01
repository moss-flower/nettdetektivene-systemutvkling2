import type { EyeColor, HairColor, SkinColor } from "./types/AvatarTypes";

export const SKIN_THEMES: Record<SkinColor, Record<string,string>> = {
    COLOR1: {
        "--skin"    :   "#764635",
        "--ear-line":   "#70322a",
        "--mouth"   :   "#502b2B",
        "--blush-1" :   "#7e3622",
        "--blush-2" :   "#8f3f2c",
        "--blush-3" :   "#773929"
    },
    COLOR2: {
        "--skin"    :   "#32180e",
        "--ear-line":   "#1c0805",
        "--mouth"   :   "#290a0a",
        "--blush-1" :   "#3d1509",
        "--blush-2" :   "#2c0e07",
        "--blush-3" :   "#321008"
    },
    COLOR3: {
        "--skin"    :   "#d57d43",
        "--ear-line":   "#a05230",
        "--mouth"   :   "#6f2e27",
        "--blush-1" :   "#cc613a",
        "--blush-2" :   "#df774e",
        "--blush-3" :   "#dd794b"
    },
    COLOR4: {
        "--skin"    :   "#FBCBA7",
        "--ear-line":   "#c28e87",
        "--mouth"   :   "#802323",
        "--blush-1" :   "#EBA491",
        "--blush-2" :   "#E08E79",
        "--blush-3" :   "#D87E68"
    },
    COLOR5: {
        "--skin"    :   "#8f4b17",
        "--ear-line":   "#712f1c",
        "--mouth"   :   "#532411",
        "--blush-1" :   "#633216",
        "--blush-2" :   "#7a411b",
        "--blush-3" :   "#713916"
    },
    COLOR6: {
        "--skin"    :   "#782d18",
        "--ear-line":   "#6a231a",
        "--mouth"   :   "#3f1313",
        "--blush-1" :   "#7c2f1d",
        "--blush-2" :   "#943720",
        "--blush-3" :   "#8b341f"
    }
}

export const HAIR_THEMES: Record<HairColor, Record<string,string>> = {
    BLACK   : {"--hair": "#0b0402"},
    BLONDE  : {"--hair": "#d7ab82"},
    BROWN   : {"--hair": "#240b07"},
    ORANGE  : {"--hair": "#9e250a"}
}

export const EYE_COLORS: Record<EyeColor, Record<string,string>> = {
    BLACK   : {"--eye-color": "#0b0402"},
    BLUE    : {"--eye-color": "#3E5297"},
    BROWN   : {"--eye-color": "#2d150f"},
    GREEN   : {"--eye-color": "#56973E"},
}