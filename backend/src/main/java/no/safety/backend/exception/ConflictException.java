package no.safety.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a request conflicts with the current state of the resource.
 * Typically results in an HTTP 409 Conflict response when handled by GlobalExceptionHandler.
 */
public class ConflictException extends RuntimeException {

  /**
   * Constructs a new ConflictException with the specified detail message.
   *
   * @param message the detail message explaining the conflict
   */
  public ConflictException(String message) {
    super(message);
  }
}
