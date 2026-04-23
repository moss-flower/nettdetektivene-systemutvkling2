package no.safety.backend.avatar.dto;

import jakarta.validation.constraints.NotNull;
import no.safety.backend.avatar.enums.EyeColor;
import no.safety.backend.avatar.enums.HairColor;
import no.safety.backend.avatar.enums.HairStyle;
import no.safety.backend.avatar.enums.Outfit;
import no.safety.backend.avatar.enums.SkinColor;
import no.safety.backend.avatar.model.AvatarConfig;

public record AvatarConfigRequest (
    @NotNull HairStyle hairStyle, 
    @NotNull HairColor hairColor, 
    @NotNull Outfit outfit,
    @NotNull EyeColor eyeColor,
    @NotNull SkinColor skinColor
) {

    public AvatarConfig toEntity() {
        return new AvatarConfig(
            hairStyle,
            hairColor,
            outfit,
            eyeColor,
            skinColor
        );
    }
}
