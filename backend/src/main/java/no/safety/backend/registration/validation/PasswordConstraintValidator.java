package no.safety.backend.registration.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import no.safety.backend.auth.service.CompromisedPasswordService;

import org.passay.rule.*;
import org.passay.*;
import java.util.List;

/**
 * Validator for the {@link ValidPassword} annotation.
 * Validates password length and checks against compromised password databases.
 */
@RequiredArgsConstructor
public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

    private final CompromisedPasswordService compromisedPasswordService;

    private static final PasswordValidator VALIDATOR = new DefaultPasswordValidator(
            List.of(new LengthRule(15, 128)));

    /**
     * Validates a password against length rules and compromised password checks.
     *
     * @param password the password to validate
     * @param context the constraint validator context for setting custom messages
     * @return true if the password is valid, false otherwise
     */
    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null)
            return false;

        if (password.isBlank() || password.isEmpty())
            return false;

        ValidationResult result = VALIDATOR.validate(new PasswordData(password));

        if (!result.isValid()){
            setMessage(context, "Password must be between 15 and 128 characters.");
            return false;
        }

        if (compromisedPasswordService.isPasswordComrpomised(password)){
            setMessage(context, "This password has been found in a data breach, please make a new password.");
            return false;
        }

        return true;
    }

    /**
     * Sets a custom error message in the constraint validation context.
     *
     * @param context the constraint validator context
     * @param message the error message to set
     */
    private void setMessage(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
               .addConstraintViolation();
    }
}