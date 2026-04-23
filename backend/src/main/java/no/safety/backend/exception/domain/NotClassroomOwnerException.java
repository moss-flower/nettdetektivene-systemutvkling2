package no.safety.backend.exception.domain;

public class NotClassroomOwnerException extends RuntimeException {

  public NotClassroomOwnerException(String message) {
    super(message);
  }
}
