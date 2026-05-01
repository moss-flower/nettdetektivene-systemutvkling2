package no.safety.backend.registration.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import no.safety.backend.auth.service.CompromisedPasswordService;

import org.passay.rule.*;
import org.passay.*;
import java.util.List;

/**
 * Vaidates a password using the Passay library.
 * Handles the annotation @ValidPassword.
 */
@RequiredArgsConstructor
public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

    private final CompromisedPasswordService compromisedPasswordService;

    private static final PasswordValidator VALIDATOR = new DefaultPasswordValidator(
            List.of(new LengthRule(15, 128)));

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

    private void setMessage(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
               .addConstraintViolation();
    }
}