package no.safety.backend.avatar.dto;

import java.util.Arrays;
import java.util.List;

import no.safety.backend.avatar.enums.EyeColor;
import no.safety.backend.avatar.enums.HairColor;
import no.safety.backend.avatar.enums.HairStyle;
import no.safety.backend.avatar.enums.Outfit;
import no.safety.backend.avatar.enums.SkinColor;

/**
 * Data Transfer Object (DTO) containing all available avatar customization options.
 * This response is used to populate avatar customization UI with available choices
 * for hair styles, hair colors, outfits, eye colors, and skin colors.
 *
 * @param hairStyle list of all available hair styles
 * @param hairColor list of all available hair colors
 * @param outfit list of all available outfits
 * @param eyeColor list of all available eye colors
 * @param skinColor list of all available skin colors
 */
public record AvatarOptionsResponse(
    List<HairStyle> hairStyle,
    List<HairColor> hairColor,
    List<Outfit> outfit,
    List<EyeColor> eyeColor,
    List<SkinColor> skinColor
) {
    /**
     * Creates an AvatarOptionsResponse containing all available enum values
     * for avatar customization options.
     *
     * @return a new AvatarOptionsResponse with all enum values populated
     */
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
