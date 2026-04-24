package no.safety.backend.avatar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.safety.backend.avatar.dto.AvatarConfigRequest;
import no.safety.backend.avatar.dto.AvatarConfigResponse;
import no.safety.backend.avatar.enums.EyeColor;
import no.safety.backend.avatar.enums.HairColor;
import no.safety.backend.avatar.enums.HairStyle;
import no.safety.backend.avatar.enums.Outfit;
import no.safety.backend.avatar.enums.SkinColor;
import no.safety.backend.avatar.model.AvatarConfig;
import no.safety.backend.avatar.service.AvatarService;
import no.safety.backend.user.model.StudentUser;
import no.safety.backend.user.repository.StudentUserRepository;

@ExtendWith(MockitoExtension.class)
public class AvatarServiceTest {

    @Mock private StudentUserRepository studentUserRepository;

    @InjectMocks AvatarService avatarService;

    private AvatarConfigRequest request;
    private Long internalUserId;
    private StudentUser student;

    @BeforeEach
    void setUp(){
        internalUserId = 42l;
        request = new AvatarConfigRequest(
            HairStyle.LONG,
            HairColor.BLONDE,
            Outfit.SHERLOCK,
            EyeColor.BLUE,
            SkinColor.COLOR5
        );

        AvatarConfig defaultAvatarConfig = AvatarConfig.defaultConfig();
        defaultAvatarConfig.setSkinColor(SkinColor.COLOR1);

        student = StudentUser.builder()
            .internalId(1l)
            .publicUuid(new UUID(0, 0))
            .username("joe")
            .email("joe@test.com")
            .password("123password")
            .roles(Set.of())
            .classroom(null)
            .avatarConfig(defaultAvatarConfig)
            .build();
        
    }

    @Test
    void successfulRead() {
        when(studentUserRepository.findById(internalUserId)).thenReturn(Optional.of(student));

        AvatarConfigResponse response = avatarService.getAvatar(internalUserId);

        assertEquals(student.getAvatarConfig().getSkinColor(), response.skinColor());
        assertEquals(student.getAvatarConfig().getHairStyle(), response.hairStyle());
    }

    @Test
    void readReturnsDefaultConfigWhenAvatarIsMissing() {
        student.setAvatarConfig(null);
        when(studentUserRepository.findById(internalUserId)).thenReturn(Optional.of(student));

        AvatarConfigResponse response = avatarService.getAvatar(internalUserId);

        assertEquals(AvatarConfig.defaultConfig().getSkinColor(), response.skinColor());
        assertEquals(AvatarConfig.defaultConfig().getHairStyle(), response.hairStyle());
    }

    @Test
    void successfulUpdate() {
        when(studentUserRepository.findById(internalUserId)).thenReturn(Optional.of(student));

        avatarService.updateAvatar(internalUserId, request);

        AvatarConfig config = request.toEntity();

        assertEquals(config.getSkinColor(), student.getAvatarConfig().getSkinColor());
    }


}
