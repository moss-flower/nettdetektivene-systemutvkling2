package no.safety.backend.avatar.dto;

import java.util.Arrays;
import java.util.List;

import no.safety.backend.avatar.enums.EyeColor;
import no.safety.backend.avatar.enums.HairColor;
import no.safety.backend.avatar.enums.HairStyle;
import no.safety.backend.avatar.enums.Outfit;
import no.safety.backend.avatar.enums.SkinColor;

public record AvatarOptionsResponse(
    List<HairStyle> hairStyle,
    List<HairColor> hairColor,
    List<Outfit> outfit,
    List<EyeColor> eyeColor,
    List<SkinColor> skinColor
) {
    public static AvatarOptionsResponse fromEnums() {
        return new AvatarOptionsResponse(
            Arrays.asList(HairStyle.values()),
            Arrays.asList(HairColor.values()),
            Arrays.asList(Outfit.values()),
            Arrays.asList(EyeColor.values()),
            Arrays.asList(SkinColor.values())
        );
    }
}
