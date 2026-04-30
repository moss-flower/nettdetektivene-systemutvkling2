package no.safety.backend.studentprofile.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;
import no.safety.backend.auth.model.UserPrincipal;
import no.safety.backend.studentprofile.dto.StudentProfileResponse;
import no.safety.backend.studentprofile.service.StudentProfileService;
import no.safety.backend.user.model.StudentUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StudentProfileControllerTest {

  @Mock
  private StudentProfileService studentProfileService;

  @InjectMocks
  private StudentProfileController studentProfileController;

  @Test
  void getMyProfile_delegatesToService() {
    UUID studentUid = UUID.randomUUID();
    StudentUser student = StudentUser.builder()
        .publicUuid(studentUid)
        .username("alice")
        .build();
    UserPrincipal principal = UserPrincipal.fromEntity(student);

    StudentProfileResponse expected = new StudentProfileResponse(studentUid, "alice", 250, 2, 1, 0);
    when(studentProfileService.getProfile(student)).thenReturn(expected);

    StudentProfileResponse result = studentProfileController.getMyProfile(principal);

    assertSame(expected, result);
    assertEquals(2, result.totalXp());
    verify(studentProfileService).getProfile(student);
  }
}
