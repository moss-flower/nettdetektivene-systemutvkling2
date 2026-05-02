package no.safety.backend.achievements.service;

import no.safety.backend.achievements.dto.AchievementRequest;
import no.safety.backend.achievements.dto.AchievementResponse;
import no.safety.backend.achievements.model.Achievement;
import no.safety.backend.achievements.model.AchievementCategory;
import no.safety.backend.achievements.model.UserAchievement;
import no.safety.backend.achievements.repository.AchievementRepository;
import no.safety.backend.achievements.repository.UserAchievementRepository;
import no.safety.backend.station.model.Station;
import no.safety.backend.user.model.StudentUser;
import no.safety.backend.user.repository.StudentUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AchievementServiceTest {

  @Mock
  private AchievementRepository achievementRepository;

  @Mock
  private UserAchievementRepository userAchievementRepository;

  @Mock
  private StudentUserRepository studentUserRepository;

  @InjectMocks
  private AchievementService achievementService;

  private StudentUser student;
  private Achievement stopAchievement;
  private Achievement specialAchievement;

  @BeforeEach
  void setUp() {
    student = new StudentUser();

    stopAchievement = new Achievement();
    stopAchievement.setInternalId(1L);
    stopAchievement.setUUID(UUID.randomUUID());
    stopAchievement.setTitle("Passordbanken");
    stopAchievement.setDescription("Fullførte Passordbanken.");
    stopAchievement.setCategory(AchievementCategory.STOP);
    stopAchievement.setSource("Passordbanken");
    stopAchievement.setReward(null);

    specialAchievement = new Achievement();
    specialAchievement.setInternalId(2L);
    specialAchievement.setUUID(UUID.randomUUID());
    specialAchievement.setTitle("Datasenteret");
    specialAchievement.setDescription("Beseiret hovedbossen i Datasenteret.");
    specialAchievement.setCategory(AchievementCategory.SPECIAL);
    specialAchievement.setSource("Datasenteret");
    specialAchievement.setReward("Spesialmedalje");
  }

  @Test
  void getAchievementsForStudent_shouldReturnAllAchievementsWithCorrectUnlockedStatus() {
    UserAchievement unlocked = new UserAchievement();
    unlocked.setStudent(student);
    unlocked.setAchievement(stopAchievement);

    when(achievementRepository.findAll()).thenReturn(List.of(stopAchievement, specialAchievement));
    when(userAchievementRepository.findByStudent(student)).thenReturn(List.of(unlocked));

    List<AchievementResponse> result = achievementService.getAchievementsForStudent(student);

    assertEquals(2, result.size());

    AchievementResponse first = result.get(0);
    assertEquals(stopAchievement.getUUID(), first.getUUID());
    assertEquals("Passordbanken", first.getTitle());
    assertTrue(first.isUnlocked());

    AchievementResponse second = result.get(1);
    assertEquals(specialAchievement.getUUID(), second.getUUID());
    assertEquals("Datasenteret", second.getTitle());
    assertFalse(second.isUnlocked());

    verify(achievementRepository).findAll();
    verify(userAchievementRepository).findByStudent(student);
  }

  @Test
  void getAllAchievements_shouldReturnAllAchievementsWithUnlockedFalse() {
    when(achievementRepository.findAll()).thenReturn(List.of(stopAchievement, specialAchievement));

    List<AchievementResponse> result = achievementService.getAllAchievements();

    assertEquals(2, result.size());
    assertFalse(result.get(0).isUnlocked());
    assertFalse(result.get(1).isUnlocked());
    assertEquals("stop", result.get(0).getCategory());
    assertEquals("special", result.get(1).getCategory());

    verify(achievementRepository).findAll();
    verifyNoInteractions(userAchievementRepository);
  }

  @Test
  void getAchievementByUUID_shouldReturnAchievementWhenFound() {
    UUID uuid = stopAchievement.getUUID();
    when(achievementRepository.findByUUID(uuid)).thenReturn(Optional.of(stopAchievement));

    AchievementResponse result = achievementService.getAchievementByUUID(uuid);

    assertEquals(uuid, result.getUUID());
    assertEquals("Passordbanken", result.getTitle());
    assertFalse(result.isUnlocked());

    verify(achievementRepository).findByUUID(uuid);
  }

  @Test
  void getAchievementByUUID_shouldThrowWhenNotFound() {
    UUID uuid = UUID.randomUUID();
    when(achievementRepository.findByUUID(uuid)).thenReturn(Optional.empty());

    ResponseStatusException exception = assertThrows(
        ResponseStatusException.class,
        () -> achievementService.getAchievementByUUID(uuid)
    );

    assertEquals(404, exception.getStatusCode().value());
    assertEquals("Achievement not found", exception.getReason());

    verify(achievementRepository).findByUUID(uuid);
  }

  @Test
  void createAchievement_shouldSaveAndReturnMappedResponse() {
    AchievementRequest request = new AchievementRequest();
    request.setTitle("Ny medalje");
    request.setDescription("Beskrivelse");
    request.setCategory(AchievementCategory.STOP);
    request.setSource("Testverden");
    request.setReward("Test reward");

    Achievement savedAchievement = new Achievement();
    savedAchievement.setInternalId(10L);
    savedAchievement.setUUID(UUID.randomUUID());
    savedAchievement.setTitle(request.getTitle());
    savedAchievement.setDescription(request.getDescription());
    savedAchievement.setCategory(request.getCategory());
    savedAchievement.setSource(request.getSource());
    savedAchievement.setReward(request.getReward());

    when(achievementRepository.save(any(Achievement.class))).thenReturn(savedAchievement);

    AchievementResponse result = achievementService.createAchievement(request);

    ArgumentCaptor<Achievement> captor = ArgumentCaptor.forClass(Achievement.class);
    verify(achievementRepository).save(captor.capture());

    Achievement captured = captor.getValue();
    assertNotNull(captured.getUUID());
    assertEquals("Ny medalje", captured.getTitle());
    assertEquals("Beskrivelse", captured.getDescription());
    assertEquals(AchievementCategory.STOP, captured.getCategory());
    assertEquals("Testverden", captured.getSource());
    assertEquals("Test reward", captured.getReward());

    assertEquals(savedAchievement.getUUID(), result.getUUID());
    assertEquals("Ny medalje", result.getTitle());
    assertFalse(result.isUnlocked());
  }

  @Test
  void updateAchievement_shouldUpdateExistingAchievementAndReturnResponse() {
    UUID uuid = stopAchievement.getUUID();

    AchievementRequest request = new AchievementRequest();
    request.setTitle("Oppdatert tittel");
    request.setDescription("Oppdatert beskrivelse");
    request.setCategory(AchievementCategory.SPECIAL);
    request.setSource("Oppdatert kilde");
    request.setReward("Ny reward");

    when(achievementRepository.findByUUID(uuid)).thenReturn(Optional.of(stopAchievement));
    when(achievementRepository.save(any(Achievement.class))).thenAnswer(invocation -> invocation.getArgument(0));

    AchievementResponse result = achievementService.updateAchievement(uuid, request);

    assertEquals("Oppdatert tittel", stopAchievement.getTitle());
    assertEquals("Oppdatert beskrivelse", stopAchievement.getDescription());
    assertEquals(AchievementCategory.SPECIAL, stopAchievement.getCategory());
    assertEquals("Oppdatert kilde", stopAchievement.getSource());
    assertEquals("Ny reward", stopAchievement.getReward());

    assertEquals(uuid, result.getUUID());
    assertEquals("Oppdatert tittel", result.getTitle());
    assertEquals("special", result.getCategory());
    assertFalse(result.isUnlocked());

    verify(achievementRepository).findByUUID(uuid);
    verify(achievementRepository).save(stopAchievement);
  }

  @Test
  void updateAchievement_shouldThrowWhenAchievementNotFound() {
    UUID uuid = UUID.randomUUID();

    AchievementRequest request = new AchievementRequest();
    request.setTitle("Oppdatert tittel");

    when(achievementRepository.findByUUID(uuid)).thenReturn(Optional.empty());

    ResponseStatusException exception = assertThrows(
        ResponseStatusException.class,
        () -> achievementService.updateAchievement(uuid, request)
    );

    assertEquals(404, exception.getStatusCode().value());
    assertEquals("Achievement not found", exception.getReason());

    verify(achievementRepository).findByUUID(uuid);
    verify(achievementRepository, never()).save(any());
  }

  @Test
  void deleteAchievement_shouldDeleteWhenFound() {
    UUID uuid = stopAchievement.getUUID();
    when(achievementRepository.findByUUID(uuid)).thenReturn(Optional.of(stopAchievement));

    achievementService.deleteAchievement(uuid);

    verify(achievementRepository).findByUUID(uuid);
    verify(achievementRepository).delete(stopAchievement);
  }

  @Test
  void deleteAchievement_shouldThrowWhenNotFound() {
    UUID uuid = UUID.randomUUID();
    when(achievementRepository.findByUUID(uuid)).thenReturn(Optional.empty());

    ResponseStatusException exception = assertThrows(
        ResponseStatusException.class,
        () -> achievementService.deleteAchievement(uuid)
    );

    assertEquals(404, exception.getStatusCode().value());
    assertEquals("Achievement not found", exception.getReason());

    verify(achievementRepository).findByUUID(uuid);
    verify(achievementRepository, never()).delete(any());
  }

  @Test
  void syncStationAchievement_shouldUpgradeLegacyTitleBasedAchievement() {
    UUID stationUid = UUID.randomUUID();
    Station station = new Station();
    station.setUid(stationUid);
    station.setTitle("Datasenteret");
    station.setBoss(true);

    when(achievementRepository.findBySource(stationUid.toString())).thenReturn(Optional.empty());
    when(achievementRepository.findBySource("Datasenteret")).thenReturn(Optional.of(stopAchievement));
    when(achievementRepository.save(any(Achievement.class))).thenAnswer(invocation -> invocation.getArgument(0));

    Achievement result = achievementService.syncStationAchievement(station, null);

    assertSame(stopAchievement, result);
    assertEquals("Datasenteret", result.getTitle());
    assertEquals("Fullførte Datasenteret.", result.getDescription());
    assertEquals(AchievementCategory.SPECIAL, result.getCategory());
    assertEquals(stationUid.toString(), result.getSource());
    assertEquals("Spesialmedalje for å ha slått bossen", result.getReward());
    verify(achievementRepository).save(stopAchievement);
  }

  @Test
  void unlockStationAchievement_shouldCreateAndUnlockMissingAchievement() {
    UUID stationUid = UUID.randomUUID();
    Station station = new Station();
    station.setUid(stationUid);
    station.setTitle("Bossrommet");
    station.setBoss(true);

    when(achievementRepository.findBySource(stationUid.toString())).thenReturn(Optional.empty());
    when(achievementRepository.findBySource("Bossrommet")).thenReturn(Optional.empty());
    when(achievementRepository.findAllByTitle("Bossrommet")).thenReturn(List.of());
    when(achievementRepository.save(any(Achievement.class))).thenAnswer(invocation -> invocation.getArgument(0));
    when(userAchievementRepository.existsByStudentAndAchievement(eq(student), any(Achievement.class))).thenReturn(false);

    AchievementResponse result = achievementService.unlockStationAchievement(student, station);

    assertTrue(result.isUnlocked());
    assertEquals("special", result.getCategory());
    assertEquals(stationUid.toString(), result.getSource());
    verify(achievementRepository).save(any(Achievement.class));
    verify(userAchievementRepository).save(any(UserAchievement.class));
    verify(studentUserRepository).save(student);
  }
}
