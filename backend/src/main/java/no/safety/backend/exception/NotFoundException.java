package no.safety.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a requested resource is not found.
 * Typically results in an HTTP 404 Not Found response when handled by GlobalExceptionHandler.
 */
public class NotFoundException extends RuntimeException {

  /**
   * Constructs a new NotFoundException with the specified detail message.
   *
   * @param message the detail message explaining what resource was not found
   */
  public NotFoundException(String message) {
    super(message);
  }
}
