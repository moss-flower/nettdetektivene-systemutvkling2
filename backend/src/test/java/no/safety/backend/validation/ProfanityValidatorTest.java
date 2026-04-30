package no.safety.backend.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class ProfanityValidatorTest {

    private ProfanityValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new ProfanityValidator();
        context = mock(ConstraintValidatorContext.class);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { "alice", "bob_42", "user.name", "Alex123", "kari-nordmann" })
    void cleanStrings_areValid(String username) {
        assertThat(validator.isValid(username, context)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = { "fuck", "shit", "asshole", "faen", "helvete" })
    void exactProfaneWords_areInvalid(String username) {
        assertThat(validator.isValid(username, context)).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = { "fuckyou", "myshitname", "prefix_faen", "user.asshole.42" })
    void stringsWithProfanityAsSubstring_areInvalid(String username) {
        assertThat(validator.isValid(username, context)).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = { "FUCK", "FuCk", "ShIt", "FAEN", "Helvete" })
    void profanityMatching_issCaseInsensitive(String username) {
        assertThat(validator.isValid(username, context)).isFalse();
    }
}
