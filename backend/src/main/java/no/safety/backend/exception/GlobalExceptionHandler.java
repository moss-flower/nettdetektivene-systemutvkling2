package no.safety.backend.exception;

import no.safety.backend.exception.domain.RoleNotFoundException;
import no.safety.backend.exception.domain.UserAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.stream.Collectors;

/**
 * Defines a global exception handler to catch and handle custom exceptions from all sources.
 * Allows for control over which error messages and data reach the user, so unintended information is not leaked from the backend.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles {@link UserAlreadyExistsException} exceptions.
     * @return Returns a {@link ProblemDetail} with HTTP status code: 409 Conflict.
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ProblemDetail handleUserAlreadyExists(UserAlreadyExistsException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Account Conflict");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    /**
     * Handles {@link RoleNotFoundException} exceptions.
     * @return Returns a {@link ProblemDetail} with HTTP status code: 500 INTERNAL_SERVER_ERROR.
     */
    @ExceptionHandler(RoleNotFoundException.class)
    public ProblemDetail handleRoleNotFound(RoleNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        problem.setTitle("Configuration Error");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    /**
     * Handles {@link MethodArgumentNotValidException} exceptions.
     * @return Returns a {@link ProblemDetail} with HTTP status code: 500 INTERNAL_SERVER_ERROR.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationErrors(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, errors);
        problem.setTitle("Validation Failed");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    /**
     * Handles all {@link Exception} that are not specifically defined in this handler.
     * Catches unhandled exceptions to protect against data leaks.
     * @return Returns a {@link ProblemDetail} with HTTP status code: 500 INTERNAL_SERVER_ERROR.
     */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneralError(Exception ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR, 
            "An unexpected error occurred. Please contact support."
        );
        problem.setTitle("Server Error");
        return problem;
    }
}
