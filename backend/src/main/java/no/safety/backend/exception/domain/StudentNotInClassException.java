package no.safety.backend.exception.domain;

/**
 * Exception thrown when a student is not enrolled in the specified classroom.
 * Typically results in an HTTP 404 Not Found response when handled by GlobalExceptionHandler.
 */
public class StudentNotInClassException extends RuntimeException {

  /**
   * Constructs a new StudentNotInClassException with the specified detail message.
   *
   * @param message the detail message explaining the enrollment issue
   */
  public StudentNotInClassException(String message) {
    super(message);
  }
}
