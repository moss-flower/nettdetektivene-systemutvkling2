package no.safety.backend.exception;

import no.safety.backend.exception.domain.NotClassroomOwnerException;
import no.safety.backend.exception.domain.RoleNotFoundException;
import no.safety.backend.exception.domain.StudentNotInClassException;
import no.safety.backend.exception.domain.UserAlreadyExistsException;
import no.safety.backend.exception.domain.UserNotFoundException;
import no.safety.backend.notepad.exception.NotepadEntryNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.stream.Collectors;

/**
 * Global exception handler that catches and handles exceptions from all controllers.
 * Provides consistent error responses using RFC 7807 Problem Detail format.
 * Ensures sensitive information is not leaked to clients.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles BadCredentialsException thrown during authentication.
     *
     * @param ex the BadCredentialsException thrown
     * @return a ProblemDetail with HTTP 401 UNAUTHORIZED status
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail handleBadCredentials(BadCredentialsException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "Invalid email or password");
    }

    /**
     * Handles IllegalArgumentException for invalid arguments.
     *
     * @param ex the IllegalArgumentException thrown
     * @return a ProblemDetail with HTTP 401 UNAUTHORIZED status
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    /**
     * Handles UserAlreadyExistsException when attempting to register a duplicate user.
     *
     * @param ex the UserAlreadyExistsException thrown
     * @return a ProblemDetail with HTTP 409 CONFLICT status
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ProblemDetail handleUserAlreadyExists(UserAlreadyExistsException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Account Conflict: User already exists.");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    /**
     * Handles UserNotFoundException when a requested user is not found.
     *
     * @param ex the UserNotFoundException thrown
     * @return a ProblemDetail with HTTP 404 NOT_FOUND status
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ProblemDetail handleUserNotFound(UserNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("User not found.");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    /**
     * Handles RoleNotFoundException when a requested role is not found in the system.
     *
     * @param ex the RoleNotFoundException thrown
     * @param request the HTTP servlet request
     * @return a ProblemDetail with HTTP 500 INTERNAL_SERVER_ERROR status
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
     * Handles MethodArgumentNotValidException for @Valid annotation validation failures.
     *
     * @param ex the MethodArgumentNotValidException thrown
     * @return a ProblemDetail with HTTP 400 BAD_REQUEST status containing validation errors
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
     * Handles NotFoundException when a requested resource is not found.
     *
     * @param ex the NotFoundException thrown
     * @return a ProblemDetail with HTTP 404 NOT_FOUND status
     */
    @ExceptionHandler(NotFoundException.class)
    public ProblemDetail handleNotFoundException(NotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Not Found");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    /**
     * Handles NotepadEntryNotFoundException when a requested notepad entry is not found.
     *
     * @param ex the NotepadEntryNotFoundException thrown
     * @return a ProblemDetail with HTTP 404 NOT_FOUND status
     */
    @ExceptionHandler(NotepadEntryNotFoundException.class)
    public ProblemDetail handleNotepadEntryNotFoundException(NotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Not Found");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    /**
     * Handles ConflictException when a request conflicts with the current resource state.
     *
     * @param ex the ConflictException thrown
     * @return a ProblemDetail with HTTP 409 CONFLICT status
     */
    @ExceptionHandler(ConflictException.class)
    public ProblemDetail handleConflict(ConflictException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Conflict");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    /**
     * Handles ForbiddenException when a user is forbidden from performing an action.
     *
     * @param ex the ForbiddenException thrown
     * @return a ProblemDetail with HTTP 403 FORBIDDEN status
     */
    @ExceptionHandler(ForbiddenException.class)
    public ProblemDetail handleForbidden(ForbiddenException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        problem.setTitle("Forbidden");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    /**
     * Handles Spring Security AuthorizationDeniedException when authorization fails.
     *
     * @param ex the AuthorizationDeniedException thrown
     * @return a ProblemDetail with HTTP 403 FORBIDDEN status
     */
    @ExceptionHandler(AuthorizationDeniedException.class)
    public ProblemDetail handleAuthorizationDenied(AuthorizationDeniedException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "Access denied");
        problem.setTitle("Forbidden");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    /**
     * Handles NoResourceFoundException when a requested resource is not found.
     *
     * @param ex the NoResourceFoundException thrown
     * @return a ProblemDetail with HTTP 404 NOT_FOUND status
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ProblemDetail handleNoResourceFound(NoResourceFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "Resource not found");
        problem.setTitle("Not Found");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    /**
     * Handles NotClassroomOwnerException when a user is not the owner of a classroom.
     *
     * @param ex the NotClassroomOwnerException thrown
     * @return a ProblemDetail with HTTP 403 FORBIDDEN status
     */
    @ExceptionHandler(NotClassroomOwnerException.class)
    public ProblemDetail handleNotClassroomOwner(NotClassroomOwnerException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        problem.setTitle("Forbidden");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    /**
     * Handles custom AccessDeniedException when a user is denied access to a resource.
     *
     * @param ex the AccessDeniedException thrown
     * @return a ProblemDetail with HTTP 403 FORBIDDEN status
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDeniedException(AccessDeniedException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        problem.setTitle("Access Denied");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    /**
     * Handles StudentNotInClassException when a student is not enrolled in a classroom.
     *
     * @param ex the StudentNotInClassException thrown
     * @return a ProblemDetail with HTTP 404 NOT_FOUND status
     */
    @ExceptionHandler(StudentNotInClassException.class)
    public ProblemDetail handleStudentNotInClass(StudentNotInClassException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Not Found");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    /**
     * Handles Spring Security AccessDeniedException when access is denied.
     *
     * @param ex the AccessDeniedException thrown
     * @return a ProblemDetail with HTTP 403 FORBIDDEN status
     */
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ProblemDetail handleSpringAccessDenied(org.springframework.security.access.AccessDeniedException ex) {

        log.error("AccessDeniedException: {}", ex.getMessage());

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "Access Denied");
        problem.setTitle("Forbidden");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }


    /**
     * Fallback handler for any unhandled exceptions.
     * Logs the full exception and returns a generic error response to prevent information leakage.
     *
     * @param ex the unhandled Exception
     * @return a ProblemDetail with HTTP 500 INTERNAL_SERVER_ERROR status
     */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneralError(Exception ex) {
        log.error("Exception: ", ex);
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR, 
            "An unexpected error occurred."
        );
        problem.setTitle("Server Error");
        return problem;
    }
}
