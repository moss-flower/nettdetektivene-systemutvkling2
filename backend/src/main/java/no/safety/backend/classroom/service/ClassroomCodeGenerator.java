package no.safety.backend.classroom.service;

import java.security.SecureRandom;
import java.util.List;
import lombok.RequiredArgsConstructor;
import no.safety.backend.classroom.repository.ClassroomRepository;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
@RequiredArgsConstructor
public class ClassroomCodeGenerator {

  private final List<String> WORDS = List.of("alle", "ingen", "noen", "mange", "få", "kake", "tur",
      "rev", "katt", "løve", "sterk", "svak", "teit", "eple", "båt", "slott", "konge", "stor",
      "due",
      "brus", "burger", "potet", "gulrot", "penn", "blyant", "tavle", "liten", "bål", "skog", "tre",
      "lava", "blad", "super", "brød", "vann", "elv", "hav", "ark", "papir", "linjal", "helt", "gummi",
      "regn", "sky", "melk", "åpen", "lukt", "kopp", "glass", "fin", "modig", "kul", "mat");
  private final ClassroomRepository repo;
  private final SecureRandom random = new SecureRandom();

  /**
   * Generates a unique 3-word join code for classrooms.
   *
   * @return a 3 word string in the format WORD-WORD-WORD
   * @throws IllegalStateException if a unique code failed to generate after 20 attempts
   */
  public String generateUniqueCode() {
    int maxAttempts = 20;
    for (int i = 0; i < maxAttempts; i++) {
      String code = String.format("%S-%S-%S", pick(), pick(), pick());
      if (!repo.existsByCode(code)) {
        return code;
      }
    }
    throw new IllegalStateException("Could not generate a unique code.");
  }

  private String pick() {
    return WORDS.get(random.nextInt(WORDS.size()));
  }
}
