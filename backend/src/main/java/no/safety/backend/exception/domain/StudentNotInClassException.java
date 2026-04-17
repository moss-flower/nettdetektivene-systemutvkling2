package no.safety.backend.exception.domain;

public class StudentNotInClassException extends RuntimeException {

  public StudentNotInClassException(String message) {
    super(message);
  }
}
