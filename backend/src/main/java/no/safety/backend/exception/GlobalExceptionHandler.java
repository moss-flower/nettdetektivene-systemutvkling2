package no.safety.backend.exception;

import no.safety.backend.exception.domain.NotClassroomOwnerException;
import no.safety.backend.exception.domain.RoleNotFoundException;
import no.safety.backend.exception.domain.StudentNotInClassException;
import no.safety.backend.exception.domain.UserAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.stream.Collectors;



/**
 * Defines a global exception handler to catch and handle custom exceptions from all sources.
 * Allows for control over which error messages and data reach the user, so unintended information is not leaked from the backend.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles {@link BadCredentialsException} exceptions.
     * @param ex
     * @return Returns a {@link ProblemDetail} with HTTP status code: 401 UNAUTHORIZED.
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail handleBadCredentials(BadCredentialsException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "Invalid email or password");
    }

    /**
     * Handles {@link UserAlreadyExistsException} exceptions.
     * @return Returns a {@link ProblemDetail} with HTTP status code: 409 Conflict.
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ProblemDetail handleUserAlreadyExists(UserAlreadyExistsException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Account Conflict: User already exists.");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    /**
     * Handles {@link RoleNotFoundException} exceptions.
     * @return Returns a {@link ProblemDetail} with HTTP status code: 500 INTERNAL_SERVER_ERROR.
     */
    @ExceptionHandler(RoleNotFoundException.class)
    public ProblemDetail handleRoleNotFound(RoleNotFoundException ex, HttpServletRequest request) {
        log.error("Internal Server Error: {}. Path: {}", ex.getMessage(), request.getRequestURI());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR, 
            "An internal server error occured.");
        problem.setTitle("Internal Server Error");
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
     * Handles {@link NotFoundException} exceptions.
     * @return Returns a {@link ProblemDetail} with HTTP status code: 404 Not Found.
     */
    @ExceptionHandler(NotFoundException.class)
    public ProblemDetail handleNotFoundException(NotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Not Found");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    /**
     * Handles {@link ConflictException} exceptions.
     * @return Returns a {@link ProblemDetail} with HTTP status code: 409 Conflict.
     */
    @ExceptionHandler(ConflictException.class)
    public ProblemDetail handleConflict(ConflictException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Conflict");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    /**
     * Handles {@link ForbiddenException} exceptions.
     * @return Returns a {@link ProblemDetail} with HTTP status code: 403 Forbidden.
     */
    @ExceptionHandler(ForbiddenException.class)
    public ProblemDetail handleForbidden(ForbiddenException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        problem.setTitle("Forbidden");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler({AccessDeniedException.class, AuthorizationDeniedException.class})
    public ProblemDetail handleAccessDenied(Exception ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "Access denied");
        problem.setTitle("Forbidden");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ProblemDetail handleNoResourceFound(NoResourceFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "Resource not found");
        problem.setTitle("Not Found");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    /**
     * Handles {@link NotClassroomOwnerException} exceptions.
     * @return Returns a {@link ProblemDetail} with HTTP status code: 403 Forbidden.
     */
    @ExceptionHandler(NotClassroomOwnerException.class)
    public ProblemDetail handleNotClassroomOwner(NotClassroomOwnerException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        problem.setTitle("Forbidden");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    /**
     * Handles {@link StudentNotInClassException} exceptions.
     * @return Returns a {@link ProblemDetail} with HTTP status code: 404 Not Found.
     */
    @ExceptionHandler(StudentNotInClassException.class)
    public ProblemDetail handleStudentNotInClass(StudentNotInClassException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Not Found");
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
        log.error("Exception: ", ex);
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR, 
            "An unexpected error occurred. Please contact support."
        );
        problem.setTitle("Server Error");
        return problem;
    }
}
