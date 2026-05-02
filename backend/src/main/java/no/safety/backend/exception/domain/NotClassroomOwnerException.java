package no.safety.backend.exception.domain;

/**
 * Exception thrown when a user attempts to access or modify a classroom they don't own.
 * Typically results in an HTTP 403 Forbidden response when handled by GlobalExceptionHandler.
 */
public class NotClassroomOwnerException extends RuntimeException {

  /**
   * Constructs a new NotClassroomOwnerException with the specified detail message.
   *
   * @param message the detail message explaining why access was denied
   */
  public NotClassroomOwnerException(String message) {
    super(message);
  }
}
