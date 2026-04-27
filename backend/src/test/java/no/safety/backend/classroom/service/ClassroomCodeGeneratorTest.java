package no.safety.backend.classroom.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import no.safety.backend.classroom.model.Classroom;
import no.safety.backend.classroom.repository.ClassroomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ClassroomCodeGeneratorTest {

  @Mock
  private ClassroomRepository repo;

  private ClassroomCodeGenerator codegen;

  @BeforeEach
  void setUp() {
    codegen = new ClassroomCodeGenerator(repo);
  }

  @Test
  void generateUniqueCode_generatesCodeInExpectedFormat() {
    when(repo.existsByCode(anyString())).thenReturn(false);
    String code = codegen.generateUniqueCode();
    assertTrue(code.matches("[A-ZÆØÅ]+-[A-ZÆØÅ]+-[A-ZÆØÅ]+"));
  }

  @Test
  void generateUniqueCode_returnsCodeWhenRepositoryReportsItUnique() {
    when(repo.existsByCode(anyString())).thenReturn(false);

    String code = codegen.generateUniqueCode();

    assertThat(code).isNotBlank();
    verify(repo, times(1)).existsByCode(code);
  }

  @Test
  void generateUniqueCode_throwsWhenAllAttemptsAreDuplicates() {
    when(repo.existsByCode(anyString())).thenReturn(true);

    assertThatThrownBy(() -> codegen.generateUniqueCode())
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Could not generate a unique code");

    verify(repo, atLeast(20)).existsByCode(anyString());
  }

}