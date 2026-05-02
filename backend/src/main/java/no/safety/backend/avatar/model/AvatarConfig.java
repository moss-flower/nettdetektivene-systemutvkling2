package no.safety.backend.avatar.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.safety.backend.avatar.enums.EyeColor;
import no.safety.backend.avatar.enums.HairColor;
import no.safety.backend.avatar.enums.HairStyle;
import no.safety.backend.avatar.enums.Outfit;
import no.safety.backend.avatar.enums.SkinColor;

/**
 * Entity class representing an avatar configuration for a student.
 * This class is embeddable and can be embedded in other entities (e.g., StudentUser).
 * Stores all visual customization options for a student's avatar.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class AvatarConfig {
    /** The hairstyle of the avatar. */
    @Enumerated(EnumType.STRING)
    private HairStyle hairStyle;

    /** The hair color of the avatar. */
    @Enumerated(EnumType.STRING)
    private HairColor hairColor;

    /** The outfit worn by the avatar. */
    @Enumerated(EnumType.STRING)
    private Outfit outfit;

    /** The eye color of the avatar. */
    @Enumerated(EnumType.STRING)
    private EyeColor eyeColor;

    /** The skin color of the avatar. */
    @Enumerated(EnumType.STRING)
    private SkinColor skinColor;

    /**
     * Creates a default avatar configuration with preset values.
     * Used when a student has not yet customized their avatar.
     *
     * @return a new AvatarConfig with default settings (short brown hair, Sherlock outfit, brown eyes, COLOR1 skin)
     */
    public static AvatarConfig defaultConfig() {
        return new AvatarConfig(
            HairStyle.SHORT,
            HairColor.BROWN,
            Outfit.SHERLOCK,
            EyeColor.BROWN,
            SkinColor.COLOR1
        );
    }
}