package no.safety.backend.auth.service;

import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiPasswordChecker;
import org.springframework.stereotype.Service;

/**
 * A service that handles compromised password checking of user provided passwords.
 * Used by the {@link PasswordConstraintValidator} as an additional password security check.
 */
@Service
public class CompromisedPasswordService {

    private final CompromisedPasswordChecker compromisedPasswordChecker;

    public CompromisedPasswordService() {
        this.compromisedPasswordChecker = new HaveIBeenPwnedRestApiPasswordChecker();
    }

    /**
     * Checks if a password is comprimised by using the {@link CompromisedPasswordChecker}.
     * @param password
     * @return
     */
    public boolean isPasswordComrpomised(String password) {
        return compromisedPasswordChecker.check(password).isCompromised();
    }
}
