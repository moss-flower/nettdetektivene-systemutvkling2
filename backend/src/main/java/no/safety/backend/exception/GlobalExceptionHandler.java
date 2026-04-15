package no.safety.backend.exception;

import no.safety.backend.exception.domain.RoleNotFoundException;
import no.safety.backend.exception.domain.UserAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles business logic conflicts (e.g., Email already exists).
     * Returns 409 Conflict.
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ProblemDetail handleUserAlreadyExists(UserAlreadyExistsException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Account Conflict");
        problem.setType(URI.create("https://api.safety.no/errors/user-already-exists"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    /**
     * Handles configuration issues (e.g., Missing Roles).
     * Returns 500 Internal Server Error because the server is misconfigured.
     */
    @ExceptionHandler(RoleNotFoundException.class)
    public ProblemDetail handleRoleNotFound(RoleNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        problem.setTitle("Configuration Error");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    /**
     * Handles @Valid validation failures from your DTOs.
     * Returns 400 Bad Request with a list of specific field errors.
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
     * Catch-all for any other unexpected errors.
     */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneralError(Exception ex) {
        // Log the exception here (e.g., log.error("Unexpected error", ex))
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR, 
            "An unexpected error occurred. Please contact support."
        );
        problem.setTitle("Server Error");
        return problem;
    }
}
