package no.safety.backend.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * A ConstraintValidator implementation that invalidates strings that contain profanities listed in
 * a local profanity list text file.
 */
@Slf4j
public class ProfanityValidator implements ConstraintValidator<NoProfanity, String> {

    private static final String LIST_RESOURCE = "/profanity-list.txt";
    private static final List<String> BLOCKED = loadList();

    private static List<String> loadList() {
        try (InputStream in = ProfanityValidator.class.getResourceAsStream(LIST_RESOURCE)) {
            if (in == null) {
                log.warn("Profanity list resource '{}' not found; profanity filter is effectively disabled.", LIST_RESOURCE);
                return List.of();
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                return reader.lines()
                        .map(String::trim)
                        .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                        .map(line -> line.toLowerCase(java.util.Locale.ROOT))
                        .toList();
            }
        } catch (IOException e) {
            log.error("Failed to load profanity list from '{}'", LIST_RESOURCE, e);
            return List.of();
        }
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        String normalized = value.toLowerCase(java.util.Locale.ROOT);
        return BLOCKED.stream().noneMatch(normalized::contains);
    }
}
