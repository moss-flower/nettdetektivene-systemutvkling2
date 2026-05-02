package no.safety.backend.avatar.dto;

import java.util.Objects;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import no.safety.backend.avatar.enums.EyeColor;
import no.safety.backend.avatar.enums.HairColor;
import no.safety.backend.avatar.enums.HairStyle;
import no.safety.backend.avatar.enums.Outfit;
import no.safety.backend.avatar.enums.SkinColor;
import no.safety.backend.avatar.model.AvatarConfig;

/**
 * Data Transfer Object (DTO) for sending avatar configuration data to the client.
 * Contains all avatar customization attributes that define a student's avatar appearance.
 *
 * @param hairStyle the selected hairstyle for the avatar
 * @param hairColor the selected hair color for the avatar
 * @param outfit the selected outfit for the avatar
 * @param eyeColor the selected eye color for the avatar
 * @param skinColor the selected skin color for the avatar
 */
@Schema(description = "Avatar configuration returned to the frontend")
public record AvatarConfigResponse(
    @NotNull
    @Schema(description = "Selected hairstyle", example = "SHORT")
    HairStyle hairStyle,

    @NotNull
    @Schema(description = "Selected hair color", example = "BROWN")
    HairColor hairColor,

    @NotNull
    @Schema(description = "Selected outfit", example = "SHERLOCK")
    Outfit outfit,

    @NotNull
    @Schema(description = "Selected eye color", example = "BROWN")
    EyeColor eyeColor,

    @NotNull
    @Schema(description = "Selected skin color", example = "COLOR1")
    SkinColor skinColor
) {
    /**
     * Creates an AvatarConfigResponse from an AvatarConfig entity.
     * If the provided entity is null, returns a response with default configuration values.
     *
     * @param avatarConfig the avatar configuration entity to convert (can be null)
     * @return a new AvatarConfigResponse with values from the entity or defaults
     */
    public static AvatarConfigResponse fromEntity(AvatarConfig avatarConfig) {
        AvatarConfig config = Objects.requireNonNullElseGet(avatarConfig, AvatarConfig::defaultConfig);

        return new AvatarConfigResponse(
            config.getHairStyle(),
            config.getHairColor(),
            config.getOutfit(),
            config.getEyeColor(),
            config.getSkinColor()
        );
    }
}
