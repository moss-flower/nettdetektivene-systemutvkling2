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

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class AvatarConfig {
    @Enumerated(EnumType.STRING)
    private HairStyle hairStyle;

    @Enumerated(EnumType.STRING)
    private HairColor hairColor;

    @Enumerated(EnumType.STRING)
    private Outfit outfit;

    @Enumerated(EnumType.STRING)
    private EyeColor eyeColor;

    @Enumerated(EnumType.STRING)
    private SkinColor skinColor;

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