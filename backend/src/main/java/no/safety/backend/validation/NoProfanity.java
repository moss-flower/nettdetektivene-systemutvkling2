package no.safety.backend.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Rejects strings that contain a substring listed in {@code profanity-list.txt} on the classpath.
 * Matching is case-insensitive. {@code null} values are considered valid.
 */
@Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ProfanityValidator.class)
public @interface NoProfanity {
    String message() default "Username contains inappropriate language.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
