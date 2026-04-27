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
