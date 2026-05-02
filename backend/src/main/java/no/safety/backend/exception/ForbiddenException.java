package no.safety.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a user is forbidden from performing a specific action.
 * Typically results in an HTTP 403 Forbidden response when handled by GlobalExceptionHandler.
 */
public class ForbiddenException extends RuntimeException {

  /**
   * Constructs a new ForbiddenException with the specified detail message.
   *
   * @param message the detail message explaining why the action is forbidden
   */
  public ForbiddenException(String message) {
    super(message);
  }
}
