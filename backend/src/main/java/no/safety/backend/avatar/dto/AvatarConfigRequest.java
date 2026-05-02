package no.safety.backend.avatar.dto;

import jakarta.validation.constraints.NotNull;
import no.safety.backend.avatar.enums.EyeColor;
import no.safety.backend.avatar.enums.HairColor;
import no.safety.backend.avatar.enums.HairStyle;
import no.safety.backend.avatar.enums.Outfit;
import no.safety.backend.avatar.enums.SkinColor;
import no.safety.backend.avatar.model.AvatarConfig;

/**
 * Data Transfer Object (DTO) for receiving avatar configuration requests from the client.
 * Contains all customizable avatar attributes that a student can modify.
 * All fields are required and validated using Jakarta Validation constraints.
 *
 * @param hairStyle the selected hairstyle for the avatar
 * @param hairColor the selected hair color for the avatar
 * @param outfit the selected outfit for the avatar
 * @param eyeColor the selected eye color for the avatar
 * @param skinColor the selected skin color for the avatar
 */
public record AvatarConfigRequest (
    @NotNull HairStyle hairStyle, 
    @NotNull HairColor hairColor, 
    @NotNull Outfit outfit,
    @NotNull EyeColor eyeColor,
    @NotNull SkinColor skinColor
) {

    /**
     * Converts this DTO to an AvatarConfig entity for persistence.
     *
     * @return a new AvatarConfig entity with the values from this request
     */
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
