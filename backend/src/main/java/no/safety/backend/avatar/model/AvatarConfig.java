package no.safety.backend.avatar.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import no.safety.backend.avatar.enums.EyeColor;
import no.safety.backend.avatar.enums.HairColor;
import no.safety.backend.avatar.enums.HairStyle;
import no.safety.backend.avatar.enums.Outfit;
import no.safety.backend.avatar.enums.SkinColor;

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

    public AvatarConfig(HairStyle hairStyle,
                        HairColor hairColor,
                        Outfit outfit,
                        EyeColor eyeColor,
                        SkinColor skinColor) {
        this.hairStyle = hairStyle;
        this.hairColor = hairColor;
        this.outfit = outfit;
        this.eyeColor = eyeColor;
        this.skinColor = skinColor;
    };

    public static AvatarConfig defaultConfig() {
        return new AvatarConfig(
            HairStyle.SHORT,
            HairColor.BROWN,
            Outfit.SHERLOCK,
            EyeColor.BROWN,
            SkinColor.COLOR1
        );
    }

    public HairStyle getHairStyle() { return this.hairStyle; }
    public HairColor getHairColor() { return this.hairColor; }
    public Outfit getOutfit() { return this.outfit; }
    public EyeColor getEyeColor() { return this.eyeColor; }
    public SkinColor getSkinColor() { return this.skinColor; }

    public void setHairStyle(HairStyle hairStyle) {
        this.hairStyle = hairStyle;
    }
    public void setHairColor(HairColor hairColor) {
        this.hairColor = hairColor;
    }
    public void setOutfit(Outfit outfit) {
        this.outfit = outfit;
    }
    public void setEyeColor(EyeColor eyeColor) {
        this.eyeColor = eyeColor;
    }
    public void setSkinColor(SkinColor skinColor) {
        this.skinColor = skinColor;
    }
    
}
